package com.twelvenexus.oneplan.project.dto;

import com.twelvenexus.oneplan.project.model.ProjectStatus;
import com.twelvenexus.oneplan.project.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProjectDto {
    @NotBlank(message = "Project name is required")
    private String name;
    private String description;
    private String projectKey;
    private ProjectVisibility visibility;
    private LocalDate startDate;
    private LocalDate targetDate;
    private ProjectStatus status;
    private String settings;
    private String metadata;
}
