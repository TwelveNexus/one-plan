package com.twelvenexus.oneplan.subscription.service;

import com.twelvenexus.oneplan.subscription.model.Subscription;
import com.twelvenexus.oneplan.subscription.model.Plan;
import com.twelvenexus.oneplan.subscription.enums.BillingCycle;
import com.twelvenexus.oneplan.subscription.enums.SubscriptionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SubscriptionService {

    Subscription createSubscription(UUID tenantId, UUID planId, BillingCycle billingCycle,
                                   Integer quantity, String couponCode);

    Subscription startTrial(UUID tenantId, UUID planId);

    Subscription updateSubscription(UUID subscriptionId, UUID newPlanId,
                                   BillingCycle billingCycle, Integer quantity);

    Subscription getSubscription(UUID subscriptionId);

    Subscription getActiveSubscription(UUID tenantId);

    List<Subscription> getTenantSubscriptions(UUID tenantId);

    void pauseSubscription(UUID subscriptionId, String reason);

    void resumeSubscription(UUID subscriptionId);

    void cancelSubscription(UUID subscriptionId, String reason, boolean cancelImmediately);

    void renewSubscription(UUID subscriptionId);

    void handleTrialExpiry(UUID subscriptionId);

    void updateSubscriptionStatus(UUID subscriptionId, SubscriptionStatus status);

    void processSubscriptionRenewals();

    void processExpiredTrials();

    void processExpiredSubscriptions();

    boolean hasFeature(UUID tenantId, String featureName);

    Integer getFeatureLimit(UUID tenantId, String limitName);
}
