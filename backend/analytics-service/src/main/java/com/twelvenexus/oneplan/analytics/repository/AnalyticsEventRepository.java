package com.twelvenexus.oneplan.analytics.repository;

import com.twelvenexus.oneplan.analytics.model.AnalyticsEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsEventRepository extends MongoRepository<AnalyticsEvent, String> {

  List<AnalyticsEvent> findByTenantIdAndEntityIdAndTimestampBetween(
      UUID tenantId, UUID entityId, LocalDateTime start, LocalDateTime end);

  List<AnalyticsEvent> findByProcessedFalseAndTimestampBefore(LocalDateTime cutoff);

  @Query("{ 'tenantId': ?0, 'eventType': ?1, 'timestamp': { $gte: ?2, $lte: ?3 } }")
  List<AnalyticsEvent> findByEventTypeAndDateRange(
      UUID tenantId, String eventType, LocalDateTime start, LocalDateTime end);

  @Query("{ 'tenantId': ?0, 'userId': ?1, 'timestamp': { $gte: ?2 } }")
  List<AnalyticsEvent> findUserEvents(UUID tenantId, UUID userId, LocalDateTime since);

  @Query(value = "{ 'tenantId': ?0, 'entityType': ?1, 'timestamp': { $gte: ?2 } }", count = true)
  Long countEventsByEntityType(UUID tenantId, String entityType, LocalDateTime since);

  void deleteByTimestampBefore(LocalDateTime cutoff);
}
