package com.example.afyabora.controller;

import com.example.afyabora.service.ZoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/zoom")
public class ZoomController {

    private final ZoomService zoomService;

    public ZoomController(ZoomService zoomService) {
        this.zoomService = zoomService;
    }

    // 🔹 API for Frontend to Create a Zoom Meeting
    @PostMapping("/create")
    public ResponseEntity<String> createMeeting(@RequestParam String doctorEmail) {
        try {
            String zoomUrl = zoomService.createZoomMeeting(doctorEmail);
            return ResponseEntity.ok(zoomUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating Zoom meeting: " + e.getMessage());
        }
    }

    // 🔹 API to Handle Zoom Webhooks
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        zoomService.processWebhookEvent(payload);
        return ResponseEntity.ok("Webhook received successfully");
    }
}
