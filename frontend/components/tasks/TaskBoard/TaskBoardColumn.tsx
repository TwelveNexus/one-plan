import React from "react";
import TaskCard from "./TaskCard";
import type { Task } from "@/types";
import { getTasksByStatus } from "@/lib/task-utils";

interface TaskBoardColumnProps {
  title: string;
  statusKey: string;
  tasks: Task[];
  color: string;
  onUpdateStatus: (taskId: string, status: string) => void;
  nextStatus?: string;
  nextStatusLabel?: string;
}

const TaskBoardColumn: React.FC<TaskBoardColumnProps> = ({
  title,
  statusKey,
  tasks,
  color,
  onUpdateStatus,
  nextStatus,
  nextStatusLabel,
}) => {
  const filteredTasks = getTasksByStatus(tasks, statusKey);

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between pb-2 border-b">
        <div className="flex items-center gap-2">
          <div className={`w-4 h-4 rounded-full ${color}`}></div>
          <h3 className="font-medium">{title}</h3>
        </div>
        <span className="px-2 py-0.5 text-xs rounded-full bg-gray-100 text-gray-800">
          {filteredTasks.length}
        </span>
      </div>

      {filteredTasks.map((task) => (
        <TaskCard
          key={task.id}
          task={task}
          onUpdateStatus={onUpdateStatus}
          nextStatus={nextStatus}
          nextStatusLabel={nextStatusLabel}
        />
      ))}

      {filteredTasks.length === 0 && (
        <div className="flex flex-col items-center justify-center p-4 border border-dashed rounded-md text-center">
          <p className="text-sm text-muted-foreground">No tasks</p>
        </div>
      )}
    </div>
  );
};

export default TaskBoardColumn;
