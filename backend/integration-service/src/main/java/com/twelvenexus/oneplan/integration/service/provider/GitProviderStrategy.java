package com.twelvenexus.oneplan.integration.service.provider;

import com.twelvenexus.oneplan.integration.model.GitCommit;
import com.twelvenexus.oneplan.integration.model.PullRequest;

import java.util.List;
import java.util.Map;

public interface GitProviderStrategy {
    
    String getProvider();
    
    // OAuth operations
    String getAuthorizationUrl(String state, String redirectUrl);
    Map<String, Object> exchangeCodeForToken(String code, String redirectUrl);
    Map<String, Object> refreshToken(String refreshToken);
    
    // Repository operations
    Map<String, Object> getRepositoryInfo(String accessToken, String owner, String repo);
    List<Map<String, Object>> listRepositories(String accessToken);
    List<String> listBranches(String accessToken, String owner, String repo);
    
    // Commit operations
    List<GitCommit> getCommits(String accessToken, String owner, String repo, String branch);
    GitCommit getCommit(String accessToken, String owner, String repo, String sha);
    
    // Pull request operations
    List<PullRequest> getPullRequests(String accessToken, String owner, String repo);
    PullRequest getPullRequest(String accessToken, String owner, String repo, Integer prNumber);
    
    // Webhook operations
    Map<String, Object> createWebhook(String accessToken, String owner, String repo, String url, String secret);
    void deleteWebhook(String accessToken, String owner, String repo, String webhookId);
    boolean verifyWebhookSignature(Map<String, String> headers, String payload, String secret);
}
