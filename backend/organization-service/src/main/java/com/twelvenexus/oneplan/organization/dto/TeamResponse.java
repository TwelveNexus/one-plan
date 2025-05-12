package com.twelvenexus.oneplan.organization.dto;

import com.twelvenexus.oneplan.organization.model.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {
    private UUID id;
    private UUID organizationId;
    private String name;
    private String description;
    private String avatar;
    private Team.TeamVisibility visibility;
    private String settings;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static TeamResponse fromEntity(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .organizationId(team.getOrganizationId())
                .name(team.getName())
                .description(team.getDescription())
                .avatar(team.getAvatar())
                .visibility(team.getVisibility())
                .settings(team.getSettings())
                .metadata(team.getMetadata())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }
}
