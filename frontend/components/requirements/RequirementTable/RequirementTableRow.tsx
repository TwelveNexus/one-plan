import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { TableCell, TableRow } from "@/components/ui/table";
import {
  getAiScoreBadge,
  getPriorityBadge,
  getStatusBadge,
  truncateText,
} from "@/lib/requirement-utils";
import type { Requirement } from "@/types";
import { MoreHorizontal } from "lucide-react";
import { useRouter } from "next/navigation";
import React from "react";
import AnalysisButton from "../RequirementAIAnalysis/AnalysisButton";

interface RequirementTableRowProps {
  requirement: Requirement;
  onAnalyzeRequirement: (id: string) => void;
}

const RequirementTableRow: React.FC<RequirementTableRowProps> = ({
  requirement,
  onAnalyzeRequirement,
}) => {
  const router = useRouter();

  return (
    <TableRow>
      <TableCell className="font-medium">
        <div className="flex flex-col">
          {requirement.title}
          {requirement.description && (
            <span className="text-muted-foreground text-xs mt-1 line-clamp-1">
              {truncateText(requirement.description, 100)}
            </span>
          )}
        </div>
      </TableCell>
      <TableCell>{getPriorityBadge(requirement.priority)}</TableCell>
      <TableCell>{getStatusBadge(requirement.status)}</TableCell>
      <TableCell>
        {requirement.category || (
          <span className="text-muted-foreground text-sm">-</span>
        )}
      </TableCell>
      <TableCell>{getAiScoreBadge(requirement.aiScore)}</TableCell>
      <TableCell className="text-right">
        <div className="flex items-center justify-end gap-2">
          <AnalysisButton
            requirementId={requirement.id}
            hasBeenAnalyzed={requirement.aiScore !== undefined}
            onAnalyze={onAnalyzeRequirement}
          />
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="sm">
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem
                onClick={() => router.push(`/requirements/${requirement.id}`)}
              >
                View Details
              </DropdownMenuItem>
              <DropdownMenuItem>Edit Requirement</DropdownMenuItem>
              <DropdownMenuItem>View History</DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem className="text-destructive">
                Delete Requirement
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </TableCell>
    </TableRow>
  );
};

export default RequirementTableRow;
