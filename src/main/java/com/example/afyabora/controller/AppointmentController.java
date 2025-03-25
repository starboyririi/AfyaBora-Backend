package com.example.afyabora.controller;

import com.example.afyabora.model.AppointmentStatus;
import com.example.afyabora.model.Appointment;
import com.example.afyabora.model.AppointmentRequest;
import com.example.afyabora.model.User;
import com.example.afyabora.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // Get All Doctors
    @GetMapping("/doctors")
    public ResponseEntity<List<User>> getAllDoctors() {
        return ResponseEntity.ok(appointmentService.getAllDoctors());
    }

    // Get All Patients
    @GetMapping("/patients")
    public ResponseEntity<List<User>> getAllPatients() {
        return ResponseEntity.ok(appointmentService.getAllPatients());
    }

    // Get Appointments for a Doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(appointments);
    }

    //  Get Appointments for a Patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatient(@PathVariable Long patientId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    @PostMapping("/book")
    public ResponseEntity<Appointment> bookAppointment(@RequestBody AppointmentRequest request) {
        Appointment appointment = appointmentService.bookAppointment(
                request.getPatientId(),
                request.getDoctorId(),
                request.getAppointmentTime(),
                request.isVirtual()
        );
        return ResponseEntity.ok(appointment);
    }


    // Accept an Appointment
    @PutMapping("/{appointmentId}/accept")
    public ResponseEntity<Appointment> acceptAppointment(@PathVariable Long appointmentId) {
        Appointment acceptedAppointment = appointmentService.acceptAppointment(appointmentId);
        return ResponseEntity.ok(acceptedAppointment);
    }

    // Reject an Appointment (Doctor can reject)
    @PutMapping("/{appointmentId}/reject")
    public ResponseEntity<Appointment> rejectAppointment(@PathVariable Long appointmentId) {
        Appointment rejectedAppointment = appointmentService.rejectAppointment(appointmentId);
        return ResponseEntity.ok(rejectedAppointment);
    }

    // Cancel an Appointment (Patient can cancel)
    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(@PathVariable Long appointmentId) {
        Appointment cancelledAppointment = appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.ok(cancelledAppointment);
    }

    // Mark an Appointment as Completed
    @PutMapping("/{appointmentId}/complete")
    public ResponseEntity<Appointment> completeAppointment(@PathVariable Long appointmentId) {
        Appointment completedAppointment = appointmentService.completeAppointment(appointmentId);
        return ResponseEntity.ok(completedAppointment);
    }
    // Get Appointments for a Doctor by Status
    @GetMapping("/doctor/{doctorId}/status/{status}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctorAndStatus(
            @PathVariable Long doctorId,
            @PathVariable AppointmentStatus status) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorAndStatus(doctorId, status);
        return ResponseEntity.ok(appointments);
    }

    // Get Appointments for a Patient by Status
    @GetMapping("/patient/{patientId}/status/{status}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatientAndStatus(
            @PathVariable Long patientId,
            @PathVariable AppointmentStatus status) {
        List<Appointment> appointments = appointmentService.getAppointmentsByPatientAndStatus(patientId, status);
        return ResponseEntity.ok(appointments);
    }

}
