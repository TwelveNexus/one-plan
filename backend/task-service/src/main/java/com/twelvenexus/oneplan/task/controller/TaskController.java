package com.twelvenexus.oneplan.task.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.twelvenexus.oneplan.task.dto.CreateTaskDto;
import com.twelvenexus.oneplan.task.dto.TaskDto;
import com.twelvenexus.oneplan.task.dto.UpdateTaskDto;
import com.twelvenexus.oneplan.task.dto.UpdateTaskStatusDto;
import com.twelvenexus.oneplan.task.mapper.TaskMapper;
import com.twelvenexus.oneplan.task.model.Task;
import com.twelvenexus.oneplan.task.model.TaskStatus;
import com.twelvenexus.oneplan.task.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    private final TaskMapper taskMapper;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@Valid @RequestBody CreateTaskDto createDto) {
        Task task = taskMapper.toEntity(createDto);
        // TODO: Set reporterId from security context
        task.setReporterId(UUID.randomUUID()); // Placeholder
        Task created = taskService.createTask(task);
        return taskMapper.toDto(created);
    }
    
    @GetMapping("/{id}")
    public TaskDto getTask(@PathVariable UUID id) {
        Task task = taskService.getTask(id);
        return taskMapper.toDto(task);
    }
    
    @GetMapping("/project/{projectId}")
    public List<TaskDto> getTasksByProject(@PathVariable UUID projectId,
                                          @RequestParam(required = false) TaskStatus status) {
        List<Task> tasks = (status != null) 
            ? taskService.getTasksByProjectAndStatus(projectId, status)
            : taskService.getTasksByProject(projectId);
            
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/assignee/{assigneeId}")
    public List<TaskDto> getTasksByAssignee(@PathVariable UUID assigneeId) {
        return taskService.getTasksByAssignee(assigneeId)
                .stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/{id}/subtasks")
    public List<TaskDto> getSubtasks(@PathVariable UUID id) {
        return taskService.getSubtasks(id)
                .stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/project/{projectId}/search")
    public List<TaskDto> searchTasks(@PathVariable UUID projectId,
                                    @RequestParam String query) {
        return taskService.searchTasks(projectId, query)
                .stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @PutMapping("/{id}")
    public TaskDto updateTask(@PathVariable UUID id,
                             @Valid @RequestBody UpdateTaskDto updateDto) {
        Task task = taskMapper.toEntity(updateDto);
        Task updated = taskService.updateTask(id, task);
        return taskMapper.toDto(updated);
    }
    
    @PatchMapping("/{id}/status")
    public TaskDto updateTaskStatus(@PathVariable UUID id,
                                   @Valid @RequestBody UpdateTaskStatusDto statusDto) {
        Task updated = taskService.updateTaskStatus(id, statusDto.getStatus());
        return taskMapper.toDto(updated);
    }
    
    @PatchMapping("/{id}/assign")
    public TaskDto assignTask(@PathVariable UUID id,
                             @RequestBody Map<String, UUID> request) {
        UUID assigneeId = request.get("assigneeId");
        Task updated = taskService.assignTask(id, assigneeId);
        return taskMapper.toDto(updated);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
    }
}
