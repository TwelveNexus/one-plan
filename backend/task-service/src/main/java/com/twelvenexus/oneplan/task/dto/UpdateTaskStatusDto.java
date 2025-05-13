package com.twelvenexus.oneplan.task.dto;

import com.twelvenexus.oneplan.task.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskStatusDto {
    @NotNull(message = "Status is required")
    private TaskStatus status;
}