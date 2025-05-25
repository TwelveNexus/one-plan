package com.twelvenexus.oneplan.subscription.repository;

import com.twelvenexus.oneplan.subscription.enums.PaymentStatus;
import com.twelvenexus.oneplan.subscription.model.Payment;
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
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

  Optional<Payment> findByGatewayOrderId(String gatewayOrderId);

  Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);

  List<Payment> findByTenantIdAndStatus(UUID tenantId, PaymentStatus status);

  Page<Payment> findByTenantId(UUID tenantId, Pageable pageable);

  @Query(
      "SELECT p FROM Payment p WHERE p.subscription.id = :subscriptionId ORDER BY p.createdAt DESC")
  List<Payment> findBySubscriptionId(@Param("subscriptionId") UUID subscriptionId);

  @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :cutoffDate")
  List<Payment> findStalePayments(@Param("cutoffDate") LocalDateTime cutoffDate);

  @Query(
      "SELECT p FROM Payment p WHERE p.tenantId = :tenantId AND p.paidAt BETWEEN :startDate AND :endDate")
  List<Payment> findPaymentsByDateRange(
      @Param("tenantId") UUID tenantId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query(
      "SELECT SUM(p.amount) FROM Payment p WHERE p.tenantId = :tenantId AND p.status = 'COMPLETED' AND p.paidAt BETWEEN :startDate AND :endDate")
  Double getTotalRevenue(
      @Param("tenantId") UUID tenantId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);
}
