package com.twelvenexus.oneplan.storyboard.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "story_cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryCard {
    @Id
    @Column(name = "id", length = 36)
    private String id = UUID.randomUUID().toString();
    
    @Column(name = "storyboard_id", nullable = false, length = 36)
    private String storyboardId;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "acceptance_criteria", columnDefinition = "TEXT")
    private String acceptanceCriteria;
    
    @Column(name = "story_points")
    private Integer storyPoints;
    
    @Column(name = "priority", length = 20)
    private String priority = "medium";
    
    @Column(name = "position_x")
    private Integer positionX = 0;
    
    @Column(name = "position_y")
    private Integer positionY = 0;
    
    @Column(name = "width")
    private Integer width = 200;
    
    @Column(name = "height")
    private Integer height = 150;
    
    @Column(name = "color", length = 7)
    private String color = "#FFFFFF";
    
    @Column(name = "status", length = 50)
    private String status = "todo";
    
    @Column(name = "requirement_id", length = 36)
    private String requirementId;
    
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
