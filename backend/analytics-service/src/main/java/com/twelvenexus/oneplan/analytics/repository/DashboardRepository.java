package com.twelvenexus.oneplan.analytics.repository;

import com.twelvenexus.oneplan.analytics.model.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {

    List<Dashboard> findByTenantIdAndOwnerId(UUID tenantId, UUID ownerId);

    List<Dashboard> findByTenantIdAndIsPublicTrue(UUID tenantId);

    Optional<Dashboard> findByTenantIdAndIsDefaultTrue(UUID tenantId);

    @Query("SELECT d FROM Dashboard d LEFT JOIN FETCH d.widgets " +
           "WHERE d.id = :dashboardId AND d.tenantId = :tenantId")
    Optional<Dashboard> findByIdWithWidgets(UUID dashboardId, UUID tenantId);

    @Query("SELECT d FROM Dashboard d WHERE d.tenantId = :tenantId " +
           "AND (d.ownerId = :userId OR d.isPublic = true)")
    List<Dashboard> findAccessibleDashboards(UUID tenantId, UUID userId);
}
