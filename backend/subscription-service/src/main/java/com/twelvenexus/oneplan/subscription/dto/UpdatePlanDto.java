package com.twelvenexus.oneplan.subscription.dto;

import com.twelvenexus.oneplan.subscription.enums.BillingCycle;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class UpdatePlanDto {
  private String name;
  private String description;
  private Map<BillingCycle, BigDecimal> billingCyclePrices;
  private Map<String, String> features;
  private Map<String, Integer> limits;
}
