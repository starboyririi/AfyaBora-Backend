package com.example.afyabora.dto;

import java.time.LocalDateTime;

public record FeedbackResponseDto(
        Long id,
        String doctorName,
        String patientName,
        String comment,
        Integer rating,
        LocalDateTime createdAt
) {}
