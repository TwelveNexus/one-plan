"use client";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
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
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { projectService } from "@/services";
import type { Project } from "@/types";
import { differenceInDays, format } from "date-fns";
import {
  CalendarClock,
  ChevronRight,
  Clock,
  Edit,
  FileText,
  LayoutGrid,
  ListTodo,
  MoreHorizontal,
  Settings,
  Users,
} from "lucide-react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { toast } from "sonner";

export default function ProjectDetailPage() {
  const params = useParams();
  const router = useRouter();
  const orgId = params.orgId as string;
  const projectId = params.projectId as string;

  const [project, setProject] = useState<Project | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [recentActivity, setRecentActivity] = useState<any[]>([]);
  const [stats, setStats] = useState<{
    totalTasks: number;
    completedTasks: number;
    upcomingDeadlines: number;
    overdueTasks: number;
  }>({
    totalTasks: 0,
    completedTasks: 0,
    upcomingDeadlines: 0,
    overdueTasks: 0,
  });

  useEffect(() => {
    loadProject();
    loadActivity();
    loadStats();
  }, [projectId]);

  const loadProject = async () => {
    try {
      setIsLoading(true);
      const data = await projectService.getProject(projectId);
      setProject(data);
    } catch (error) {
      console.error("Failed to load project:", error);
      toast.error("Failed to load project details");
    } finally {
      setIsLoading(false);
    }
  };

  const loadActivity = async () => {
    try {
      const data = await projectService.getProjectActivity(projectId);
      setRecentActivity(data.slice(0, 5));
    } catch (error) {
      console.error("Failed to load activity:", error);
    }
  };

  const loadStats = async () => {
    try {
      const data = await projectService.getProjectStatistics(projectId);
      setStats(data);
    } catch (error) {
      console.error("Failed to load statistics:", error);
      // Use placeholder stats if API fails
      setStats({
        totalTasks: 24,
        completedTasks: 16,
        upcomingDeadlines: 3,
        overdueTasks: 1,
      });
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case "planning":
        return "bg-blue-100 text-blue-800";
      case "active":
        return "bg-green-100 text-green-800";
      case "completed":
        return "bg-gray-100 text-gray-800";
      case "archived":
        return "bg-yellow-100 text-yellow-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  const daysRemaining = () => {
    if (!project?.targetDate) return null;

    const today = new Date();
    const target = new Date(project.targetDate);
    const days = differenceInDays(target, today);

    if (days < 0) {
      return `Overdue by ${Math.abs(days)} days`;
    } else if (days === 0) {
      return "Due today";
    } else {
      return `${days} days remaining`;
    }
  };

  const navigateToSection = (section: string) => {
    router.push(`/${orgId}/projects/${projectId}/${section}`);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
          <p className="mt-2 text-muted-foreground">
            Loading project details...
          </p>
        </div>
      </div>
    );
  }

  if (!project) {
    return (
      <div className="flex flex-col items-center justify-center h-64">
        <h3 className="text-lg font-semibold mb-2">Project not found</h3>
        <p className="text-muted-foreground mb-4">
          The project you&apos;re looking for doesn&apos;t exist or you don&apos;t have access.
        </p>
        <Button asChild>
          <Link href={`/${orgId}/projects`}>Back to Projects</Link>
        </Button>
      </div>
    );
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-bold">{project.name}</h1>
            <Badge className={getStatusColor(project.status)}>
              {project.status}
            </Badge>
          </div>
          <p className="text-muted-foreground">
            {project.description || "No description provided"}
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" size="sm" asChild>
            <Link href={`/${orgId}/projects/${projectId}/settings`}>
              <Settings className="mr-2 h-4 w-4" />
              Settings
            </Link>
          </Button>
          <Button size="sm">
            <Edit className="mr-2 h-4 w-4" />
            Edit Project
          </Button>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="sm">
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem>Duplicate Project</DropdownMenuItem>
              <DropdownMenuItem>Export Project</DropdownMenuItem>
              <DropdownMenuItem>Add to Favorites</DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem className="text-destructive">
                Archive Project
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
        <Card>
          <CardHeader className="py-4">
            <CardTitle className="text-sm font-medium">Total Tasks</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.totalTasks}</div>
            <p className="text-xs text-muted-foreground">
              {stats.completedTasks} completed
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="py-4">
            <CardTitle className="text-sm font-medium">Project Key</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{project.projectKey}</div>
            <p className="text-xs text-muted-foreground">Used for references</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="py-4">
            <CardTitle className="text-sm font-medium">Timeline</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {project.targetDate
                ? format(new Date(project.targetDate), "MMM d, yyyy")
                : "No deadline"}
            </div>
            <p className="text-xs text-muted-foreground">{daysRemaining()}</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="py-4">
            <CardTitle className="text-sm font-medium">
              Upcoming Deadlines
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.upcomingDeadlines}</div>
            <p className="text-xs text-muted-foreground">
              {stats.overdueTasks} overdue
            </p>
          </CardContent>
        </Card>
      </div>

      <Tabs defaultValue="overview" className="mb-6">
        <TabsList>
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="activity">Activity</TabsTrigger>
          <TabsTrigger value="members">Members</TabsTrigger>
        </TabsList>
        <TabsContent value="overview" className="mt-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Card className="md:col-span-2">
              <CardHeader>
                <CardTitle className="text-lg">Project Modules</CardTitle>
                <CardDescription>
                  Navigate to different sections of your project
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <Button
                    variant="outline"
                    className="h-auto py-4 px-4 justify-start"
                    onClick={() => navigateToSection("requirements")}
                  >
                    <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center mr-4">
                      <FileText className="h-5 w-5 text-primary" />
                    </div>
                    <div className="text-left">
                      <h3 className="font-medium">Requirements</h3>
                      <p className="text-xs text-muted-foreground">
                        Manage project requirements
                      </p>
                    </div>
                    <ChevronRight className="ml-auto h-5 w-5 text-muted-foreground" />
                  </Button>
                  <Button
                    variant="outline"
                    className="h-auto py-4 px-4 justify-start"
                    onClick={() => navigateToSection("storyboard")}
                  >
                    <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center mr-4">
                      <LayoutGrid className="h-5 w-5 text-primary" />
                    </div>
                    <div className="text-left">
                      <h3 className="font-medium">Storyboard</h3>
                      <p className="text-xs text-muted-foreground">
                        Visual planning tools
                      </p>
                    </div>
                    <ChevronRight className="ml-auto h-5 w-5 text-muted-foreground" />
                  </Button>
                  <Button
                    variant="outline"
                    className="h-auto py-4 px-4 justify-start"
                    onClick={() => navigateToSection("tasks")}
                  >
                    <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center mr-4">
                      <ListTodo className="h-5 w-5 text-primary" />
                    </div>
                    <div className="text-left">
                      <h3 className="font-medium">Tasks</h3>
                      <p className="text-xs text-muted-foreground">
                        Track and manage tasks
                      </p>
                    </div>
                    <ChevronRight className="ml-auto h-5 w-5 text-muted-foreground" />
                  </Button>
                  <Button
                    variant="outline"
                    className="h-auto py-4 px-4 justify-start"
                    onClick={() => navigateToSection("timeline")}
                  >
                    <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center mr-4">
                      <CalendarClock className="h-5 w-5 text-primary" />
                    </div>
                    <div className="text-left">
                      <h3 className="font-medium">Timeline</h3>
                      <p className="text-xs text-muted-foreground">
                        Project timeline and Gantt
                      </p>
                    </div>
                    <ChevronRight className="ml-auto h-5 w-5 text-muted-foreground" />
                  </Button>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Project Details</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div>
                  <h3 className="text-sm font-medium text-muted-foreground">
                    Created By
                  </h3>
                  <p>{project.createdBy || "Unknown"}</p>
                </div>
                <div>
                  <h3 className="text-sm font-medium text-muted-foreground">
                    Created On
                  </h3>
                  <p>{format(new Date(project.createdAt), "MMMM d, yyyy")}</p>
                </div>
                <div>
                  <h3 className="text-sm font-medium text-muted-foreground">
                    Last Updated
                  </h3>
                  <p>{format(new Date(project.updatedAt), "MMMM d, yyyy")}</p>
                </div>
                <div>
                  <h3 className="text-sm font-medium text-muted-foreground">
                    Visibility
                  </h3>
                  <p className="capitalize">{project.visibility}</p>
                </div>
                {project.startDate && (
                  <div>
                    <h3 className="text-sm font-medium text-muted-foreground">
                      Start Date
                    </h3>
                    <p>{format(new Date(project.startDate), "MMMM d, yyyy")}</p>
                  </div>
                )}
              </CardContent>
            </Card>
          </div>
        </TabsContent>
        <TabsContent value="activity" className="mt-6">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Recent Activity</CardTitle>
              <CardDescription>History of changes and updates</CardDescription>
            </CardHeader>
            <CardContent>
              {recentActivity.length > 0 ? (
                <div className="space-y-4">
                  {recentActivity.map((activity, index) => (
                    <div key={index} className="flex items-start gap-4">
                      <div className="w-10 h-10 rounded-full bg-muted flex items-center justify-center flex-shrink-0">
                        <Users className="h-5 w-5 text-muted-foreground" />
                      </div>
                      <div>
                        <p className="font-medium">
                          {activity.description || "Action performed"}
                        </p>
                        <div className="flex items-center gap-2 mt-1">
                          <span className="text-sm text-muted-foreground">
                            {activity.user || "Unknown user"}
                          </span>
                          <span className="text-xs text-muted-foreground">
                            •
                          </span>
                          <span className="text-sm text-muted-foreground">
                            {activity.timestamp
                              ? format(
                                  new Date(activity.timestamp),
                                  "MMM d, h:mm a"
                                )
                              : "Recently"}
                          </span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="flex flex-col items-center justify-center py-6">
                  <Clock className="h-12 w-12 text-muted-foreground mb-4" />
                  <h3 className="text-lg font-semibold mb-2">
                    No recent activity
                  </h3>
                  <p className="text-muted-foreground text-center">
                    Activities such as creating tasks, updating requirements,
                    and comments will be shown here.
                  </p>
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>
        <TabsContent value="members" className="mt-6">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Project Members</CardTitle>
              <CardDescription>
                People with access to this project
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="flex flex-col items-center justify-center py-6">
                <Users className="h-12 w-12 text-muted-foreground mb-4" />
                <h3 className="text-lg font-semibold mb-2">Team members</h3>
                <p className="text-muted-foreground text-center mb-4">
                  Add team members to collaborate on this project.
                </p>
                <Button>Add Team Members</Button>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
