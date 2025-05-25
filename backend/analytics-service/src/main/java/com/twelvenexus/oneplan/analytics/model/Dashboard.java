package com.twelvenexus.oneplan.analytics.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "dashboards")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Dashboard {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @EqualsAndHashCode.Include
  private UUID id;

  @Column(nullable = false)
  private UUID tenantId;

  @Column(nullable = false)
  private String name;

  @Column(length = 500)
  private String description;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "dashboard")
  @OrderBy("position ASC")
  private List<DashboardWidget> widgets;

  @Column(nullable = false)
  private UUID ownerId;

  @Column(nullable = false)
  private boolean isPublic = false;

  @Column(nullable = false)
  private boolean isDefault = false;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  @PreUpdate
  private void preUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
