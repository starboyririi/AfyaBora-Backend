package com.example.afyabora.service;

import com.example.afyabora.websocket.NotificationWebSocketHandler;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationWebSocketHandler notificationWebSocketHandler;

    public NotificationService(NotificationWebSocketHandler notificationWebSocketHandler) {
        this.notificationWebSocketHandler = notificationWebSocketHandler;
    }

    // 🔹 General method to send notifications
    public void sendNotification(Long userId, String message) {
        notificationWebSocketHandler.sendNotification(userId, message);
    }

    // 🔹 Send feedback notification to a doctor
    public void sendFeedbackNotification(Long doctorId, String s) {
        String message = "You have received new feedback from a patient!";
        sendNotification(doctorId, message);
    }

    // 🔹 Send booking notification to a doctor or patient
    public void sendBookingNotification(Long userId, boolean isDoctor) {
        String message = isDoctor ?
                "You have a new appointment booking request!" :
                "Your appointment has been successfully booked!";
        sendNotification(userId, message);
    }


}
