package com.twelvenexus.oneplan.requirement.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RequirementResponseDto {
    private String id;
    private String projectId;
    private String title;
    private String description;
    private String format;
    private String status;
    private String priority;
    private String category;
    private String tags;
    private Float aiScore;
    private String aiSuggestions;
    private Integer version;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
