package com.twelvenexus.oneplan.integration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateConnectionDto {
    @NotNull(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Project ID is required")
    private String projectId;
    
    @NotBlank(message = "Provider is required")
    private String provider;
    
    @NotBlank(message = "Repository name is required")
    private String repositoryName;
    
    private String repositoryFullName;
    private String repositoryUrl;
    private String defaultBranch = "main";
    private String accessToken;
}
