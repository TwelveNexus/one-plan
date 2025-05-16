import React from 'react';
import { Button } from '@/components/ui/button';
import { Sparkles } from 'lucide-react';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";

interface AnalysisButtonProps {
  requirementId: string;
  hasBeenAnalyzed: boolean;
  onAnalyze: (id: string) => void;
  disabled?: boolean;
}

const AnalysisButton: React.FC<AnalysisButtonProps> = ({
  requirementId,
  hasBeenAnalyzed,
  onAnalyze,
  disabled = false
}) => {
  if (hasBeenAnalyzed) {
    return (
      <TooltipProvider>
        <Tooltip>
          <TooltipTrigger asChild>
            <Button variant="outline" size="sm" disabled className="text-muted-foreground">
              <Sparkles className="h-4 w-4 mr-1" />
              Analyzed
            </Button>
          </TooltipTrigger>
          <TooltipContent>
            <p>This requirement has already been analyzed</p>
          </TooltipContent>
        </Tooltip>
      </TooltipProvider>
    );
  }

  return (
    <Button
      variant="outline"
      size="sm"
      onClick={() => onAnalyze(requirementId)}
      disabled={disabled}
    >
      <Sparkles className="h-4 w-4 mr-1" />
      Analyze
    </Button>
  );
};

export default AnalysisButton;
