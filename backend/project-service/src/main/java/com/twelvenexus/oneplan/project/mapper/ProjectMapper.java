package com.twelvenexus.oneplan.project.mapper;

import com.twelvenexus.oneplan.project.dto.CreateProjectDto;
import com.twelvenexus.oneplan.project.dto.ProjectDto;
import com.twelvenexus.oneplan.project.dto.UpdateProjectDto;
import com.twelvenexus.oneplan.project.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {
    
    public ProjectDto toDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setOrganizationId(project.getOrganizationId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setProjectKey(project.getProjectKey());
        dto.setVisibility(project.getVisibility());
        dto.setStartDate(project.getStartDate());
        dto.setTargetDate(project.getTargetDate());
        dto.setStatus(project.getStatus());
        dto.setSettings(project.getSettings());
        dto.setMetadata(project.getMetadata());
        dto.setCreatedBy(project.getCreatedBy());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        return dto;
    }
    
    public Project toEntity(CreateProjectDto dto) {
        Project project = new Project();
        project.setOrganizationId(dto.getOrganizationId());
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setProjectKey(dto.getProjectKey());
        project.setVisibility(dto.getVisibility());
        project.setStartDate(dto.getStartDate());
        project.setTargetDate(dto.getTargetDate());
        project.setStatus(dto.getStatus());
        project.setSettings(dto.getSettings());
        project.setMetadata(dto.getMetadata());
        // project.setCreatedBy(UUID.randomUUID()); // TODO: Set from security context
        return project;
    }
    
    public Project toEntity(UpdateProjectDto dto) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setProjectKey(dto.getProjectKey());
        project.setVisibility(dto.getVisibility());
        project.setStartDate(dto.getStartDate());
        project.setTargetDate(dto.getTargetDate());
        project.setStatus(dto.getStatus());
        project.setSettings(dto.getSettings());
        project.setMetadata(dto.getMetadata());
        return project;
    }
}
