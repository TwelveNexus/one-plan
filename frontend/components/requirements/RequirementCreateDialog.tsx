import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Plus } from 'lucide-react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import type { Requirement, TaskPriority } from '@/types';

interface RequirementCreateDialogProps {
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  onCreateRequirement: (requirement: Partial<Requirement>) => void;
}

const RequirementCreateDialog: React.FC<RequirementCreateDialogProps> = ({
  isOpen,
  onOpenChange,
  onCreateRequirement
}) => {
  const [newRequirement, setNewRequirement] = useState<Partial<Requirement>>({
    title: '',
    description: '',
    priority: 'medium',
    status: 'draft',
    format: 'text'
  });

  const handleSubmit = () => {
    onCreateRequirement(newRequirement);
    // Reset form after submission
    setNewRequirement({
      title: '',
      description: '',
      priority: 'medium',
      status: 'draft',
      format: 'text'
    });
  };

  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="mr-2 h-4 w-4" />
          New Requirement
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[525px]">
        <DialogHeader>
          <DialogTitle>Create New Requirement</DialogTitle>
          <DialogDescription>
            Add a new requirement to your project. Provide clear and specific details.
          </DialogDescription>
        </DialogHeader>
        <div className="grid gap-4 py-4">
          <div className="grid gap-2">
            <Label htmlFor="title">Title</Label>
            <Input
              id="title"
              value={newRequirement.title}
              onChange={(e) => setNewRequirement({ ...newRequirement, title: e.target.value })}
              placeholder="Enter requirement title"
            />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="description">Description</Label>
            <Textarea
              id="description"
              value={newRequirement.description || ""}
              onChange={(e) => setNewRequirement({ ...newRequirement, description: e.target.value })}
              placeholder="Describe the requirement in detail"
              rows={4}
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div className="grid gap-2">
              <Label htmlFor="priority">Priority</Label>
              <select
                id="priority"
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                value={newRequirement.priority}
                onChange={(e) => setNewRequirement({ ...newRequirement, priority: e.target.value as TaskPriority })}
              >
                <option value="low">Low</option>
                <option value="medium">Medium</option>
                <option value="high">High</option>
                <option value="critical">Critical</option>
              </select>
            </div>
            <div className="grid gap-2">
              <Label htmlFor="category">Category (Optional)</Label>
              <Input
                id="category"
                value={newRequirement.category || ""}
                onChange={(e) => setNewRequirement({ ...newRequirement, category: e.target.value })}
                placeholder="E.g., Functional, UX"
              />
            </div>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div className="grid gap-2">
              <Label htmlFor="status">Status</Label>
              <select
                id="status"
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                value={newRequirement.status}
                onChange={(e) => setNewRequirement({ ...newRequirement, status: e.target.value as Requirement['status'] })}
              >
                <option value="draft">Draft</option>
                <option value="review">Review</option>
                <option value="approved">Approved</option>
                <option value="implemented">Implemented</option>
              </select>
            </div>
            <div className="grid gap-2">
              <Label htmlFor="format">Format</Label>
              <select
                id="format"
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                value={newRequirement.format}
                onChange={(e) => setNewRequirement({ ...newRequirement, format: e.target.value as Requirement['format'] })}
              >
                <option value="text">Plain Text</option>
                <option value="markdown">Markdown</option>
                <option value="html">HTML</option>
              </select>
            </div>
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button onClick={handleSubmit}>
            Create Requirement
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default RequirementCreateDialog;
