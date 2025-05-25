package com.twelvenexus.oneplan.subscription.model;

import com.twelvenexus.oneplan.subscription.enums.BillingCycle;
import com.twelvenexus.oneplan.subscription.enums.PlanType;
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
@Table(name = "plans")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Plan {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @EqualsAndHashCode.Include
  private UUID id;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(nullable = false)
  private String name;

  @Column(length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PlanType type;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal basePrice;

  @Column(nullable = false)
  private String currency = "INR";

  @ElementCollection
  @CollectionTable(name = "plan_billing_cycles")
  @MapKeyColumn(name = "billing_cycle")
  @Column(name = "price")
  private Map<BillingCycle, BigDecimal> billingCyclePrices;

  @ElementCollection
  @CollectionTable(name = "plan_features")
  @MapKeyColumn(name = "feature_name")
  @Column(name = "feature_value")
  private Map<String, String> features;

  @ElementCollection
  @CollectionTable(name = "plan_limits")
  @MapKeyColumn(name = "limit_name")
  @Column(name = "limit_value")
  private Map<String, Integer> limits;

  @Column(nullable = false)
  private Integer trialDays = 14;

  @Column(nullable = false)
  private boolean active = true;

  @Column(nullable = false)
  private boolean popular = false;

  @Column(nullable = false)
  private Integer sortOrder = 0;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  @PreUpdate
  private void preUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
