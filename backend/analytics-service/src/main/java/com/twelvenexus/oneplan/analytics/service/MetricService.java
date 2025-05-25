package com.twelvenexus.oneplan.analytics.service;

import com.twelvenexus.oneplan.analytics.enums.AggregationPeriod;
import com.twelvenexus.oneplan.analytics.enums.MetricType;
import com.twelvenexus.oneplan.analytics.model.AggregatedMetric;
import com.twelvenexus.oneplan.analytics.model.Metric;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MetricService {

  Metric recordMetric(
      UUID tenantId,
      UUID entityId,
      String entityType,
      MetricType type,
      Double value,
      Map<String, String> dimensions);

  List<Metric> getMetrics(
      UUID tenantId, UUID entityId, MetricType type, LocalDateTime start, LocalDateTime end);

  List<AggregatedMetric> getAggregatedMetrics(
      UUID tenantId,
      UUID entityId,
      MetricType type,
      AggregationPeriod period,
      LocalDateTime start,
      LocalDateTime end);

  Map<String, Double> getMetricStatistics(
      UUID tenantId, UUID entityId, MetricType type, LocalDateTime start, LocalDateTime end);

  List<Metric> getTopPerformers(UUID tenantId, MetricType type, LocalDateTime since, int limit);

  void aggregateMetrics(AggregationPeriod period);

  void cleanupOldMetrics(int daysToKeep);
}
