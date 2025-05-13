package com.twelvenexus.oneplan.integration.service;

import com.twelvenexus.oneplan.integration.document.WebhookEvent;

import java.util.List;
import java.util.Map;

public interface WebhookService {
    
    WebhookEvent createEvent(String connectionId, String eventType, Map<String, Object> payload, Map<String, String> headers);
    WebhookEvent processEvent(String eventId);
    List<WebhookEvent> getPendingEvents();
    List<WebhookEvent> getEventsByConnection(String connectionId);
    boolean verifyWebhookSignature(String provider, Map<String, String> headers, String payload);
}
