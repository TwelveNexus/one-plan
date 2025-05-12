package com.twelvenexus.oneplan.organization.dto;

import com.twelvenexus.oneplan.organization.model.Team;
import jakarta.validation.constraints.NotBlank;
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
public class TeamRequest {
    @NotNull(message = "Organization ID is required")
    private UUID organizationId;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    private String avatar;
    
    @NotNull(message = "Visibility is required")
    private Team.TeamVisibility visibility;
    
    private String settings;
    private String metadata;
}
