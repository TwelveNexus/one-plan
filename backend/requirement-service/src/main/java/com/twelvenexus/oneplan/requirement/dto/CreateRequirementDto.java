package com.twelvenexus.oneplan.requirement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRequirementDto {
    @NotNull(message = "Project ID is required")
    private String projectId;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    private String format = "text";
    private String status = "draft";
    private String priority = "medium";
    private String category;
    private String tags;
    private String createdBy;
}
