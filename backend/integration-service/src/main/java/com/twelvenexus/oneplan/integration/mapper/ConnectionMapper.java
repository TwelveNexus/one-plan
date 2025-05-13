package com.twelvenexus.oneplan.integration.mapper;

import com.twelvenexus.oneplan.integration.dto.ConnectionResponseDto;
import com.twelvenexus.oneplan.integration.dto.CreateConnectionDto;
import com.twelvenexus.oneplan.integration.model.GitConnection;
import org.springframework.stereotype.Component;

@Component
public class ConnectionMapper {
    
    public GitConnection toEntity(CreateConnectionDto dto) {
        return GitConnection.builder()
                .userId(dto.getUserId())
                .projectId(dto.getProjectId())
                .provider(dto.getProvider())
                .repositoryName(dto.getRepositoryName())
                .repositoryFullName(dto.getRepositoryFullName())
                .repositoryUrl(dto.getRepositoryUrl())
                .defaultBranch(dto.getDefaultBranch())
                .accessToken(dto.getAccessToken())
                .build();
    }
    
    public ConnectionResponseDto toDto(GitConnection entity) {
        ConnectionResponseDto dto = new ConnectionResponseDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setProjectId(entity.getProjectId());
        dto.setProvider(entity.getProvider());
        dto.setRepositoryName(entity.getRepositoryName());
        dto.setRepositoryFullName(entity.getRepositoryFullName());
        dto.setRepositoryUrl(entity.getRepositoryUrl());
        dto.setDefaultBranch(entity.getDefaultBranch());
        dto.setIsActive(entity.getIsActive());
        dto.setLastSyncAt(entity.getLastSyncAt());
        dto.setSyncStatus(entity.getSyncStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
