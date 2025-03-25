package com.example.afyabora.controller;

import com.example.afyabora.model.Transaction;
import com.example.afyabora.model.TransactionStatus;
import com.example.afyabora.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final TransactionRepository transactionRepository;
    private static final Logger logger = Logger.getLogger(SubscriptionController.class.getName());

    public SubscriptionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkSubscription(@RequestParam String email) {
        boolean isActive = transactionRepository.existsByEmailAndStatusAndExpiryDateAfter(
                email, TransactionStatus.PAID, LocalDateTime.now()
        );

        // Log the result
        logger.info("Subscription check for email: " + email + " -> Active: " + isActive);

        // Construct JSON response
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("isActive", isActive);
        response.put("message", isActive ? "User has an active subscription." : "User does not have an active subscription.");

        return ResponseEntity.ok(response);
    }
    // ✅ Fetch All Users with Active Subscriptions
    @GetMapping("/all")
    public ResponseEntity<?> getAllActiveSubscriptions() {
        List<Transaction> activeSubscriptions = transactionRepository.findByStatusAndExpiryDateAfter(
                TransactionStatus.PAID, LocalDateTime.now()
        );

        if (activeSubscriptions.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No active subscriptions found."));
        }

        return ResponseEntity.ok(activeSubscriptions);
    }
}
