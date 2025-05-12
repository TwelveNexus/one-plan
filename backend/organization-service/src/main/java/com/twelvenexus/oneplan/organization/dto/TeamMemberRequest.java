package com.twelvenexus.oneplan.organization.dto;

import com.twelvenexus.oneplan.organization.model.TeamMember;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberRequest {
    @NotNull(message = "Team ID is required")
    private UUID teamId;
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Role is required")
    private TeamMember.TeamMemberRole role;
    
    private String permissions;
    private UUID invitedBy;
}
