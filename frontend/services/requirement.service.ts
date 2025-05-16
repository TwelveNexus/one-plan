import { apiClient } from "@/lib/api/client";
import type {
  Requirement,
  RequirementVersion,
  RequirementAttachment,
} from "@/types";

export class RequirementService {
  async getRequirements(projectId: string): Promise<Requirement[]> {
    return apiClient.get<Requirement[]>(`/projects/${projectId}/requirements`);
  }

  async getRequirement(id: string): Promise<Requirement> {
    return apiClient.get<Requirement>(`/requirements/${id}`);
  }

  async createRequirement(
    projectId: string,
    data: Partial<Requirement>
  ): Promise<Requirement> {
    return apiClient.post<Requirement>(
      `/projects/${projectId}/requirements`,
      data
    );
  }

  async updateRequirement(
    id: string,
    data: Partial<Requirement>
  ): Promise<Requirement> {
    return apiClient.put<Requirement>(`/requirements/${id}`, data);
  }

  async deleteRequirement(id: string): Promise<void> {
    return apiClient.delete(`/requirements/${id}`);
  }

  async analyzeRequirement(id: string): Promise<Requirement> {
    return apiClient.post<Requirement>(`/requirements/${id}/analyze`);
  }

  async getRequirementVersions(id: string): Promise<RequirementVersion[]> {
    return apiClient.get<RequirementVersion[]>(`/requirements/${id}/versions`);
  }

  async getRequirementVersion(
    id: string,
    versionId: string
  ): Promise<RequirementVersion> {
    return apiClient.get<RequirementVersion>(
      `/requirements/${id}/versions/${versionId}`
    );
  }

  async uploadAttachment(
    requirementId: string,
    file: File
  ): Promise<RequirementAttachment> {
    const formData = new FormData();
    formData.append("file", file);

    const response = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/api/v1/requirements/${requirementId}/attachments`,
      {
        method: "POST",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        body: formData,
      }
    );

    if (!response.ok) {
      throw new Error("Failed to upload attachment");
    }

    return response.json();
  }
}

export const requirementService = new RequirementService();
