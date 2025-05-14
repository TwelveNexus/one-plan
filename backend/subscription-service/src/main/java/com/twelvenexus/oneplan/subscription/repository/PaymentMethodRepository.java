package com.twelvenexus.oneplan.subscription.repository;

import com.twelvenexus.oneplan.subscription.model.PaymentMethod;
import com.twelvenexus.oneplan.subscription.enums.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {

    List<PaymentMethod> findByTenantIdAndActiveTrue(UUID tenantId);

    Optional<PaymentMethod> findByTenantIdAndIsDefaultTrue(UUID tenantId);

    List<PaymentMethod> findByTenantIdAndGateway(UUID tenantId, PaymentGateway gateway);

    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.tenantId = :tenantId AND pm.tokenId = :tokenId")
    Optional<PaymentMethod> findByTenantIdAndTokenId(@Param("tenantId") UUID tenantId,
                                                    @Param("tokenId") String tokenId);

    @Query("UPDATE PaymentMethod pm SET pm.isDefault = false WHERE pm.tenantId = :tenantId")
    void resetDefaultPaymentMethod(@Param("tenantId") UUID tenantId);
}
