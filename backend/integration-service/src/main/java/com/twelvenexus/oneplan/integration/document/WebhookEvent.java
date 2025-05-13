package com.twelvenexus.oneplan.integration.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "webhook_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEvent {
    @Id
    private String id;
    
    private String connectionId;
    
    private String eventType; // push, pull_request, issues, etc.
    
    private String eventId; // External event ID from provider
    
    private Map<String, Object> payload; // Full webhook payload
    
    private Map<String, String> headers; // HTTP headers from webhook
    
    private LocalDateTime receivedAt;
    
    private LocalDateTime processedAt;
    
    private EventStatus status;
    
    private String errorMessage;
    
    private ProcessingMetadata processingMetadata;
    
    private String provider; // github, gitlab, bitbucket
    
    public enum EventStatus {
        PENDING,
        PROCESSING,
        PROCESSED,
        FAILED,
        IGNORED
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessingMetadata {
        private Integer retryCount;
        private LocalDateTime lastRetryAt;
        private String processingNode;
        private Long processingDurationMs;
        private Map<String, Object> additionalInfo;
    }
}
