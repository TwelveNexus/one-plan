package com.twelvenexus.oneplan.notification.repository;

import com.twelvenexus.oneplan.notification.enums.NotificationType;
import com.twelvenexus.oneplan.notification.model.NotificationPreference;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationPreferenceRepository
    extends JpaRepository<NotificationPreference, UUID> {

  Optional<NotificationPreference> findByUserIdAndTenantIdAndNotificationType(
      UUID userId, UUID tenantId, NotificationType type);

  List<NotificationPreference> findByUserIdAndTenantId(UUID userId, UUID tenantId);

  List<NotificationPreference> findByUserIdAndTenantIdAndEnabled(
      UUID userId, UUID tenantId, boolean enabled);

  void deleteByUserIdAndTenantId(UUID userId, UUID tenantId);
}
