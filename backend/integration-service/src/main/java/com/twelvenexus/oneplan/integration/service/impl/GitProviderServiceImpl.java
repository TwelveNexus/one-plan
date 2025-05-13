package com.twelvenexus.oneplan.integration.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twelvenexus.oneplan.integration.model.GitCommit;
import com.twelvenexus.oneplan.integration.model.GitConnection;
import com.twelvenexus.oneplan.integration.model.OAuthState;
import com.twelvenexus.oneplan.integration.model.PullRequest;
import com.twelvenexus.oneplan.integration.repository.GitCommitRepository;
import com.twelvenexus.oneplan.integration.repository.GitConnectionRepository;
import com.twelvenexus.oneplan.integration.repository.OAuthStateRepository;
import com.twelvenexus.oneplan.integration.repository.PullRequestRepository;
import com.twelvenexus.oneplan.integration.service.GitProviderService;
import com.twelvenexus.oneplan.integration.service.WebhookService;
import com.twelvenexus.oneplan.integration.service.provider.GitProviderStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GitProviderServiceImpl implements GitProviderService {
    
    private final GitConnectionRepository connectionRepository;
    private final OAuthStateRepository oauthStateRepository;
    private final GitCommitRepository commitRepository;
    private final PullRequestRepository pullRequestRepository;
    private final WebhookService webhookService;
    private final Map<String, GitProviderStrategy> providerStrategies;
    private final ObjectMapper objectMapper;
    
    @Value("${server.port}")
    private String serverPort;
    
    @Override
    public String getAuthorizationUrl(String userId, String projectId, String provider, String redirectUrl) {
        GitProviderStrategy strategy = getStrategy(provider);
        
        // Create OAuth state
        OAuthState state = OAuthState.builder()
                .userId(userId)
                .projectId(projectId)
                .provider(provider)
                .redirectUrl(redirectUrl)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        
        state = oauthStateRepository.save(state);
        
        return strategy.getAuthorizationUrl(state.getState(), redirectUrl);
    }
    
    @Override
    public GitConnection handleOAuthCallback(String code, String state) {
        // Find and validate OAuth state
        OAuthState oauthState = oauthStateRepository.findByState(state)
                .orElseThrow(() -> new RuntimeException("Invalid OAuth state"));
        
        if (oauthState.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OAuth state expired");
        }
        
        GitProviderStrategy strategy = getStrategy(oauthState.getProvider());
        
        // Exchange code for token
        Map<String, Object> tokenResponse = strategy.exchangeCodeForToken(code, oauthState.getRedirectUrl());
        
        // Create or update connection
        GitConnection connection = new GitConnection();
        connection.setUserId(oauthState.getUserId());
        connection.setProjectId(oauthState.getProjectId());
        connection.setProvider(oauthState.getProvider());
        connection.setAccessToken((String) tokenResponse.get("access_token"));
        connection.setRefreshToken((String) tokenResponse.get("refresh_token"));
        
        // Set token expiry if available
        if (tokenResponse.containsKey("expires_in")) {
            Integer expiresIn = (Integer) tokenResponse.get("expires_in");
            connection.setTokenExpiresAt(LocalDateTime.now().plusSeconds(expiresIn));
        }
        
        // Get user repositories to select one
        List<Map<String, Object>> repos = strategy.listRepositories(connection.getAccessToken());
        if (!repos.isEmpty()) {
            // For now, just use the first repository - in real implementation, user would select
            Map<String, Object> repo = repos.get(0);
            connection.setRepositoryName((String) repo.get("name"));
            connection.setRepositoryFullName((String) repo.get("full_name"));
            connection.setRepositoryUrl((String) repo.get("html_url"));
            connection.setDefaultBranch((String) repo.get("default_branch"));
        }
        
        connection = connectionRepository.save(connection);
        
        // Create webhook
        try {
            createWebhook(connection.getId());
        } catch (Exception e) {
            log.error("Failed to create webhook for connection {}", connection.getId(), e);
        }
        
        // Clean up OAuth state
        oauthStateRepository.delete(oauthState);
        
        return connection;
    }
    
    @Override
    public GitConnection createConnection(GitConnection connection) {
        // Check if connection already exists
        connectionRepository.findByProjectIdAndProviderAndRepositoryFullName(
                connection.getProjectId(),
                connection.getProvider(),
                connection.getRepositoryFullName()
        ).ifPresent(existing -> {
            throw new RuntimeException("Connection already exists for this repository");
        });
        
        return connectionRepository.save(connection);
    }
    
    @Override
    public GitConnection updateConnection(String id, GitConnection connection) {
        GitConnection existing = getConnection(id);
        existing.setDefaultBranch(connection.getDefaultBranch());
        existing.setIsActive(connection.getIsActive());
        existing.setWebhookUrl(connection.getWebhookUrl());
        return connectionRepository.save(existing);
    }
    
    @Override
    public GitConnection getConnection(String id) {
        return connectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Connection not found"));
    }
    
    @Override
    public List<GitConnection> getConnectionsByProject(String projectId) {
        return connectionRepository.findByProjectId(projectId);
    }
    
    @Override
    public void deleteConnection(String id) {
        GitConnection connection = getConnection(id);
        
        // Delete webhook if exists
        if (connection.getWebhookId() != null) {
            try {
                deleteWebhook(id);
            } catch (Exception e) {
                log.error("Failed to delete webhook for connection {}", id, e);
            }
        }
        
        connectionRepository.deleteById(id);
    }
    
    @Override
    public Map<String, Object> getRepositoryInfo(String connectionId) {
        GitConnection connection = getConnection(connectionId);
        GitProviderStrategy strategy = getStrategy(connection.getProvider());
        
        String[] parts = connection.getRepositoryFullName().split("/");
        return strategy.getRepositoryInfo(connection.getAccessToken(), parts[0], parts[1]);
    }
    
    @Override
    public List<Map<String, Object>> listRepositories(String connectionId) {
        GitConnection connection = getConnection(connectionId);
        GitProviderStrategy strategy = getStrategy(connection.getProvider());
        
        return strategy.listRepositories(connection.getAccessToken());
    }
    
    @Override
    public List<String> listBranches(String connectionId) {
        GitConnection connection = getConnection(connectionId);
        GitProviderStrategy strategy = getStrategy(connection.getProvider());
        
        String[] parts = connection.getRepositoryFullName().split("/");
        return strategy.listBranches(connection.getAccessToken(), parts[0], parts[1]);
    }
    
    @Override
    public List<GitCommit> syncCommits(String connectionId, String branch) {
        GitConnection connection = getConnection(connectionId);
        GitProviderStrategy strategy = getStrategy(connection.getProvider());
        
        String[] parts = connection.getRepositoryFullName().split("/");
        List<GitCommit> commits = strategy.getCommits(
                connection.getAccessToken(), parts[0], parts[1], branch);
        
        // Save commits
        for (GitCommit commit : commits) {
            commit.setConnectionId(connectionId);
            commit.setBranch(branch);
            
            // Check if commit already exists
            if (commitRepository.findByConnectionIdAndCommitSha(connectionId, commit.getCommitSha()).isEmpty()) {
                commitRepository.save(commit);
            }
        }
        
        connection.setLastSyncAt(LocalDateTime.now());
        connection.setSyncStatus("completed");
        connectionRepository.save(connection);
        
        return commits;
    }
    
    @Override
    public GitCommit getCommit(String connectionId, String commitSha) {
        return commitRepository.findByConnectionIdAndCommitSha(connectionId, commitSha)
                .orElseGet(() -> {
                    // If not in database, fetch from provider
                    GitConnection connection = getConnection(connectionId);
                    GitProviderStrategy strategy = getStrategy(connection.getProvider());
                    
                    String[] parts = connection.getRepositoryFullName().split("/");
                    GitCommit commit = strategy.getCommit(
                            connection.getAccessToken(), parts[0], parts[1], commitSha);
                    
                    commit.setConnectionId(connectionId);
                    return commitRepository.save(commit);
                });
    }
    
    @Override
    public List<PullRequest> syncPullRequests(String connectionId) {
        GitConnection connection = getConnection(connectionId);
        GitProviderStrategy strategy = getStrategy(connection.getProvider());
        
        String[] parts = connection.getRepositoryFullName().split("/");
        List<PullRequest> pullRequests = strategy.getPullRequests(
                connection.getAccessToken(), parts[0], parts[1]);
        
        // Save pull requests
        for (PullRequest pr : pullRequests) {
            pr.setConnectionId(connectionId);
            
            // Check if PR already exists
            pullRequestRepository.findByConnectionIdAndPrNumber(connectionId, pr.getPrNumber())
                    .ifPresentOrElse(
                            existing -> {
                                // Update existing PR
                                existing.setState(pr.getState());
                                existing.setTitle(pr.getTitle());
                                existing.setDescription(pr.getDescription());
                                existing.setUpdatedDate(pr.getUpdatedDate());
                                existing.setClosedDate(pr.getClosedDate());
                                existing.setMergedDate(pr.getMergedDate());
                                existing.setIsMerged(pr.getIsMerged());
                                pullRequestRepository.save(existing);
                            },
                            () -> pullRequestRepository.save(pr)
                    );
        }
        
        return pullRequests;
    }
    
    @Override
    public PullRequest getPullRequest(String connectionId, Integer prNumber) {
        return pullRequestRepository.findByConnectionIdAndPrNumber(connectionId, prNumber)
                .orElseGet(() -> {
                    // If not in database, fetch from provider
                    GitConnection connection = getConnection(connectionId);
                    GitProviderStrategy strategy = getStrategy(connection.getProvider());
                    
                    String[] parts = connection.getRepositoryFullName().split("/");
                    PullRequest pr = strategy.getPullRequest(
                            connection.getAccessToken(), parts[0], parts[1], prNumber);
                    
                    pr.setConnectionId(connectionId);
                    return pullRequestRepository.save(pr);
                });
    }
    
    @Override
    public void createWebhook(String connectionId) {
        GitConnection connection = getConnection(connectionId);
        GitProviderStrategy strategy = getStrategy(connection.getProvider());
        
        // Generate webhook URL and secret
        String webhookUrl = String.format("https://api.oneplan.cloud/api/v1/webhooks/%s/%s",
                connection.getProvider(), connection.getId());
        String webhookSecret = UUID.randomUUID().toString();
        
        String[] parts = connection.getRepositoryFullName().split("/");
        Map<String, Object> webhook = strategy.createWebhook(
                connection.getAccessToken(), parts[0], parts[1], webhookUrl, webhookSecret);
        
        // Update connection with webhook info
        connection.setWebhookId(webhook.get("id").toString());
        connection.setWebhookSecret(webhookSecret);
        connection.setWebhookUrl(webhookUrl);
        connectionRepository.save(connection);
    }
    
    @Override
    public void deleteWebhook(String connectionId) {
        GitConnection connection = getConnection(connectionId);
        
        if (connection.getWebhookId() != null) {
            GitProviderStrategy strategy = getStrategy(connection.getProvider());
            String[] parts = connection.getRepositoryFullName().split("/");
            
            strategy.deleteWebhook(
                    connection.getAccessToken(), parts[0], parts[1], connection.getWebhookId());
            
            // Clear webhook info
            connection.setWebhookId(null);
            connection.setWebhookSecret(null);
            connection.setWebhookUrl(null);
            connectionRepository.save(connection);
        }
    }
    
    @Override
    public void processWebhookEvent(String provider, Map<String, String> headers, String payload) {
        // This will be handled by the webhook service
        webhookService.verifyWebhookSignature(provider, headers, payload);
    }
    
    private GitProviderStrategy getStrategy(String provider) {
        GitProviderStrategy strategy = providerStrategies.get(provider + "ProviderStrategy");
        if (strategy == null) {
            throw new RuntimeException("Unsupported provider: " + provider);
        }
        return strategy;
    }
}
