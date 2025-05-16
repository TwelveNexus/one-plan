import React from 'react';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { ChevronLeft } from 'lucide-react';

interface TaskHeaderProps {
  orgId: string;
  projectId: string;
}

const TaskHeader: React.FC<TaskHeaderProps> = ({ orgId, projectId }) => {
  return (
    <div className="flex items-center gap-2 mb-6">
      <Button variant="outline" size="sm" asChild className="gap-1">
        <Link href={`/${orgId}/projects/${projectId}`}>
          <ChevronLeft className="h-4 w-4" />
          Back to Project
        </Link>
      </Button>
      <div className="w-px h-6 bg-border"></div>
      <h1 className="text-2xl font-bold">Tasks</h1>
    </div>
  );
};

export default TaskHeader;
