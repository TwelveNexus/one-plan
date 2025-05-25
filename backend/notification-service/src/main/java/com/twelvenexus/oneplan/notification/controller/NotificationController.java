package com.twelvenexus.oneplan.notification.controller;

import com.twelvenexus.oneplan.notification.dto.CreateNotificationDto;
import com.twelvenexus.oneplan.notification.dto.NotificationDto;
import com.twelvenexus.oneplan.notification.model.Notification;
import com.twelvenexus.oneplan.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {

  private final NotificationService notificationService;

  @PostMapping
  @Operation(summary = "Send a notification")
  public ResponseEntity<NotificationDto> sendNotification(
      @Valid @RequestBody CreateNotificationDto dto) {
    Notification notification =
        notificationService.sendNotification(
            dto.getUserId(),
            dto.getTenantId(),
            dto.getType(),
            dto.getChannel(),
            dto.getTitle(),
            dto.getContent(),
            dto.getMetadata());

    return new ResponseEntity<>(toDto(notification), HttpStatus.CREATED);
  }

  @PostMapping("/async")
  @Operation(summary = "Send a notification asynchronously")
  public ResponseEntity<Void> sendNotificationAsync(@Valid @RequestBody CreateNotificationDto dto) {
    notificationService.sendNotificationAsync(
        dto.getUserId(),
        dto.getTenantId(),
        dto.getType(),
        dto.getChannel(),
        dto.getTitle(),
        dto.getContent(),
        dto.getMetadata());

    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @GetMapping("/users/{userId}")
  @Operation(summary = "Get user notifications")
  public ResponseEntity<Page<NotificationDto>> getUserNotifications(
      @PathVariable UUID userId, @RequestParam UUID tenantId, Pageable pageable) {
    Page<Notification> notifications =
        notificationService.getUserNotifications(userId, tenantId, pageable);

    return ResponseEntity.ok(notifications.map(this::toDto));
  }

  @GetMapping("/users/{userId}/unread")
  @Operation(summary = "Get unread notifications")
  public ResponseEntity<Page<NotificationDto>> getUnreadNotifications(
      @PathVariable UUID userId, @RequestParam UUID tenantId, Pageable pageable) {
    Page<Notification> notifications =
        notificationService.getUnreadNotifications(userId, tenantId, pageable);

    return ResponseEntity.ok(notifications.map(this::toDto));
  }

  @GetMapping("/users/{userId}/unread/count")
  @Operation(summary = "Get unread notification count")
  public ResponseEntity<Map<String, Long>> getUnreadCount(
      @PathVariable UUID userId, @RequestParam UUID tenantId) {
    long count = notificationService.getUnreadCount(userId, tenantId);
    return ResponseEntity.ok(Map.of("count", count));
  }

  @PutMapping("/{notificationId}/read")
  @Operation(summary = "Mark notification as read")
  public ResponseEntity<NotificationDto> markAsRead(
      @PathVariable UUID notificationId, @RequestParam UUID userId) {
    Notification notification = notificationService.markAsRead(notificationId, userId);
    return ResponseEntity.ok(toDto(notification));
  }

  @PutMapping("/users/{userId}/read-all")
  @Operation(summary = "Mark all notifications as read")
  public ResponseEntity<Void> markAllAsRead(
      @PathVariable UUID userId, @RequestParam UUID tenantId) {
    notificationService.markAllAsRead(userId, tenantId);
    return ResponseEntity.ok().build();
  }

  private NotificationDto toDto(Notification notification) {
    NotificationDto dto = new NotificationDto();
    dto.setId(notification.getId());
    dto.setUserId(notification.getUserId());
    dto.setTenantId(notification.getTenantId());
    dto.setType(notification.getType());
    dto.setChannel(notification.getChannel());
    dto.setStatus(notification.getStatus());
    dto.setTitle(notification.getTitle());
    dto.setContent(notification.getContent());
    dto.setMetadata(notification.getMetadata());
    dto.setScheduledAt(notification.getScheduledAt());
    dto.setSentAt(notification.getSentAt());
    dto.setDeliveredAt(notification.getDeliveredAt());
    dto.setReadAt(notification.getReadAt());
    dto.setCreatedAt(notification.getCreatedAt());
    dto.setUpdatedAt(notification.getUpdatedAt());
    return dto;
  }
}
