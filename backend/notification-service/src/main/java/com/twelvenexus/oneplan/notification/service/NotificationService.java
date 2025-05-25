package com.twelvenexus.oneplan.notification.service;

import com.twelvenexus.oneplan.notification.enums.NotificationChannel;
import com.twelvenexus.oneplan.notification.enums.NotificationType;
import com.twelvenexus.oneplan.notification.model.Notification;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

  Notification createNotification(
      UUID userId,
      UUID tenantId,
      NotificationType type,
      String title,
      String content,
      Map<String, String> metadata);

  Notification sendNotification(
      UUID userId,
      UUID tenantId,
      NotificationType type,
      NotificationChannel channel,
      String title,
      String content,
      Map<String, String> metadata);

  void sendNotificationAsync(
      UUID userId,
      UUID tenantId,
      NotificationType type,
      NotificationChannel channel,
      String title,
      String content,
      Map<String, String> metadata);

  Page<Notification> getUserNotifications(UUID userId, UUID tenantId, Pageable pageable);

  Page<Notification> getUnreadNotifications(UUID userId, UUID tenantId, Pageable pageable);

  long getUnreadCount(UUID userId, UUID tenantId);

  Notification markAsRead(UUID notificationId, UUID userId);

  void markAllAsRead(UUID userId, UUID tenantId);

  void processScheduledNotifications();

  void retryFailedNotifications();

  void deleteOldNotifications(int daysToKeep);
}
