package com.twelvenexus.oneplan.task.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.xml.stream.events.Comment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "project_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID projectId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "assignee_id", columnDefinition = "BINARY(16)")
    private UUID assigneeId;

    @Column(name = "reporter_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID reporterId;

    @Column(name = "story_points")
    private Integer storyPoints;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "estimated_hours")
    private Float estimatedHours;

    @Column(name = "actual_hours")
    private Float actualHours;

    @Column(name = "parent_id", columnDefinition = "BINARY(16)")
    private UUID parentId;

    @Column(columnDefinition = "JSON")
    private String tags;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskAttachment> attachments;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<TaskDependency> dependencies;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
