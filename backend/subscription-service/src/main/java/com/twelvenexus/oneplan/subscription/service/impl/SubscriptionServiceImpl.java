package com.twelvenexus.oneplan.subscription.service.impl;

import com.twelvenexus.oneplan.subscription.enums.BillingCycle;
import com.twelvenexus.oneplan.subscription.enums.SubscriptionStatus;
import com.twelvenexus.oneplan.subscription.model.Plan;
import com.twelvenexus.oneplan.subscription.model.Subscription;
import com.twelvenexus.oneplan.subscription.repository.SubscriptionRepository;
import com.twelvenexus.oneplan.subscription.service.PlanService;
import com.twelvenexus.oneplan.subscription.service.SubscriptionService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

  private final SubscriptionRepository subscriptionRepository;
  private final PlanService planService;

  @Override
  public Subscription createSubscription(
      UUID tenantId, UUID planId, BillingCycle billingCycle, Integer quantity, String couponCode) {
    // Check if tenant already has an active subscription
    subscriptionRepository
        .findByTenantId(tenantId)
        .filter(
            s ->
                s.getStatus() == SubscriptionStatus.ACTIVE
                    || s.getStatus() == SubscriptionStatus.TRIALING)
        .ifPresent(
            s -> {
              throw new IllegalStateException("Tenant already has an active subscription");
            });

    Plan plan = planService.getPlan(planId);

    Subscription subscription = new Subscription();
    subscription.setTenantId(tenantId);
    subscription.setPlan(plan);
    subscription.setBillingCycle(billingCycle);
    subscription.setQuantity(quantity);
    subscription.setAmount(planService.calculatePrice(plan, billingCycle, quantity));
    subscription.setStatus(SubscriptionStatus.INCOMPLETE);
    subscription.setStartDate(LocalDateTime.now());
    subscription.setCurrentPeriodStart(LocalDateTime.now());
    subscription.setCurrentPeriodEnd(calculatePeriodEnd(LocalDateTime.now(), billingCycle));

    if (couponCode != null) {
      // Apply coupon logic here
      subscription.setCouponCode(couponCode);
    }

    log.info("Creating subscription for tenant: {} with plan: {}", tenantId, plan.getName());
    return subscriptionRepository.save(subscription);
  }

  @Override
  public Subscription startTrial(UUID tenantId, UUID planId) {
    Plan plan = planService.getPlan(planId);

    Subscription subscription = new Subscription();
    subscription.setTenantId(tenantId);
    subscription.setPlan(plan);
    subscription.setBillingCycle(BillingCycle.MONTHLY); // Default for trial
    subscription.setQuantity(1);
    subscription.setAmount(planService.calculatePrice(plan, BillingCycle.MONTHLY, 1));
    subscription.setStatus(SubscriptionStatus.TRIALING);
    subscription.setStartDate(LocalDateTime.now());
    subscription.setTrialStart(LocalDateTime.now());
    subscription.setTrialEnd(LocalDateTime.now().plusDays(plan.getTrialDays()));
    subscription.setCurrentPeriodStart(LocalDateTime.now());
    subscription.setCurrentPeriodEnd(LocalDateTime.now().plusDays(plan.getTrialDays()));

    log.info("Starting {} day trial for tenant: {}", plan.getTrialDays(), tenantId);
    return subscriptionRepository.save(subscription);
  }

  @Override
  public Subscription updateSubscription(
      UUID subscriptionId, UUID newPlanId, BillingCycle billingCycle, Integer quantity) {
    Subscription subscription = getSubscription(subscriptionId);
    Plan newPlan = planService.getPlan(newPlanId);

    subscription.setPlan(newPlan);
    subscription.setBillingCycle(billingCycle);
    subscription.setQuantity(quantity);
    subscription.setAmount(planService.calculatePrice(newPlan, billingCycle, quantity));

    log.info("Updated subscription {} to plan: {}", subscriptionId, newPlan.getName());
    return subscriptionRepository.save(subscription);
  }

  @Override
  @Transactional(readOnly = true)
  public Subscription getSubscription(UUID subscriptionId) {
    return subscriptionRepository
        .findById(subscriptionId)
        .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Subscription getActiveSubscription(UUID tenantId) {
    return subscriptionRepository
        .findByTenantId(tenantId)
        .filter(
            s ->
                s.getStatus() == SubscriptionStatus.ACTIVE
                    || s.getStatus() == SubscriptionStatus.TRIALING)
        .orElse(null);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Subscription> getTenantSubscriptions(UUID tenantId) {
    return subscriptionRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
  }

  @Override
  public void pauseSubscription(UUID subscriptionId, String reason) {
    Subscription subscription = getSubscription(subscriptionId);
    subscription.setStatus(SubscriptionStatus.PAUSED);
    subscription.getMetadata().put("pause_reason", reason);
    subscription.getMetadata().put("paused_at", LocalDateTime.now().toString());
    subscriptionRepository.save(subscription);
    log.info("Paused subscription: {}", subscriptionId);
  }

  @Override
  public void resumeSubscription(UUID subscriptionId) {
    Subscription subscription = getSubscription(subscriptionId);
    subscription.setStatus(SubscriptionStatus.ACTIVE);
    subscription.getMetadata().remove("pause_reason");
    subscription.getMetadata().remove("paused_at");
    subscriptionRepository.save(subscription);
    log.info("Resumed subscription: {}", subscriptionId);
  }

  @Override
  public void cancelSubscription(UUID subscriptionId, String reason, boolean cancelImmediately) {
    Subscription subscription = getSubscription(subscriptionId);
    subscription.setCancelledAt(LocalDateTime.now());
    subscription.setCancellationReason(reason);

    if (cancelImmediately) {
      subscription.setStatus(SubscriptionStatus.CANCELED);
      subscription.setEndDate(LocalDateTime.now());
    } else {
      subscription.setStatus(SubscriptionStatus.ACTIVE);
      subscription.setAutoRenew(false);
      subscription.setEndDate(subscription.getCurrentPeriodEnd());
    }

    subscriptionRepository.save(subscription);
    log.info("Canceled subscription: {} (immediate: {})", subscriptionId, cancelImmediately);
  }

  @Override
  public void renewSubscription(UUID subscriptionId) {
    Subscription subscription = getSubscription(subscriptionId);

    LocalDateTime newPeriodStart = subscription.getCurrentPeriodEnd();
    LocalDateTime newPeriodEnd = calculatePeriodEnd(newPeriodStart, subscription.getBillingCycle());

    subscription.setCurrentPeriodStart(newPeriodStart);
    subscription.setCurrentPeriodEnd(newPeriodEnd);
    subscription.setStatus(SubscriptionStatus.ACTIVE);

    subscriptionRepository.save(subscription);
    log.info("Renewed subscription: {} until {}", subscriptionId, newPeriodEnd);
  }

  @Override
  public void handleTrialExpiry(UUID subscriptionId) {
    Subscription subscription = getSubscription(subscriptionId);

    if (subscription.getStatus() == SubscriptionStatus.TRIALING) {
      subscription.setStatus(SubscriptionStatus.INCOMPLETE);
      subscription.setTrialEnd(LocalDateTime.now());
      subscriptionRepository.save(subscription);
      log.info("Trial expired for subscription: {}", subscriptionId);
    }
  }

  @Override
  public void updateSubscriptionStatus(UUID subscriptionId, SubscriptionStatus status) {
    Subscription subscription = getSubscription(subscriptionId);
    subscription.setStatus(status);
    subscriptionRepository.save(subscription);
  }

  @Override
  public void processSubscriptionRenewals() {
    LocalDateTime now = LocalDateTime.now();
    List<Subscription> subscriptionsToRenew =
        subscriptionRepository.findSubscriptionsForRenewal(now);

    for (Subscription subscription : subscriptionsToRenew) {
      try {
        renewSubscription(subscription.getId());
        // Trigger payment process
      } catch (Exception e) {
        log.error("Failed to renew subscription: {}", subscription.getId(), e);
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);
      }
    }
  }

  @Override
  public void processExpiredTrials() {
    LocalDateTime now = LocalDateTime.now();
    List<Subscription> expiredTrials = subscriptionRepository.findExpiredTrials(now);

    for (Subscription subscription : expiredTrials) {
      handleTrialExpiry(subscription.getId());
    }
  }

  @Override
  public void processExpiredSubscriptions() {
    LocalDateTime now = LocalDateTime.now();
    List<Subscription> expiredSubscriptions =
        subscriptionRepository.findExpiredSubscriptions(SubscriptionStatus.ACTIVE, now);

    for (Subscription subscription : expiredSubscriptions) {
      if (!subscription.isAutoRenew()) {
        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription.setEndDate(now);
        subscriptionRepository.save(subscription);
        log.info("Subscription {} has expired and been canceled", subscription.getId());
      }
    }
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasFeature(UUID tenantId, String featureName) {
    Subscription subscription = getActiveSubscription(tenantId);
    if (subscription == null) {
      return false;
    }

    String featureValue = subscription.getPlan().getFeatures().get(featureName);
    return "true".equalsIgnoreCase(featureValue) || "enabled".equalsIgnoreCase(featureValue);
  }

  @Override
  @Transactional(readOnly = true)
  public Integer getFeatureLimit(UUID tenantId, String limitName) {
    Subscription subscription = getActiveSubscription(tenantId);
    if (subscription == null) {
      return 0;
    }

    return subscription.getPlan().getLimits().getOrDefault(limitName, 0)
        * subscription.getQuantity();
  }

  private LocalDateTime calculatePeriodEnd(LocalDateTime start, BillingCycle cycle) {
    return switch (cycle) {
      case MONTHLY -> start.plusMonths(1);
      case QUARTERLY -> start.plusMonths(3);
      case HALF_YEARLY -> start.plusMonths(6);
      case YEARLY -> start.plusYears(1);
    };
  }
}
