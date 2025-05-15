/* eslint-disable @typescript-eslint/no-explicit-any */
import { User } from "./auth";

export type ProjectStatus = "planning" | "active" | "completed" | "archived";
export type ProjectVisibility = "private" | "internal" | "public";
export type TaskStatus =
  | "todo"
  | "in_progress"
  | "in_review"
  | "done"
  | "cancelled";
export type TaskPriority = "low" | "medium" | "high" | "critical";

export interface Project {
  id: string;
  organizationId: string;
  name: string;
  description?: string;
  projectKey: string;
  visibility: string;
  startDate?: string;
  targetDate?: string;
  status: string;
  settings?: Record<string, any>;
  metadata?: Record<string, any>;
  createdBy: string;
  createdAt: string;
  updatedAt: string;
}

export interface Task {
  id: string;
  projectId: string;
  title: string;
  description: string;
  status: TaskStatus;
  priority: TaskPriority;
  assigneeId?: string;
  reporterId: string;
  storyPoints?: number;
  startDate?: string;
  dueDate?: string;
  estimatedHours?: number;
  actualHours?: number;
  tags: string[];
  attachments: Attachment[];
  parentId?: string;
  dependsOn: string[];
  createdAt: string;
  updatedAt: string;
}

export interface Attachment {
  id: string;
  name: string;
  url: string;
  contentType?: string;
  size?: number;
}

export interface Comment {
  id: string;
  resourceType: "task" | "requirement" | "storyboard";
  resourceId: string;
  content: string;
  format: "text" | "markdown" | "html";
  mentions: string[];
  attachments: Attachment[];
  parentId?: string;
  authorId: string;
  isEdited: boolean;
  reactions: Reaction[];
  isResolved: boolean;
  resolvedBy?: string;
  resolvedAt?: string;
  createdAt: string;
  updatedAt: string;
  author?: User;
}

export interface Reaction {
  emoji: string;
  count: number;
  users: string[];
}
