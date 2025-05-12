package com.twelvenexus.oneplan.project.service.impl;

import com.twelvenexus.oneplan.project.model.Project;
import com.twelvenexus.oneplan.project.repository.ProjectRepository;
import com.twelvenexus.oneplan.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {
    
    private final ProjectRepository projectRepository;
    
    @Override
    public Project createProject(Project project) {
        // Validate project key uniqueness
        if (projectRepository.existsByOrganizationIdAndProjectKey(
                project.getOrganizationId(), project.getProjectKey())) {
            throw new RuntimeException("Project key already exists for this organization");
        }
        return projectRepository.save(project);
    }
    
    @Override
    public Project getProject(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }
    
    @Override
    public Project getProjectByKey(UUID organizationId, String projectKey) {
        return projectRepository.findByOrganizationIdAndProjectKey(organizationId, projectKey)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }
    
    @Override
    public List<Project> getProjectsByOrganization(UUID organizationId) {
        return projectRepository.findByOrganizationId(organizationId);
    }
    
    @Override
    public Project updateProject(UUID id, Project project) {
        Project existing = getProject(id);
        // Update fields except id and creation info
        existing.setName(project.getName());
        existing.setDescription(project.getDescription());
        existing.setProjectKey(project.getProjectKey());
        existing.setVisibility(project.getVisibility());
        existing.setStartDate(project.getStartDate());
        existing.setTargetDate(project.getTargetDate());
        existing.setStatus(project.getStatus());
        existing.setSettings(project.getSettings());
        existing.setMetadata(project.getMetadata());
        return projectRepository.save(existing);
    }
    
    @Override
    public void deleteProject(UUID id) {
        projectRepository.deleteById(id);
    }
}
