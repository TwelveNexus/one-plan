package com.twelvenexus.oneplan.organization.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twelvenexus.oneplan.organization.dto.TeamMemberRequest;
import com.twelvenexus.oneplan.organization.dto.TeamMemberResponse;
import com.twelvenexus.oneplan.organization.exception.ResourceAlreadyExistsException;
import com.twelvenexus.oneplan.organization.exception.ResourceNotFoundException;
import com.twelvenexus.oneplan.organization.model.TeamMember;
import com.twelvenexus.oneplan.organization.repository.TeamMemberRepository;
import com.twelvenexus.oneplan.organization.repository.TeamRepository;
import com.twelvenexus.oneplan.organization.service.TeamMemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public TeamMemberResponse addMemberToTeam(TeamMemberRequest request) {
        // Verify team exists
        if (!teamRepository.existsById(request.getTeamId())) {
            throw new ResourceNotFoundException("Team not found with id: " + request.getTeamId());
        }
        
        // Check if user is already a member of this team
        if (teamMemberRepository.existsByTeamIdAndUserId(request.getTeamId(), request.getUserId())) {
            throw new ResourceAlreadyExistsException("User is already a member of this team");
        }

        TeamMember teamMember = TeamMember.builder()
                .teamId(request.getTeamId())
                .userId(request.getUserId())
                .role(request.getRole())
                .permissions(request.getPermissions())
                .joinedAt(LocalDateTime.now())
                .invitedBy(request.getInvitedBy())
                .status(TeamMember.TeamMemberStatus.PENDING)
                .build();

        TeamMember savedMember = teamMemberRepository.save(teamMember);
        return TeamMemberResponse.fromEntity(savedMember);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamMemberResponse getMemberById(UUID id) {
        TeamMember teamMember = teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found with id: " + id));
        
        return TeamMemberResponse.fromEntity(teamMember);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getMembersByTeamId(UUID teamId) {
        return teamMemberRepository.findByTeamId(teamId).stream()
                .map(TeamMemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getMembersByUserId(UUID userId) {
        return teamMemberRepository.findByUserId(userId).stream()
                .map(TeamMemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeamMemberResponse updateMemberRole(UUID id, TeamMember.TeamMemberRole role) {
        TeamMember teamMember = teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found with id: " + id));
        
        teamMember.setRole(role);
        TeamMember updatedMember = teamMemberRepository.save(teamMember);
        
        return TeamMemberResponse.fromEntity(updatedMember);
    }

    @Override
    @Transactional
    public TeamMemberResponse updateMemberStatus(UUID id, TeamMember.TeamMemberStatus status) {
        TeamMember teamMember = teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found with id: " + id));
        
        teamMember.setStatus(status);
        TeamMember updatedMember = teamMemberRepository.save(teamMember);
        
        return TeamMemberResponse.fromEntity(updatedMember);
    }

    @Override
    @Transactional
    public void removeMemberFromTeam(UUID id) {
        if (!teamMemberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team member not found with id: " + id);
        }
        teamMemberRepository.deleteById(id);
    }
}
