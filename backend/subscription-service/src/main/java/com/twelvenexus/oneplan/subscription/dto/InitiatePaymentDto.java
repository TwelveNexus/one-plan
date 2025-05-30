package com.twelvenexus.oneplan.subscription.dto;

import com.twelvenexus.oneplan.subscription.enums.PaymentGateway;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
public class InitiatePaymentDto {
  private UUID subscriptionId;
  private BigDecimal amount;
  private PaymentGateway gateway;
  private Map<String, String> metadata;
}
