package com.twelvenexus.oneplan.subscription.dto;

import com.twelvenexus.oneplan.subscription.enums.PaymentGateway;
import java.util.UUID;
import lombok.Data;

@Data
public class PaymentMethodDto {
  private UUID id;
  private PaymentGateway gateway;
  private String type;
  private String cardLast4;
  private String cardBrand;
  private String upiId;
  private String bankName;
  private boolean isDefault;
  private boolean active;
}
