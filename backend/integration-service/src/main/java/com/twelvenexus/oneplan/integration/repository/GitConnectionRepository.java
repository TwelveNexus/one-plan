package com.twelvenexus.oneplan.integration.repository;

import com.twelvenexus.oneplan.integration.model.GitConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GitConnectionRepository extends JpaRepository<GitConnection, String> {
    
    List<GitConnection> findByProjectId(String projectId);
    
    List<GitConnection> findByUserId(String userId);
    
    Optional<GitConnection> findByProjectIdAndProviderAndRepositoryFullName(
            String projectId, String provider, String repositoryFullName);
    
    List<GitConnection> findByIsActiveTrue();
    
    Optional<GitConnection> findByWebhookId(String webhookId);
}
