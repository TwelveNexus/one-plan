package com.twelvenexus.oneplan.integration.service.provider.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twelvenexus.oneplan.integration.model.GitCommit;
import com.twelvenexus.oneplan.integration.model.PullRequest;
import com.twelvenexus.oneplan.integration.service.provider.GitProviderStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubProviderStrategy implements GitProviderStrategy {
    
    private static final String GITHUB_API_BASE = "https://api.github.com";
    private static final String GITHUB_OAUTH_BASE = "https://github.com";
    
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;
    
    @Override
    public String getProvider() {
        return "github";
    }
    
    @Override
    public String getAuthorizationUrl(String state, String redirectUrl) {
        return String.format("%s/login/oauth/authorize?client_id=%s&redirect_uri=%s&state=%s&scope=repo,read:user",
                GITHUB_OAUTH_BASE, clientId, redirectUrl, state);
    }
    
    @Override
    public Map<String, Object> exchangeCodeForToken(String code, String redirectUrl) {
        try {
            FormBody formBody = new FormBody.Builder()
                    .add("client_id", clientId)
                    .add("client_secret", clientSecret)
                    .add("code", code)
                    .add("redirect_uri", redirectUrl)
                    .build();
            
            Request request = new Request.Builder()
                    .url(GITHUB_OAUTH_BASE + "/login/oauth/access_token")
                    .header("Accept", "application/json")
                    .post(formBody)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to exchange code for token: " + response.code());
                }
                
                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, Map.class);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error exchanging code for token", e);
        }
    }
    
    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        // GitHub tokens don't expire, so no refresh needed
        Map<String, Object> result = new HashMap<>();
        result.put("access_token", refreshToken);
        return result;
    }
    
    @Override
    public Map<String, Object> getRepositoryInfo(String accessToken, String owner, String repo) {
        try {
            Request request = new Request.Builder()
                    .url(String.format("%s/repos/%s/%s", GITHUB_API_BASE, owner, repo))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to get repository info: " + response.code());
                }
                
                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, Map.class);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting repository info", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> listRepositories(String accessToken) {
        try {
            Request request = new Request.Builder()
                    .url(GITHUB_API_BASE + "/user/repos?per_page=100")
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to list repositories: " + response.code());
                }
                
                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, List.class);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error listing repositories", e);
        }
    }
    
    @Override
    public List<String> listBranches(String accessToken, String owner, String repo) {
        try {
            Request request = new Request.Builder()
                    .url(String.format("%s/repos/%s/%s/branches", GITHUB_API_BASE, owner, repo))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to list branches: " + response.code());
                }
                
                String responseBody = response.body().string();
                List<Map<String, Object>> branches = objectMapper.readValue(responseBody, List.class);
                return branches.stream()
                        .map(branch -> (String) branch.get("name"))
                        .toList();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error listing branches", e);
        }
    }
    
    @Override
    public List<GitCommit> getCommits(String accessToken, String owner, String repo, String branch) {
        try {
            Request request = new Request.Builder()
                    .url(String.format("%s/repos/%s/%s/commits?sha=%s&per_page=100", 
                            GITHUB_API_BASE, owner, repo, branch))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to get commits: " + response.code());
                }
                
                String responseBody = response.body().string();
                List<Map<String, Object>> commits = objectMapper.readValue(responseBody, List.class);
                
                return commits.stream()
                        .map(this::mapToGitCommit)
                        .toList();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting commits", e);
        }
    }
    
    @Override
    public GitCommit getCommit(String accessToken, String owner, String repo, String sha) {
        try {
            Request request = new Request.Builder()
                    .url(String.format("%s/repos/%s/%s/commits/%s", 
                            GITHUB_API_BASE, owner, repo, sha))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to get commit: " + response.code());
                }
                
                String responseBody = response.body().string();
                Map<String, Object> commit = objectMapper.readValue(responseBody, Map.class);
                return mapToGitCommit(commit);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting commit", e);
        }
    }
    
    @Override
    public List<PullRequest> getPullRequests(String accessToken, String owner, String repo) {
        try {
            Request request = new Request.Builder()
                    .url(String.format("%s/repos/%s/%s/pulls?state=all&per_page=100", 
                            GITHUB_API_BASE, owner, repo))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to get pull requests: " + response.code());
                }
                
                String responseBody = response.body().string();
                List<Map<String, Object>> prs = objectMapper.readValue(responseBody, List.class);
                
                return prs.stream()
                        .map(this::mapToPullRequest)
                        .toList();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting pull requests", e);
        }
    }
    
    @Override
    public PullRequest getPullRequest(String accessToken, String owner, String repo, Integer prNumber) {
        try {
            Request request = new Request.Builder()
                    .url(String.format("%s/repos/%s/%s/pulls/%d", 
                            GITHUB_API_BASE, owner, repo, prNumber))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to get pull request: " + response.code());
                }
                
                String responseBody = response.body().string();
                Map<String, Object> pr = objectMapper.readValue(responseBody, Map.class);
                return mapToPullRequest(pr);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting pull request", e);
        }
    }
    
    @Override
    public Map<String, Object> createWebhook(String accessToken, String owner, String repo, String url, String secret) {
        try {
            Map<String, Object> webhook = new HashMap<>();
            webhook.put("name", "web");
            webhook.put("active", true);
            webhook.put("events", Arrays.asList("push", "pull_request", "issues"));
            webhook.put("config", Map.of(
                    "url", url,
                    "content_type", "json",
                    "secret", secret
            ));
            
            RequestBody body = RequestBody.create(
                    objectMapper.writeValueAsString(webhook),
                    MediaType.parse("application/json")
            );
            
            Request request = new Request.Builder()
                    .url(String.format("%s/repos/%s/%s/hooks", GITHUB_API_BASE, owner, repo))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .post(body)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to create webhook: " + response.code());
                }
                
                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, Map.class);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating webhook", e);
        }
    }
    
    @Override
    public void deleteWebhook(String accessToken, String owner, String repo, String webhookId) {
        try {
            Request request = new Request.Builder()
                    .url(String.format("%s/repos/%s/%s/hooks/%s", 
                            GITHUB_API_BASE, owner, repo, webhookId))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .delete()
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful() && response.code() != 404) {
                    throw new RuntimeException("Failed to delete webhook: " + response.code());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting webhook", e);
        }
    }
    
    @Override
    public boolean verifyWebhookSignature(Map<String, String> headers, String payload, String secret) {
        String signature = headers.get("X-Hub-Signature-256");
        if (signature == null) {
            return false;
        }
        
        // Remove "sha256=" prefix
        signature = signature.substring(7);
        
        String computedSignature = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret)
                .hmacHex(payload);
        
        return signature.equals(computedSignature);
    }
    
    private GitCommit mapToGitCommit(Map<String, Object> commitData) {
        Map<String, Object> commit = (Map<String, Object>) commitData.get("commit");
        Map<String, Object> author = (Map<String, Object>) commit.get("author");
        Map<String, Object> stats = (Map<String, Object>) commitData.get("stats");
        
        return GitCommit.builder()
                .commitSha((String) commitData.get("sha"))
                .message((String) commit.get("message"))
                .authorName((String) author.get("name"))
                .authorEmail((String) author.get("email"))
                .authorDate(parseDate((String) author.get("date")))
                .filesChanged(stats != null ? (Integer) stats.get("total") : null)
                .additions(stats != null ? (Integer) stats.get("additions") : null)
                .deletions(stats != null ? (Integer) stats.get("deletions") : null)
                .url((String) commitData.get("html_url"))
                .build();
    }
    
    private PullRequest mapToPullRequest(Map<String, Object> prData) {
        Map<String, Object> head = (Map<String, Object>) prData.get("head");
        Map<String, Object> base = (Map<String, Object>) prData.get("base");
        Map<String, Object> user = (Map<String, Object>) prData.get("user");
        
        return PullRequest.builder()
                .prNumber((Integer) prData.get("number"))
                .title((String) prData.get("title"))
                .description((String) prData.get("body"))
                .state((String) prData.get("state"))
                .sourceBranch((String) head.get("ref"))
                .targetBranch((String) base.get("ref"))
                .authorName((String) user.get("login"))
                .url((String) prData.get("html_url"))
                .isMerged(prData.get("merged_at") != null)
                .mergeCommitSha((String) prData.get("merge_commit_sha"))
                .createdDate(parseDate((String) prData.get("created_at")))
                .updatedDate(parseDate((String) prData.get("updated_at")))
                .closedDate(prData.get("closed_at") != null ? parseDate((String) prData.get("closed_at")) : null)
                .mergedDate(prData.get("merged_at") != null ? parseDate((String) prData.get("merged_at")) : null)
                .build();
    }
    
    private LocalDateTime parseDate(String dateString) {
        if (dateString == null) return null;
        return OffsetDateTime.parse(dateString).toLocalDateTime();
    }
}
