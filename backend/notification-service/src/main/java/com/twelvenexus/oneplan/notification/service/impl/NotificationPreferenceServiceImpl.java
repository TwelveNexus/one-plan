package com.twelvenexus.oneplan.notification.service.impl;

import com.twelvenexus.oneplan.notification.enums.NotificationChannel;
import com.twelvenexus.oneplan.notification.enums.NotificationType;
import com.twelvenexus.oneplan.notification.model.NotificationPreference;
import com.twelvenexus.oneplan.notification.repository.NotificationPreferenceRepository;
import com.twelvenexus.oneplan.notification.service.NotificationPreferenceService;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

  private final NotificationPreferenceRepository preferenceRepository;

  @Override
  public NotificationPreference createOrUpdatePreference(
      UUID userId,
      UUID tenantId,
      NotificationType type,
      Set<NotificationChannel> channels,
      boolean enabled) {
    NotificationPreference preference =
        preferenceRepository
            .findByUserIdAndTenantIdAndNotificationType(userId, tenantId, type)
            .orElse(new NotificationPreference());

    preference.setUserId(userId);
    preference.setTenantId(tenantId);
    preference.setNotificationType(type);
    preference.setEnabledChannels(channels);
    preference.setEnabled(enabled);

    return preferenceRepository.save(preference);
  }

  @Override
  @Transactional(readOnly = true)
  public NotificationPreference getPreference(UUID userId, UUID tenantId, NotificationType type) {
    return preferenceRepository
        .findByUserIdAndTenantIdAndNotificationType(userId, tenantId, type)
        .orElse(null);
  }

  @Override
  @Transactional(readOnly = true)
  public List<NotificationPreference> getUserPreferences(UUID userId, UUID tenantId) {
    return preferenceRepository.findByUserIdAndTenantId(userId, tenantId);
  }

  @Override
  public void updateDigestSettings(
      UUID userId, UUID tenantId, NotificationType type, boolean digestEnabled, String schedule) {
    NotificationPreference preference = getPreference(userId, tenantId, type);
    if (preference != null) {
      preference.setDigestEnabled(digestEnabled);
      preference.setDigestSchedule(schedule);
      preferenceRepository.save(preference);
    }
  }

  @Override
  public void updateQuietTime(UUID userId, UUID tenantId, String startTime, String endTime) {
    List<NotificationPreference> preferences = getUserPreferences(userId, tenantId);
    LocalTime start = LocalTime.parse(startTime);
    LocalTime end = LocalTime.parse(endTime);

    preferences.forEach(
        pref -> {
          pref.setQuietTimeStart(LocalDateTime.now().with(start));
          pref.setQuietTimeEnd(LocalDateTime.now().with(end));
        });

    preferenceRepository.saveAll(preferences);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isNotificationEnabled(
      UUID userId, UUID tenantId, NotificationType type, NotificationChannel channel) {
    NotificationPreference preference = getPreference(userId, tenantId, type);

    if (preference == null || !preference.isEnabled()) {
      return false;
    }

    // Check quiet time
    LocalDateTime now = LocalDateTime.now();
    if (preference.getQuietTimeStart() != null && preference.getQuietTimeEnd() != null) {
      LocalTime currentTime = now.toLocalTime();
      LocalTime quietStart = preference.getQuietTimeStart().toLocalTime();
      LocalTime quietEnd = preference.getQuietTimeEnd().toLocalTime();

      if (currentTime.isAfter(quietStart) && currentTime.isBefore(quietEnd)) {
        return false;
      }
    }

    return preference.getEnabledChannels().contains(channel);
  }

  @Override
  public void deleteUserPreferences(UUID userId, UUID tenantId) {
    preferenceRepository.deleteByUserIdAndTenantId(userId, tenantId);
  }
}
