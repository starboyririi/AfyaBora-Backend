package com.example.afyabora.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "appointments")  // ✅ Explicit table name to avoid conflicts
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "id", nullable = false)
    private User doctor;

    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private String zoomMeetingUrl; // ✅ Fixed variable naming issue

    // ✅ Correct Getter and Setter
    public String getZoomMeetingUrl() {
        return zoomMeetingUrl;
    }

    public void setZoomMeetingUrl(String zoomMeetingUrl) {
        this.zoomMeetingUrl = zoomMeetingUrl;
    }

    // ✅ Default Constructor
    public Appointment() {
    }
}
