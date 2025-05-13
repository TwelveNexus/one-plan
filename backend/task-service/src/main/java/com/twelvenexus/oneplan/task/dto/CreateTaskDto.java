package com.twelvenexus.oneplan.task.dto;

import com.twelvenexus.oneplan.task.model.Priority;
import com.twelvenexus.oneplan.task.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class CreateTaskDto {
    @NotNull(message = "Project ID is required")
    private UUID projectId;
    
    @NotBlank(message = "Task title is required")
    private String title;
    
    private String description;
    private TaskStatus status = TaskStatus.TODO;
    private Priority priority = Priority.MEDIUM;
    private UUID assigneeId;
    private Integer storyPoints;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Float estimatedHours;
    private UUID parentId;
    private List<String> tags;
    private List<UUID> dependsOn;
}
