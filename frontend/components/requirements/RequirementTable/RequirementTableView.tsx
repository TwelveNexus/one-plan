import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import RequirementTableRow from './RequirementTableRow';
import type { Requirement } from '@/types';

interface RequirementTableViewProps {
  requirements: Requirement[];
  onAnalyzeRequirement: (id: string) => void;
}

const RequirementTableView: React.FC<RequirementTableViewProps> = ({ requirements, onAnalyzeRequirement }) => {
  return (
    <Card>
      <CardHeader className="pb-3">
        <CardTitle>Project Requirements</CardTitle>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="w-[300px]">Title</TableHead>
              <TableHead>Priority</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Category</TableHead>
              <TableHead>AI Score</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {requirements.map((requirement) => (
              <RequirementTableRow
                key={requirement.id}
                requirement={requirement}
                onAnalyzeRequirement={onAnalyzeRequirement}
              />
            ))}
            {requirements.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-6 text-muted-foreground">
                  No requirements found.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
};

export default RequirementTableView;
