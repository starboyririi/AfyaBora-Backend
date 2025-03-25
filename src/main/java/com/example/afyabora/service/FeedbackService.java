package com.example.afyabora.service;

import com.example.afyabora.dto.FeedbackRequestDto;
import com.example.afyabora.dto.FeedbackResponseDto;
import com.example.afyabora.model.Feedback;
import com.example.afyabora.model.User;
import com.example.afyabora.repository.FeedbackRepository;
import com.example.afyabora.repository.UserRepository;
import com.example.afyabora.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public FeedbackResponseDto submitFeedback(FeedbackRequestDto request) {
        User doctor = userRepository.findById(request.doctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        User patient = userRepository.findById(request.patientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Feedback feedback = Feedback.builder()
                .doctor(doctor)
                .patient(patient)
                .comment(request.comment())
                .rating(request.rating())
                .createdAt(LocalDateTime.now())
                .build();

        Feedback savedFeedback = feedbackRepository.save(feedback);

        // Notify doctor in real-time
        notificationService.sendFeedbackNotification(doctor.getId(), "New feedback received!");

        return mapToResponse(savedFeedback);
    }

    public Page<FeedbackResponseDto> getDoctorFeedback(Long doctorId, Pageable pageable) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return feedbackRepository.findByDoctor(doctor, pageable).map(this::mapToResponse);
    }

    private FeedbackResponseDto mapToResponse(Feedback feedback) {
        return new FeedbackResponseDto(
                feedback.getId(),
                feedback.getDoctor().getUsername(),
                feedback.getPatient().getUsername(),
                feedback.getComment(),
                feedback.getRating(),
                feedback.getCreatedAt()
        );
    }
}
