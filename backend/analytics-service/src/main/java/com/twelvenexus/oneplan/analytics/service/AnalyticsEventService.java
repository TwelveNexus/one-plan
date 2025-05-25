package com.twelvenexus.oneplan.analytics.service;

import com.twelvenexus.oneplan.analytics.model.AnalyticsEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AnalyticsEventService {

  void trackEvent(
      UUID tenantId,
      UUID entityId,
      String entityType,
      String eventType,
      String eventName,
      Map<String, Object> properties,
      UUID userId,
      String sessionId,
      String userAgent,
      String ipAddress);

  List<AnalyticsEvent> getEvents(
      UUID tenantId, UUID entityId, LocalDateTime start, LocalDateTime end);

  List<AnalyticsEvent> getUserEvents(UUID tenantId, UUID userId, LocalDateTime since);

  Map<String, Long> getEventCounts(UUID tenantId, String entityType, LocalDateTime since);

  void processEvents();

  void cleanupOldEvents(int daysToKeep);
}
