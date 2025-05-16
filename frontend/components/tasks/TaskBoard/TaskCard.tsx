import React from "react";
import { Card, CardContent } from "@/components/ui/card";
import { MoreHorizontal, User, Calendar, ArrowRightCircle } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { format } from "date-fns";
import type { Task } from "@/types";
import { getPriorityBadge, getStatusTransitions } from "@/lib/task-utils";
import { Button } from "@/components/ui/button";

interface TaskCardProps {
  task: Task;
  onUpdateStatus: (taskId: string, status: string) => void;
  nextStatus?: string;
  nextStatusLabel?: string;
}

const TaskCard: React.FC<TaskCardProps> = ({
  task,
  onUpdateStatus,
  nextStatus,
}) => {
  return (
    <Card className="transition-shadow hover:shadow-md">
      <CardContent className="p-4">
        <div className="flex items-start justify-between">
          <h4 className="font-medium">{task.title}</h4>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="sm" className="h-8 w-8 p-0">
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem>View Details</DropdownMenuItem>
              <DropdownMenuItem>Edit Task</DropdownMenuItem>
              <DropdownMenuSeparator />
              {getStatusTransitions(task).map((transition) => (
                <DropdownMenuItem
                  key={transition.status}
                  onClick={() => onUpdateStatus(task.id, transition.status)}
                >
                  {transition.status === nextStatus && (
                    <ArrowRightCircle className="mr-2 h-4 w-4" />
                  )}
                  {transition.label}
                </DropdownMenuItem>
              ))}
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
        {task.description && (
          <p className="text-sm text-muted-foreground mt-2 line-clamp-2">
            {task.description}
          </p>
        )}
        <div className="mt-4 flex items-center justify-between text-sm">
          <div className="flex items-center gap-2">
            {getPriorityBadge(task.priority)}
          </div>
          <div className="flex items-center gap-1 text-muted-foreground">
            <User className="h-3 w-3" />
            <span>{task.assigneeId ? "Assigned" : "Unassigned"}</span>
          </div>
        </div>
        {task.dueDate && (
          <div className="mt-2 flex items-center gap-1 text-sm text-muted-foreground">
            <Calendar className="h-3 w-3" />
            <span>Due {format(new Date(task.dueDate), "MMM d")}</span>
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default TaskCard;
