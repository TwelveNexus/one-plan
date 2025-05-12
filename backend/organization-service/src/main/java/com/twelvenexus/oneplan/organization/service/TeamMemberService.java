package com.twelvenexus.oneplan.organization.service;

import java.util.List;
import java.util.UUID;

import com.twelvenexus.oneplan.organization.dto.TeamMemberRequest;
import com.twelvenexus.oneplan.organization.dto.TeamMemberResponse;
import com.twelvenexus.oneplan.organization.model.TeamMember;

public interface TeamMemberService {
    TeamMemberResponse addMemberToTeam(TeamMemberRequest request);
    TeamMemberResponse getMemberById(UUID id);
    List<TeamMemberResponse> getMembersByTeamId(UUID teamId);
    List<TeamMemberResponse> getMembersByUserId(UUID userId);
    TeamMemberResponse updateMemberRole(UUID id, TeamMember.TeamMemberRole role);
    TeamMemberResponse updateMemberStatus(UUID id, TeamMember.TeamMemberStatus status);
    void removeMemberFromTeam(UUID id);
}
