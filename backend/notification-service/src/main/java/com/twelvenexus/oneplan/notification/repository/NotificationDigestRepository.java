package com.twelvenexus.oneplan.notification.repository;

import com.twelvenexus.oneplan.notification.model.NotificationDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationDigestRepository extends JpaRepository<NotificationDigest, UUID> {

  Optional<NotificationDigest> findByUserIdAndPeriodStartAndPeriodEnd(
      UUID userId, LocalDateTime periodStart, LocalDateTime periodEnd);

  List<NotificationDigest> findBySentFalseAndPeriodEndLessThanEqual(LocalDateTime currentTime);

  @Query(
      "SELECT d FROM NotificationDigest d WHERE d.userId = :userId AND d.tenantId = :tenantId "
          + "AND d.sentAt BETWEEN :startDate AND :endDate")
  List<NotificationDigest> findByUserIdAndSentDateRange(
      UUID userId, UUID tenantId, LocalDateTime startDate, LocalDateTime endDate);
}
