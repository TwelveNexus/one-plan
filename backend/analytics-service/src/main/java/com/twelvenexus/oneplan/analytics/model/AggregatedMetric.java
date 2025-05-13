package com.twelvenexus.oneplan.analytics.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.twelvenexus.oneplan.analytics.enums.AggregationPeriod;
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
@Table(name = "aggregated_metrics",
       indexes = @Index(name = "idx_aggregated_lookup",
                        columnList = "tenantId, entityId, entityType, type, period, periodStart"))
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AggregatedMetric {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID entityId;

    @Column(nullable = false)
    private String entityType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetricType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AggregationPeriod period;

    @Column(nullable = false)
    private LocalDateTime periodStart;

    @Column(nullable = false)
    private LocalDateTime periodEnd;

    @Column(nullable = false)
    private Double minValue;

    @Column(nullable = false)
    private Double maxValue;

    @Column(nullable = false)
    private Double avgValue;

    @Column(nullable = false)
    private Double sumValue;

    @Column(nullable = false)
    private Long count;

    @ElementCollection
    @CollectionTable(name = "aggregated_metric_percentiles")
    @MapKeyColumn(name = "percentile")
    @Column(name = "value")
    private Map<Integer, Double> percentiles; // e.g., 50th, 90th, 95th, 99th

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
