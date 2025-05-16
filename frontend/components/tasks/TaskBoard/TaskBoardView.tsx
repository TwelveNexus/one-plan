import { getKanbanColumns } from "@/lib/task-utils";
import type { Task } from "@/types";
import React from "react";
import TaskBoardColumn from "./TaskBoardColumn";

interface TaskBoardViewProps {
  tasks: Task[];
  onUpdateStatus: (taskId: string, status: string) => void;
}

const TaskBoardView: React.FC<TaskBoardViewProps> = ({
  tasks,
  onUpdateStatus,
}) => {
  const columns = getKanbanColumns();

  return (
    <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
      {columns.map((column) => (
        <TaskBoardColumn
          key={column.statusKey}
          title={column.title}
          statusKey={column.statusKey}
          tasks={tasks}
          color={column.color}
          onUpdateStatus={onUpdateStatus}
          nextStatus={column.nextStatus}
          nextStatusLabel={column.nextStatusLabel}
        />
      ))}
    </div>
  );
};

export default TaskBoardView;
