package com.twelvenexus.oneplan.analytics.repository;

import com.twelvenexus.oneplan.analytics.enums.MetricType;
import com.twelvenexus.oneplan.analytics.model.Metric;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricRepository extends JpaRepository<Metric, UUID> {

  List<Metric> findByTenantIdAndEntityIdAndTypeAndTimestampBetween(
      UUID tenantId, UUID entityId, MetricType type, LocalDateTime start, LocalDateTime end);

  @Query(
      "SELECT m FROM Metric m WHERE m.tenantId = :tenantId "
          + "AND m.entityType = :entityType AND m.type = :type "
          + "AND m.timestamp BETWEEN :start AND :end")
  List<Metric> findByEntityTypeAndDateRange(
      @Param("tenantId") UUID tenantId,
      @Param("entityType") String entityType,
      @Param("type") MetricType type,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  @Query(
      "SELECT m FROM Metric m WHERE m.tenantId = :tenantId "
          + "AND m.type = :type AND m.timestamp > :since "
          + "ORDER BY m.value DESC")
  Page<Metric> findTopPerformers(
      @Param("tenantId") UUID tenantId,
      @Param("type") MetricType type,
      @Param("since") LocalDateTime since,
      Pageable pageable);

  @Query(
      "SELECT COUNT(DISTINCT m.entityId) FROM Metric m "
          + "WHERE m.tenantId = :tenantId AND m.entityType = :entityType "
          + "AND m.timestamp > :since")
  Long countDistinctEntities(
      @Param("tenantId") UUID tenantId,
      @Param("entityType") String entityType,
      @Param("since") LocalDateTime since);

  @Query("DELETE FROM Metric m WHERE m.timestamp < :cutoffDate")
  void deleteOldMetrics(@Param("cutoffDate") LocalDateTime cutoffDate);
}
