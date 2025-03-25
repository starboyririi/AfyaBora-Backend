package com.example.afyabora.controller;


import com.example.afyabora.dto.PaystackPaymentRequestDto;
import com.example.afyabora.service.PaystackPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paystack")
public class PaystackController {

    private final PaystackPaymentService paystackService;

    public PaystackController(PaystackPaymentService paystackService) {
        this.paystackService = paystackService;
    }

    @PostMapping("/pay")
    public ResponseEntity<String> initiatePayment(@RequestBody PaystackPaymentRequestDto request) {
        String response = paystackService.initiatePayment(request);
        return ResponseEntity.ok(response);
    }
    @Autowired
    private PaystackPaymentService paystackPaymentService;

    @GetMapping("/verify/{reference}")
    public ResponseEntity<String> verifyTransaction(@PathVariable String reference) {
        return ResponseEntity.ok(paystackPaymentService.verifyTransaction(reference));
    }
}
