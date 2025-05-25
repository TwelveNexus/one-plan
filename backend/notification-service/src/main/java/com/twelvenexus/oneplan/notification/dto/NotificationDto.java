package com.twelvenexus.oneplan.notification.dto;

import com.twelvenexus.oneplan.notification.enums.NotificationChannel;
import com.twelvenexus.oneplan.notification.enums.NotificationStatus;
import com.twelvenexus.oneplan.notification.enums.NotificationType;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
public class NotificationDto {
  private UUID id;
  private UUID userId;
  private UUID tenantId;
  private NotificationType type;
  private NotificationChannel channel;
  private NotificationStatus status;
  private String title;
  private String content;
  private Map<String, String> metadata;
  private LocalDateTime scheduledAt;
  private LocalDateTime sentAt;
  private LocalDateTime deliveredAt;
  private LocalDateTime readAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
