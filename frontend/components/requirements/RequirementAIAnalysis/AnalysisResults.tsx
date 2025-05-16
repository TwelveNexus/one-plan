/* eslint-disable @typescript-eslint/no-explicit-any */
import React from 'react';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import type { Requirement } from '@/types';

interface AnalysisResultsProps {
  requirement: Requirement;
}

const AnalysisResults: React.FC<AnalysisResultsProps> = ({ requirement }) => {
  if (!requirement.aiScore && !requirement.aiSuggestions) {
    return null;
  }

  // Extract AI analysis scores and suggestions
  const aiScore = requirement.aiScore || 0;
  const aiSuggestions = requirement.aiSuggestions || {};

  // Determine color based on score
  const getScoreColor = (score: number) => {
    if (score >= 80) return 'bg-green-500';
    if (score >= 60) return 'bg-blue-500';
    if (score >= 40) return 'bg-yellow-500';
    return 'bg-red-500';
  };

  // Generate feedback based on score
  const getFeedback = (score: number) => {
    if (score >= 80) return 'Excellent! This requirement is well-defined and clear.';
    if (score >= 60) return 'Good requirement, but has some room for improvement.';
    if (score >= 40) return 'Fair requirement. Consider addressing the suggestions below.';
    return 'This requirement needs significant improvement.';
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">AI Analysis Results</CardTitle>
        <CardDescription>
          Automated assessment of requirement quality
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="space-y-2">
          <div className="flex justify-between">
            <span className="text-sm font-medium">Overall Score</span>
            <span className="text-sm font-medium">{aiScore}%</span>
          </div>
          <Progress value={aiScore} className={getScoreColor(aiScore)} />
          <p className="text-sm text-muted-foreground mt-2">
            {getFeedback(aiScore)}
          </p>
        </div>

        {/* If there are detailed breakdown scores, show them */}
        {aiSuggestions && Object.keys(aiSuggestions).length > 0 && (
          <div className="space-y-4 border-t pt-4 mt-4">
            <h4 className="font-medium">Analysis Details</h4>
            <div className="space-y-3">
              {(aiSuggestions as any).clarity !== undefined && (
                <div className="space-y-1">
                  <div className="flex justify-between">
                    <span className="text-sm">Clarity</span>
                    <span className="text-sm">{(aiSuggestions as any).clarity}%</span>
                  </div>
                  <Progress value={(aiSuggestions as any).clarity} className={getScoreColor((aiSuggestions as any).clarity)} />
                </div>
              )}

              {(aiSuggestions as any).completeness !== undefined && (
                <div className="space-y-1">
                  <div className="flex justify-between">
                    <span className="text-sm">Completeness</span>
                    <span className="text-sm">{(aiSuggestions as any).completeness}%</span>
                  </div>
                  <Progress value={(aiSuggestions as any).completeness} className={getScoreColor((aiSuggestions as any).completeness)} />
                </div>
              )}

              {(aiSuggestions as any).testability !== undefined && (
                <div className="space-y-1">
                  <div className="flex justify-between">
                    <span className="text-sm">Testability</span>
                    <span className="text-sm">{(aiSuggestions as any).testability}%</span>
                  </div>
                  <Progress value={(aiSuggestions as any).testability} className={getScoreColor((aiSuggestions as any).testability)} />
                </div>
              )}
            </div>
          </div>
        )}

        {/* If there are improvement suggestions, show them */}
        {(aiSuggestions as any).suggestions && (
          <div className="space-y-3 border-t pt-4 mt-4">
            <h4 className="font-medium">Improvement Suggestions</h4>
            <ul className="space-y-2">
              {((aiSuggestions as any).suggestions as string[]).map((suggestion, index) => (
                <li key={index} className="text-sm pl-4 border-l-2 border-primary/20">
                  {suggestion}
                </li>
              ))}
            </ul>
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default AnalysisResults;
