package com.twelvenexus.oneplan.subscription.service;

import com.twelvenexus.oneplan.subscription.enums.BillingCycle;
import com.twelvenexus.oneplan.subscription.enums.PlanType;
import com.twelvenexus.oneplan.subscription.model.Plan;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PlanService {

  Plan createPlan(
      String code,
      String name,
      String description,
      PlanType type,
      BigDecimal basePrice,
      Map<BillingCycle, BigDecimal> billingCyclePrices,
      Map<String, String> features,
      Map<String, Integer> limits);

  Plan updatePlan(
      UUID planId,
      String name,
      String description,
      Map<BillingCycle, BigDecimal> prices,
      Map<String, String> features,
      Map<String, Integer> limits);

  Plan getPlan(UUID planId);

  Plan getPlanByCode(String code);

  List<Plan> getAllActivePlans();

  List<Plan> getPopularPlans();

  List<Plan> getPlansByType(PlanType type);

  void activatePlan(UUID planId);

  void deactivatePlan(UUID planId);

  void markAsPopular(UUID planId, boolean popular);

  BigDecimal calculatePrice(Plan plan, BillingCycle billingCycle, Integer quantity);
}
