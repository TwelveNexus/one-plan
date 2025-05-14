package com.twelvenexus.oneplan.subscription.dto;

import com.twelvenexus.oneplan.subscription.enums.PaymentGateway;

import lombok.Data;

@Data
public class CompletePaymentDto {
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String signature;
    private PaymentGateway gateway;
}

