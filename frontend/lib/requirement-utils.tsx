import { Badge } from '@/components/ui/badge';
import type { Requirement } from '@/types';

/**
 * Generates a status badge with appropriate styling
 * @param status Requirement status
 * @returns A properly styled Badge component for the given status
 */
export const getStatusBadge = (status: string) => {
  switch (status) {
    case 'draft':
      return <Badge variant="outline" className="bg-gray-50 text-gray-700 hover:bg-gray-50">Draft</Badge>;
    case 'review':
      return <Badge variant="outline" className="bg-blue-50 text-blue-700 hover:bg-blue-50">Review</Badge>;
    case 'approved':
      return <Badge variant="outline" className="bg-green-50 text-green-700 hover:bg-green-50">Approved</Badge>;
    case 'implemented':
      return <Badge variant="outline" className="bg-purple-50 text-purple-700 hover:bg-purple-50">Implemented</Badge>;
    case 'deprecated':
      return <Badge variant="outline" className="bg-red-50 text-red-700 hover:bg-red-50">Deprecated</Badge>;
    default:
      return <Badge variant="outline">{status}</Badge>;
  }
};

/**
 * Generates a priority badge with appropriate styling
 * @param priority Requirement priority
 * @returns A properly styled Badge component for the given priority
 */
export const getPriorityBadge = (priority: string) => {
  switch (priority) {
    case 'low':
      return <Badge variant="outline" className="bg-blue-50 text-blue-700 hover:bg-blue-50">Low</Badge>;
    case 'medium':
      return <Badge variant="outline" className="bg-yellow-50 text-yellow-700 hover:bg-yellow-50">Medium</Badge>;
    case 'high':
      return <Badge variant="outline" className="bg-orange-50 text-orange-700 hover:bg-orange-50">High</Badge>;
    case 'critical':
      return <Badge variant="outline" className="bg-red-50 text-red-700 hover:bg-red-50">Critical</Badge>;
    default:
      return <Badge variant="outline">{priority}</Badge>;
  }
};

/**
 * Generates an AI score badge with appropriate styling
 * @param score AI analysis score
 * @returns A properly styled Badge component for the AI score
 */
export const getAiScoreBadge = (score?: number) => {
  if (score === undefined || score === null) {
    return <span className="text-muted-foreground text-sm">Not analyzed</span>;
  }

  if (score >= 80) {
    return <Badge className="bg-green-100 text-green-800">Excellent ({score}%)</Badge>;
  } else if (score >= 60) {
    return <Badge className="bg-blue-100 text-blue-800">Good ({score}%)</Badge>;
  } else if (score >= 40) {
    return <Badge className="bg-yellow-100 text-yellow-800">Fair ({score}%)</Badge>;
  } else {
    return <Badge className="bg-red-100 text-red-800">Poor ({score}%)</Badge>;
  }
};

/**
 * Truncates text to a specified length with ellipsis
 * @param text Text to truncate
 * @param length Maximum length
 * @returns Truncated text
 */
export const truncateText = (text?: string, length = 100): string => {
  if (!text) return '';
  return text.length > length ? `${text.substring(0, length)}...` : text;
};

/**
 * Filters requirements by status
 * @param requirements Array of requirements
 * @param status Status to filter by
 * @returns Filtered array of requirements
 */
export const getRequirementsByStatus = (requirements: Requirement[], status: string): Requirement[] => {
  return status === 'all'
    ? requirements
    : requirements.filter(requirement => requirement.status === status);
};

/**
 * Gets unique categories from requirements array
 * @param requirements Array of requirements
 * @returns Array of unique categories
 */
export const getUniqueCategories = (requirements: Requirement[]): string[] => {
  return ['all', ...new Set(requirements.map(req => req.category).filter(Boolean) as string[])];
};
