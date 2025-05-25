package com.twelvenexus.oneplan.subscription.model;

import com.twelvenexus.oneplan.subscription.enums.BillingCycle;
import com.twelvenexus.oneplan.subscription.enums.SubscriptionStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "subscriptions")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Subscription {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @EqualsAndHashCode.Include
  private UUID id;

  @Column(nullable = false)
  private UUID tenantId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plan_id", nullable = false)
  private Plan plan;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BillingCycle billingCycle;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false)
  private Integer quantity = 1;

  @Column(nullable = false)
  private LocalDateTime startDate;

  private LocalDateTime endDate;

  @Column(nullable = false)
  private LocalDateTime currentPeriodStart;

  @Column(nullable = false)
  private LocalDateTime currentPeriodEnd;

  private LocalDateTime trialStart;

  private LocalDateTime trialEnd;

  private LocalDateTime cancelledAt;

  private String cancellationReason;

  @ElementCollection
  @CollectionTable(name = "subscription_metadata")
  @MapKeyColumn(name = "key")
  @Column(name = "value")
  private Map<String, String> metadata;

  @Column(nullable = false)
  private boolean autoRenew = true;

  private String couponCode;

  @Column(precision = 5, scale = 2)
  private BigDecimal discountPercentage;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  @PreUpdate
  private void preUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
