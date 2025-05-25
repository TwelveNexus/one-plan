package com.twelvenexus.oneplan.subscription.dto;

import com.twelvenexus.oneplan.subscription.enums.BillingCycle;
import com.twelvenexus.oneplan.subscription.enums.SubscriptionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class SubscriptionDto {
  private UUID id;
  private UUID tenantId;
  private String planName;
  private String planCode;
  private BillingCycle billingCycle;
  private SubscriptionStatus status;
  private BigDecimal amount;
  private Integer quantity;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private LocalDateTime currentPeriodStart;
  private LocalDateTime currentPeriodEnd;
  private LocalDateTime trialEnd;
  private boolean autoRenew;
}
