package com.twelvenexus.oneplan.storyboard.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StoryCardResponseDto {
    private String id;
    private String storyboardId;
    private String title;
    private String description;
    private String acceptanceCriteria;
    private Integer storyPoints;
    private String priority;
    private Integer positionX;
    private Integer positionY;
    private Integer width;
    private Integer height;
    private String color;
    private String status;
    private String requirementId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
