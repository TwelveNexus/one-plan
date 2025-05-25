package com.twelvenexus.oneplan.analytics.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
public class EventDto {
  @NotNull private UUID entityId;

  @NotNull private String entityType;

  @NotNull private String eventType;

  @NotNull private String eventName;

  private Map<String, Object> properties;

  private UUID userId;
  private String sessionId;
  private String userAgent;
  private String ipAddress;
}
