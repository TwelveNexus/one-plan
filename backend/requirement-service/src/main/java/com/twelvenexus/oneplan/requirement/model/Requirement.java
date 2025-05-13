package com.twelvenexus.oneplan.requirement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "requirements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Requirement {
    @Id
    @Column(name = "id", length = 36)
    private String id = UUID.randomUUID().toString();
    
    @Column(name = "project_id", nullable = false, length = 36)
    private String projectId;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "format", length = 20)
    private String format = "text";
    
    @Column(name = "status", length = 50)
    private String status = "draft";
    
    @Column(name = "priority", length = 20)
    private String priority = "medium";
    
    @Column(name = "category", length = 100)
    private String category;
    
    @Column(name = "tags", columnDefinition = "JSON")
    private String tags;
    
    @Column(name = "ai_score")
    private Float aiScore;
    
    @Column(name = "ai_suggestions", columnDefinition = "JSON")
    private String aiSuggestions;
    
    @Column(name = "version")
    private Integer version = 1;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
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
