package com.twelvenexus.oneplan.analytics.dto;

import com.twelvenexus.oneplan.analytics.enums.MetricType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class MetricDto {
    private UUID id;
    private UUID tenantId;
    private UUID entityId;
    private String entityType;
    private MetricType type;
    private Double value;
    private Map<String, String> dimensions;
    private LocalDateTime timestamp;
}
