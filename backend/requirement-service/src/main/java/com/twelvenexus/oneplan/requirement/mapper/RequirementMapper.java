package com.twelvenexus.oneplan.requirement.mapper;

import com.twelvenexus.oneplan.requirement.dto.CreateRequirementDto;
import com.twelvenexus.oneplan.requirement.dto.RequirementResponseDto;
import com.twelvenexus.oneplan.requirement.dto.UpdateRequirementDto;
import com.twelvenexus.oneplan.requirement.model.Requirement;
import org.springframework.stereotype.Component;

@Component
public class RequirementMapper {
    
    public Requirement toEntity(CreateRequirementDto dto) {
        return Requirement.builder()
                .projectId(dto.getProjectId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .format(dto.getFormat())
                .status(dto.getStatus())
                .priority(dto.getPriority())
                .category(dto.getCategory())
                .tags(dto.getTags())
                .createdBy(dto.getCreatedBy())
                .build();
    }
    
    public RequirementResponseDto toDto(Requirement entity) {
        RequirementResponseDto dto = new RequirementResponseDto();
        dto.setId(entity.getId());
        dto.setProjectId(entity.getProjectId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setFormat(entity.getFormat());
        dto.setStatus(entity.getStatus());
        dto.setPriority(entity.getPriority());
        dto.setCategory(entity.getCategory());
        dto.setTags(entity.getTags());
        dto.setAiScore(entity.getAiScore());
        dto.setAiSuggestions(entity.getAiSuggestions());
        dto.setVersion(entity.getVersion());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
    
    public void updateEntity(Requirement entity, UpdateRequirementDto dto) {
        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getPriority() != null) {
            entity.setPriority(dto.getPriority());
        }
        if (dto.getCategory() != null) {
            entity.setCategory(dto.getCategory());
        }
        if (dto.getTags() != null) {
            entity.setTags(dto.getTags());
        }
    }
}
