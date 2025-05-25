package com.twelvenexus.oneplan.analytics.dto;

import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
public class DashboardWidgetDto {
  private UUID id;
  private String title;
  private String type;
  private Integer position;
  private Integer width;
  private Integer height;
  private Map<String, String> configuration;
}
