import { apiClient } from "@/lib/api/client";
import type {
  Storyboard,
  StoryCard,
  StoryRelationship,
  StoryboardCanvas,
} from "@/types";

export class StoryboardService {
  // Storyboard endpoints
  async getStoryboards(projectId: string): Promise<Storyboard[]> {
    return apiClient.get<Storyboard[]>(`/projects/${projectId}/storyboards`);
  }

  async getStoryboard(id: string): Promise<Storyboard> {
    return apiClient.get<Storyboard>(`/storyboards/${id}`);
  }

  async createStoryboard(
    projectId: string,
    data: Partial<Storyboard>
  ): Promise<Storyboard> {
    return apiClient.post<Storyboard>(
      `/projects/${projectId}/storyboards`,
      data
    );
  }

  async updateStoryboard(
    id: string,
    data: Partial<Storyboard>
  ): Promise<Storyboard> {
    return apiClient.put<Storyboard>(`/storyboards/${id}`, data);
  }

  async deleteStoryboard(id: string): Promise<void> {
    return apiClient.delete(`/storyboards/${id}`);
  }

  async generateStoryboard(id: string): Promise<Storyboard> {
    return apiClient.post<Storyboard>(`/storyboards/${id}/generate`);
  }

  async shareStoryboard(
    id: string,
    data: { password?: string; expiresAt?: string }
  ): Promise<{ shareUrl: string }> {
    return apiClient.post<{ shareUrl: string }>(
      `/storyboards/${id}/share`,
      data
    );
  }

  // Story card endpoints
  async getStoryCards(storyboardId: string): Promise<StoryCard[]> {
    return apiClient.get<StoryCard[]>(`/storyboards/${storyboardId}/stories`);
  }

  async createStoryCard(
    storyboardId: string,
    data: Partial<StoryCard>
  ): Promise<StoryCard> {
    return apiClient.post<StoryCard>(
      `/storyboards/${storyboardId}/stories`,
      data
    );
  }

  async updateStoryCard(
    storyboardId: string,
    cardId: string,
    data: Partial<StoryCard>
  ): Promise<StoryCard> {
    return apiClient.put<StoryCard>(
      `/storyboards/${storyboardId}/stories/${cardId}`,
      data
    );
  }

  async deleteStoryCard(storyboardId: string, cardId: string): Promise<void> {
    return apiClient.delete(`/storyboards/${storyboardId}/stories/${cardId}`);
  }

  // Canvas operations
  async getCanvas(storyboardId: string): Promise<StoryboardCanvas> {
    return apiClient.get<StoryboardCanvas>(
      `/storyboards/${storyboardId}/canvas`
    );
  }

  async updateCanvas(
    storyboardId: string,
    data: Partial<StoryboardCanvas>
  ): Promise<StoryboardCanvas> {
    return apiClient.put<StoryboardCanvas>(
      `/storyboards/${storyboardId}/canvas`,
      data
    );
  }

  // Relationships
  async createRelationship(
    storyboardId: string,
    data: Partial<StoryRelationship>
  ): Promise<StoryRelationship> {
    return apiClient.post<StoryRelationship>(
      `/storyboards/${storyboardId}/relationships`,
      data
    );
  }

  async deleteRelationship(
    storyboardId: string,
    relationshipId: string
  ): Promise<void> {
    return apiClient.delete(
      `/storyboards/${storyboardId}/relationships/${relationshipId}`
    );
  }
}

export const storyboardService = new StoryboardService();
