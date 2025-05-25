package com.twelvenexus.oneplan.analytics.service.impl;

import com.twelvenexus.oneplan.analytics.enums.MetricType;
import com.twelvenexus.oneplan.analytics.model.AnalyticsEvent;
import com.twelvenexus.oneplan.analytics.repository.AnalyticsEventRepository;
import com.twelvenexus.oneplan.analytics.service.AnalyticsEventService;
import com.twelvenexus.oneplan.analytics.service.MetricService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsEventServiceImpl implements AnalyticsEventService {

  private final AnalyticsEventRepository eventRepository;
  private final MetricService metricService;

  @Override
  public void trackEvent(
      UUID tenantId,
      UUID entityId,
      String entityType,
      String eventType,
      String eventName,
      Map<String, Object> properties,
      UUID userId,
      String sessionId,
      String userAgent,
      String ipAddress) {
    AnalyticsEvent event = new AnalyticsEvent();
    event.setTenantId(tenantId);
    event.setEntityId(entityId);
    event.setEntityType(entityType);
    event.setEventType(eventType);
    event.setEventName(eventName);
    event.setProperties(properties);
    event.setUserId(userId);
    event.setSessionId(sessionId);
    event.setUserAgent(userAgent);
    event.setIpAddress(ipAddress);
    event.setTimestamp(LocalDateTime.now());
    event.setProcessed(false);

    log.debug("Tracking event: {} for entity {}", eventName, entityId);
    eventRepository.save(event);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AnalyticsEvent> getEvents(
      UUID tenantId, UUID entityId, LocalDateTime start, LocalDateTime end) {
    return eventRepository.findByTenantIdAndEntityIdAndTimestampBetween(
        tenantId, entityId, start, end);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AnalyticsEvent> getUserEvents(UUID tenantId, UUID userId, LocalDateTime since) {
    return eventRepository.findUserEvents(tenantId, userId, since);
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Long> getEventCounts(UUID tenantId, String entityType, LocalDateTime since) {
    List<AnalyticsEvent> events =
        eventRepository.findByEventTypeAndDateRange(
            tenantId, entityType, since, LocalDateTime.now());

    return events.stream()
        .collect(Collectors.groupingBy(AnalyticsEvent::getEventName, Collectors.counting()));
  }

  @Override
  public void processEvents() {
    LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5);
    List<AnalyticsEvent> unprocessedEvents =
        eventRepository.findByProcessedFalseAndTimestampBefore(cutoff);

    log.info("Processing {} unprocessed events", unprocessedEvents.size());

    Map<String, List<AnalyticsEvent>> groupedByType =
        unprocessedEvents.stream().collect(Collectors.groupingBy(AnalyticsEvent::getEventType));

    for (Map.Entry<String, List<AnalyticsEvent>> entry : groupedByType.entrySet()) {
      String eventType = entry.getKey();
      List<AnalyticsEvent> events = entry.getValue();

      try {
        processEventType(eventType, events);

        // Mark events as processed
        events.forEach(
            event -> {
              event.setProcessed(true);
              event.setProcessedAt(LocalDateTime.now());
            });
        eventRepository.saveAll(events);
      } catch (Exception e) {
        log.error("Error processing events of type {}: {}", eventType, e.getMessage());
      }
    }
  }

  @Override
  public void cleanupOldEvents(int daysToKeep) {
    LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
    log.info("Cleaning up events older than {}", cutoffDate);
    eventRepository.deleteByTimestampBefore(cutoffDate);
  }

  private void processEventType(String eventType, List<AnalyticsEvent> events) {
    switch (eventType) {
      case "project.created" -> processProjectCreatedEvents(events);
      case "task.completed" -> processTaskCompletedEvents(events);
      case "user.login" -> processUserLoginEvents(events);
      case "requirement.analyzed" -> processRequirementAnalyzedEvents(events);
      default -> log.warn("Unknown event type: {}", eventType);
    }
  }

  private void processProjectCreatedEvents(List<AnalyticsEvent> events) {
    for (AnalyticsEvent event : events) {
      metricService.recordMetric(
          event.getTenantId(),
          event.getEntityId(),
          "project",
          MetricType.PROJECT_CREATED,
          1.0,
          Map.of("userId", event.getUserId().toString()));
    }
  }

  private void processTaskCompletedEvents(List<AnalyticsEvent> events) {
    for (AnalyticsEvent event : events) {
      Map<String, Object> props = event.getProperties();
      Double leadTime = (Double) props.get("leadTime");
      Double cycleTime = (Double) props.get("cycleTime");

      if (leadTime != null) {
        metricService.recordMetric(
            event.getTenantId(),
            event.getEntityId(),
            "task",
            MetricType.TASK_LEAD_TIME,
            leadTime,
            Map.of("priority", (String) props.getOrDefault("priority", "medium")));
      }

      if (cycleTime != null) {
        metricService.recordMetric(
            event.getTenantId(),
            event.getEntityId(),
            "task",
            MetricType.TASK_CYCLE_TIME,
            cycleTime,
            Map.of("priority", (String) props.getOrDefault("priority", "medium")));
      }

      metricService.recordMetric(
          event.getTenantId(),
          event.getEntityId(),
          "task",
          MetricType.TASK_COMPLETED,
          1.0,
          Map.of("userId", event.getUserId().toString()));
    }
  }

  private void processUserLoginEvents(List<AnalyticsEvent> events) {
    Map<UUID, List<AnalyticsEvent>> byUser =
        events.stream().collect(Collectors.groupingBy(AnalyticsEvent::getUserId));

    for (Map.Entry<UUID, List<AnalyticsEvent>> entry : byUser.entrySet()) {
      UUID userId = entry.getKey();
      List<AnalyticsEvent> userEvents = entry.getValue();

      if (!userEvents.isEmpty()) {
        AnalyticsEvent lastEvent = userEvents.get(userEvents.size() - 1);
        metricService.recordMetric(
            lastEvent.getTenantId(),
            userId,
            "user",
            MetricType.USER_ACTIVE,
            1.0,
            Map.of("loginCount", String.valueOf(userEvents.size())));
      }
    }
  }

  private void processRequirementAnalyzedEvents(List<AnalyticsEvent> events) {
    for (AnalyticsEvent event : events) {
      Map<String, Object> props = event.getProperties();
      Double accuracy = (Double) props.get("accuracy");
      Double processingTime = (Double) props.get("processingTime");

      if (accuracy != null) {
        metricService.recordMetric(
            event.getTenantId(),
            event.getEntityId(),
            "requirement",
            MetricType.AI_REQUIREMENT_ANALYSIS_ACCURACY,
            accuracy,
            Map.of("type", (String) props.getOrDefault("requirementType", "general")));
      }

      if (processingTime != null) {
        metricService.recordMetric(
            event.getTenantId(),
            event.getEntityId(),
            "ai",
            MetricType.AI_PROCESSING_TIME,
            processingTime,
            Map.of("operation", "requirement_analysis"));
      }
    }
  }
}
