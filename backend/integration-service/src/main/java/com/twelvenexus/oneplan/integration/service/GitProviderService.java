package com.twelvenexus.oneplan.integration.service;

import com.twelvenexus.oneplan.integration.model.GitConnection;
import com.twelvenexus.oneplan.integration.model.GitCommit;
import com.twelvenexus.oneplan.integration.model.PullRequest;

import java.util.List;
import java.util.Map;

public interface GitProviderService {
    
    // OAuth operations
    String getAuthorizationUrl(String userId, String projectId, String provider, String redirectUrl);
    GitConnection handleOAuthCallback(String code, String state);
    
    // Connection management
    GitConnection createConnection(GitConnection connection);
    GitConnection updateConnection(String id, GitConnection connection);
    GitConnection getConnection(String id);
    List<GitConnection> getConnectionsByProject(String projectId);
    void deleteConnection(String id);
    
    // Repository operations
    Map<String, Object> getRepositoryInfo(String connectionId);
    List<Map<String, Object>> listRepositories(String connectionId);
    List<String> listBranches(String connectionId);
    
    // Commit operations
    List<GitCommit> syncCommits(String connectionId, String branch);
    GitCommit getCommit(String connectionId, String commitSha);
    
    // Pull request operations
    List<PullRequest> syncPullRequests(String connectionId);
    PullRequest getPullRequest(String connectionId, Integer prNumber);
    
    // Webhook operations
    void createWebhook(String connectionId);
    void deleteWebhook(String connectionId);
    void processWebhookEvent(String provider, Map<String, String> headers, String payload);
}
