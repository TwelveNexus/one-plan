package com.twelvenexus.oneplan.organization.controller;

import com.twelvenexus.oneplan.organization.dto.TeamRequest;
import com.twelvenexus.oneplan.organization.dto.TeamResponse;
import com.twelvenexus.oneplan.organization.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@Tag(name = "Team Management", description = "APIs for managing teams")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @Operation(summary = "Create a new team")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest request) {
        return new ResponseEntity<>(teamService.createTeam(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable UUID id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Get teams by organization ID")
    public ResponseEntity<List<TeamResponse>> getTeamsByOrganizationId(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(teamService.getTeamsByOrganizationId(organizationId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a team")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable UUID id, 
            @Valid @RequestBody TeamRequest request) {
        return ResponseEntity.ok(teamService.updateTeam(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a team")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}
