package com.twelvenexus.oneplan.organization.service;

import com.twelvenexus.oneplan.organization.dto.TeamRequest;
import com.twelvenexus.oneplan.organization.dto.TeamResponse;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    TeamResponse createTeam(TeamRequest request);
    TeamResponse getTeamById(UUID id);
    List<TeamResponse> getTeamsByOrganizationId(UUID organizationId);
    TeamResponse updateTeam(UUID id, TeamRequest request);
    void deleteTeam(UUID id);
}
