package com.twelvenexus.oneplan.task.service;

import com.twelvenexus.oneplan.task.model.Task;
import com.twelvenexus.oneplan.task.model.TaskStatus;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    Task createTask(Task task);
    Task getTask(UUID id);
    List<Task> getTasksByProject(UUID projectId);
    List<Task> getTasksByAssignee(UUID assigneeId);
    List<Task> getTasksByProjectAndStatus(UUID projectId, TaskStatus status);
    List<Task> getSubtasks(UUID parentId);
    List<Task> searchTasks(UUID projectId, String searchTerm);
    Task updateTask(UUID id, Task task);
    Task updateTaskStatus(UUID id, TaskStatus status);
    Task assignTask(UUID id, UUID assigneeId);
    void deleteTask(UUID id);
}
