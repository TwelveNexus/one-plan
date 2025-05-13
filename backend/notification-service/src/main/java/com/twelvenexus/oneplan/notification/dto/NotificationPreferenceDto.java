package com.twelvenexus.oneplan.notification.dto;

import com.twelvenexus.oneplan.notification.enums.NotificationChannel;
import com.twelvenexus.oneplan.notification.enums.NotificationType;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class NotificationPreferenceDto {
    private UUID id;
    private UUID userId;
    private UUID tenantId;
    private NotificationType notificationType;
    private Set<NotificationChannel> enabledChannels;
    private boolean enabled;
    private boolean digestEnabled;
    private String digestSchedule;
    private String quietTimeStart;
    private String quietTimeEnd;
}
