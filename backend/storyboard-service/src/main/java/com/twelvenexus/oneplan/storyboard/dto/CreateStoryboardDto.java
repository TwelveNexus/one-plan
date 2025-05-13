package com.twelvenexus.oneplan.storyboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateStoryboardDto {
    @NotNull(message = "Project ID is required")
    private String projectId;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    private String visibility = "private";
    private String status = "draft";
    private String createdBy;
}
