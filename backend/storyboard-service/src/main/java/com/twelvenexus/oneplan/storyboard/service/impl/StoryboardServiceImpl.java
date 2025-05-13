package com.twelvenexus.oneplan.storyboard.service.impl;

import com.twelvenexus.oneplan.storyboard.document.StoryboardCanvas;
import com.twelvenexus.oneplan.storyboard.exception.InvalidShareTokenException;
import com.twelvenexus.oneplan.storyboard.exception.StoryCardNotFoundException;
import com.twelvenexus.oneplan.storyboard.exception.StoryboardNotFoundException;
import com.twelvenexus.oneplan.storyboard.model.Storyboard;
import com.twelvenexus.oneplan.storyboard.model.StoryCard;
import com.twelvenexus.oneplan.storyboard.model.StoryRelationship;
import com.twelvenexus.oneplan.storyboard.repository.StoryboardCanvasRepository;
import com.twelvenexus.oneplan.storyboard.repository.StoryboardRepository;
import com.twelvenexus.oneplan.storyboard.repository.StoryCardRepository;
import com.twelvenexus.oneplan.storyboard.repository.StoryRelationshipRepository;
import com.twelvenexus.oneplan.storyboard.service.StoryboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StoryboardServiceImpl implements StoryboardService {

    private final StoryboardRepository storyboardRepository;
    private final StoryCardRepository storyCardRepository;
    private final StoryRelationshipRepository relationshipRepository;
    private final StoryboardCanvasRepository canvasRepository;

    @Override
    public Storyboard createStoryboard(Storyboard storyboard) {
        return storyboardRepository.save(storyboard);
    }

    @Override
    public Storyboard updateStoryboard(String id, Storyboard storyboard) {
        Storyboard existing = getStoryboard(id);
        existing.setTitle(storyboard.getTitle());
        existing.setDescription(storyboard.getDescription());
        existing.setStatus(storyboard.getStatus());
        existing.setVisibility(storyboard.getVisibility());
        existing.setVersion(existing.getVersion() + 1);
        return storyboardRepository.save(existing);
    }

    @Override
    public Storyboard getStoryboard(String id) {
        return storyboardRepository.findById(id)
                .orElseThrow(() -> new StoryboardNotFoundException("Storyboard with id " + id + " not found"));
    }

    @Override
    public List<Storyboard> getStoryboardsByProject(String projectId) {
        return storyboardRepository.findByProjectId(projectId);
    }

    @Override
    public void deleteStoryboard(String id) {
        // Verify storyboard exists
        getStoryboard(id);

        // Delete canvas data
        canvasRepository.deleteByStoryboardId(id);

        // Delete relationships
        List<StoryRelationship> relationships = relationshipRepository.findByStoryboardId(id);
        relationshipRepository.deleteAll(relationships);

        // Delete story cards
        List<StoryCard> cards = storyCardRepository.findByStoryboardId(id);
        storyCardRepository.deleteAll(cards);

        // Delete storyboard
        storyboardRepository.deleteById(id);
    }

    @Override
    public StoryCard createStoryCard(StoryCard storyCard) {
        // Verify storyboard exists
        getStoryboard(storyCard.getStoryboardId());
        return storyCardRepository.save(storyCard);
    }

    @Override
    public StoryCard updateStoryCard(String id, StoryCard storyCard) {
        StoryCard existing = getStoryCard(id);
        existing.setTitle(storyCard.getTitle());
        existing.setDescription(storyCard.getDescription());
        existing.setAcceptanceCriteria(storyCard.getAcceptanceCriteria());
        existing.setStoryPoints(storyCard.getStoryPoints());
        existing.setPriority(storyCard.getPriority());
        existing.setStatus(storyCard.getStatus());
        existing.setPositionX(storyCard.getPositionX());
        existing.setPositionY(storyCard.getPositionY());
        existing.setWidth(storyCard.getWidth());
        existing.setHeight(storyCard.getHeight());
        existing.setColor(storyCard.getColor());
        return storyCardRepository.save(existing);
    }

    @Override
    public StoryCard getStoryCard(String id) {
        return storyCardRepository.findById(id)
                .orElseThrow(() -> new StoryCardNotFoundException("Story card with id " + id + " not found"));
    }

    @Override
    public List<StoryCard> getStoryCardsByStoryboard(String storyboardId) {
        // Verify storyboard exists
        getStoryboard(storyboardId);
        return storyCardRepository.findByStoryboardIdOrderByPositionYAscPositionXAsc(storyboardId);
    }

    @Override
    public void deleteStoryCard(String id) {
        // Verify card exists
        getStoryCard(id);

        // Delete relationships
        relationshipRepository.deleteByFromStoryIdOrToStoryId(id, id);

        // Delete card
        storyCardRepository.deleteById(id);
    }

    @Override
    public StoryRelationship createRelationship(StoryRelationship relationship) {
        // Verify stories exist
        getStoryCard(relationship.getFromStoryId());
        getStoryCard(relationship.getToStoryId());

        return relationshipRepository.save(relationship);
    }

    @Override
    public List<StoryRelationship> getRelationshipsByStoryboard(String storyboardId) {
        // Verify storyboard exists
        getStoryboard(storyboardId);
        return relationshipRepository.findByStoryboardId(storyboardId);
    }

    @Override
    public void deleteRelationship(String id) {
        relationshipRepository.deleteById(id);
    }

    @Override
    public StoryboardCanvas saveCanvas(String storyboardId, StoryboardCanvas canvas) {
        // Verify storyboard exists
        getStoryboard(storyboardId);

        canvas.setStoryboardId(storyboardId);
        canvas.setLastModified(LocalDateTime.now());
        return canvasRepository.save(canvas);
    }

    @Override
    public StoryboardCanvas getCanvas(String storyboardId) {
        // Verify storyboard exists
        getStoryboard(storyboardId);

        return canvasRepository.findByStoryboardId(storyboardId)
                .orElse(null);
    }

    @Override
    public Storyboard shareStoryboard(String id, boolean passwordProtected, String password, Integer expiryDays) {
        Storyboard storyboard = getStoryboard(id);
        storyboard.setShareToken(UUID.randomUUID().toString());
        storyboard.setIsPasswordProtected(passwordProtected);

        if (passwordProtected && password != null) {
            // TODO: Hash password before storing (use BCrypt)
            storyboard.setSharePassword(password);
        }

        if (expiryDays != null) {
            storyboard.setShareExpiresAt(LocalDateTime.now().plusDays(expiryDays));
        }

        return storyboardRepository.save(storyboard);
    }

    @Override
    public Storyboard getSharedStoryboard(String shareToken, String password) {
        Storyboard storyboard = storyboardRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new InvalidShareTokenException("Invalid share token"));

        // Check expiry
        if (storyboard.getShareExpiresAt() != null &&
                storyboard.getShareExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidShareTokenException("Share link has expired");
        }

        // Check password
        if (storyboard.getIsPasswordProtected() && !storyboard.getSharePassword().equals(password)) {
            throw new InvalidShareTokenException("Invalid password");
        }

        return storyboard;
    }

    @Override
    public void revokeShare(String id) {
        Storyboard storyboard = getStoryboard(id);
        storyboard.setShareToken(null);
        storyboard.setSharePassword(null);
        storyboard.setShareExpiresAt(null);
        storyboard.setIsPasswordProtected(false);
        storyboardRepository.save(storyboard);
    }

    @Override
    public List<StoryCard> generateStoriesFromRequirements(String storyboardId, List<String> requirementIds) {
        // TODO: Implement AI integration for story generation
        // This would:
        // 1. Fetch requirements from requirement service
        // 2. Send to AI service for story generation
        // 3. Create story cards from AI response
        // 4. Return created cards

        return List.of();
    }
}
