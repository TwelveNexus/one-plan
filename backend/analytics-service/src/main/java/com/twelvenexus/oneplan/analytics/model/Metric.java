package com.twelvenexus.oneplan.analytics.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.twelvenexus.oneplan.analytics.enums.MetricType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "metrics",
       indexes = @Index(name = "idx_metric_lookup",
                        columnList = "tenantId, entityId, entityType, type, timestamp"))
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Metric {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID entityId; // Could be projectId, userId, teamId, etc.

    @Column(nullable = false)
    private String entityType; // project, user, team, system

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetricType type;

    @Column(nullable = false)
    private Double value;

    @ElementCollection
    @CollectionTable(name = "metric_dimensions")
    @MapKeyColumn(name = "dimension")
    @Column(name = "value")
    private Map<String, String> dimensions;

    @ElementCollection
    @CollectionTable(name = "metric_tags")
    private Set<String> tags;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
