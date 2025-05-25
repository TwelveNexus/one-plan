package com.twelvenexus.oneplan.analytics.controller;

import com.twelvenexus.oneplan.analytics.dto.EventDto;
import com.twelvenexus.oneplan.analytics.model.AnalyticsEvent;
import com.twelvenexus.oneplan.analytics.service.AnalyticsEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Analytics event tracking")
public class EventController {

  private final AnalyticsEventService eventService;

  @PostMapping
  @Operation(summary = "Track an event")
  public ResponseEntity<Void> trackEvent(
      @RequestHeader("X-Tenant-Id") UUID tenantId,
      @Valid @RequestBody EventDto dto,
      HttpServletRequest request) {
    eventService.trackEvent(
        tenantId,
        dto.getEntityId(),
        dto.getEntityType(),
        dto.getEventType(),
        dto.getEventName(),
        dto.getProperties(),
        dto.getUserId(),
        dto.getSessionId(),
        dto.getUserAgent() != null ? dto.getUserAgent() : request.getHeader("User-Agent"),
        dto.getIpAddress() != null ? dto.getIpAddress() : request.getRemoteAddr());

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping("/entity/{entityId}")
  @Operation(summary = "Get events for an entity")
  public ResponseEntity<List<AnalyticsEvent>> getEntityEvents(
      @RequestHeader("X-Tenant-Id") UUID tenantId,
      @PathVariable UUID entityId,
      @RequestParam String startDate,
      @RequestParam String endDate) {
    LocalDateTime start = LocalDateTime.parse(startDate);
    LocalDateTime end = LocalDateTime.parse(endDate);

    List<AnalyticsEvent> events = eventService.getEvents(tenantId, entityId, start, end);
    return ResponseEntity.ok(events);
  }

  @GetMapping("/user/{userId}")
  @Operation(summary = "Get user events")
  public ResponseEntity<List<AnalyticsEvent>> getUserEvents(
      @RequestHeader("X-Tenant-Id") UUID tenantId,
      @PathVariable UUID userId,
      @RequestParam String since) {
    LocalDateTime sinceDate = LocalDateTime.parse(since);
    List<AnalyticsEvent> events = eventService.getUserEvents(tenantId, userId, sinceDate);
    return ResponseEntity.ok(events);
  }

  @GetMapping("/counts")
  @Operation(summary = "Get event counts")
  public ResponseEntity<Map<String, Long>> getEventCounts(
      @RequestHeader("X-Tenant-Id") UUID tenantId,
      @RequestParam String entityType,
      @RequestParam String since) {
    LocalDateTime sinceDate = LocalDateTime.parse(since);
    Map<String, Long> counts = eventService.getEventCounts(tenantId, entityType, sinceDate);
    return ResponseEntity.ok(counts);
  }
}
