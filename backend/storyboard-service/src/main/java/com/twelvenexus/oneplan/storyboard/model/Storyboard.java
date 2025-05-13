package com.twelvenexus.oneplan.storyboard.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "storyboards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Storyboard {
    @Id
    @Column(name = "id", length = 36)
    private String id = UUID.randomUUID().toString();
    
    @Column(name = "project_id", nullable = false, length = 36)
    private String projectId;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "visibility", length = 20)
    private String visibility = "private";
    
    @Column(name = "status", length = 50)
    private String status = "draft";
    
    @Column(name = "share_token", length = 100, unique = true)
    private String shareToken;
    
    @Column(name = "share_password")
    private String sharePassword;
    
    @Column(name = "share_expires_at")
    private LocalDateTime shareExpiresAt;
    
    @Column(name = "is_password_protected")
    private Boolean isPasswordProtected = false;
    
    @Column(name = "version")
    private Integer version = 1;
    
    @Column(name = "created_by", length = 36)
    private String createdBy;
    
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
