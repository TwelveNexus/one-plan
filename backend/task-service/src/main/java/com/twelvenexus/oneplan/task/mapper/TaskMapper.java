package com.twelvenexus.oneplan.task.mapper;

import com.twelvenexus.oneplan.task.dto.CreateTaskDto;
import com.twelvenexus.oneplan.task.dto.TaskDto;
import com.twelvenexus.oneplan.task.dto.UpdateTaskDto;
import com.twelvenexus.oneplan.task.model.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskMapper {
    
    public TaskDto toDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setProjectId(task.getProjectId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setAssigneeId(task.getAssigneeId());
        dto.setReporterId(task.getReporterId());
        dto.setStoryPoints(task.getStoryPoints());
        dto.setStartDate(task.getStartDate());
        dto.setDueDate(task.getDueDate());
        dto.setEstimatedHours(task.getEstimatedHours());
        dto.setActualHours(task.getActualHours());
        dto.setParentId(task.getParentId());
        
        // Convert JSON tags to List
        if (task.getTags() != null) {
            // Parse JSON tags - assuming simple JSON array
            dto.setTags(List.of(task.getTags().replace("[", "").replace("]", "").split(",")));
        }
        
        dto.setCommentCount(task.getComments() != null ? task.getComments().size() : 0);
        dto.setAttachmentCount(task.getAttachments() != null ? task.getAttachments().size() : 0);
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        
        return dto;
    }
    
    public Task toEntity(CreateTaskDto dto) {
        Task task = new Task();
        task.setProjectId(dto.getProjectId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setAssigneeId(dto.getAssigneeId());
        task.setStoryPoints(dto.getStoryPoints());
        task.setStartDate(dto.getStartDate());
        task.setDueDate(dto.getDueDate());
        task.setEstimatedHours(dto.getEstimatedHours());
        task.setParentId(dto.getParentId());
        
        // Convert List to JSON tags
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            task.setTags("[" + String.join(",", dto.getTags()) + "]");
        }
        
        return task;
    }
    
    public Task toEntity(UpdateTaskDto dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setStoryPoints(dto.getStoryPoints());
        task.setStartDate(dto.getStartDate());
        task.setDueDate(dto.getDueDate());
        task.setEstimatedHours(dto.getEstimatedHours());
        task.setActualHours(dto.getActualHours());
        
        // Convert List to JSON tags
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            task.setTags("[" + String.join(",", dto.getTags()) + "]");
        }
        
        return task;
    }
}
