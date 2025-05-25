package com.twelvenexus.oneplan.analytics.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class DashboardDto {
  private UUID id;
  private String name;
  private String description;
  private List<DashboardWidgetDto> widgets;
  private boolean isPublic;
  private boolean isDefault;
}
