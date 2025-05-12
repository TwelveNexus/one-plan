package com.twelvenexus.oneplan.organization.repository;

import com.twelvenexus.oneplan.organization.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    List<Organization> findByTenantId(UUID tenantId);
    boolean existsByNameAndTenantId(String name, UUID tenantId);
}
