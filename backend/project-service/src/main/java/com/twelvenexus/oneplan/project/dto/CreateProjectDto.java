package com.twelvenexus.oneplan.project.dto;

import com.twelvenexus.oneplan.project.model.ProjectStatus;
import com.twelvenexus.oneplan.project.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateProjectDto {
    @NotNull(message = "Organization ID is required")
    private UUID organizationId;
    
    @NotBlank(message = "Project name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Project key is required")
    @Pattern(regexp = "^[A-Z]{2,10}$", message = "Project key must be 2-10 uppercase letters")
    private String projectKey;
    
    private ProjectVisibility visibility = ProjectVisibility.PRIVATE;
    private LocalDate startDate;
    private LocalDate targetDate;
    private ProjectStatus status = ProjectStatus.PLANNING;
    private String settings;
    private String metadata;
}
