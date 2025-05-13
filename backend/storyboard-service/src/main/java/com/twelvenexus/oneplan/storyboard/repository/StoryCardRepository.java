package com.twelvenexus.oneplan.storyboard.repository;

import com.twelvenexus.oneplan.storyboard.model.StoryCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryCardRepository extends JpaRepository<StoryCard, String> {
    
    List<StoryCard> findByStoryboardId(String storyboardId);
    
    List<StoryCard> findByStoryboardIdOrderByPositionYAscPositionXAsc(String storyboardId);
    
    List<StoryCard> findByRequirementId(String requirementId);
}
