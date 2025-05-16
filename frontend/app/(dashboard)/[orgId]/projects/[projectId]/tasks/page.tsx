"use client";

import { useState, useEffect } from "react";
import { useParams } from "next/navigation";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Card, CardContent } from "@/components/ui/card";
import { FileQuestion } from "lucide-react";
import { Button } from "@/components/ui/button";
import { projectService } from "@/services";
import type { Task } from "@/types";
import { toast } from "sonner";

import TaskHeader from "@/components/tasks/TaskHeader";
import TaskFilters from "@/components/tasks/TaskFilters";
import TaskCreateDialog from "@/components/tasks/TaskCreateDialog";
import TaskBoardView from "@/components/tasks/TaskBoard/TaskBoardView";
import TaskListView from "@/components/tasks/TaskList/TaskListView";

export default function TasksPage() {
  const params = useParams();
  const orgId = params.orgId as string;
  const projectId = params.projectId as string;

  const [tasks, setTasks] = useState<Task[]>([]);
  const [filteredTasks, setFilteredTasks] = useState<Task[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");
  const [priorityFilter, setPriorityFilter] = useState("all");
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  useEffect(() => {
    loadTasks();
  }, [projectId]);

  useEffect(() => {
    filterTasks();
  }, [tasks, searchQuery, statusFilter, priorityFilter]);

  const loadTasks = async () => {
    try {
      setIsLoading(true);
      const data = await projectService.getTasks(projectId);
      setTasks(data);
    } catch (error) {
      console.error('Failed to load tasks:', error);
      toast.error('Failed to load tasks');
    } finally {
      setIsLoading(false);
    }
  };

  const filterTasks = () => {
    let filtered = [...tasks];

    if (searchQuery) {
      filtered = filtered.filter(task =>
        task.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        task.description?.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    if (statusFilter !== "all") {
      filtered = filtered.filter(task => task.status === statusFilter);
    }

    if (priorityFilter !== "all") {
      filtered = filtered.filter(task => task.priority === priorityFilter);
    }

    setFilteredTasks(filtered);
  };

  const handleCreateTask = async (newTask: Partial<Task>) => {
    try {
      if (!newTask.title) {
        toast.error('Title is required');
        return;
      }

      const created = await projectService.createTask(projectId, newTask);
      setTasks([...tasks, created]);
      setIsDialogOpen(false);
      toast.success('Task created successfully');
    } catch (error) {
      console.error('Failed to create task:', error);
      toast.error('Failed to create task');
    }
  };

  const handleUpdateTaskStatus = async (taskId: string, status: string) => {
    try {
      const updated = await projectService.updateTaskStatus(taskId, status);

      // Update the task in the list
      setTasks(tasks.map(task => task.id === taskId ? updated : task));

      toast.success('Task status updated');
    } catch (error) {
      console.error('Failed to update task status:', error);
      toast.error('Failed to update task status');
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
          <p className="mt-2 text-muted-foreground">Loading tasks...</p>
        </div>
      </div>
    );
  }

  return (
    <div>
      <TaskHeader orgId={orgId} projectId={projectId} />

      <div className="flex items-center justify-between mb-6">
        <TaskFilters
          searchQuery={searchQuery}
          setSearchQuery={setSearchQuery}
          statusFilter={statusFilter}
          setStatusFilter={setStatusFilter}
          priorityFilter={priorityFilter}
          setPriorityFilter={setPriorityFilter}
        />
        <TaskCreateDialog
          isOpen={isDialogOpen}
          onOpenChange={setIsDialogOpen}
          onCreateTask={handleCreateTask}
        />
      </div>

      {filteredTasks.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <FileQuestion className="h-12 w-12 text-muted-foreground mb-4" />
            <h3 className="text-lg font-semibold mb-2">No tasks found</h3>
            <p className="text-muted-foreground mb-4 text-center max-w-md">
              {searchQuery || statusFilter !== "all" || priorityFilter !== "all"
                ? "No tasks match your search criteria. Try adjusting your filters."
                : "Get started by creating your first task. Tasks help track work that needs to be done."}
            </p>
            {!searchQuery && statusFilter === "all" && priorityFilter === "all" && (
              <Button onClick={() => setIsDialogOpen(true)}>
                Create First Task
              </Button>
            )}
          </CardContent>
        </Card>
      ) : (
        <Tabs defaultValue="kanban" className="mb-6">
          <TabsList className="mb-4">
            <TabsTrigger value="kanban">Kanban Board</TabsTrigger>
            <TabsTrigger value="list">List View</TabsTrigger>
          </TabsList>

          <TabsContent value="kanban">
            <TaskBoardView
              tasks={filteredTasks}
              onUpdateStatus={handleUpdateTaskStatus}
            />
          </TabsContent>

          <TabsContent value="list">
            <TaskListView
              tasks={filteredTasks}
              onUpdateStatus={handleUpdateTaskStatus}
            />
          </TabsContent>
        </Tabs>
      )}
    </div>
  );
}
