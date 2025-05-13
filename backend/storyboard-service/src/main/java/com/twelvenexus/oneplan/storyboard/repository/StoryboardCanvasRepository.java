package com.twelvenexus.oneplan.storyboard.repository;

import com.twelvenexus.oneplan.storyboard.document.StoryboardCanvas;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoryboardCanvasRepository extends MongoRepository<StoryboardCanvas, String> {
    
    Optional<StoryboardCanvas> findByStoryboardId(String storyboardId);
    
    void deleteByStoryboardId(String storyboardId);
}
