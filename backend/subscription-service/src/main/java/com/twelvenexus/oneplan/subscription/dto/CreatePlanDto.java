package com.twelvenexus.oneplan.subscription.dto;

import java.math.BigDecimal;
import java.util.Map;

import com.twelvenexus.oneplan.subscription.enums.BillingCycle;
import com.twelvenexus.oneplan.subscription.enums.PlanType;

import lombok.Data;

@Data
public class CreatePlanDto {
    private String code;
    private String name;
    private String description;
    private PlanType type;
    private BigDecimal basePrice;
    private Map<BillingCycle, BigDecimal> billingCyclePrices;
    private Map<String, String> features;
    private Map<String, Integer> limits;
}
