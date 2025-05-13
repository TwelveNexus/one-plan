package com.twelvenexus.oneplan.integration.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pull_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PullRequest {
    @Id
    @Column(name = "id", length = 36)
    private String id = UUID.randomUUID().toString();
    
    @Column(name = "connection_id", nullable = false, length = 36)
    private String connectionId;
    
    @Column(name = "pr_number", nullable = false)
    private Integer prNumber;
    
    @Column(name = "title", length = 500)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "state", length = 20)
    private String state; // open, closed, merged
    
    @Column(name = "source_branch", length = 100)
    private String sourceBranch;
    
    @Column(name = "target_branch", length = 100)
    private String targetBranch;
    
    @Column(name = "author_name")
    private String authorName;
    
    @Column(name = "author_email")
    private String authorEmail;
    
    @Column(name = "url", length = 500)
    private String url;
    
    @Column(name = "is_merged")
    private Boolean isMerged = false;
    
    @Column(name = "merge_commit_sha", length = 40)
    private String mergeCommitSha;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @Column(name = "closed_date")
    private LocalDateTime closedDate;
    
    @Column(name = "merged_date")
    private LocalDateTime mergedDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    public void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
    }
}
