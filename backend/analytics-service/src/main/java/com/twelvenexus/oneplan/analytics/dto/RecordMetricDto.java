package com.twelvenexus.oneplan.analytics.dto;

import com.twelvenexus.oneplan.analytics.enums.MetricType;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class RecordMetricDto {
    private UUID entityId;
    private String entityType;
    private MetricType type;
    private Double value;
    private Map<String, String> dimensions;
}

