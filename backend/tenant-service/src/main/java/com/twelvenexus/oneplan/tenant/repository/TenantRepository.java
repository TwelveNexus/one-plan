package com.twelvenexus.oneplan.tenant.repository;

import com.twelvenexus.oneplan.tenant.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findByDomain(String domain);
    boolean existsByDomain(String domain);
}
