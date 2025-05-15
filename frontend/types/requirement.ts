/* eslint-disable @typescript-eslint/no-explicit-any */
export interface Requirement {
  id: string;
  projectId: string;
  title: string;
  description?: string;
  format: "text" | "markdown" | "html";
  status: "draft" | "review" | "approved" | "implemented" | "deprecated";
  priority: "low" | "medium" | "high" | "critical";
  category?: string;
  tags: string[];
  aiScore?: number;
  aiSuggestions?: Record<string, any>;
  version: number;
  isDeleted: boolean;
  createdBy?: string;
  createdAt: string;
  updatedAt: string;
  attachments?: RequirementAttachment[];
}

export interface RequirementVersion {
  id: string;
  requirementId: string;
  versionNumber: number;
  title: string;
  description?: string;
  changedBy?: string;
  changeSummary?: string;
  createdAt: string;
}

export interface RequirementAttachment {
  id: string;
  requirementId: string;
  fileName: string;
  fileUrl: string;
  contentType?: string;
  fileSize?: number;
  uploadedBy?: string;
  uploadedAt: string;
}
