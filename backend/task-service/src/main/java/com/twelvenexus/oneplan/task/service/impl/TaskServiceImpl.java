package com.twelvenexus.oneplan.task.service.impl;

import com.twelvenexus.oneplan.task.exception.TaskNotFoundException;
import com.twelvenexus.oneplan.task.model.Task;
import com.twelvenexus.oneplan.task.model.TaskStatus;
import com.twelvenexus.oneplan.task.repository.TaskRepository;
import com.twelvenexus.oneplan.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {
    
    private final TaskRepository taskRepository;
    
    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Task getTask(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByProject(UUID projectId) {
        return taskRepository.findByProjectId(projectId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByAssignee(UUID assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByProjectAndStatus(UUID projectId, TaskStatus status) {
        return taskRepository.findByProjectIdAndStatus(projectId, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Task> getSubtasks(UUID parentId) {
        return taskRepository.findByParentId(parentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Task> searchTasks(UUID projectId, String searchTerm) {
        return taskRepository.searchByTitleOrDescription(projectId, searchTerm);
    }
    
    @Override
    public Task updateTask(UUID id, Task updatedTask) {
        Task existingTask = getTask(id);
        
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setStoryPoints(updatedTask.getStoryPoints());
        existingTask.setStartDate(updatedTask.getStartDate());
        existingTask.setDueDate(updatedTask.getDueDate());
        existingTask.setEstimatedHours(updatedTask.getEstimatedHours());
        existingTask.setActualHours(updatedTask.getActualHours());
        existingTask.setTags(updatedTask.getTags());
        
        return taskRepository.save(existingTask);
    }
    
    @Override
    public Task updateTaskStatus(UUID id, TaskStatus status) {
        Task task = getTask(id);
        task.setStatus(status);
        return taskRepository.save(task);
    }
    
    @Override
    public Task assignTask(UUID id, UUID assigneeId) {
        Task task = getTask(id);
        task.setAssigneeId(assigneeId);
        return taskRepository.save(task);
    }
    
    @Override
    public void deleteTask(UUID id) {
        taskRepository.deleteById(id);
    }
}
