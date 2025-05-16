import { Badge } from "@/components/ui/badge";
import type { Task } from "@/types";

/**
 * Generates a status badge with appropriate styling
 * @param status Task status
 * @returns A properly styled Badge component for the given status
 */
export const getStatusBadge = (status: string) => {
  switch (status) {
    case "todo":
      return (
        <Badge
          variant="outline"
          className="bg-gray-50 text-gray-700 hover:bg-gray-50"
        >
          To Do
        </Badge>
      );
    case "in_progress":
      return (
        <Badge
          variant="outline"
          className="bg-blue-50 text-blue-700 hover:bg-blue-50"
        >
          In Progress
        </Badge>
      );
    case "in_review":
      return (
        <Badge
          variant="outline"
          className="bg-purple-50 text-purple-700 hover:bg-purple-50"
        >
          In Review
        </Badge>
      );
    case "done":
      return (
        <Badge
          variant="outline"
          className="bg-green-50 text-green-700 hover:bg-green-50"
        >
          Done
        </Badge>
      );
    case "cancelled":
      return (
        <Badge
          variant="outline"
          className="bg-red-50 text-red-700 hover:bg-red-50"
        >
          Cancelled
        </Badge>
      );
    default:
      return <Badge variant="outline">{status}</Badge>;
  }
};

/**
 * Generates a priority badge with appropriate styling
 * @param priority Task priority
 * @returns A properly styled Badge component for the given priority
 */
export const getPriorityBadge = (priority: string) => {
  switch (priority) {
    case "low":
      return (
        <Badge
          variant="outline"
          className="bg-blue-50 text-blue-700 hover:bg-blue-50"
        >
          Low
        </Badge>
      );
    case "medium":
      return (
        <Badge
          variant="outline"
          className="bg-yellow-50 text-yellow-700 hover:bg-yellow-50"
        >
          Medium
        </Badge>
      );
    case "high":
      return (
        <Badge
          variant="outline"
          className="bg-orange-50 text-orange-700 hover:bg-orange-50"
        >
          High
        </Badge>
      );
    case "critical":
      return (
        <Badge
          variant="outline"
          className="bg-red-50 text-red-700 hover:bg-red-50"
        >
          Critical
        </Badge>
      );
    default:
      return <Badge variant="outline">{priority}</Badge>;
  }
};

/**
 * Gets possible status transitions for a task
 * @param task Current task
 * @returns Array of possible status transitions with their labels
 */
export const getStatusTransitions = (task: Task) => {
  switch (task.status) {
    case "todo":
      return [{ status: "in_progress", label: "Move to In Progress" }];
    case "in_progress":
      return [
        { status: "todo", label: "Move to To Do" },
        { status: "in_review", label: "Move to In Review" },
      ];
    case "in_review":
      return [
        { status: "in_progress", label: "Move to In Progress" },
        { status: "done", label: "Move to Done" },
      ];
    case "done":
      return [{ status: "in_review", label: "Move to In Review" }];
    default:
      return [];
  }
};

/**
 * Filters tasks by status
 * @param tasks Array of tasks
 * @param status Status to filter by
 * @returns Filtered array of tasks
 */
export const getTasksByStatus = (tasks: Task[], status: string): Task[] => {
  return tasks.filter((task) => task.status === status);
};

/**
 * Gets the column configuration for the kanban board
 * @returns Array of column configurations
 */
export const getKanbanColumns = () => {
  return [
    {
      title: "To Do",
      statusKey: "todo",
      color: "bg-gray-400",
      nextStatus: "in_progress",
      nextStatusLabel: "Move to In Progress",
    },
    {
      title: "In Progress",
      statusKey: "in_progress",
      color: "bg-blue-400",
      nextStatus: "in_review",
      nextStatusLabel: "Move to In Review",
    },
    {
      title: "In Review",
      statusKey: "in_review",
      color: "bg-purple-400",
      nextStatus: "done",
      nextStatusLabel: "Move to Done",
    },
    {
      title: "Done",
      statusKey: "done",
      color: "bg-green-400",
      nextStatus: undefined,
      nextStatusLabel: undefined,
    },
  ];
};
