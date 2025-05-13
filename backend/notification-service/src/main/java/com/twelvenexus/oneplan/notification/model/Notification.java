package com.twelvenexus.oneplan.notification.model;

import com.twelvenexus.oneplan.notification.enums.NotificationChannel;
import com.twelvenexus.oneplan.notification.enums.NotificationStatus;
import com.twelvenexus.oneplan.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notification {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @EqualsAndHashCode.Include
    private UUID id;
    
    @Column(nullable = false)
    private UUID userId;
    
    @Column(nullable = false)
    private UUID tenantId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.PENDING;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @ElementCollection
    @CollectionTable(name = "notification_metadata")
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> metadata;
    
    private String recipient; // email, phone number, etc.
    
    private LocalDateTime scheduledAt;
    
    private LocalDateTime sentAt;
    
    private LocalDateTime deliveredAt;
    
    private LocalDateTime readAt;
    
    private Integer retryCount = 0;
    
    private String errorMessage;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
