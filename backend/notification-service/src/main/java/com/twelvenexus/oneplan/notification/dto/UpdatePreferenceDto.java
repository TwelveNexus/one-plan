package com.twelvenexus.oneplan.notification.dto;

import com.twelvenexus.oneplan.notification.enums.NotificationChannel;
import com.twelvenexus.oneplan.notification.enums.NotificationType;
import java.util.Set;
import lombok.Data;

@Data
public class UpdatePreferenceDto {
  private NotificationType notificationType;
  private Set<NotificationChannel> enabledChannels;
  private boolean enabled;
  private boolean digestEnabled;
  private String digestSchedule;
}
