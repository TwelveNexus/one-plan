import { User } from "@/types";

/**
 * Get the stored authentication token
 * @returns The authentication token or null if not found
 */
export const getToken = (): string | null => {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("token");
};

/**
 * Get the stored refresh token
 * @returns The refresh token or null if not found
 */
export const getRefreshToken = (): string | null => {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("refreshToken");
};

/**
 * Store authentication tokens
 * @param token The authentication token
 * @param refreshToken The refresh token
 */
export const setTokens = (token: string, refreshToken: string): void => {
  localStorage.setItem("token", token);
  localStorage.setItem("refreshToken", refreshToken);
};

/**
 * Clear all authentication data from storage
 */
export const clearAuth = (): void => {
  localStorage.removeItem("token");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("user");
};

/**
 * Check if the user is authenticated
 * @returns True if the user is authenticated
 */
export const isAuthenticated = (): boolean => {
  return !!getToken();
};

/**
 * Get the current user from storage
 * @returns The current user or null if not found
 */
export const getCurrentUser = (): User | null => {
  if (typeof window === "undefined") return null;

  const userJson = localStorage.getItem("user");
  if (!userJson) return null;

  try {
    return JSON.parse(userJson) as User;
  } catch (error) {
    console.error("Failed to parse user JSON:", error);
    return null;
  }
};

/**
 * Store the current user
 * @param user The user object to store
 */
export const setCurrentUser = (user: User): void => {
  localStorage.setItem("user", JSON.stringify(user));
};

/**
 * Check if the token is expired
 * @param token JWT token to check
 * @returns True if the token is expired
 */
export const isTokenExpired = (token: string): boolean => {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );

    const { exp } = JSON.parse(jsonPayload);

    // Add a 10 second buffer to account for network latency
    return Date.now() >= exp * 1000 - 10000;
  } catch (error) {
    console.error("Failed to check token expiration:", error);
    return true; // Assume expired on error
  }
};

/**
 * Get the user's role from the token
 * @param token JWT token
 * @returns The user's role or null if not found
 */
export const getRoleFromToken = (token: string): string | null => {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );

    const { role } = JSON.parse(jsonPayload);
    return role || null;
  } catch (error) {
    console.error("Failed to extract role from token:", error);
    return null;
  }
};

/**
 * Check if the user has a specific role
 * @param role The role to check
 * @returns True if the user has the role
 */
export const hasRole = (role: string): boolean => {
  const token = getToken();
  if (!token) return false;

  const userRole = getRoleFromToken(token);
  return userRole === role;
};

/**
 * Check if the user has one of the specified roles
 * @param roles The roles to check
 * @returns True if the user has one of the roles
 */
export const hasAnyRole = (roles: string[]): boolean => {
  const token = getToken();
  if (!token) return false;

  const userRole = getRoleFromToken(token);
  return userRole ? roles.includes(userRole) : false;
};
