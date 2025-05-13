package com.twelvenexus.oneplan.integration.repository;

import com.twelvenexus.oneplan.integration.document.WebhookEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WebhookEventRepository extends MongoRepository<WebhookEvent, String> {
    
    List<WebhookEvent> findByConnectionIdOrderByReceivedAtDesc(String connectionId);
    
    List<WebhookEvent> findByStatusOrderByReceivedAtAsc(WebhookEvent.EventStatus status);
    
    List<WebhookEvent> findByConnectionIdAndEventType(String connectionId, String eventType);
    
    List<WebhookEvent> findByReceivedAtBetween(LocalDateTime start, LocalDateTime end);
}
