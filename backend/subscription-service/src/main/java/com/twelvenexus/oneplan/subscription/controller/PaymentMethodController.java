package com.twelvenexus.oneplan.subscription.controller;

import com.twelvenexus.oneplan.subscription.dto.PaymentMethodDto;
import com.twelvenexus.oneplan.subscription.dto.CreatePaymentMethodDto;
import com.twelvenexus.oneplan.subscription.model.PaymentMethod;
import com.twelvenexus.oneplan.subscription.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payment-methods")
@RequiredArgsConstructor
@Tag(name = "Payment Methods", description = "Payment method management")
public class PaymentMethodController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Save a payment method")
    public ResponseEntity<PaymentMethodDto> savePaymentMethod(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @Valid @RequestBody CreatePaymentMethodDto dto) {
        PaymentMethod paymentMethod = paymentService.savePaymentMethod(
            tenantId,
            dto.getGateway(),
            dto.getType(),
            dto.getDetails()
        );

        return new ResponseEntity<>(toDto(paymentMethod), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get tenant payment methods")
    public ResponseEntity<List<PaymentMethodDto>> getTenantPaymentMethods(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        List<PaymentMethod> methods = paymentService.getTenantPaymentMethods(tenantId);
        return ResponseEntity.ok(
            methods.stream().map(this::toDto).collect(Collectors.toList())
        );
    }

    @PutMapping("/{paymentMethodId}/default")
    @Operation(summary = "Set default payment method")
    public ResponseEntity<Void> setDefaultPaymentMethod(
            @PathVariable UUID paymentMethodId) {
        paymentService.setDefaultPaymentMethod(paymentMethodId);
        return ResponseEntity.ok().build();
    }

    private PaymentMethodDto toDto(PaymentMethod method) {
        PaymentMethodDto dto = new PaymentMethodDto();
        dto.setId(method.getId());
        dto.setGateway(method.getGateway());
        dto.setType(method.getType());
        dto.setCardLast4(method.getCardLast4());
        dto.setCardBrand(method.getCardBrand());
        dto.setUpiId(method.getUpiId());
        dto.setBankName(method.getBankName());
        dto.setDefault(method.isDefault());
        dto.setActive(method.isActive());
        return dto;
    }
}
