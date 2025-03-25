package com.example.afyabora.service;

import com.example.afyabora.model.AppointmentStatus;
import com.example.afyabora.model.Appointment;
import com.example.afyabora.model.User;
import com.example.afyabora.repository.AppointmentRepository;
import com.example.afyabora.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ZoomService zoomService;
    private final NotificationService notificationService;

    public AppointmentService(AppointmentRepository appointmentRepository, UserRepository userRepository,
                              ZoomService zoomService, NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.zoomService = zoomService;
        this.notificationService = notificationService;
    }

    // 🔹 Get All Doctors from Users Table
    public List<User> getAllDoctors() {
        return userRepository.findByRole("DOCTOR");
    }

    // 🔹 Get All Patients from Users Table
    public List<User> getAllPatients() {
        return userRepository.findByRole("PATIENT");
    }

    // 🔹 Book an Appointment
    public Appointment bookAppointment(Long patientId, Long doctorId, LocalDateTime appointmentTime, boolean isVirtual) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus(AppointmentStatus.PENDING);

        // If Virtual, Create Zoom Meeting
        if (isVirtual) {
            String zoomUrl = zoomService.createZoomMeeting(doctor.getEmail());
            appointment.setZoomMeetingUrl(zoomUrl);
        }

        appointmentRepository.save(appointment);

        // 🔔 Notify the doctor about the new appointment request
        notificationService.sendBookingNotification(doctor.getId(), true);

// 🔔 Notify the patient that their appointment is booked
        notificationService.sendBookingNotification(patient.getId(), false);

    return appointment;
    }
        // 🔹 Accept Appointment
    public Appointment acceptAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.ACCEPTED);
        appointmentRepository.save(appointment);

        String message = "Your appointment with " + appointment.getDoctor().getUsername() + " has been accepted.";
        if (appointment.getZoomMeetingUrl() != null) {
            message += " Join the meeting here: " + appointment.getZoomMeetingUrl();
        }

        // 🔔 Send WebSocket Notification to the Patient
        notificationService.sendNotification(appointment.getPatient().getId(), message);

        return appointment;
    }

    // 🔹 Reject Appointment
    public Appointment rejectAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.REJECTED);
        appointmentRepository.save(appointment);

        // 🔔 Notify the patient about rejection
        String message = "Your appointment with " + appointment.getDoctor().getUsername() + " has been rejected.";
        notificationService.sendNotification(appointment.getPatient().getId(), message);

        return appointment;
    }

    // 🔹 Cancel Appointment
    public Appointment cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CANCELED);
        appointmentRepository.save(appointment);

        // 🔔 Notify the doctor about cancellation
        String message = "Your appointment with " + appointment.getPatient().getUsername() + " has been cancelled.";
        notificationService.sendNotification(appointment.getDoctor().getId(), message);

        return appointment;
    }

    // 🔹 Complete Appointment
    public Appointment completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Ensure the appointment is accepted before completing
        if (appointment.getStatus() != AppointmentStatus.ACCEPTED) {
            throw new RuntimeException("Only accepted appointments can be marked as completed.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        // 🔔 Notify the patient and doctor
        String message = "Your appointment with " + appointment.getDoctor().getUsername() + " has been marked as completed.";
        notificationService.sendNotification(appointment.getPatient().getId(), message);

        String doctorMessage = "Your appointment with " + appointment.getPatient().getUsername() + " has been completed.";
        notificationService.sendNotification(appointment.getDoctor().getId(), doctorMessage);

        return appointment;
    }

    // 🔹 Get Appointments for a Doctor
    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return appointmentRepository.findByDoctor(doctor);
    }

    // 🔹 Get Appointments for a Patient
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return appointmentRepository.findByPatient(patient);
    }
    public List<Appointment> getAppointmentsByDoctorAndStatus(Long doctorId, AppointmentStatus status) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return appointmentRepository.findByDoctorAndStatus(doctor, status);
    }

    public List<Appointment> getAppointmentsByPatientAndStatus(Long patientId, AppointmentStatus status) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return appointmentRepository.findByPatientAndStatus(patient, status);
    }

}
