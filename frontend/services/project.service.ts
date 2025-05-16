/* eslint-disable @typescript-eslint/no-explicit-any */
import { apiClient } from "@/lib/api/client";
import type { Project, Task, Comment } from "@/types";

export class ProjectService {
  // Project endpoints
  async getProjects(organizationId: string): Promise<Project[]> {
    return apiClient.get<Project[]>(
      `/organizations/${organizationId}/projects`
    );
  }

  async getProject(id: string): Promise<Project> {
    return apiClient.get<Project>(`/projects/${id}`);
  }

  async createProject(
    organizationId: string,
    data: Partial<Project>
  ): Promise<Project> {
    return apiClient.post<Project>(
      `/organizations/${organizationId}/projects`,
      data
    );
  }

  async updateProject(id: string, data: Partial<Project>): Promise<Project> {
    return apiClient.put<Project>(`/projects/${id}`, data);
  }

  async deleteProject(id: string): Promise<void> {
    return apiClient.delete(`/projects/${id}`);
  }

  async getProjectActivity(id: string): Promise<any[]> {
    return apiClient.get<any[]>(`/projects/${id}/activity`);
  }

  async getProjectStatistics(id: string): Promise<any> {
    return apiClient.get<any>(`/projects/${id}/statistics`);
  }

  // Task endpoints
  async getTasks(
    projectId: string,
    params?: { status?: string; assigneeId?: string }
  ): Promise<Task[]> {
    return apiClient.get<Task[]>(`/projects/${projectId}/tasks`, params);
  }

  async getTask(id: string): Promise<Task> {
    return apiClient.get<Task>(`/tasks/${id}`);
  }

  async createTask(projectId: string, data: Partial<Task>): Promise<Task> {
    return apiClient.post<Task>(`/projects/${projectId}/tasks`, data);
  }

  async updateTask(id: string, data: Partial<Task>): Promise<Task> {
    return apiClient.put<Task>(`/tasks/${id}`, data);
  }

  async updateTaskStatus(id: string, status: string): Promise<Task> {
    return apiClient.put<Task>(`/tasks/${id}/status`, { status });
  }

  async assignTask(id: string, assigneeId: string): Promise<Task> {
    return apiClient.put<Task>(`/tasks/${id}/assignee`, { assigneeId });
  }

  async deleteTask(id: string): Promise<void> {
    return apiClient.delete(`/tasks/${id}`);
  }

  // Comments
  async getTaskComments(taskId: string): Promise<Comment[]> {
    return apiClient.get<Comment[]>(`/tasks/${taskId}/comments`);
  }

  async createComment(
    resourceType: string,
    resourceId: string,
    data: Partial<Comment>
  ): Promise<Comment> {
    return apiClient.post<Comment>(
      `/${resourceType}/${resourceId}/comments`,
      data
    );
  }

  async updateComment(id: string, data: Partial<Comment>): Promise<Comment> {
    return apiClient.put<Comment>(`/comments/${id}`, data);
  }

  async deleteComment(id: string): Promise<void> {
    return apiClient.delete(`/comments/${id}`);
  }
}

export const projectService = new ProjectService();
