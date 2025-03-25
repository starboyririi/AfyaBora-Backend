package com.example.afyabora.controller;

import com.example.afyabora.dto.FeedbackRequestDto;
import com.example.afyabora.dto.FeedbackResponseDto;
import com.example.afyabora.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/submit")
    public ResponseEntity<FeedbackResponseDto> submitFeedback(@Valid @RequestBody FeedbackRequestDto request) {
        return ResponseEntity.ok(feedbackService.submitFeedback(request));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<Page<FeedbackResponseDto>> getDoctorFeedback(@PathVariable Long doctorId, Pageable pageable) {
        return ResponseEntity.ok(feedbackService.getDoctorFeedback(doctorId, pageable));
    }
}
