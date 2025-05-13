package com.twelvenexus.oneplan.notification.dto;

import com.twelvenexus.oneplan.notification.enums.NotificationChannel;
import com.twelvenexus.oneplan.notification.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class CreateNotificationDto {
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Tenant ID is required")
    private UUID tenantId;
    
    @NotNull(message = "Notification type is required")
    private NotificationType type;
    
    @NotNull(message = "Notification channel is required")
    private NotificationChannel channel;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private Map<String, String> metadata;
    private LocalDateTime scheduledAt;
    private String recipient;
}
