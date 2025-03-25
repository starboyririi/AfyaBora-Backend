package com.example.afyabora.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reference;
    private String email;
    private int amount;
    private String currency;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status; // Enum: PENDING, PAID, FAILED

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Default to now
    private LocalDateTime expiryDate; // New field for subscriptions
    private String subscriptionType; // "monthly" or "yearly"

    public void updateExpiryDate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now(); // Ensure it's not null
        }
        if ("yearly".equalsIgnoreCase(this.subscriptionType)) {
            this.expiryDate = this.createdAt.plusYears(1);
        } else {
            this.expiryDate = this.createdAt.plusMonths(1);
        }
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
        updateExpiryDate(); // Ensure expiry date updates
    }
}
