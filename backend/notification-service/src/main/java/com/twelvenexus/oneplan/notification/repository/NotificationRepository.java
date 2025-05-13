package com.twelvenexus.oneplan.notification.repository;

import com.twelvenexus.oneplan.notification.model.Notification;
import com.twelvenexus.oneplan.notification.enums.NotificationStatus;
import com.twelvenexus.oneplan.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    Page<Notification> findByUserIdAndTenantId(UUID userId, UUID tenantId, Pageable pageable);
    
    Page<Notification> findByUserIdAndTenantIdAndStatus(UUID userId, UUID tenantId, NotificationStatus status, Pageable pageable);
    
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.tenantId = :tenantId AND n.readAt IS NULL")
    Page<Notification> findUnreadByUserIdAndTenantId(UUID userId, UUID tenantId, Pageable pageable);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.tenantId = :tenantId AND n.readAt IS NULL")
    long countUnreadByUserIdAndTenantId(UUID userId, UUID tenantId);
    
    List<Notification> findByStatusAndScheduledAtLessThanEqual(NotificationStatus status, LocalDateTime scheduledTime);
    
    List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, Integer maxRetries);
    
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.tenantId = :tenantId " +
           "AND n.type IN :types AND n.createdAt BETWEEN :startDate AND :endDate")
    List<Notification> findForDigest(UUID userId, UUID tenantId, List<NotificationType> types, 
                                   LocalDateTime startDate, LocalDateTime endDate);
}
