import { format, formatDistanceToNow, isToday, isYesterday } from "date-fns";

/**
 * Formats a date string to a human-readable format
 * @param dateString ISO date string
 * @param includeTime Whether to include the time
 * @returns Formatted date string
 */
export const formatDate = (
  dateString?: string,
  includeTime = false
): string => {
  if (!dateString) return "";

  const date = new Date(dateString);

  if (isToday(date)) {
    return includeTime ? `Today at ${format(date, "h:mm a")}` : "Today";
  }

  if (isYesterday(date)) {
    return includeTime ? `Yesterday at ${format(date, "h:mm a")}` : "Yesterday";
  }

  return includeTime
    ? format(date, "MMM d, yyyy h:mm a")
    : format(date, "MMM d, yyyy");
};

/**
 * Formats a date string as a relative time (e.g., "2 days ago")
 * @param dateString ISO date string
 * @returns Relative time string
 */
export const formatRelativeTime = (dateString?: string): string => {
  if (!dateString) return "";

  const date = new Date(dateString);
  return formatDistanceToNow(date, { addSuffix: true });
};

/**
 * Formats a file size in bytes to a human-readable format
 * @param bytes File size in bytes
 * @param decimals Number of decimal places
 * @returns Formatted file size string (e.g., "1.5 MB")
 */
export const formatFileSize = (bytes?: number, decimals = 2): string => {
  if (bytes === undefined || bytes === 0) return "0 Bytes";

  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ["Bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"];

  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + " " + sizes[i];
};

/**
 * Get initials from a first and last name
 * @param firstName First name
 * @param lastName Last name
 * @returns Initials (e.g., "JD" for "John Doe")
 */
export const getInitials = (firstName?: string, lastName?: string): string => {
  if (!firstName && !lastName) return "";

  const firstInitial = firstName ? firstName.charAt(0).toUpperCase() : "";
  const lastInitial = lastName ? lastName.charAt(0).toUpperCase() : "";

  return `${firstInitial}${lastInitial}`;
};

/**
 * Format a number with commas as thousands separators
 * @param num Number to format
 * @returns Formatted number string
 */
export const formatNumber = (num?: number): string => {
  if (num === undefined) return "";

  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

/**
 * Truncate text with ellipsis
 * @param text Text to truncate
 * @param length Maximum length
 * @returns Truncated text with ellipsis if needed
 */
export const truncateText = (text?: string, length = 100): string => {
  if (!text) return "";

  return text.length > length ? `${text.substring(0, length)}...` : text;
};

/**
 * Format a value as a percentage
 * @param value Value to format
 * @param decimals Number of decimal places
 * @returns Formatted percentage string
 */
export const formatPercentage = (value?: number, decimals = 0): string => {
  if (value === undefined) return "";

  return `${value.toFixed(decimals)}%`;
};

/**
 * Format a duration in seconds to a human-readable format
 * @param seconds Duration in seconds
 * @returns Formatted duration string (e.g., "1h 30m")
 */
export const formatDuration = (seconds?: number): string => {
  if (seconds === undefined || seconds < 0) return "";

  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const remainingSeconds = seconds % 60;

  let result = "";

  if (hours > 0) {
    result += `${hours}h `;
  }

  if (minutes > 0 || hours > 0) {
    result += `${minutes}m `;
  }

  if (remainingSeconds > 0 || (hours === 0 && minutes === 0)) {
    result += `${remainingSeconds}s`;
  }

  return result.trim();
};
