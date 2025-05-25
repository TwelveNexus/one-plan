package com.twelvenexus.oneplan.subscription.dto;

import com.twelvenexus.oneplan.subscription.enums.BillingCycle;
import com.twelvenexus.oneplan.subscription.enums.PlanType;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
public class PlanDto {
  private UUID id;
  private String code;
  private String name;
  private String description;
  private PlanType type;
  private BigDecimal basePrice;
  private String currency;
  private Map<BillingCycle, BigDecimal> billingCyclePrices;
  private Map<String, String> features;
  private Map<String, Integer> limits;
  private Integer trialDays;
  private boolean active;
  private boolean popular;
}
