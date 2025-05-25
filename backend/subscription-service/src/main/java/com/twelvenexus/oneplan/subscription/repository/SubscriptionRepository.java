package com.twelvenexus.oneplan.subscription.repository;

import com.twelvenexus.oneplan.subscription.enums.SubscriptionStatus;
import com.twelvenexus.oneplan.subscription.model.Subscription;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

  Optional<Subscription> findByTenantId(UUID tenantId);

  List<Subscription> findByStatus(SubscriptionStatus status);

  Page<Subscription> findByStatus(SubscriptionStatus status, Pageable pageable);

  @Query("SELECT s FROM Subscription s WHERE s.status = :status AND s.currentPeriodEnd <= :date")
  List<Subscription> findExpiredSubscriptions(
      @Param("status") SubscriptionStatus status, @Param("date") LocalDateTime date);

  @Query("SELECT s FROM Subscription s WHERE s.status = 'TRIALING' AND s.trialEnd <= :date")
  List<Subscription> findExpiredTrials(@Param("date") LocalDateTime date);

  @Query(
      "SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.currentPeriodEnd <= :date AND s.autoRenew = true")
  List<Subscription> findSubscriptionsForRenewal(@Param("date") LocalDateTime date);

  @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = :status")
  Long countByStatus(@Param("status") SubscriptionStatus status);

  @Query("SELECT s FROM Subscription s WHERE s.tenantId = :tenantId ORDER BY s.createdAt DESC")
  List<Subscription> findByTenantIdOrderByCreatedAtDesc(@Param("tenantId") UUID tenantId);
}
