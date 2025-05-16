/* eslint-disable @typescript-eslint/no-explicit-any */
import {
  getToken,
  getRefreshToken,
  setTokens,
  clearAuth,
  isTokenExpired,
} from "@/lib/auth-utils";
import { ApiError } from "@/lib/error-utils";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";

class ApiClient {
  private async refreshAuthToken(): Promise<string> {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
      clearAuth();
      throw new ApiError(401, "Authentication required");
    }

    try {
      const response = await fetch(`${API_BASE_URL}/auth/refresh`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ refreshToken }),
      });

      if (!response.ok) {
        clearAuth();
        throw new ApiError(response.status, "Failed to refresh token");
      }

      const data = await response.json();
      setTokens(data.token, data.refreshToken);
      return data.token;
    } catch (error) {
      clearAuth();
      throw error;
    }
  }

  private async getHeaders(requiresAuth = true): Promise<HeadersInit> {
    const headers: HeadersInit = {
      "Content-Type": "application/json",
    };

    if (requiresAuth) {
      let token = getToken();

      // If token exists but is expired, try to refresh it
      if (token && isTokenExpired(token)) {
        token = await this.refreshAuthToken();
      }

      if (!token) {
        throw new ApiError(401, "Authentication required");
      }

      headers["Authorization"] = `Bearer ${token}`;
    }

    return headers;
  }

  private async handleResponse<T>(response: Response): Promise<T> {
    const contentType = response.headers.get("content-type");
    const isJson = contentType && contentType.includes("application/json");

    const data = isJson ? await response.json() : await response.text();

    if (!response.ok) {
      throw new ApiError(
        response.status,
        isJson && data.message ? data.message : response.statusText,
        isJson ? data : undefined
      );
    }

    return data;
  }

  async get<T>(
    endpoint: string,
    params?: Record<string, any>,
    requiresAuth = true
  ): Promise<T> {
    const url = new URL(`${API_BASE_URL}${endpoint}`);
    if (params) {
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          url.searchParams.append(key, value.toString());
        }
      });
    }

    const headers = await this.getHeaders(requiresAuth);
    const response = await fetch(url.toString(), {
      method: "GET",
      headers,
    });

    return this.handleResponse<T>(response);
  }

  async post<T>(endpoint: string, data?: any, requiresAuth = true): Promise<T> {
    const headers = await this.getHeaders(requiresAuth);
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "POST",
      headers,
      body: data ? JSON.stringify(data) : undefined,
    });

    return this.handleResponse<T>(response);
  }

  async put<T>(endpoint: string, data?: any, requiresAuth = true): Promise<T> {
    const headers = await this.getHeaders(requiresAuth);
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "PUT",
      headers,
      body: data ? JSON.stringify(data) : undefined,
    });

    return this.handleResponse<T>(response);
  }

  async patch<T>(
    endpoint: string,
    data?: any,
    requiresAuth = true
  ): Promise<T> {
    const headers = await this.getHeaders(requiresAuth);
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "PATCH",
      headers,
      body: data ? JSON.stringify(data) : undefined,
    });

    return this.handleResponse<T>(response);
  }

  async delete<T>(endpoint: string, requiresAuth = true): Promise<T> {
    const headers = await this.getHeaders(requiresAuth);
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "DELETE",
      headers,
    });

    return this.handleResponse<T>(response);
  }

  async upload<T>(
    endpoint: string,
    file: File,
    additionalData?: Record<string, any>,
    requiresAuth = true
  ): Promise<T> {
    const formData = new FormData();
    formData.append("file", file);

    if (additionalData) {
      Object.entries(additionalData).forEach(([key, value]) => {
        formData.append(key, value.toString());
      });
    }

    // For file uploads, we only set the Authorization header
    const headers: HeadersInit = {};
    if (requiresAuth) {
      let token = getToken();

      if (token && isTokenExpired(token)) {
        token = await this.refreshAuthToken();
      }

      if (!token) {
        throw new ApiError(401, "Authentication required");
      }

      headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "POST",
      headers,
      body: formData,
    });

    return this.handleResponse<T>(response);
  }
}

export const apiClient = new ApiClient();
