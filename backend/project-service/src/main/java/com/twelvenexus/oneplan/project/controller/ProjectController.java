package com.twelvenexus.oneplan.project.controller;

import com.twelvenexus.oneplan.project.dto.CreateProjectDto;
import com.twelvenexus.oneplan.project.dto.ProjectDto;
import com.twelvenexus.oneplan.project.dto.UpdateProjectDto;
import com.twelvenexus.oneplan.project.mapper.ProjectMapper;
import com.twelvenexus.oneplan.project.model.Project;
import com.twelvenexus.oneplan.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto createProject(@Valid @RequestBody CreateProjectDto createDto) {
        Project project = projectMapper.toEntity(createDto);
        Project created = projectService.createProject(project);
        return projectMapper.toDto(created);
    }
    
    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable UUID id) {
        Project project = projectService.getProject(id);
        return projectMapper.toDto(project);
    }
    
    @GetMapping("/organization/{organizationId}")
    public List<ProjectDto> getProjectsByOrganization(@PathVariable UUID organizationId) {
        return projectService.getProjectsByOrganization(organizationId)
                .stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/organization/{organizationId}/key/{projectKey}")
    public ProjectDto getProjectByKey(@PathVariable UUID organizationId, 
                                     @PathVariable String projectKey) {
        Project project = projectService.getProjectByKey(organizationId, projectKey);
        return projectMapper.toDto(project);
    }
    
    @PutMapping("/{id}")
    public ProjectDto updateProject(@PathVariable UUID id, 
                                    @Valid @RequestBody UpdateProjectDto updateDto) {
        Project project = projectMapper.toEntity(updateDto);
        Project updated = projectService.updateProject(id, project);
        return projectMapper.toDto(updated);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
    }
}
