package com.twelvenexus.oneplan.analytics.dto;

import com.twelvenexus.oneplan.analytics.enums.MetricType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MetricQueryDto {
    private UUID entityId;
    private MetricType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
