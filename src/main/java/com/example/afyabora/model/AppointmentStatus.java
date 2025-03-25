package com.example.afyabora.model;

public enum AppointmentStatus {
    PENDING,    // When an appointment is created
    ACCEPTED,   // When the doctor accepts the appointment
    REJECTED,   // When the doctor rejects the appointment
    COMPLETED,  // After the appointment is done
    CANCELED    // If the patient cancels
}
