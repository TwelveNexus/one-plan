package com.twelvenexus.oneplan.organization.repository;

import com.twelvenexus.oneplan.organization.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    List<Team> findByOrganizationId(UUID organizationId);
    boolean existsByNameAndOrganizationId(String name, UUID organizationId);
}
