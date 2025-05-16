import React from 'react';
import { TableRow, TableCell } from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { MoreHorizontal, User } from 'lucide-react';
import { format } from 'date-fns';
import type { Task } from '@/types';
import { getStatusBadge, getPriorityBadge, getStatusTransitions } from '@/lib/task-utils';

interface TaskListItemProps {
  task: Task;
  onUpdateStatus: (taskId: string, status: string) => void;
}

const TaskListItem: React.FC<TaskListItemProps> = ({ task, onUpdateStatus }) => {
  return (
    <TableRow className="hover:bg-muted/20">
      <TableCell>
        <div>
          <div className="font-medium">{task.title}</div>
          {task.description && (
            <div className="text-sm text-muted-foreground line-clamp-1 mt-1">
              {task.description}
            </div>
          )}
        </div>
      </TableCell>
      <TableCell>{getStatusBadge(task.status)}</TableCell>
      <TableCell>{getPriorityBadge(task.priority)}</TableCell>
      <TableCell>
        {task.dueDate ? format(new Date(task.dueDate), "MMM d, yyyy") : "-"}
      </TableCell>
      <TableCell>
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 rounded-full bg-muted flex items-center justify-center">
            <User className="h-3 w-3 text-muted-foreground" />
          </div>
          <span>{task.assigneeId || "Unassigned"}</span>
        </div>
      </TableCell>
      <TableCell className="text-right">
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="sm">
              <MoreHorizontal className="h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem>View Details</DropdownMenuItem>
            <DropdownMenuItem>Edit Task</DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem>Add Comment</DropdownMenuItem>
            <DropdownMenuSeparator />
            {getStatusTransitions(task).map((transition) => (
              <DropdownMenuItem
                key={transition.status}
                onClick={() => onUpdateStatus(task.id, transition.status)}
              >
                {transition.label}
              </DropdownMenuItem>
            ))}
          </DropdownMenuContent>
        </DropdownMenu>
      </TableCell>
    </TableRow>
  );
};

export default TaskListItem;
