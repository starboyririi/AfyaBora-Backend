package com.example.afyabora.dto;

public class PaystackPaymentRequestDto {
    private String email;

    private String currency;
    private String mobile_number;
    private String subscriptionType; // "monthly" or "yearly"

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getMobile_number() { return mobile_number; }
    public void setMobile_number(String mobile_number) { this.mobile_number = mobile_number; }

    public String getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(String subscriptionType) { this.subscriptionType = subscriptionType; }
}
