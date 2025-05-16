import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  getAiScoreBadge,
  getPriorityBadge,
  getStatusBadge,
} from "@/lib/requirement-utils";
import type { Requirement } from "@/types";
import { format } from "date-fns";
import { CalendarClock, FileText, MoreHorizontal } from "lucide-react";
import { useRouter } from "next/navigation";
import React from "react";
import AnalysisButton from "../RequirementAIAnalysis/AnalysisButton";

interface RequirementCardProps {
  requirement: Requirement;
  onAnalyzeRequirement: (id: string) => void;
}

const RequirementCard: React.FC<RequirementCardProps> = ({
  requirement,
  onAnalyzeRequirement,
}) => {
  const router = useRouter();

  return (
    <Card className="transition-shadow hover:shadow-md">
      <CardHeader className="pb-2">
        <div className="flex items-start justify-between">
          <div>
            <CardTitle className="text-lg">{requirement.title}</CardTitle>
            {requirement.category && (
              <div className="text-sm text-muted-foreground mt-1">
                Category: {requirement.category}
              </div>
            )}
          </div>
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
      </CardHeader>
      <CardContent>
        {requirement.description ? (
          <CardDescription className="text-sm line-clamp-3 mb-4">
            {requirement.description}
          </CardDescription>
        ) : (
          <div className="text-sm text-muted-foreground mb-4">
            No description provided
          </div>
        )}
        <div className="flex flex-wrap gap-2 mb-4">
          {getPriorityBadge(requirement.priority)}
          {getStatusBadge(requirement.status)}
        </div>
        <div className="flex items-center justify-between">
          <div className="text-sm text-muted-foreground flex items-center gap-1">
            <CalendarClock className="h-3 w-3" />
            <span>
              Updated {format(new Date(requirement.updatedAt), "MMM d, yyyy")}
            </span>
          </div>
          <div className="text-sm text-muted-foreground flex items-center gap-1">
            <FileText className="h-3 w-3" />
            <span>v{requirement.version || 1}</span>
          </div>
        </div>
      </CardContent>
      <CardFooter className="flex justify-between pt-2 border-t">
        <div>
          <div className="text-sm font-medium">AI Score</div>
          <div className="mt-1">{getAiScoreBadge(requirement.aiScore)}</div>
        </div>
        <AnalysisButton
          requirementId={requirement.id}
          hasBeenAnalyzed={requirement.aiScore !== undefined}
          onAnalyze={onAnalyzeRequirement}
        />
      </CardFooter>
    </Card>
  );
};

export default RequirementCard;
