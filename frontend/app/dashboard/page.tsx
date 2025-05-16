"use client";

import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { useNextAuth, withAuth } from "@/hooks/use-next-auth";
import { organizationService } from "@/services";
import type { Organization } from "@/types";
import {
  ArrowRight,
  CalendarClock,
  Clock,
  FolderKanban,
  Plus,
  Sparkles
} from "lucide-react";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { toast } from "sonner";

function DashboardPage() {
  const { user } = useNextAuth();
  const router = useRouter();
  const [organizations, setOrganizations] = useState<Organization[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedOrgId, setSelectedOrgId] = useState<string | null>(null);

  useEffect(() => {
    loadOrganizations();
  }, []);

  const loadOrganizations = async () => {
    try {
      setIsLoading(true);
      const data = await organizationService.getOrganizations();
      setOrganizations(data);

      if (data.length > 0) {
        setSelectedOrgId(data[0].id);
      }
    } catch (error) {
      console.error("Failed to load organizations:", error);
      toast.error("Failed to load organizations");
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateOrganization = () => {
    router.push("/create-organization");
  };

  const handleSelectOrganization = (orgId: string) => {
    setSelectedOrgId(orgId);
  };

  const navigateToProjects = (orgId: string) => {
    router.push(`/${orgId}/projects`);
  };

  // Sample data for mockups
  const recentProjects = [
    {
      id: "1",
      name: "Website Redesign",
      status: "active",
      lastUpdated: new Date().toISOString(),
    },
    {
      id: "2",
      name: "Mobile App Development",
      status: "planning",
      lastUpdated: new Date(Date.now() - 86400000).toISOString(),
    },
    {
      id: "3",
      name: "Marketing Campaign",
      status: "completed",
      lastUpdated: new Date(Date.now() - 172800000).toISOString(),
    },
  ];

  const upcomingDeadlines = [
    {
      id: "1",
      task: "Finalize Wireframes",
      project: "Website Redesign",
      dueDate: new Date(Date.now() + 86400000).toISOString(),
    },
    {
      id: "2",
      task: "API Documentation",
      project: "Mobile App Development",
      dueDate: new Date(Date.now() + 172800000).toISOString(),
    },
  ];

  if (isLoading) {
    return (
      <div className="container mx-auto p-6">
        <div className="space-y-4">
          <Skeleton className="h-12 w-[250px]" />
          <Skeleton className="h-4 w-[300px]" />
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mt-6">
            <Skeleton className="h-[180px] w-full rounded-xl" />
            <Skeleton className="h-[180px] w-full rounded-xl" />
            <Skeleton className="h-[180px] w-full rounded-xl" />
            <Skeleton className="h-[180px] w-full rounded-xl" />
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-3xl font-bold">
            Welcome, {user?.firstName || "User"}
          </h1>
          <p className="text-muted-foreground">
            Here&apos;s what&apos;s happening with your projects
          </p>
        </div>
        <Button onClick={handleCreateOrganization}>
          <Plus className="mr-2 h-4 w-4" />
          New Organization
        </Button>
      </div>

      {organizations.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <FolderKanban className="h-12 w-12 text-muted-foreground mb-4" />
            <h3 className="text-lg font-semibold mb-2">
              No organizations found
            </h3>
            <p className="text-muted-foreground mb-4 text-center max-w-md">
              Get started by creating your first organization. Organizations
              help you organize your projects and teams.
            </p>
            <Button onClick={handleCreateOrganization}>
              <Plus className="mr-2 h-4 w-4" />
              Create Organization
            </Button>
          </CardContent>
        </Card>
      ) : (
        <>
          <Tabs
            value={selectedOrgId || ""}
            onValueChange={handleSelectOrganization}
            className="mb-6"
          >
            <TabsList className="mb-4">
              {organizations.map((org) => (
                <TabsTrigger key={org.id} value={org.id}>
                  {org.name}
                </TabsTrigger>
              ))}
            </TabsList>

            {organizations.map((org) => (
              <TabsContent key={org.id} value={org.id}>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
                  <Card>
                    <CardHeader className="pb-2">
                      <CardTitle className="text-sm font-medium">
                        Total Projects
                      </CardTitle>
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold">12</div>
                      <p className="text-xs text-muted-foreground mt-1">
                        8 active, 4 completed
                      </p>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="mt-4 w-full justify-between"
                        onClick={() => navigateToProjects(org.id)}
                      >
                        View all projects
                        <ArrowRight className="h-4 w-4 ml-2" />
                      </Button>
                    </CardContent>
                  </Card>

                  <Card>
                    <CardHeader className="pb-2">
                      <CardTitle className="text-sm font-medium">
                        Upcoming Deadlines
                      </CardTitle>
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold">5</div>
                      <p className="text-xs text-muted-foreground mt-1">
                        2 this week, 3 next week
                      </p>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="mt-4 w-full justify-between"
                      >
                        View calendar
                        <ArrowRight className="h-4 w-4 ml-2" />
                      </Button>
                    </CardContent>
                  </Card>

                  <Card>
                    <CardHeader className="pb-2">
                      <CardTitle className="text-sm font-medium">
                        Team Members
                      </CardTitle>
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold">18</div>
                      <p className="text-xs text-muted-foreground mt-1">
                        Across 5 teams
                      </p>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="mt-4 w-full justify-between"
                        onClick={() => router.push(`/${org.id}/teams`)}
                      >
                        Manage teams
                        <ArrowRight className="h-4 w-4 ml-2" />
                      </Button>
                    </CardContent>
                  </Card>

                  <Card>
                    <CardHeader className="pb-2">
                      <CardTitle className="text-sm font-medium">
                        AI Analysis
                      </CardTitle>
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold">82%</div>
                      <p className="text-xs text-muted-foreground mt-1">
                        Average requirement quality
                      </p>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="mt-4 w-full justify-between"
                      >
                        View insights
                        <ArrowRight className="h-4 w-4 ml-2" />
                      </Button>
                    </CardContent>
                  </Card>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <Card>
                    <CardHeader>
                      <CardTitle>Recent Projects</CardTitle>
                      <CardDescription>
                        Your recently updated projects
                      </CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="space-y-4">
                        {recentProjects.map((project) => (
                          <div
                            key={project.id}
                            className="flex items-center justify-between border-b pb-4 last:border-0 last:pb-0"
                          >
                            <div>
                              <div className="font-medium">{project.name}</div>
                              <div className="flex items-center mt-1">
                                <Clock className="h-3 w-3 text-muted-foreground mr-1" />
                                <span className="text-xs text-muted-foreground">
                                  Updated yesterday
                                </span>
                              </div>
                            </div>
                            <div>
                              <Button
                                variant="ghost"
                                size="sm"
                                onClick={() =>
                                  router.push(
                                    `/${org.id}/projects/${project.id}`
                                  )
                                }
                              >
                                View
                              </Button>
                            </div>
                          </div>
                        ))}
                      </div>
                    </CardContent>
                  </Card>

                  <Card>
                    <CardHeader>
                      <CardTitle>Upcoming Deadlines</CardTitle>
                      <CardDescription>
                        Tasks due in the next week
                      </CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="space-y-4">
                        {upcomingDeadlines.map((deadline) => (
                          <div
                            key={deadline.id}
                            className="flex items-center justify-between border-b pb-4 last:border-0 last:pb-0"
                          >
                            <div>
                              <div className="font-medium">{deadline.task}</div>
                              <div className="flex items-center mt-1">
                                <CalendarClock className="h-3 w-3 text-muted-foreground mr-1" />
                                <span className="text-xs text-muted-foreground">
                                  Due tomorrow
                                </span>
                              </div>
                            </div>
                            <div className="text-sm text-muted-foreground">
                              {deadline.project}
                            </div>
                          </div>
                        ))}
                      </div>
                    </CardContent>
                  </Card>
                </div>
              </TabsContent>
            ))}
          </Tabs>

          <Card className="bg-gradient-to-r from-indigo-500/10 via-purple-500/10 to-pink-500/10 border-0">
            <CardContent className="flex flex-col md:flex-row items-center justify-between p-6">
              <div className="flex items-center mb-4 md:mb-0">
                <div className="bg-primary/10 p-3 rounded-full mr-4">
                  <Sparkles className="h-8 w-8 text-primary" />
                </div>
                <div>
                  <h3 className="text-xl font-bold mb-1">
                    Enhance your projects with AI
                  </h3>
                  <p className="text-muted-foreground max-w-md">
                    Use our AI-powered analysis to improve requirement quality
                    and automate storyboard creation.
                  </p>
                </div>
              </div>
              <Button size="lg" className="bg-primary">
                Try AI Features
              </Button>
            </CardContent>
          </Card>
        </>
      )}
    </div>
  );
}

export default withAuth(DashboardPage);
