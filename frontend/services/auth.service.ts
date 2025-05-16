import { apiClient } from "@/lib/api/client";
import type {
  User,
  AuthResponse,
  LoginRequest,
  RegisterRequest,
} from "@/types";

export class AuthService {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    return apiClient.post<AuthResponse>("/auth/login", credentials);
  }

  async register(data: RegisterRequest): Promise<AuthResponse> {
    return apiClient.post<AuthResponse>("/auth/register", data);
  }

  async logout(): Promise<void> {
    return apiClient.post("/auth/logout");
  }

  async refreshToken(refreshToken: string): Promise<AuthResponse> {
    return apiClient.post<AuthResponse>("/auth/refresh", { refreshToken });
  }

  async getMe(): Promise<User> {
    return apiClient.get<User>("/users/me");
  }

  async updateProfile(data: Partial<User>): Promise<User> {
    return apiClient.put<User>("/users/me", data);
  }

  // Helper methods for token management
  setTokens(tokens: { token: string; refreshToken: string }) {
    localStorage.setItem("token", tokens.token);
    localStorage.setItem("refreshToken", tokens.refreshToken);
  }

  clearTokens() {
    localStorage.removeItem("token");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("user");
  }

  getToken(): string | null {
    return localStorage.getItem("token");
  }

  getRefreshToken(): string | null {
    return localStorage.getItem("refreshToken");
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}

export const authService = new AuthService();
