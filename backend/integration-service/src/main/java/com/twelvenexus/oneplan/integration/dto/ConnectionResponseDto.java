package com.twelvenexus.oneplan.integration.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConnectionResponseDto {
    private String id;
    private String userId;
    private String projectId;
    private String provider;
    private String repositoryName;
    private String repositoryFullName;
    private String repositoryUrl;
    private String defaultBranch;
    private Boolean isActive;
    private LocalDateTime lastSyncAt;
    private String syncStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
