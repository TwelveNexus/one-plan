package com.twelvenexus.oneplan.storyboard.service;

import com.twelvenexus.oneplan.storyboard.model.Storyboard;
import com.twelvenexus.oneplan.storyboard.model.StoryCard;
import com.twelvenexus.oneplan.storyboard.model.StoryRelationship;
import com.twelvenexus.oneplan.storyboard.document.StoryboardCanvas;

import java.util.List;

public interface StoryboardService {
    // Storyboard operations
    Storyboard createStoryboard(Storyboard storyboard);
    Storyboard updateStoryboard(String id, Storyboard storyboard);
    Storyboard getStoryboard(String id);
    List<Storyboard> getStoryboardsByProject(String projectId);
    void deleteStoryboard(String id);
    
    // Story card operations
    StoryCard createStoryCard(StoryCard storyCard);
    StoryCard updateStoryCard(String id, StoryCard storyCard);
    StoryCard getStoryCard(String id);
    List<StoryCard> getStoryCardsByStoryboard(String storyboardId);
    void deleteStoryCard(String id);
    
    // Relationship operations
    StoryRelationship createRelationship(StoryRelationship relationship);
    List<StoryRelationship> getRelationshipsByStoryboard(String storyboardId);
    void deleteRelationship(String id);
    
    // Canvas operations
    StoryboardCanvas saveCanvas(String storyboardId, StoryboardCanvas canvas);
    StoryboardCanvas getCanvas(String storyboardId);
    
    // Sharing operations
    Storyboard shareStoryboard(String id, boolean passwordProtected, String password, Integer expiryDays);
    Storyboard getSharedStoryboard(String shareToken, String password);
    void revokeShare(String id);
    
    // AI operations
    List<StoryCard> generateStoriesFromRequirements(String storyboardId, List<String> requirementIds);
}
