package com.twelvenexus.oneplan.storyboard.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "story_relationships")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryRelationship {
    @Id
    @Column(name = "id", length = 36)
    private String id = UUID.randomUUID().toString();
    
    @Column(name = "storyboard_id", nullable = false, length = 36)
    private String storyboardId;
    
    @Column(name = "from_story_id", nullable = false, length = 36)
    private String fromStoryId;
    
    @Column(name = "to_story_id", nullable = false, length = 36)
    private String toStoryId;
    
    @Column(name = "relationship_type", length = 50)
    private String relationshipType = "depends_on";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    public void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
    }
}
