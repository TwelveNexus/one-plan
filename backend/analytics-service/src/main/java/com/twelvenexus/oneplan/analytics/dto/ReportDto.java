package com.twelvenexus.oneplan.analytics.dto;

import com.twelvenexus.oneplan.analytics.enums.ReportType;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
public class ReportDto {
  private UUID id;
  private String name;
  private ReportType type;
  private Map<String, String> parameters;
  private String schedule;
  private boolean active;
  private LocalDateTime lastRunAt;
  private LocalDateTime nextRunAt;
  private String lastRunResult;
}
