package com.twelvenexus.oneplan.task.dto;

import com.twelvenexus.oneplan.task.model.Priority;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateTaskDto {
    @NotBlank(message = "Task title is required")
    private String title;
    private String description;
    private Priority priority;
    private Integer storyPoints;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Float estimatedHours;
    private Float actualHours;
    private List<String> tags;
}
