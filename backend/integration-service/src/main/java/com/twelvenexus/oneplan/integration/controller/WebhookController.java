package com.twelvenexus.oneplan.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twelvenexus.oneplan.integration.document.WebhookEvent;
import com.twelvenexus.oneplan.integration.service.GitProviderService;
import com.twelvenexus.oneplan.integration.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Webhook handling endpoints")
@Slf4j
public class WebhookController {
    
    private final WebhookService webhookService;
    private final GitProviderService gitProviderService;
    private final ObjectMapper objectMapper;
    
    @PostMapping("/{provider}/{connectionId}")
    @Operation(summary = "Receive webhook from provider")
    public ResponseEntity<Void> receiveWebhook(
            @PathVariable String provider,
            @PathVariable String connectionId,
            @RequestBody String payload,
            HttpServletRequest request) {
        
        try {
            // Extract headers
            Map<String, String> headers = new HashMap<>();
            request.getHeaderNames().asIterator().forEachRemaining(name ->
                    headers.put(name, request.getHeader(name)));
            
            // Log webhook receipt
            log.info("Received webhook from {} for connection {}", provider, connectionId);
            
            // Verify signature
            if (!webhookService.verifyWebhookSignature(provider, headers, payload)) {
                log.warn("Invalid webhook signature from {} for connection {}", provider, connectionId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Parse payload
            Map<String, Object> parsedPayload = objectMapper.readValue(payload, Map.class);
            
            // Determine event type
            String eventType = extractEventType(provider, headers, parsedPayload);
            
            // Create webhook event for processing
            webhookService.createEvent(connectionId, eventType, parsedPayload, headers);
            
            return ResponseEntity.ok().build();
            
        } catch (IOException e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping("/events")
    @Operation(summary = "Get webhook events")
    public ResponseEntity<List<WebhookEvent>> getEvents(
            @RequestParam(required = false) String connectionId,
            @RequestParam(required = false) String status) {
        
        List<WebhookEvent> events;
        if (connectionId != null) {
            events = webhookService.getEventsByConnection(connectionId);
        } else if ("pending".equals(status)) {
            events = webhookService.getPendingEvents();
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(events);
    }
    
    @PostMapping("/events/{eventId}/process")
    @Operation(summary = "Process a webhook event")
    public ResponseEntity<WebhookEvent> processEvent(@PathVariable String eventId) {
        WebhookEvent processedEvent = webhookService.processEvent(eventId);
        return ResponseEntity.ok(processedEvent);
    }
    
    private String extractEventType(String provider, Map<String, String> headers, Map<String, Object> payload) {
        switch (provider) {
            case "github":
                return headers.getOrDefault("X-GitHub-Event", "unknown");
            case "gitlab":
                return headers.getOrDefault("X-Gitlab-Event", "unknown");
            case "bitbucket":
                return headers.getOrDefault("X-Event-Key", "unknown");
            default:
                return "unknown";
        }
    }
}
