package com.twelvenexus.oneplan.notification.model;

import com.twelvenexus.oneplan.notification.enums.NotificationChannel;
import com.twelvenexus.oneplan.notification.enums.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "notification_preferences")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotificationPreference {

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
  private NotificationType notificationType;

  @ElementCollection(targetClass = NotificationChannel.class)
  @CollectionTable(name = "preference_channels")
  @Enumerated(EnumType.STRING)
  @Column(name = "channel")
  private Set<NotificationChannel> enabledChannels;

  @Column(nullable = false)
  private boolean enabled = true;

  private boolean digestEnabled = false;

  private String digestSchedule; // cron expression

  private LocalDateTime quietTimeStart;

  private LocalDateTime quietTimeEnd;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  @PreUpdate
  private void preUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
