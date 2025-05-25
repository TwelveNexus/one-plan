package com.twelvenexus.oneplan.analytics.service.impl;

import com.twelvenexus.oneplan.analytics.enums.AggregationPeriod;
import com.twelvenexus.oneplan.analytics.enums.MetricType;
import com.twelvenexus.oneplan.analytics.model.AggregatedMetric;
import com.twelvenexus.oneplan.analytics.model.Metric;
import com.twelvenexus.oneplan.analytics.repository.AggregatedMetricRepository;
import com.twelvenexus.oneplan.analytics.repository.MetricRepository;
import com.twelvenexus.oneplan.analytics.service.MetricService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MetricServiceImpl implements MetricService {

  private final MetricRepository metricRepository;
  private final AggregatedMetricRepository aggregatedMetricRepository;

  @Override
  public Metric recordMetric(
      UUID tenantId,
      UUID entityId,
      String entityType,
      MetricType type,
      Double value,
      Map<String, String> dimensions) {
    Metric metric = new Metric();
    metric.setTenantId(tenantId);
    metric.setEntityId(entityId);
    metric.setEntityType(entityType);
    metric.setType(type);
    metric.setValue(value);
    metric.setDimensions(dimensions);
    metric.setTimestamp(LocalDateTime.now());

    log.debug("Recording metric: {} for entity {} with value {}", type, entityId, value);
    return metricRepository.save(metric);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(
      value = "metrics",
      key = "#tenantId + '_' + #entityId + '_' + #type + '_' + #start + '_' + #end")
  public List<Metric> getMetrics(
      UUID tenantId, UUID entityId, MetricType type, LocalDateTime start, LocalDateTime end) {
    return metricRepository.findByTenantIdAndEntityIdAndTypeAndTimestampBetween(
        tenantId, entityId, type, start, end);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(
      value = "aggregatedMetrics",
      key = "#tenantId + '_' + #entityId + '_' + #type + '_' + #period + '_' + #start + '_' + #end")
  public List<AggregatedMetric> getAggregatedMetrics(
      UUID tenantId,
      UUID entityId,
      MetricType type,
      AggregationPeriod period,
      LocalDateTime start,
      LocalDateTime end) {
    return aggregatedMetricRepository
        .findByTenantIdAndEntityIdAndTypeAndPeriodAndPeriodStartBetween(
            tenantId, entityId, type, period, start, end);
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Double> getMetricStatistics(
      UUID tenantId, UUID entityId, MetricType type, LocalDateTime start, LocalDateTime end) {
    List<Metric> metrics = getMetrics(tenantId, entityId, type, start, end);

    if (metrics.isEmpty()) {
      return Collections.emptyMap();
    }

    DoubleSummaryStatistics stats =
        metrics.stream().mapToDouble(Metric::getValue).summaryStatistics();

    Map<String, Double> result = new HashMap<>();
    result.put("min", stats.getMin());
    result.put("max", stats.getMax());
    result.put("avg", stats.getAverage());
    result.put("sum", stats.getSum());
    result.put("count", (double) stats.getCount());

    // Calculate percentiles
    List<Double> sortedValues =
        metrics.stream().map(Metric::getValue).sorted().collect(Collectors.toList());

    result.put("p50", calculatePercentile(sortedValues, 50));
    result.put("p90", calculatePercentile(sortedValues, 90));
    result.put("p95", calculatePercentile(sortedValues, 95));
    result.put("p99", calculatePercentile(sortedValues, 99));

    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Metric> getTopPerformers(
      UUID tenantId, MetricType type, LocalDateTime since, int limit) {
    return metricRepository
        .findTopPerformers(tenantId, type, since, PageRequest.of(0, limit))
        .getContent();
  }

  @Override
  public void aggregateMetrics(AggregationPeriod period) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime periodStart = truncateToPeriod(now.minus(1, getChronoUnit(period)), period);
    LocalDateTime periodEnd = periodStart.plus(1, getChronoUnit(period));

    log.info("Aggregating metrics for period {} from {} to {}", period, periodStart, periodEnd);

    // Get all unique tenant/entity combinations for the period
    List<Metric> metrics = metricRepository.findAll(); // This should be optimized

    Map<AggregationKey, List<Metric>> groupedMetrics =
        metrics.stream()
            .filter(
                m ->
                    !m.getTimestamp().isBefore(periodStart) && m.getTimestamp().isBefore(periodEnd))
            .collect(
                Collectors.groupingBy(
                    m ->
                        new AggregationKey(
                            m.getTenantId(), m.getEntityId(), m.getEntityType(), m.getType())));

    for (Map.Entry<AggregationKey, List<Metric>> entry : groupedMetrics.entrySet()) {
      AggregationKey key = entry.getKey();
      List<Metric> metricList = entry.getValue();

      if (!metricList.isEmpty()) {
        AggregatedMetric aggregated =
            createAggregatedMetric(key, metricList, period, periodStart, periodEnd);
        aggregatedMetricRepository.save(aggregated);
      }
    }
  }

  @Override
  public void cleanupOldMetrics(int daysToKeep) {
    LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
    log.info("Cleaning up metrics older than {}", cutoffDate);
    metricRepository.deleteOldMetrics(cutoffDate);
  }

  private double calculatePercentile(List<Double> sortedValues, int percentile) {
    if (sortedValues.isEmpty()) return 0.0;

    int index = (int) Math.ceil(percentile / 100.0 * sortedValues.size()) - 1;
    return sortedValues.get(Math.max(0, Math.min(index, sortedValues.size() - 1)));
  }

  private LocalDateTime truncateToPeriod(LocalDateTime dateTime, AggregationPeriod period) {
    return switch (period) {
      case MINUTE -> dateTime.truncatedTo(ChronoUnit.MINUTES);
      case HOUR -> dateTime.truncatedTo(ChronoUnit.HOURS);
      case DAY -> dateTime.truncatedTo(ChronoUnit.DAYS);
      case WEEK -> dateTime
          .truncatedTo(ChronoUnit.DAYS)
          .minusDays(dateTime.getDayOfWeek().getValue() - 1);
      case MONTH -> dateTime.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
      case QUARTER -> dateTime
          .withMonth(((dateTime.getMonthValue() - 1) / 3) * 3 + 1)
          .withDayOfMonth(1)
          .truncatedTo(ChronoUnit.DAYS);
      case YEAR -> dateTime.withDayOfYear(1).truncatedTo(ChronoUnit.DAYS);
    };
  }

  private ChronoUnit getChronoUnit(AggregationPeriod period) {
    return switch (period) {
      case MINUTE -> ChronoUnit.MINUTES;
      case HOUR -> ChronoUnit.HOURS;
      case DAY -> ChronoUnit.DAYS;
      case WEEK -> ChronoUnit.WEEKS;
      case MONTH -> ChronoUnit.MONTHS;
      case QUARTER -> ChronoUnit.MONTHS; // Handle quarters as 3 months
      case YEAR -> ChronoUnit.YEARS;
    };
  }

  private AggregatedMetric createAggregatedMetric(
      AggregationKey key,
      List<Metric> metrics,
      AggregationPeriod period,
      LocalDateTime periodStart,
      LocalDateTime periodEnd) {
    DoubleSummaryStatistics stats =
        metrics.stream().mapToDouble(Metric::getValue).summaryStatistics();

    AggregatedMetric aggregated = new AggregatedMetric();
    aggregated.setTenantId(key.tenantId());
    aggregated.setEntityId(key.entityId());
    aggregated.setEntityType(key.entityType());
    aggregated.setType(key.type());
    aggregated.setPeriod(period);
    aggregated.setPeriodStart(periodStart);
    aggregated.setPeriodEnd(periodEnd);
    aggregated.setMinValue(stats.getMin());
    aggregated.setMaxValue(stats.getMax());
    aggregated.setAvgValue(stats.getAverage());
    aggregated.setSumValue(stats.getSum());
    aggregated.setCount(stats.getCount());

    // Calculate percentiles
    List<Double> sortedValues =
        metrics.stream().map(Metric::getValue).sorted().collect(Collectors.toList());

    Map<Integer, Double> percentiles = new HashMap<>();
    percentiles.put(50, calculatePercentile(sortedValues, 50));
    percentiles.put(90, calculatePercentile(sortedValues, 90));
    percentiles.put(95, calculatePercentile(sortedValues, 95));
    percentiles.put(99, calculatePercentile(sortedValues, 99));
    aggregated.setPercentiles(percentiles);

    return aggregated;
  }

  private record AggregationKey(UUID tenantId, UUID entityId, String entityType, MetricType type) {}
}
