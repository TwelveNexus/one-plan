import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Clock, Edit, Eye, MoreHorizontal, Trash2 } from "lucide-react";
import { useRouter } from "next/navigation";
import React from "react";

interface RequirementActionsProps {
  requirementId: string;
  onEdit?: () => void;
  onDelete?: () => void;
  onViewHistory?: () => void;
}

const RequirementActions: React.FC<RequirementActionsProps> = ({
  requirementId,
  onEdit,
  onDelete,
  onViewHistory,
}) => {
  const router = useRouter();

  const handleView = () => {
    router.push(`/requirements/${requirementId}`);
  };

  const handleEdit = () => {
    if (onEdit) {
      onEdit();
    } else {
      // Default behavior if no custom handler provided
      router.push(`/requirements/${requirementId}/edit`);
    }
  };

  const handleDelete = () => {
    if (onDelete) {
      onDelete();
    }
    // No default behavior for delete since it's destructive
  };

  const handleViewHistory = () => {
    if (onViewHistory) {
      onViewHistory();
    } else {
      // Default behavior if no custom handler provided
      router.push(`/requirements/${requirementId}/history`);
    }
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="sm">
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuItem onClick={handleView}>
          <Eye className="mr-2 h-4 w-4" />
          View Details
        </DropdownMenuItem>
        <DropdownMenuItem onClick={handleEdit}>
          <Edit className="mr-2 h-4 w-4" />
          Edit Requirement
        </DropdownMenuItem>
        <DropdownMenuItem onClick={handleViewHistory}>
          <Clock className="mr-2 h-4 w-4" />
          View History
        </DropdownMenuItem>
        <DropdownMenuSeparator />
        <DropdownMenuItem
          onClick={handleDelete}
          className="text-destructive focus:text-destructive"
        >
          <Trash2 className="mr-2 h-4 w-4" />
          Delete Requirement
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default RequirementActions;
