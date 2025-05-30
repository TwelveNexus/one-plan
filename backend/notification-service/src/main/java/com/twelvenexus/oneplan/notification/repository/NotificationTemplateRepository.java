package com.twelvenexus.oneplan.notification.repository;

import com.twelvenexus.oneplan.notification.enums.NotificationChannel;
import com.twelvenexus.oneplan.notification.enums.NotificationType;
import com.twelvenexus.oneplan.notification.model.NotificationTemplate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {

  Optional<NotificationTemplate> findByTypeAndChannelAndLanguageAndActive(
      NotificationType type, NotificationChannel channel, String language, boolean active);

  default Optional<NotificationTemplate> findActiveTemplate(
      NotificationType type, NotificationChannel channel, String language) {
    return findByTypeAndChannelAndLanguageAndActive(type, channel, language, true);
  }
}
