package com.twelvenexus.oneplan.subscription.controller;

import com.twelvenexus.oneplan.subscription.dto.CreateSubscriptionDto;
import com.twelvenexus.oneplan.subscription.dto.SubscriptionDto;
import com.twelvenexus.oneplan.subscription.dto.UpdateSubscriptionDto;
import com.twelvenexus.oneplan.subscription.model.Subscription;
import com.twelvenexus.oneplan.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Subscription management")
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  @PostMapping
  @Operation(summary = "Create a subscription")
  public ResponseEntity<SubscriptionDto> createSubscription(
      @RequestHeader("X-Tenant-Id") UUID tenantId, @Valid @RequestBody CreateSubscriptionDto dto) {
    Subscription subscription =
        subscriptionService.createSubscription(
            tenantId,
            dto.getPlanId(),
            dto.getBillingCycle(),
            dto.getQuantity(),
            dto.getCouponCode());

    return new ResponseEntity<>(toDto(subscription), HttpStatus.CREATED);
  }

  @PostMapping("/trial")
  @Operation(summary = "Start a trial subscription")
  public ResponseEntity<SubscriptionDto> startTrial(
      @RequestHeader("X-Tenant-Id") UUID tenantId, @RequestParam UUID planId) {
    Subscription subscription = subscriptionService.startTrial(tenantId, planId);
    return new ResponseEntity<>(toDto(subscription), HttpStatus.CREATED);
  }

  @PutMapping("/{subscriptionId}")
  @Operation(summary = "Update a subscription")
  public ResponseEntity<SubscriptionDto> updateSubscription(
      @PathVariable UUID subscriptionId, @Valid @RequestBody UpdateSubscriptionDto dto) {
    Subscription subscription =
        subscriptionService.updateSubscription(
            subscriptionId, dto.getPlanId(), dto.getBillingCycle(), dto.getQuantity());

    return ResponseEntity.ok(toDto(subscription));
  }

  @GetMapping("/active")
  @Operation(summary = "Get active subscription")
  public ResponseEntity<SubscriptionDto> getActiveSubscription(
      @RequestHeader("X-Tenant-Id") UUID tenantId) {
    Subscription subscription = subscriptionService.getActiveSubscription(tenantId);
    return subscription != null
        ? ResponseEntity.ok(toDto(subscription))
        : ResponseEntity.notFound().build();
  }

  @GetMapping
  @Operation(summary = "Get all tenant subscriptions")
  public ResponseEntity<List<SubscriptionDto>> getTenantSubscriptions(
      @RequestHeader("X-Tenant-Id") UUID tenantId) {
    List<Subscription> subscriptions = subscriptionService.getTenantSubscriptions(tenantId);
    return ResponseEntity.ok(subscriptions.stream().map(this::toDto).collect(Collectors.toList()));
  }

  @PutMapping("/{subscriptionId}/cancel")
  @Operation(summary = "Cancel a subscription")
  public ResponseEntity<Void> cancelSubscription(
      @PathVariable UUID subscriptionId,
      @RequestParam String reason,
      @RequestParam(defaultValue = "false") boolean immediate) {
    subscriptionService.cancelSubscription(subscriptionId, reason, immediate);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/{subscriptionId}/pause")
  @Operation(summary = "Pause a subscription")
  public ResponseEntity<Void> pauseSubscription(
      @PathVariable UUID subscriptionId, @RequestParam String reason) {
    subscriptionService.pauseSubscription(subscriptionId, reason);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/{subscriptionId}/resume")
  @Operation(summary = "Resume a subscription")
  public ResponseEntity<Void> resumeSubscription(@PathVariable UUID subscriptionId) {
    subscriptionService.resumeSubscription(subscriptionId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/features/{featureName}")
  @Operation(summary = "Check if tenant has feature")
  public ResponseEntity<Boolean> hasFeature(
      @RequestHeader("X-Tenant-Id") UUID tenantId, @PathVariable String featureName) {
    boolean hasFeature = subscriptionService.hasFeature(tenantId, featureName);
    return ResponseEntity.ok(hasFeature);
  }

  @GetMapping("/limits/{limitName}")
  @Operation(summary = "Get feature limit")
  public ResponseEntity<Integer> getFeatureLimit(
      @RequestHeader("X-Tenant-Id") UUID tenantId, @PathVariable String limitName) {
    Integer limit = subscriptionService.getFeatureLimit(tenantId, limitName);
    return ResponseEntity.ok(limit);
  }

  private SubscriptionDto toDto(Subscription subscription) {
    SubscriptionDto dto = new SubscriptionDto();
    dto.setId(subscription.getId());
    dto.setTenantId(subscription.getTenantId());
    dto.setPlanName(subscription.getPlan().getName());
    dto.setPlanCode(subscription.getPlan().getCode());
    dto.setBillingCycle(subscription.getBillingCycle());
    dto.setStatus(subscription.getStatus());
    dto.setAmount(subscription.getAmount());
    dto.setQuantity(subscription.getQuantity());
    dto.setStartDate(subscription.getStartDate());
    dto.setEndDate(subscription.getEndDate());
    dto.setCurrentPeriodStart(subscription.getCurrentPeriodStart());
    dto.setCurrentPeriodEnd(subscription.getCurrentPeriodEnd());
    dto.setTrialEnd(subscription.getTrialEnd());
    dto.setAutoRenew(subscription.isAutoRenew());
    return dto;
  }
}
