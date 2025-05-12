package com.twelvenexus.oneplan.organization.controller;

import com.twelvenexus.oneplan.organization.dto.TeamMemberRequest;
import com.twelvenexus.oneplan.organization.dto.TeamMemberResponse;
import com.twelvenexus.oneplan.organization.model.TeamMember;
import com.twelvenexus.oneplan.organization.service.TeamMemberService;
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
@RequestMapping("/team-members")
@RequiredArgsConstructor
@Tag(name = "Team Member Management", description = "APIs for managing team members")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @PostMapping
    @Operation(summary = "Add a member to a team")
    public ResponseEntity<TeamMemberResponse> addMemberToTeam(@Valid @RequestBody TeamMemberRequest request) {
        return new ResponseEntity<>(teamMemberService.addMemberToTeam(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team member by ID")
    public ResponseEntity<TeamMemberResponse> getMemberById(@PathVariable UUID id) {
        return ResponseEntity.ok(teamMemberService.getMemberById(id));
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Get members by team ID")
    public ResponseEntity<List<TeamMemberResponse>> getMembersByTeamId(@PathVariable UUID teamId) {
        return ResponseEntity.ok(teamMemberService.getMembersByTeamId(teamId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get teams by user ID")
    public ResponseEntity<List<TeamMemberResponse>> getMembersByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(teamMemberService.getMembersByUserId(userId));
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Update a member's role")
    public ResponseEntity<TeamMemberResponse> updateMemberRole(
            @PathVariable UUID id, 
            @RequestParam TeamMember.TeamMemberRole role) {
        return ResponseEntity.ok(teamMemberService.updateMemberRole(id, role));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update a member's status")
    public ResponseEntity<TeamMemberResponse> updateMemberStatus(
            @PathVariable UUID id, 
            @RequestParam TeamMember.TeamMemberStatus status) {
        return ResponseEntity.ok(teamMemberService.updateMemberStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a member from a team")
    public ResponseEntity<Void> removeMemberFromTeam(@PathVariable UUID id) {
        teamMemberService.removeMemberFromTeam(id);
        return ResponseEntity.noContent().build();
    }
}
