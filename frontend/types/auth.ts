/* eslint-disable @typescript-eslint/no-explicit-any */
export interface User {
  id: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  passwordHash?: string;
  avatar?: string;
  status?: string;
  lastLogin?: string;
  createdAt: string;
  updatedAt?: string;
  preferences?: UserPreferences;
  roles?: string[];
}

export interface UserPreferences {
  userId: string;
  theme: 'light' | 'dark' | 'system';
  language: string;
  notificationSettings: Record<string, any>;
}

export interface UserRole {
  userId: string;
  role: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}
