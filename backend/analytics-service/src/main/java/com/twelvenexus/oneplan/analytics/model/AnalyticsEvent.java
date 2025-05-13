package com.twelvenexus.oneplan.analytics.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Document(collection = "analytics_events")
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "tenant_entity_time", def = "{'tenantId': 1, 'entityId': 1, 'timestamp': -1}")
public class AnalyticsEvent {

    @Id
    private String id;

    @Indexed
    private UUID tenantId;

    @Indexed
    private UUID entityId;

    @Indexed
    private String entityType;

    @Indexed
    private String eventType;

    private String eventName;

    private Map<String, Object> properties;

    private UUID userId;

    private String sessionId;

    private String userAgent;

    private String ipAddress;

    @Indexed
    private LocalDateTime timestamp;

    private LocalDateTime processedAt;

    private boolean processed = false;
}
