package com.twelvenexus.oneplan.subscription.dto;

import com.twelvenexus.oneplan.subscription.enums.BillingCycle;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateSubscriptionDto {
  private UUID planId;
  private BillingCycle billingCycle;
  private Integer quantity;
  private String couponCode;
}
