package com.example.afyabora.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FeedbackRequestDto(
        @NotNull Long doctorId,
        @NotNull Long patientId,
        @NotBlank String comment,
        @Min(1) @Max(5) Integer rating
) {}
