package com.twelvenexus.oneplan.integration.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "git_commits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitCommit {
    @Id
    @Column(name = "id", length = 36)
    private String id = UUID.randomUUID().toString();
    
    @Column(name = "connection_id", nullable = false, length = 36)
    private String connectionId;
    
    @Column(name = "commit_sha", nullable = false, length = 40)
    private String commitSha;
    
    @Column(name = "branch", length = 100)
    private String branch;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "author_name")
    private String authorName;
    
    @Column(name = "author_email")
    private String authorEmail;
    
    @Column(name = "author_date")
    private LocalDateTime authorDate;
    
    @Column(name = "files_changed")
    private Integer filesChanged;
    
    @Column(name = "additions")
    private Integer additions;
    
    @Column(name = "deletions")
    private Integer deletions;
    
    @Column(name = "url", length = 500)
    private String url;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    public void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
    }
}
