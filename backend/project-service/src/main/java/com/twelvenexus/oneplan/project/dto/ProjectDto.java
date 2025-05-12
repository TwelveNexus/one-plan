package com.twelvenexus.oneplan.project.dto;

import com.twelvenexus.oneplan.project.model.ProjectStatus;
import com.twelvenexus.oneplan.project.model.ProjectVisibility;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProjectDto {
    private UUID id;
    private UUID organizationId;
    private String name;
    private String description;
    private String projectKey;
    private ProjectVisibility visibility;
    private LocalDate startDate;
    private LocalDate targetDate;
    private ProjectStatus status;
    private String settings;
    private String metadata;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
