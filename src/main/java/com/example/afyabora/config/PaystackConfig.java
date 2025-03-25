package com.example.afyabora.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaystackConfig {

    @Value("${paystack.secret.key}")
    private String secretKey;

    @Value("${paystack.public.key}")
    private String publicKey;

    @Value("${paystack.charge.url}")
    private String chargeUrl;

    @Value("${paystack.verify.url}")
    private String verifyUrl;

    public String getSecretKey() {
        return secretKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getChargeUrl() {
        return chargeUrl;
    }

    public String getVerifyUrl() {
        return verifyUrl;
    }
}
