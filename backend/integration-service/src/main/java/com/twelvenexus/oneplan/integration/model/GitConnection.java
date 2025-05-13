package com.twelvenexus.oneplan.integration.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "git_connections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitConnection {
    @Id
    @Column(name = "id", length = 36)
    private String id = UUID.randomUUID().toString();
    
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;
    
    @Column(name = "project_id", nullable = false, length = 36)
    private String projectId;
    
    @Column(name = "provider", nullable = false, length = 20)
    private String provider; // github, gitlab, bitbucket
    
    @Column(name = "repository_name", nullable = false)
    private String repositoryName;
    
    @Column(name = "repository_full_name", length = 500)
    private String repositoryFullName;
    
    @Column(name = "repository_url", length = 500)
    private String repositoryUrl;
    
    @Column(name = "default_branch", length = 100)
    private String defaultBranch = "main";
    
    @Column(name = "access_token", columnDefinition = "TEXT")
    private String accessToken;
    
    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;
    
    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;
    
    @Column(name = "webhook_id", length = 100)
    private String webhookId;
    
    @Column(name = "webhook_secret")
    private String webhookSecret;
    
    @Column(name = "webhook_url", length = 500)
    private String webhookUrl;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;
    
    @Column(name = "sync_status", length = 50)
    private String syncStatus = "pending";
    
    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
