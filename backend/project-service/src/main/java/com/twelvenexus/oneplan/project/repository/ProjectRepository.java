package com.twelvenexus.oneplan.project.repository;

import com.twelvenexus.oneplan.project.model.Project;
import com.twelvenexus.oneplan.project.model.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByOrganizationId(UUID organizationId);
    
    Optional<Project> findByOrganizationIdAndProjectKey(UUID organizationId, String projectKey);
    
    List<Project> findByOrganizationIdAndStatus(UUID organizationId, ProjectStatus status);
    
    boolean existsByOrganizationIdAndProjectKey(UUID organizationId, String projectKey);
}
