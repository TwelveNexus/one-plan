package com.twelvenexus.oneplan.subscription.dto;

import com.twelvenexus.oneplan.subscription.enums.PaymentGateway;
import java.util.Map;
import lombok.Data;

@Data
public class CreatePaymentMethodDto {
  private PaymentGateway gateway;
  private String type;
  private Map<String, String> details;
}
