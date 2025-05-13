package com.twelvenexus.oneplan.notification.service;

import com.twelvenexus.oneplan.notification.model.NotificationPreference;
import com.twelvenexus.oneplan.notification.enums.NotificationChannel;
import com.twelvenexus.oneplan.notification.enums.NotificationType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface NotificationPreferenceService {
    
    NotificationPreference createOrUpdatePreference(UUID userId, UUID tenantId, 
                                                   NotificationType type, 
                                                   Set<NotificationChannel> channels,
                                                   boolean enabled);
    
    NotificationPreference getPreference(UUID userId, UUID tenantId, NotificationType type);
    
    List<NotificationPreference> getUserPreferences(UUID userId, UUID tenantId);
    
    void updateDigestSettings(UUID userId, UUID tenantId, NotificationType type,
                            boolean digestEnabled, String schedule);
    
    void updateQuietTime(UUID userId, UUID tenantId, String startTime, String endTime);
    
    boolean isNotificationEnabled(UUID userId, UUID tenantId, NotificationType type,
                                 NotificationChannel channel);
    
    void deleteUserPreferences(UUID userId, UUID tenantId);
}
