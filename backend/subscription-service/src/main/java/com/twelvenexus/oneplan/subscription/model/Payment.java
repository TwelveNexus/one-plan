package com.twelvenexus.oneplan.subscription.model;

import com.twelvenexus.oneplan.subscription.enums.PaymentGateway;
import com.twelvenexus.oneplan.subscription.enums.PaymentStatus;
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
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Payment {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @EqualsAndHashCode.Include
  private UUID id;

  @Column(nullable = false)
  private UUID tenantId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subscription_id")
  private Subscription subscription;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus status = PaymentStatus.PENDING;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentGateway gateway;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false)
  private String currency = "INR";

  private String gatewayOrderId;

  private String gatewayPaymentId;

  private String gatewaySignature;

  @Column(length = 500)
  private String description;

  @ElementCollection
  @CollectionTable(name = "payment_metadata")
  @MapKeyColumn(name = "key")
  @Column(name = "value")
  private Map<String, String> metadata;

  private String failureReason;

  private Integer attemptCount = 1;

  private LocalDateTime paidAt;

  private BigDecimal refundedAmount;

  private LocalDateTime refundedAt;

  private String refundReason;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  @PreUpdate
  private void preUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
