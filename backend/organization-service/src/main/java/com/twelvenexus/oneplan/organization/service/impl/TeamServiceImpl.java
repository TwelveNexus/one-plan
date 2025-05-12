package com.twelvenexus.oneplan.organization.service.impl;

import com.twelvenexus.oneplan.organization.dto.TeamRequest;
import com.twelvenexus.oneplan.organization.dto.TeamResponse;
import com.twelvenexus.oneplan.organization.exception.ResourceAlreadyExistsException;
import com.twelvenexus.oneplan.organization.exception.ResourceNotFoundException;
import com.twelvenexus.oneplan.organization.model.Team;
import com.twelvenexus.oneplan.organization.repository.OrganizationRepository;
import com.twelvenexus.oneplan.organization.repository.TeamRepository;
import com.twelvenexus.oneplan.organization.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public TeamResponse createTeam(TeamRequest request) {
        // Verify organization exists
        if (!organizationRepository.existsById(request.getOrganizationId())) {
            throw new ResourceNotFoundException("Organization not found with id: " + request.getOrganizationId());
        }
        
        // Check if team name is unique within the organization
        if (teamRepository.existsByNameAndOrganizationId(request.getName(), request.getOrganizationId())) {
            throw new ResourceAlreadyExistsException("Team with name " + request.getName() + 
                    " already exists in this organization");
        }

        Team team = Team.builder()
                .organizationId(request.getOrganizationId())
                .name(request.getName())
                .description(request.getDescription())
                .avatar(request.getAvatar())
                .visibility(request.getVisibility())
                .settings(request.getSettings())
                .metadata(request.getMetadata())
                .build();

        Team savedTeam = teamRepository.save(team);
        return TeamResponse.fromEntity(savedTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse getTeamById(UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        
        return TeamResponse.fromEntity(team);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse> getTeamsByOrganizationId(UUID organizationId) {
        return teamRepository.findByOrganizationId(organizationId).stream()
                .map(TeamResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeamResponse updateTeam(UUID id, TeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        
        // Check if the organization exists
        if (!organizationRepository.existsById(request.getOrganizationId())) {
            throw new ResourceNotFoundException("Organization not found with id: " + request.getOrganizationId());
        }
        
        // Check if the name is already taken by another team in the same organization
        if (!team.getName().equals(request.getName()) && 
                teamRepository.existsByNameAndOrganizationId(request.getName(), request.getOrganizationId())) {
            throw new ResourceAlreadyExistsException("Team with name " + request.getName() + 
                    " already exists in this organization");
        }
        
        team.setOrganizationId(request.getOrganizationId());
        team.setName(request.getName());
        team.setDescription(request.getDescription());
        team.setAvatar(request.getAvatar());
        team.setVisibility(request.getVisibility());
        team.setSettings(request.getSettings());
        team.setMetadata(request.getMetadata());
        
        Team updatedTeam = teamRepository.save(team);
        return TeamResponse.fromEntity(updatedTeam);
    }

    @Override
    @Transactional
    public void deleteTeam(UUID id) {
        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team not found with id: " + id);
        }
        teamRepository.deleteById(id);
    }
}
