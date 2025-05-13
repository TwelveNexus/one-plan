package com.twelvenexus.oneplan.storyboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateStoryCardDto {
    @NotNull(message = "Storyboard ID is required")
    private String storyboardId;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    private String acceptanceCriteria;
    private Integer storyPoints;
    private String priority = "medium";
    private Integer positionX = 0;
    private Integer positionY = 0;
    private Integer width = 200;
    private Integer height = 150;
    private String color = "#FFFFFF";
    private String status = "todo";
    private String requirementId;
}
