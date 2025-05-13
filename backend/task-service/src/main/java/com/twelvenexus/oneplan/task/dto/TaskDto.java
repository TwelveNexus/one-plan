package com.twelvenexus.oneplan.task.dto;

import com.twelvenexus.oneplan.task.model.Priority;
import com.twelvenexus.oneplan.task.model.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class TaskDto {
    private UUID id;
    private UUID projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private UUID assigneeId;
    private UUID reporterId;
    private Integer storyPoints;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Float estimatedHours;
    private Float actualHours;
    private UUID parentId;
    private List<String> tags;
    private int commentCount;
    private int attachmentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
