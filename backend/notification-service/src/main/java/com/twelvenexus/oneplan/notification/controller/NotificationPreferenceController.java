package com.twelvenexus.oneplan.notification.controller;

import com.twelvenexus.oneplan.notification.dto.NotificationPreferenceDto;
import com.twelvenexus.oneplan.notification.dto.UpdatePreferenceDto;
import com.twelvenexus.oneplan.notification.model.NotificationPreference;
import com.twelvenexus.oneplan.notification.service.NotificationPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/preferences")
@RequiredArgsConstructor
@Tag(name = "Notification Preferences", description = "User notification preferences")
public class NotificationPreferenceController {
    
    private final NotificationPreferenceService preferenceService;
    
    @PostMapping
    @Operation(summary = "Create or update notification preference")
    public ResponseEntity<NotificationPreferenceDto> createOrUpdatePreference(
            @RequestParam UUID userId,
            @RequestParam UUID tenantId,
            @Valid @RequestBody UpdatePreferenceDto dto) {
        NotificationPreference preference = preferenceService.createOrUpdatePreference(
            userId,
            tenantId,
            dto.getNotificationType(),
            dto.getEnabledChannels(),
            dto.isEnabled()
        );
        
        return ResponseEntity.ok(toDto(preference));
    }
    
    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user preferences")
    public ResponseEntity<List<NotificationPreferenceDto>> getUserPreferences(
            @PathVariable UUID userId,
            @RequestParam UUID tenantId) {
        List<NotificationPreference> preferences = preferenceService
            .getUserPreferences(userId, tenantId);
        
        return ResponseEntity.ok(
            preferences.stream()
                .map(this::toDto)
                .collect(Collectors.toList())
        );
    }
    
    @PutMapping("/users/{userId}/quiet-time")
    @Operation(summary = "Update quiet time settings")
    public ResponseEntity<Void> updateQuietTime(
            @PathVariable UUID userId,
            @RequestParam UUID tenantId,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        preferenceService.updateQuietTime(userId, tenantId, startTime, endTime);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete user preferences")
    public ResponseEntity<Void> deleteUserPreferences(
            @PathVariable UUID userId,
            @RequestParam UUID tenantId) {
        preferenceService.deleteUserPreferences(userId, tenantId);
        return ResponseEntity.noContent().build();
    }
    
    private NotificationPreferenceDto toDto(NotificationPreference preference) {
        NotificationPreferenceDto dto = new NotificationPreferenceDto();
        dto.setId(preference.getId());
        dto.setUserId(preference.getUserId());
        dto.setTenantId(preference.getTenantId());
        dto.setNotificationType(preference.getNotificationType());
        dto.setEnabledChannels(preference.getEnabledChannels());
        dto.setEnabled(preference.isEnabled());
        dto.setDigestEnabled(preference.isDigestEnabled());
        dto.setDigestSchedule(preference.getDigestSchedule());
        dto.setQuietTimeStart(preference.getQuietTimeStart() != null ? 
            preference.getQuietTimeStart().toLocalTime().toString() : null);
        dto.setQuietTimeEnd(preference.getQuietTimeEnd() != null ? 
            preference.getQuietTimeEnd().toLocalTime().toString() : null);
        return dto;
    }
}
