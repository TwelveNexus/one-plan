import { apiClient } from "@/lib/api/client";
import type { Organization, Team, TeamMember } from "@/types";

export class OrganizationService {
  // Organization endpoints
  async getOrganizations(): Promise<Organization[]> {
    return apiClient.get<Organization[]>("/organizations");
  }

  async getOrganization(id: string): Promise<Organization> {
    return apiClient.get<Organization>(`/organizations/${id}`);
  }

  async createOrganization(data: Partial<Organization>): Promise<Organization> {
    return apiClient.post<Organization>("/organizations", data);
  }

  async updateOrganization(
    id: string,
    data: Partial<Organization>
  ): Promise<Organization> {
    return apiClient.put<Organization>(`/organizations/${id}`, data);
  }

  async deleteOrganization(id: string): Promise<void> {
    return apiClient.delete(`/organizations/${id}`);
  }

  // Team endpoints
  async getTeams(organizationId: string): Promise<Team[]> {
    return apiClient.get<Team[]>(`/organizations/${organizationId}/teams`);
  }

  async getTeam(id: string): Promise<Team> {
    return apiClient.get<Team>(`/teams/${id}`);
  }

  async createTeam(organizationId: string, data: Partial<Team>): Promise<Team> {
    return apiClient.post<Team>(`/organizations/${organizationId}/teams`, data);
  }

  async updateTeam(id: string, data: Partial<Team>): Promise<Team> {
    return apiClient.put<Team>(`/teams/${id}`, data);
  }

  async deleteTeam(id: string): Promise<void> {
    return apiClient.delete(`/teams/${id}`);
  }

  // Team member endpoints
  async getTeamMembers(teamId: string): Promise<TeamMember[]> {
    return apiClient.get<TeamMember[]>(`/teams/${teamId}/members`);
  }

  async addTeamMember(
    teamId: string,
    data: { userId: string; role: string }
  ): Promise<TeamMember> {
    return apiClient.post<TeamMember>(`/teams/${teamId}/members`, data);
  }

  async updateTeamMember(
    teamId: string,
    userId: string,
    data: { role: string }
  ): Promise<TeamMember> {
    return apiClient.put<TeamMember>(
      `/teams/${teamId}/members/${userId}/role`,
      data
    );
  }

  async removeTeamMember(teamId: string, userId: string): Promise<void> {
    return apiClient.delete(`/teams/${teamId}/members/${userId}`);
  }
}

export const organizationService = new OrganizationService();
