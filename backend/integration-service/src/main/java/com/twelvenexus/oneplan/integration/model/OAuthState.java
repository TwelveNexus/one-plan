package com.twelvenexus.oneplan.integration.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "oauth_states")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthState {
    @Id
    @Column(name = "id", length = 36)
    private String id = UUID.randomUUID().toString();
    
    @Column(name = "state", unique = true, nullable = false)
    private String state;
    
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;
    
    @Column(name = "project_id", length = 36)
    private String projectId;
    
    @Column(name = "provider", nullable = false, length = 20)
    private String provider;
    
    @Column(name = "redirect_url", length = 500)
    private String redirectUrl;
    
    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    public void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        if (state == null) state = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
        if (expiresAt == null) expiresAt = LocalDateTime.now().plusMinutes(10);
    }
}
