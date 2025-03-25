package com.example.afyabora.service;

import com.example.afyabora.model.Appointment;
import com.example.afyabora.repository.AppointmentRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class ZoomService {

    private final ZoomAuthService zoomAuthService;
    private final AppointmentRepository appointmentRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = Logger.getLogger(ZoomService.class.getName());

    public ZoomService(ZoomAuthService zoomAuthService, AppointmentRepository appointmentRepository) {
        this.zoomAuthService = zoomAuthService;
        this.appointmentRepository = appointmentRepository;
    }

    // ✅ Fetch the most recent appointment for the doctor
    private Optional<Appointment> getLatestAppointmentForDoctor(String doctorEmail) {
        return appointmentRepository.findTopByDoctorEmailOrderByAppointmentTimeDesc(doctorEmail);
    }

    // ✅ Check if the doctor has a registered Zoom account
    public boolean isZoomUserValid(String doctorEmail) {
        String url = "https://api.zoom.us/v2/users/" + doctorEmail;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + zoomAuthService.getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException.NotFound e) {
            logger.warning("❌ Zoom user not found: " + doctorEmail);
            return false;
        } catch (Exception e) {
            logger.severe("⚠️ Error checking Zoom user: " + e.getMessage());
            return false;
        }
    }

    // ✅ Create a Zoom Meeting and Save URL in Appointment
    public String createZoomMeeting(String doctorEmail) {
        try {
            // 🔹 Ensure the doctor has a Zoom account
            if (!isZoomUserValid(doctorEmail)) {
                throw new RuntimeException("Error: The selected doctor does not have a registered Zoom account.");
            }

            // 🔹 Fetch the latest appointment for the doctor
            Appointment appointment = getLatestAppointmentForDoctor(doctorEmail)
                    .orElseThrow(() -> new RuntimeException("No appointment found for the doctor."));

            String url = "https://api.zoom.us/v2/users/" + doctorEmail + "/meetings";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + zoomAuthService.getAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 🔹 Meeting Details
            Map<String, Object> body = new HashMap<>();
            body.put("topic", "Doctor Consultation");
            body.put("type", 2); // Scheduled meeting
            body.put("duration", 30);
            body.put("timezone", "Africa/Nairobi");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            JSONObject jsonResponse = new JSONObject(response.getBody());
            String zoomMeetingUrl = jsonResponse.getString("join_url");

            // 🔹 Save Zoom URL to appointment
            appointment.setZoomMeetingUrl(zoomMeetingUrl);
            appointmentRepository.save(appointment);
            logger.info("✅ Zoom meeting URL saved in the appointment.");

            return zoomMeetingUrl;

        } catch (HttpClientErrorException e) {
            logger.severe("❌ Zoom API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("Error creating Zoom meeting: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.severe("⚠️ Failed to create Zoom meeting: " + e.getMessage());
            throw new RuntimeException("Unexpected error creating Zoom meeting.");
        }
    }

    // ✅ Process Zoom Webhook Events
    public void processWebhookEvent(Map<String, Object> payload) {
        logger.info("🔔 Received Zoom Webhook: " + payload);

        String eventType = (String) payload.get("event");
        if ("meeting.started".equals(eventType)) {
            logger.info("✅ Zoom meeting started: " + payload);
        } else if ("meeting.ended".equals(eventType)) {
            logger.info("🛑 Zoom meeting ended: " + payload);
        } else {
            logger.info("⚠️ Unhandled Zoom event: " + eventType);
        }
    }
}
