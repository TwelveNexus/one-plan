package com.twelvenexus.oneplan.storyboard.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StoryboardResponseDto {
    private String id;
    private String projectId;
    private String title;
    private String description;
    private String visibility;
    private String status;
    private String shareToken;
    private Boolean isPasswordProtected;
    private LocalDateTime shareExpiresAt;
    private Integer version;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
