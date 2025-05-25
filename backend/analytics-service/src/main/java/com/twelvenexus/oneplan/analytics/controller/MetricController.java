package com.twelvenexus.oneplan.analytics.controller;

import com.twelvenexus.oneplan.analytics.dto.MetricDto;
import com.twelvenexus.oneplan.analytics.dto.MetricQueryDto;
import com.twelvenexus.oneplan.analytics.dto.RecordMetricDto;
import com.twelvenexus.oneplan.analytics.enums.AggregationPeriod;
import com.twelvenexus.oneplan.analytics.model.AggregatedMetric;
import com.twelvenexus.oneplan.analytics.model.Metric;
import com.twelvenexus.oneplan.analytics.service.MetricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
@Tag(name = "Metrics", description = "Metric recording and retrieval")
public class MetricController {

  private final MetricService metricService;

  @PostMapping
  @Operation(summary = "Record a metric")
  public ResponseEntity<MetricDto> recordMetric(
      @RequestHeader("X-Tenant-Id") UUID tenantId, @Valid @RequestBody RecordMetricDto dto) {
    Metric metric =
        metricService.recordMetric(
            tenantId,
            dto.getEntityId(),
            dto.getEntityType(),
            dto.getType(),
            dto.getValue(),
            dto.getDimensions());

    return new ResponseEntity<>(toDto(metric), HttpStatus.CREATED);
  }

  @GetMapping
  @Operation(summary = "Query metrics")
  public ResponseEntity<List<MetricDto>> getMetrics(
      @RequestHeader("X-Tenant-Id") UUID tenantId, @Valid MetricQueryDto query) {
    List<Metric> metrics =
        metricService.getMetrics(
            tenantId,
            query.getEntityId(),
            query.getType(),
            query.getStartDate(),
            query.getEndDate());

    return ResponseEntity.ok(metrics.stream().map(this::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/aggregated")
  @Operation(summary = "Get aggregated metrics")
  public ResponseEntity<List<AggregatedMetric>> getAggregatedMetrics(
      @RequestHeader("X-Tenant-Id") UUID tenantId,
      @RequestParam UUID entityId,
      @RequestParam String type,
      @RequestParam AggregationPeriod period,
      @RequestParam String startDate,
      @RequestParam String endDate) {
    // Parse dates and call service
    List<AggregatedMetric> metrics =
        metricService.getAggregatedMetrics(tenantId, entityId, null, period, null, null);

    return ResponseEntity.ok(metrics);
  }

  @GetMapping("/statistics")
  @Operation(summary = "Get metric statistics")
  public ResponseEntity<Map<String, Double>> getMetricStatistics(
      @RequestHeader("X-Tenant-Id") UUID tenantId, @Valid MetricQueryDto query) {
    Map<String, Double> stats =
        metricService.getMetricStatistics(
            tenantId,
            query.getEntityId(),
            query.getType(),
            query.getStartDate(),
            query.getEndDate());

    return ResponseEntity.ok(stats);
  }

  @GetMapping("/top-performers")
  @Operation(summary = "Get top performers")
  public ResponseEntity<List<MetricDto>> getTopPerformers(
      @RequestHeader("X-Tenant-Id") UUID tenantId,
      @RequestParam String type,
      @RequestParam String since,
      @RequestParam(defaultValue = "10") int limit) {
    // Parse date and call service
    List<Metric> metrics = metricService.getTopPerformers(tenantId, null, null, limit);

    return ResponseEntity.ok(metrics.stream().map(this::toDto).collect(Collectors.toList()));
  }

  private MetricDto toDto(Metric metric) {
    MetricDto dto = new MetricDto();
    dto.setId(metric.getId());
    dto.setTenantId(metric.getTenantId());
    dto.setEntityId(metric.getEntityId());
    dto.setEntityType(metric.getEntityType());
    dto.setType(metric.getType());
    dto.setValue(metric.getValue());
    dto.setDimensions(metric.getDimensions());
    dto.setTimestamp(metric.getTimestamp());
    return dto;
  }
}
