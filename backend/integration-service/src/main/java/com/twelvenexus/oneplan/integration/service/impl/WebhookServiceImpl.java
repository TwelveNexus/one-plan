package com.twelvenexus.oneplan.integration.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twelvenexus.oneplan.integration.document.WebhookEvent;
import com.twelvenexus.oneplan.integration.model.GitConnection;
import com.twelvenexus.oneplan.integration.repository.GitConnectionRepository;
import com.twelvenexus.oneplan.integration.repository.WebhookEventRepository;
import com.twelvenexus.oneplan.integration.service.WebhookService;
import com.twelvenexus.oneplan.integration.service.provider.GitProviderStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WebhookServiceImpl implements WebhookService {
    
    private final WebhookEventRepository eventRepository;
    private final GitConnectionRepository connectionRepository;
    private final Map<String, GitProviderStrategy> providerStrategies;
    private final ObjectMapper objectMapper;
    
    @Override
    public WebhookEvent createEvent(String connectionId, String eventType, 
                                  Map<String, Object> payload, Map<String, String> headers) {
        GitConnection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found"));
                
        WebhookEvent event = WebhookEvent.builder()
                .connectionId(connectionId)
                .eventType(eventType)
                .provider(connection.getProvider())
                .payload(payload)
                .headers(headers)
                .receivedAt(LocalDateTime.now())
                .status(WebhookEvent.EventStatus.PENDING)
                .build();
        
        // Extract event ID from payload if available
        if (payload.containsKey("id")) {
            event.setEventId(String.valueOf(payload.get("id")));
        }
        
        return eventRepository.save(event);
    }
    
    @Override
    public WebhookEvent processEvent(String eventId) {
        WebhookEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (event.getStatus() != WebhookEvent.EventStatus.PENDING) {
            log.warn("Event {} already processed with status {}", eventId, event.getStatus());
            return event;
        }
        
        GitConnection connection = connectionRepository.findById(event.getConnectionId())
                .orElseThrow(() -> new RuntimeException("Connection not found"));
        
        try {
            event.setStatus(WebhookEvent.EventStatus.PROCESSING);
            event.setProcessingMetadata(WebhookEvent.ProcessingMetadata.builder()
                    .processingNode(getNodeIdentifier())
                    .build());
            eventRepository.save(event);
            
            // Process based on event type
            switch (event.getEventType()) {
                case "push":
                    processPushEvent(connection, event);
                    break;
                case "pull_request":
                    processPullRequestEvent(connection, event);
                    break;
                case "issues":
                    processIssuesEvent(connection, event);
                    break;
                default:
                    log.warn("Unknown event type: {}", event.getEventType());
                    event.setStatus(WebhookEvent.EventStatus.IGNORED);
            }
            
            event.setStatus(WebhookEvent.EventStatus.PROCESSED);
            event.setProcessedAt(LocalDateTime.now());
            
            // Calculate processing duration
            if (event.getProcessingMetadata() != null) {
                long duration = java.time.Duration.between(
                    event.getReceivedAt(), 
                    event.getProcessedAt()
                ).toMillis();
                event.getProcessingMetadata().setProcessingDurationMs(duration);
            }
            
        } catch (Exception e) {
            log.error("Error processing webhook event {}", eventId, e);
            event.setStatus(WebhookEvent.EventStatus.FAILED);
            event.setErrorMessage(e.getMessage());
            
            // Increment retry count
            if (event.getProcessingMetadata() == null) {
                event.setProcessingMetadata(WebhookEvent.ProcessingMetadata.builder().build());
            }
            
            Integer retryCount = event.getProcessingMetadata().getRetryCount();
            event.getProcessingMetadata().setRetryCount(retryCount != null ? retryCount + 1 : 1);
            event.getProcessingMetadata().setLastRetryAt(LocalDateTime.now());
        }
        
        return eventRepository.save(event);
    }
    
    @Override
    public List<WebhookEvent> getPendingEvents() {
        return eventRepository.findByStatusOrderByReceivedAtAsc(WebhookEvent.EventStatus.PENDING);
    }
    
    @Override
    public List<WebhookEvent> getEventsByConnection(String connectionId) {
        return eventRepository.findByConnectionIdOrderByReceivedAtDesc(connectionId);
    }
    
    @Override
    public boolean verifyWebhookSignature(String provider, Map<String, String> headers, String payload) {
        // Find connection by the webhook path or header
        String connectionId = extractConnectionId(provider, headers);
        
        GitConnection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found"));
        
        GitProviderStrategy strategy = getStrategy(provider);
        return strategy.verifyWebhookSignature(headers, payload, connection.getWebhookSecret());
    }
    
    private void processPushEvent(GitConnection connection, WebhookEvent event) {
        Map<String, Object> payload = event.getPayload();
        
        // Extract push information
        String ref = (String) payload.get("ref");
        String branch = ref.substring(ref.lastIndexOf('/') + 1);
        
        List<Map<String, Object>> commits = (List<Map<String, Object>>) payload.get("commits");
        
        // TODO: Process commits and update database
        log.info("Processing push event with {} commits on branch {}", commits.size(), branch);
        
        // Here you would typically:
        // 1. Extract commit information
        // 2. Store commits in the database
        // 3. Link commits to tasks if mentioned in commit messages
        // 4. Trigger notifications
    }
    
    private void processPullRequestEvent(GitConnection connection, WebhookEvent event) {
        Map<String, Object> payload = event.getPayload();
        String action = (String) payload.get("action");
        Map<String, Object> pullRequest = (Map<String, Object>) payload.get("pull_request");
        
        // TODO: Process pull request and update database
        log.info("Processing pull request event: {} for PR #{}", 
            action, pullRequest.get("number"));
        
        // Here you would typically:
        // 1. Extract PR information
        // 2. Store/update PR in the database
        // 3. Link PR to tasks if mentioned
        // 4. Update task status based on PR state
        // 5. Trigger notifications
    }
    
    private void processIssuesEvent(GitConnection connection, WebhookEvent event) {
        Map<String, Object> payload = event.getPayload();
        String action = (String) payload.get("action");
        Map<String, Object> issue = (Map<String, Object>) payload.get("issue");
        
        // TODO: Process issue event
        log.info("Processing issue event: {} for issue #{}", 
            action, issue.get("number"));
        
        // Here you would typically:
        // 1. Extract issue information
        // 2. Create/update tasks based on issues
        // 3. Sync issue state with task state
        // 4. Trigger notifications
    }
    
    private String extractConnectionId(String provider, Map<String, String> headers) {
        // This would typically extract the connection ID from the webhook URL path
        // or from a special header set by the provider
        // For now, we'll use a simple approach
        
        // GitHub sends the webhook URL in the headers
        if ("github".equals(provider)) {
            String deliveryId = headers.get("X-GitHub-Delivery");
            // In a real implementation, you'd parse the URL to get the connection ID
        }
        
        // This is a placeholder - in reality, you'd extract from the URL path
        return headers.getOrDefault("X-Connection-Id", "");
    }
    
    private GitProviderStrategy getStrategy(String provider) {
        GitProviderStrategy strategy = providerStrategies.get(provider + "ProviderStrategy");
        if (strategy == null) {
            throw new RuntimeException("Unsupported provider: " + provider);
        }
        return strategy;
    }
    
    private String getNodeIdentifier() {
        // In a distributed system, this would return a unique identifier for the processing node
        return "node-1";
    }
}
