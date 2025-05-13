package com.twelvenexus.oneplan.analytics.repository;

import com.twelvenexus.oneplan.analytics.model.Report;
import com.twelvenexus.oneplan.analytics.enums.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    List<Report> findByTenantIdAndActiveTrue(UUID tenantId);

    List<Report> findByTenantIdAndType(UUID tenantId, ReportType type);

    List<Report> findByTenantIdAndCreatedBy(UUID tenantId, UUID userId);

    @Query("SELECT r FROM Report r WHERE r.active = true " +
           "AND r.schedule IS NOT NULL AND r.nextRunAt <= :now")
    List<Report> findScheduledReportsToRun(LocalDateTime now);

    List<Report> findByTenantIdAndNameContainingIgnoreCase(UUID tenantId, String searchTerm);
}
