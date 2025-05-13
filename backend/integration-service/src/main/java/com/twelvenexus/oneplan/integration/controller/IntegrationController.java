package com.twelvenexus.oneplan.integration.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twelvenexus.oneplan.integration.mapper.ConnectionMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.java.com.twelvenexus.oneplan.integration.dto.ConnectionResponseDto;
import main.java.com.twelvenexus.oneplan.integration.dto.CreateConnectionDto;
import main.java.com.twelvenexus.oneplan.integration.dto.OAuthCallbackDto;
import main.java.com.twelvenexus.oneplan.integration.model.GitCommit;
import main.java.com.twelvenexus.oneplan.integration.model.GitConnection;
import main.java.com.twelvenexus.oneplan.integration.model.PullRequest;
import main.java.com.twelvenexus.oneplan.integration.service.GitProviderService;
import main.java.com.twelvenexus.oneplan.integration.service.WebhookService;

@RestController
@RequestMapping("/integrations")
@RequiredArgsConstructor
@Tag(name = "Integrations", description = "Git provider integration API")
@Slf4j
public class IntegrationController {
    
    private final GitProviderService gitProviderService;
    private final WebhookService webhookService;
    private final ConnectionMapper connectionMapper;
    private final ObjectMapper objectMapper;
    
    // OAuth endpoints
    @GetMapping("/oauth/{provider}/authorize")
    @Operation(summary = "Get OAuth authorization URL")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl(
            @PathVariable String provider,
            @RequestParam String userId,
            @RequestParam String projectId,
            @RequestParam String redirectUrl) {
        
        String authUrl = gitProviderService.getAuthorizationUrl(userId, projectId, provider, redirectUrl);
        
        Map<String, String> response = new HashMap<>();
        response.put("authorizationUrl", authUrl);
        response.put("provider", provider);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/oauth/callback")
    @Operation(summary = "Handle OAuth callback")
    public ResponseEntity<ConnectionResponseDto> handleOAuthCallback(@Valid @RequestBody OAuthCallbackDto dto) {
        GitConnection connection = gitProviderService.handleOAuthCallback(dto.getCode(), dto.getState());
        return ResponseEntity.ok(connectionMapper.toDto(connection));
    }
    
    // Connection management endpoints
    @PostMapping("/connections")
    @Operation(summary = "Create a new connection")
    public ResponseEntity<ConnectionResponseDto> createConnection(@Valid @RequestBody CreateConnectionDto dto) {
        GitConnection connection = connectionMapper.toEntity(dto);
        GitConnection created = gitProviderService.createConnection(connection);
        return ResponseEntity.status(HttpStatus.CREATED).body(connectionMapper.toDto(created));
    }
    
    @GetMapping("/connections/{id}")
    @Operation(summary = "Get connection by ID")
    public ResponseEntity<ConnectionResponseDto> getConnection(@PathVariable String id) {
        GitConnection connection = gitProviderService.getConnection(id);
        return ResponseEntity.ok(connectionMapper.toDto(connection));
    }
    
    @GetMapping("/projects/{projectId}/connections")
    @Operation(summary = "Get all connections for a project")
    public ResponseEntity<List<ConnectionResponseDto>> getProjectConnections(@PathVariable String projectId) {
        List<GitConnection> connections = gitProviderService.getConnectionsByProject(projectId);
        List<ConnectionResponseDto> dtos = connections.stream()
                .map(connectionMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @DeleteMapping("/connections/{id}")
    @Operation(summary = "Delete a connection")
    public ResponseEntity<Void> deleteConnection(@PathVariable String id) {
        gitProviderService.deleteConnection(id);
        return ResponseEntity.noContent().build();
    }
    
    // Repository operations
    @GetMapping("/connections/{id}/repository")
    @Operation(summary = "Get repository information")
    public ResponseEntity<Map<String, Object>> getRepositoryInfo(@PathVariable String id) {
        Map<String, Object> repoInfo = gitProviderService.getRepositoryInfo(id);
        return ResponseEntity.ok(repoInfo);
    }
    
    @GetMapping("/connections/{id}/branches")
    @Operation(summary = "List repository branches")
    public ResponseEntity<List<String>> listBranches(@PathVariable String id) {
        List<String> branches = gitProviderService.listBranches(id);
        return ResponseEntity.ok(branches);
    }
    
    // Commit operations
    @PostMapping("/connections/{id}/commits/sync")
    @Operation(summary = "Sync commits from repository")
    public ResponseEntity<List<GitCommit>> syncCommits(
            @PathVariable String id,
            @RequestParam(defaultValue = "main") String branch) {
        List<GitCommit> commits = gitProviderService.syncCommits(id, branch);
        return ResponseEntity.ok(commits);
    }
    
    @GetMapping("/connections/{id}/commits/{sha}")
    @Operation(summary = "Get specific commit")
    public ResponseEntity<GitCommit> getCommit(@PathVariable String id, @PathVariable String sha) {
        GitCommit commit = gitProviderService.getCommit(id, sha);
        return ResponseEntity.ok(commit);
    }
    
    // Pull request operations
    @PostMapping("/connections/{id}/pull-requests/sync")
    @Operation(summary = "Sync pull requests from repository")
    public ResponseEntity<List<PullRequest>> syncPullRequests(@PathVariable String id) {
        List<PullRequest> pullRequests = gitProviderService.syncPullRequests(id);
        return ResponseEntity.ok(pullRequests);
    }
    
    @GetMapping("/connections/{id}/pull-requests/{prNumber}")
    @Operation(summary = "Get specific pull request")
    public ResponseEntity<PullRequest> getPullRequest(@PathVariable String id, @PathVariable Integer prNumber) {
        PullRequest pullRequest = gitProviderService.getPullRequest(id, prNumber);
        return ResponseEntity.ok(pullRequest);
    }
    
    // Webhook management
    @PostMapping("/connections/{id}/webhook")
    @Operation(summary = "Create webhook for connection")
    public ResponseEntity<Void> createWebhook(@PathVariable String id) {
        gitProviderService.createWebhook(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/connections/{id}/webhook")
    @Operation(summary = "Delete webhook for connection")
    public ResponseEntity<Void> deleteWebhook(@PathVariable String id) {
        gitProviderService.deleteWebhook(id);
        return ResponseEntity.noContent().build();
    }
}
