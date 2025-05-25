package com.twelvenexus.oneplan.analytics.dto;

import com.twelvenexus.oneplan.analytics.enums.MetricType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class MetricQueryDto {
  private UUID entityId;
  private MetricType type;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
}
