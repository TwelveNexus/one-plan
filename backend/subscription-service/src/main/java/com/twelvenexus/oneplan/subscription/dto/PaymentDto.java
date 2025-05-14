package com.twelvenexus.oneplan.subscription.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.twelvenexus.oneplan.subscription.enums.PaymentGateway;
import com.twelvenexus.oneplan.subscription.enums.PaymentStatus;

import lombok.Data;

@Data
public class PaymentDto {
    private UUID id;
    private PaymentStatus status;
    private PaymentGateway gateway;
    private BigDecimal amount;
    private String currency;
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private LocalDateTime paidAt;
    private String failureReason;
}
