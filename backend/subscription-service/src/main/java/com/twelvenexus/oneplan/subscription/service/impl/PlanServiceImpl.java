package com.twelvenexus.oneplan.subscription.service.impl;

import com.twelvenexus.oneplan.subscription.enums.BillingCycle;
import com.twelvenexus.oneplan.subscription.enums.PlanType;
import com.twelvenexus.oneplan.subscription.model.Plan;
import com.twelvenexus.oneplan.subscription.repository.PlanRepository;
import com.twelvenexus.oneplan.subscription.service.PlanService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlanServiceImpl implements PlanService {

  private final PlanRepository planRepository;

  @Override
  public Plan createPlan(
      String code,
      String name,
      String description,
      PlanType type,
      BigDecimal basePrice,
      Map<BillingCycle, BigDecimal> billingCyclePrices,
      Map<String, String> features,
      Map<String, Integer> limits) {

    if (planRepository.existsByCode(code)) {
      throw new IllegalArgumentException("Plan with code " + code + " already exists");
    }

    Plan plan = new Plan();
    plan.setCode(code);
    plan.setName(name);
    plan.setDescription(description);
    plan.setType(type);
    plan.setBasePrice(basePrice);
    plan.setBillingCyclePrices(billingCyclePrices);
    plan.setFeatures(features);
    plan.setLimits(limits);
    plan.setCurrency("INR");
    plan.setActive(true);

    log.info("Creating plan: {} with code: {}", name, code);
    return planRepository.save(plan);
  }

  @Override
  public Plan updatePlan(
      UUID planId,
      String name,
      String description,
      Map<BillingCycle, BigDecimal> prices,
      Map<String, String> features,
      Map<String, Integer> limits) {
    Plan plan =
        planRepository
            .findById(planId)
            .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

    plan.setName(name);
    plan.setDescription(description);
    if (prices != null) {
      plan.setBillingCyclePrices(prices);
    }
    if (features != null) {
      plan.setFeatures(features);
    }
    if (limits != null) {
      plan.setLimits(limits);
    }

    return planRepository.save(plan);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "plans", key = "#planId")
  public Plan getPlan(UUID planId) {
    return planRepository
        .findById(planId)
        .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "plans", key = "#code")
  public Plan getPlanByCode(String code) {
    return planRepository
        .findByCode(code)
        .orElseThrow(() -> new IllegalArgumentException("Plan not found with code: " + code));
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "activePlans")
  public List<Plan> getAllActivePlans() {
    return planRepository.findByActiveTrueOrderBySortOrder();
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "popularPlans")
  public List<Plan> getPopularPlans() {
    return planRepository.findPopularPlans();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Plan> getPlansByType(PlanType type) {
    return planRepository.findByTypeAndActiveTrue(type);
  }

  @Override
  public void activatePlan(UUID planId) {
    Plan plan = getPlan(planId);
    plan.setActive(true);
    planRepository.save(plan);
    log.info("Activated plan: {}", plan.getName());
  }

  @Override
  public void deactivatePlan(UUID planId) {
    Plan plan = getPlan(planId);
    plan.setActive(false);
    planRepository.save(plan);
    log.info("Deactivated plan: {}", plan.getName());
  }

  @Override
  public void markAsPopular(UUID planId, boolean popular) {
    Plan plan = getPlan(planId);
    plan.setPopular(popular);
    planRepository.save(plan);
  }

  @Override
  public BigDecimal calculatePrice(Plan plan, BillingCycle billingCycle, Integer quantity) {
    BigDecimal price = plan.getBillingCyclePrices().getOrDefault(billingCycle, plan.getBasePrice());
    return price.multiply(BigDecimal.valueOf(quantity));
  }
}
