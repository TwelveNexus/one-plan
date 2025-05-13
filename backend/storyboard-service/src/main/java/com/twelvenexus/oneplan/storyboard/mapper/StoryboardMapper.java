package com.twelvenexus.oneplan.storyboard.mapper;

import com.twelvenexus.oneplan.storyboard.dto.*;
import com.twelvenexus.oneplan.storyboard.model.Storyboard;
import com.twelvenexus.oneplan.storyboard.model.StoryCard;
import org.springframework.stereotype.Component;

@Component
public class StoryboardMapper {
    
    public Storyboard toEntity(CreateStoryboardDto dto) {
        return Storyboard.builder()
                .projectId(dto.getProjectId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .visibility(dto.getVisibility())
                .status(dto.getStatus())
                .createdBy(dto.getCreatedBy())
                .build();
    }
    
    public StoryboardResponseDto toDto(Storyboard entity) {
        StoryboardResponseDto dto = new StoryboardResponseDto();
        dto.setId(entity.getId());
        dto.setProjectId(entity.getProjectId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setVisibility(entity.getVisibility());
        dto.setStatus(entity.getStatus());
        dto.setShareToken(entity.getShareToken());
        dto.setIsPasswordProtected(entity.getIsPasswordProtected());
        dto.setShareExpiresAt(entity.getShareExpiresAt());
        dto.setVersion(entity.getVersion());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
    
    public void updateEntity(Storyboard entity, UpdateStoryboardDto dto) {
        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getVisibility() != null) {
            entity.setVisibility(dto.getVisibility());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
    }
    
    public StoryCard toEntity(CreateStoryCardDto dto) {
        return StoryCard.builder()
                .storyboardId(dto.getStoryboardId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .acceptanceCriteria(dto.getAcceptanceCriteria())
                .storyPoints(dto.getStoryPoints())
                .priority(dto.getPriority())
                .positionX(dto.getPositionX())
                .positionY(dto.getPositionY())
                .width(dto.getWidth())
                .height(dto.getHeight())
                .color(dto.getColor())
                .status(dto.getStatus())
                .requirementId(dto.getRequirementId())
                .build();
    }
    
    public StoryCardResponseDto toDto(StoryCard entity) {
        StoryCardResponseDto dto = new StoryCardResponseDto();
        dto.setId(entity.getId());
        dto.setStoryboardId(entity.getStoryboardId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setAcceptanceCriteria(entity.getAcceptanceCriteria());
        dto.setStoryPoints(entity.getStoryPoints());
        dto.setPriority(entity.getPriority());
        dto.setPositionX(entity.getPositionX());
        dto.setPositionY(entity.getPositionY());
        dto.setWidth(entity.getWidth());
        dto.setHeight(entity.getHeight());
        dto.setColor(entity.getColor());
        dto.setStatus(entity.getStatus());
        dto.setRequirementId(entity.getRequirementId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
