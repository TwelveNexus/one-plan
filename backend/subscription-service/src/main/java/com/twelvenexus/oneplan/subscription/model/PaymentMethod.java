package com.twelvenexus.oneplan.subscription.model;

import com.twelvenexus.oneplan.subscription.enums.PaymentGateway;
import jakarta.persistence.*;
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
@Table(name = "payment_methods")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaymentMethod {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @EqualsAndHashCode.Include
  private UUID id;

  @Column(nullable = false)
  private UUID tenantId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentGateway gateway;

  @Column(nullable = false)
  private String type; // card, upi, netbanking, wallet

  private String tokenId; // Razorpay token id

  private String cardLast4;

  private String cardBrand;

  private String upiId;

  private String bankName;

  @ElementCollection
  @CollectionTable(name = "payment_method_metadata")
  @MapKeyColumn(name = "key")
  @Column(name = "value")
  private Map<String, String> metadata;

  @Column(nullable = false)
  private boolean isDefault = false;

  @Column(nullable = false)
  private boolean active = true;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  @PreUpdate
  private void preUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
