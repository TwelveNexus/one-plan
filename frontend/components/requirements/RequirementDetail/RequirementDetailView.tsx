import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle
} from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import type { Requirement, RequirementVersion } from '@/types';
import { getPriorityBadge, getStatusBadge } from '@/lib/requirement-utils';
import { format } from 'date-fns';
import { Calendar, Clock, FileText, User } from 'lucide-react';
import React from 'react';
import AnalysisResults from '../RequirementAIAnalysis/AnalysisResults';
import RequirementComments from './RequirementComments';
import RequirementHistory from './RequirementHistory';

interface RequirementDetailViewProps {
  requirement: Requirement;
  versions?: RequirementVersion[];
  comments?: any[]; // Replace with your comment type
}

const RequirementDetailView: React.FC<RequirementDetailViewProps> = ({
  requirement,
  versions = [],
  comments = []
}) => {
  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <div>
              <CardTitle className="text-2xl">{requirement.title}</CardTitle>
              {requirement.category && (
                <CardDescription className="mt-1">
                  Category: {requirement.category}
                </CardDescription>
              )}
            </div>
            <div className="flex items-center gap-3">
              {getPriorityBadge(requirement.priority)}
              {getStatusBadge(requirement.status)}
            </div>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          {requirement.description ? (
            <div className="prose max-w-none dark:prose-invert">
              {requirement.format === 'markdown' ? (
                <div dangerouslySetInnerHTML={{ __html: requirement.description }} />
              ) : (
                <p>{requirement.description}</p>
              )}
            </div>
          ) : (
            <p className="text-muted-foreground">No description provided</p>
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-6 pt-6 border-t">
            <div className="space-y-2">
              <div className="flex items-center gap-2 text-sm">
                <User className="h-4 w-4 text-muted-foreground" />
                <span className="text-muted-foreground">Created by:</span>
                <span>{requirement.createdBy || 'Unknown'}</span>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <Calendar className="h-4 w-4 text-muted-foreground" />
                <span className="text-muted-foreground">Created on:</span>
                <span>{format(new Date(requirement.createdAt), 'MMMM d, yyyy')}</span>
              </div>
            </div>
            <div className="space-y-2">
              <div className="flex items-center gap-2 text-sm">
                <Clock className="h-4 w-4 text-muted-foreground" />
                <span className="text-muted-foreground">Last updated:</span>
                <span>{format(new Date(requirement.updatedAt), 'MMMM d, yyyy')}</span>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <FileText className="h-4 w-4 text-muted-foreground" />
                <span className="text-muted-foreground">Version:</span>
                <span>{requirement.version || 1}</span>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      <Tabs defaultValue="analysis" className="space-y-4">
        <TabsList>
          <TabsTrigger value="analysis">AI Analysis</TabsTrigger>
          <TabsTrigger value="history">History</TabsTrigger>
          <TabsTrigger value="comments">Comments</TabsTrigger>
        </TabsList>

        <TabsContent value="analysis" className="space-y-4">
          {requirement.aiScore ? (
            <AnalysisResults requirement={requirement} />
          ) : (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <div className="text-center">
                  <h3 className="text-lg font-semibold mb-2">No Analysis Available</h3>
                  <p className="text-muted-foreground mb-4">
                    This requirement hasn&apos;t been analyzed by AI yet.
                  </p>
                </div>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="history">
          <RequirementHistory versions={versions} />
        </TabsContent>

        <TabsContent value="comments">
          <RequirementComments
            requirementId={requirement.id}
            comments={comments}
          />
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default RequirementDetailView;
