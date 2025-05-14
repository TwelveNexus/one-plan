package com.twelvenexus.oneplan.subscription.repository;

import com.twelvenexus.oneplan.subscription.model.Invoice;
import com.twelvenexus.oneplan.subscription.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Page<Invoice> findByTenantId(UUID tenantId, Pageable pageable);

    List<Invoice> findByTenantIdAndStatus(UUID tenantId, InvoiceStatus status);

    @Query("SELECT i FROM Invoice i WHERE i.subscription.id = :subscriptionId ORDER BY i.invoiceDate DESC")
    List<Invoice> findBySubscriptionId(@Param("subscriptionId") UUID subscriptionId);

    @Query("SELECT i FROM Invoice i WHERE i.status = 'ISSUED' AND i.dueDate < :date")
    List<Invoice> findOverdueInvoices(@Param("date") LocalDate date);

    @Query("SELECT i FROM Invoice i WHERE i.tenantId = :tenantId AND i.invoiceDate BETWEEN :startDate AND :endDate")
    List<Invoice> findByDateRange(@Param("tenantId") UUID tenantId,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.tenantId = :tenantId AND i.status = :status")
    Long countByTenantIdAndStatus(@Param("tenantId") UUID tenantId, @Param("status") InvoiceStatus status);

    @Query("SELECT MAX(i.invoiceNumber) FROM Invoice i WHERE i.invoiceNumber LIKE :prefix%")
    String findLastInvoiceNumber(@Param("prefix") String prefix);
}
