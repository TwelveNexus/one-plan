package com.twelvenexus.oneplan.subscription.controller;

import com.twelvenexus.oneplan.subscription.service.gateway.RazorpayService;
import com.twelvenexus.oneplan.subscription.service.gateway.PhonePeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Payment gateway webhooks")
public class WebhookController {

    private final RazorpayService razorpayService;
    private final PhonePeService phonePeService;

    @PostMapping("/razorpay")
    @Operation(summary = "Handle Razorpay webhook")
    public ResponseEntity<String> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        log.info("Received Razorpay webhook");

        try {
            razorpayService.handleWebhook(payload, signature);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Failed to process Razorpay webhook", e);
            return ResponseEntity.badRequest().body("Failed");
        }
    }

    @PostMapping("/phonepe")
    @Operation(summary = "Handle PhonePe webhook")
    public ResponseEntity<String> handlePhonePeWebhook(
            @RequestBody String payload,
            @RequestHeader("X-VERIFY") String signature) {
        log.info("Received PhonePe webhook");

        try {
            phonePeService.handleWebhook(payload, signature);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Failed to process PhonePe webhook", e);
            return ResponseEntity.badRequest().body("Failed");
        }
    }
}
