package com.twelvenexus.oneplan.organization.dto;

import com.twelvenexus.oneplan.organization.model.TeamMember;
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
public class TeamMemberResponse {
    private UUID id;
    private UUID teamId;
    private UUID userId;
    private TeamMember.TeamMemberRole role;
    private String permissions;
    private LocalDateTime joinedAt;
    private UUID invitedBy;
    private TeamMember.TeamMemberStatus status;
    
    public static TeamMemberResponse fromEntity(TeamMember teamMember) {
        return TeamMemberResponse.builder()
                .id(teamMember.getId())
                .teamId(teamMember.getTeamId())
                .userId(teamMember.getUserId())
                .role(teamMember.getRole())
                .permissions(teamMember.getPermissions())
                .joinedAt(teamMember.getJoinedAt())
                .invitedBy(teamMember.getInvitedBy())
                .status(teamMember.getStatus())
                .build();
    }
}
