/* eslint-disable @typescript-eslint/no-explicit-any */
import { toast } from 'sonner';

/**
 * API Error class for handling API errors
 */
export class ApiError extends Error {
  status: number;
  data?: any;

  constructor(status: number, message: string, data?: any) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
  }
}

/**
 * Handle API errors and display appropriate toast messages
 * @param error Error object
 * @param fallbackMessage Fallback message to display if error is not an ApiError
 */
export const handleApiError = (error: unknown, fallbackMessage = 'An error occurred'): void => {
  console.error('API Error:', error);

  if (error instanceof ApiError) {
    // Handle specific status codes
    switch (error.status) {
      case 400:
        toast.error(error.message || 'Invalid request');
        break;
      case 401:
        toast.error('Authentication required. Please log in again.');
        // You could also trigger a logout or redirect to login here
        break;
      case 403:
        toast.error('You do not have permission to perform this action');
        break;
      case 404:
        toast.error(error.message || 'Resource not found');
        break;
      case 409:
        toast.error(error.message || 'Conflict with current state');
        break;
      case 422:
        toast.error(error.message || 'Validation error');
        break;
      case 429:
        toast.error('Rate limit exceeded. Please try again later.');
        break;
      case 500:
      case 502:
      case 503:
      case 504:
        toast.error('Server error. Please try again later.');
        break;
      default:
        toast.error(error.message || fallbackMessage);
    }
  } else if (error instanceof Error) {
    toast.error(error.message || fallbackMessage);
  } else {
    toast.error(fallbackMessage);
  }
};

/**
 * Parse validation errors from API response
 * @param errors Error object from API
 * @returns Object with field names as keys and error messages as values
 */
export const parseValidationErrors = (errors: any): Record<string, string> => {
  if (!errors) return {};

  const result: Record<string, string> = {};

  // Handle different validation error formats
  if (Array.isArray(errors)) {
    // Format: [{ field: "name", message: "Name is required" }, ...]
    errors.forEach(error => {
      if (error.field && error.message) {
        result[error.field] = error.message;
      }
    });
  } else if (typeof errors === 'object') {
    // Format: { name: "Name is required", email: "Invalid email" }
    Object.entries(errors).forEach(([key, value]) => {
      result[key] = Array.isArray(value) ? value[0] : value as string;
    });
  }

  return result;
};

/**
 * Check if the error is a network error
 * @param error Error object
 * @returns True if the error is a network error
 */
export const isNetworkError = (error: unknown): boolean => {
  return error instanceof Error &&
    (error.message.includes('Network Error') ||
     error.message.includes('Failed to fetch') ||
     error.message.includes('Network request failed'));
};

/**
 * Check if the error is an authentication error
 * @param error Error object
 * @returns True if the error is an authentication error
 */
export const isAuthError = (error: unknown): boolean => {
  return error instanceof ApiError && (error.status === 401 || error.status === 403);
};
