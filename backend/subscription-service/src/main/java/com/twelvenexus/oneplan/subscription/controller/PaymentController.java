package com.twelvenexus.oneplan.subscription.controller;

import com.twelvenexus.oneplan.subscription.dto.*;
import com.twelvenexus.oneplan.subscription.model.Payment;
import com.twelvenexus.oneplan.subscription.model.PaymentMethod;
import com.twelvenexus.oneplan.subscription.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing and management")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    @Operation(summary = "Initiate a payment")
    public ResponseEntity<PaymentDto> initiatePayment(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @Valid @RequestBody InitiatePaymentDto dto) {
        Payment payment = paymentService.initiatePayment(
            tenantId,
            dto.getSubscriptionId(),
            dto.getAmount(),
            dto.getGateway(),
            dto.getMetadata()
        );

        return new ResponseEntity<>(toDto(payment), HttpStatus.CREATED);
    }

    @PostMapping("/complete")
    @Operation(summary = "Complete a payment")
    public ResponseEntity<PaymentDto> completePayment(
            @Valid @RequestBody CompletePaymentDto dto) {
        Payment payment = paymentService.completePayment(
            dto.getGatewayOrderId(),
            dto.getGatewayPaymentId(),
            dto.getSignature(),
            dto.getGateway()
        );

        return ResponseEntity.ok(toDto(payment));
    }

    @PostMapping("/link")
    @Operation(summary = "Create payment link")
    public ResponseEntity<Map<String, Object>> createPaymentLink(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @Valid @RequestBody InitiatePaymentDto dto) {
        Map<String, Object> linkDetails = paymentService.createPaymentLink(
            tenantId,
            dto.getSubscriptionId(),
            dto.getAmount(),
            dto.getGateway()
        );

        return ResponseEntity.ok(linkDetails);
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment details")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable UUID paymentId) {
        Payment payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(toDto(payment));
    }

    @GetMapping
    @Operation(summary = "Get tenant payments")
    public ResponseEntity<List<PaymentDto>> getTenantPayments(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        List<Payment> payments = paymentService.getTenantPayments(tenantId);
        return ResponseEntity.ok(
            payments.stream().map(this::toDto).collect(Collectors.toList())
        );
    }

    @GetMapping("/subscription/{subscriptionId}")
    @Operation(summary = "Get subscription payments")
    public ResponseEntity<List<PaymentDto>> getSubscriptionPayments(
            @PathVariable UUID subscriptionId) {
        List<Payment> payments = paymentService.getSubscriptionPayments(subscriptionId);
        return ResponseEntity.ok(
            payments.stream().map(this::toDto).collect(Collectors.toList())
        );
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Refund a payment")
    public ResponseEntity<PaymentDto> refundPayment(
            @PathVariable UUID paymentId,
            @RequestParam String amount,
            @RequestParam String reason) {
        Payment payment = paymentService.refundPayment(
            paymentId,
            new java.math.BigDecimal(amount),
            reason
        );

        return ResponseEntity.ok(toDto(payment));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue statistics")
    public ResponseEntity<Map<String, Object>> getRevenue(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);

        Double revenue = paymentService.getTotalRevenue(tenantId, start, end);

        return ResponseEntity.ok(Map.of(
            "tenantId", tenantId,
            "startDate", startDate,
            "endDate", endDate,
            "totalRevenue", revenue,
            "currency", "INR"
        ));
    }

    private PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setStatus(payment.getStatus());
        dto.setGateway(payment.getGateway());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setGatewayOrderId(payment.getGatewayOrderId());
        dto.setGatewayPaymentId(payment.getGatewayPaymentId());
        dto.setPaidAt(payment.getPaidAt());
        dto.setFailureReason(payment.getFailureReason());
        return dto;
    }
}
