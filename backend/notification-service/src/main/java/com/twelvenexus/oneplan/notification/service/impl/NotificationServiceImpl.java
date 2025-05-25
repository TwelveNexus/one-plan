package com.twelvenexus.oneplan.notification.service.impl;

import com.twelvenexus.oneplan.notification.enums.NotificationChannel;
import com.twelvenexus.oneplan.notification.enums.NotificationStatus;
import com.twelvenexus.oneplan.notification.enums.NotificationType;
import com.twelvenexus.oneplan.notification.model.Notification;
import com.twelvenexus.oneplan.notification.repository.NotificationRepository;
import com.twelvenexus.oneplan.notification.service.NotificationPreferenceService;
import com.twelvenexus.oneplan.notification.service.NotificationSenderService;
import com.twelvenexus.oneplan.notification.service.NotificationService;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationPreferenceService preferenceService;
  private final NotificationSenderService senderService;

  @Override
  public Notification createNotification(
      UUID userId,
      UUID tenantId,
      NotificationType type,
      String title,
      String content,
      Map<String, String> metadata) {
    Notification notification = new Notification();
    notification.setUserId(userId);
    notification.setTenantId(tenantId);
    notification.setType(type);
    notification.setTitle(title);
    notification.setContent(content);
    notification.setMetadata(metadata);
    notification.setStatus(NotificationStatus.PENDING);

    return notificationRepository.save(notification);
  }

  @Override
  public Notification sendNotification(
      UUID userId,
      UUID tenantId,
      NotificationType type,
      NotificationChannel channel,
      String title,
      String content,
      Map<String, String> metadata) {
    // Check if notification is enabled for this user
    if (!preferenceService.isNotificationEnabled(userId, tenantId, type, channel)) {
      log.debug("Notification type {} via {} is disabled for user {}", type, channel, userId);
      return null;
    }

    Notification notification =
        createNotification(userId, tenantId, type, title, content, metadata);
    notification.setChannel(channel);

    try {
      switch (channel) {
        case EMAIL -> senderService.sendEmail(notification);
        case IN_APP -> senderService.sendInApp(notification);
        case PUSH -> senderService.sendPush(notification);
        case SMS -> senderService.sendSms(notification);
        case WEBHOOK -> senderService.sendWebhook(notification);
      }

      notification.setStatus(NotificationStatus.SENT);
      notification.setSentAt(LocalDateTime.now());
    } catch (Exception e) {
      log.error("Failed to send notification: {}", e.getMessage());
      notification.setStatus(NotificationStatus.FAILED);
      notification.setErrorMessage(e.getMessage());
      notification.setRetryCount(notification.getRetryCount() + 1);
    }

    return notificationRepository.save(notification);
  }

  @Override
  @Async
  public void sendNotificationAsync(
      UUID userId,
      UUID tenantId,
      NotificationType type,
      NotificationChannel channel,
      String title,
      String content,
      Map<String, String> metadata) {
    sendNotification(userId, tenantId, type, channel, title, content, metadata);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Notification> getUserNotifications(UUID userId, UUID tenantId, Pageable pageable) {
    return notificationRepository.findByUserIdAndTenantId(userId, tenantId, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Notification> getUnreadNotifications(UUID userId, UUID tenantId, Pageable pageable) {
    return notificationRepository.findUnreadByUserIdAndTenantId(userId, tenantId, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public long getUnreadCount(UUID userId, UUID tenantId) {
    return notificationRepository.countUnreadByUserIdAndTenantId(userId, tenantId);
  }

  @Override
  public Notification markAsRead(UUID notificationId, UUID userId) {
    Notification notification =
        notificationRepository
            .findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

    if (!notification.getUserId().equals(userId)) {
      throw new IllegalArgumentException("Notification does not belong to user");
    }

    notification.setReadAt(LocalDateTime.now());
    notification.setStatus(NotificationStatus.READ);
    return notificationRepository.save(notification);
  }

  @Override
  public void markAllAsRead(UUID userId, UUID tenantId) {
    Page<Notification> unreadNotifications =
        getUnreadNotifications(userId, tenantId, Pageable.unpaged());

    unreadNotifications.forEach(
        notification -> {
          notification.setReadAt(LocalDateTime.now());
          notification.setStatus(NotificationStatus.READ);
        });

    notificationRepository.saveAll(unreadNotifications.getContent());
  }

  @Override
  public void processScheduledNotifications() {
    LocalDateTime now = LocalDateTime.now();
    var scheduledNotifications =
        notificationRepository.findByStatusAndScheduledAtLessThanEqual(
            NotificationStatus.PENDING, now);

    for (Notification notification : scheduledNotifications) {
      sendNotification(
          notification.getUserId(),
          notification.getTenantId(),
          notification.getType(),
          notification.getChannel(),
          notification.getTitle(),
          notification.getContent(),
          notification.getMetadata());
    }
  }

  @Override
  public void retryFailedNotifications() {
    var failedNotifications =
        notificationRepository.findByStatusAndRetryCountLessThan(NotificationStatus.FAILED, 3);

    for (Notification notification : failedNotifications) {
      sendNotification(
          notification.getUserId(),
          notification.getTenantId(),
          notification.getType(),
          notification.getChannel(),
          notification.getTitle(),
          notification.getContent(),
          notification.getMetadata());
    }
  }

  @Override
  public void deleteOldNotifications(int daysToKeep) {
    LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
    notificationRepository.deleteAll(
        notificationRepository.findAll().stream()
            .filter(n -> n.getCreatedAt().isBefore(cutoffDate))
            .toList());
  }
}
