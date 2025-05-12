package com.twelvenexus.oneplan.project.service;

import com.twelvenexus.oneplan.project.model.Project;
import java.util.List;
import java.util.UUID;

public interface ProjectService {
    Project createProject(Project project);
    Project getProject(UUID id);
    Project getProjectByKey(UUID organizationId, String projectKey);
    List<Project> getProjectsByOrganization(UUID organizationId);
    Project updateProject(UUID id, Project project);
    void deleteProject(UUID id);
}
