package com.twelvenexus.oneplan.storyboard.repository;

import com.twelvenexus.oneplan.storyboard.model.StoryRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRelationshipRepository extends JpaRepository<StoryRelationship, String> {
    
    List<StoryRelationship> findByStoryboardId(String storyboardId);
    
    List<StoryRelationship> findByFromStoryId(String storyId);
    
    List<StoryRelationship> findByToStoryId(String storyId);
    
    void deleteByFromStoryIdOrToStoryId(String fromStoryId, String toStoryId);
}
