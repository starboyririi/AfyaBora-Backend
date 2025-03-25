package com.example.afyabora.service;

import com.example.afyabora.config.PaystackConfig;
import com.example.afyabora.dto.PaystackPaymentRequestDto;
import com.example.afyabora.model.Transaction;
import com.example.afyabora.model.TransactionStatus;
import com.example.afyabora.repository.TransactionRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class PaystackPaymentService {

    @Autowired
    private PaystackConfig paystackConfig;

    @Autowired
    private TransactionRepository transactionRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = Logger.getLogger(PaystackPaymentService.class.getName());

    public String initiatePayment(PaystackPaymentRequestDto request) {
        String url = paystackConfig.getChargeUrl();

        // Set fixed amounts based on subscription type
        int amountInKobo = "yearly".equalsIgnoreCase(request.getSubscriptionType()) ? 5 * 100 : 1 * 100;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", request.getEmail());
        requestBody.put("amount", amountInKobo);
        requestBody.put("currency", request.getCurrency());
        requestBody.put("callback_url", "https://api.paystack.co/callback");

        Map<String, Object> mobileMoney = new HashMap<>();
        mobileMoney.put("phone", request.getMobile_number());
        mobileMoney.put("provider", "mpesa");

        requestBody.put("mobile_money", mobileMoney);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(paystackConfig.getSecretKey());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            String reference = (String) data.get("reference");

            Transaction transaction = new Transaction();
            transaction.setReference(reference);
            transaction.setEmail(request.getEmail());
            transaction.setAmount(amountInKobo / 100);
            transaction.setCurrency(request.getCurrency());
            transaction.setStatus(TransactionStatus.PENDING);
            transaction.setSubscriptionType(request.getSubscriptionType());
            transactionRepository.save(transaction);

            return "Transaction initiated. Reference: " + reference;
        }

        return "Failed to initiate transaction.";
    }

    public String verifyTransaction(String reference) {
        String url = "https://api.paystack.co/transaction/verify/" + reference;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(paystackConfig.getSecretKey());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");

            if (data == null || !data.containsKey("status")) {
                logger.warning("Invalid Paystack verification response: " + response.getBody());
                return "Transaction verification failed: Invalid response.";
            }

            String status = (String) data.get("status");
            if ("success".equalsIgnoreCase(status)) {
                Optional<Transaction> transactionOptional = transactionRepository.findByReference(reference);
                if (transactionOptional.isPresent()) {
                    Transaction transaction = transactionOptional.get();
                    transaction.setStatus(TransactionStatus.PAID);
                    transaction.updateExpiryDate();
                    transactionRepository.save(transaction);
                    logger.info("Transaction verified: Reference = " + reference);
                    return "Transaction verified and updated to Paid.";
                } else {
                    logger.warning("Transaction not found: Reference = " + reference);
                }
            } else {
                logger.warning("Transaction verification failed. Status: " + status);
            }
        }

        return "Transaction verification failed.";
    }
}
