package com.twelvenexus.oneplan.analytics.repository;

import com.twelvenexus.oneplan.analytics.enums.AggregationPeriod;
import com.twelvenexus.oneplan.analytics.enums.MetricType;
import com.twelvenexus.oneplan.analytics.model.AggregatedMetric;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregatedMetricRepository extends JpaRepository<AggregatedMetric, UUID> {

  Optional<AggregatedMetric> findByTenantIdAndEntityIdAndTypeAndPeriodAndPeriodStart(
      UUID tenantId,
      UUID entityId,
      MetricType type,
      AggregationPeriod period,
      LocalDateTime periodStart);

  List<AggregatedMetric> findByTenantIdAndEntityIdAndTypeAndPeriodAndPeriodStartBetween(
      UUID tenantId,
      UUID entityId,
      MetricType type,
      AggregationPeriod period,
      LocalDateTime start,
      LocalDateTime end);

  @Query(
      "SELECT a FROM AggregatedMetric a WHERE a.tenantId = :tenantId "
          + "AND a.entityType = :entityType AND a.type = :type "
          + "AND a.period = :period AND a.periodStart BETWEEN :start AND :end "
          + "ORDER BY a.avgValue DESC")
  List<AggregatedMetric> findTopPerformersByAverage(
      @Param("tenantId") UUID tenantId,
      @Param("entityType") String entityType,
      @Param("type") MetricType type,
      @Param("period") AggregationPeriod period,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  @Query(
      "SELECT a FROM AggregatedMetric a WHERE a.tenantId = :tenantId "
          + "AND a.entityId = :entityId AND a.type IN :types "
          + "AND a.period = :period AND a.periodStart = :periodStart")
  List<AggregatedMetric> findMultipleMetrics(
      @Param("tenantId") UUID tenantId,
      @Param("entityId") UUID entityId,
      @Param("types") List<MetricType> types,
      @Param("period") AggregationPeriod period,
      @Param("periodStart") LocalDateTime periodStart);
}
