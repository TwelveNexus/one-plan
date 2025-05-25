package com.twelvenexus.oneplan.notification.config;

import com.twelvenexus.oneplan.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfiguration {

  private final NotificationService notificationService;

  @Scheduled(fixedDelay = 60000) // Every minute
  public void processScheduledNotifications() {
    log.debug("Processing scheduled notifications");
    notificationService.processScheduledNotifications();
  }

  @Scheduled(fixedDelay = 300000) // Every 5 minutes
  public void retryFailedNotifications() {
    log.debug("Retrying failed notifications");
    notificationService.retryFailedNotifications();
  }

  @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
  public void cleanupOldNotifications() {
    log.info("Cleaning up old notifications");
    notificationService.deleteOldNotifications(90); // Keep 90 days
  }
}
