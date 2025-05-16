"use client";

import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Textarea } from "@/components/ui/textarea";
import { withAuth } from "@/hooks/use-next-auth";
import { projectService } from "@/services";
import { Calendar, ChevronLeft, FolderKanban, Loader2 } from "lucide-react";
import { useParams, useRouter } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";

function CreateProjectPage() {
  const params = useParams();
  const router = useRouter();
  const orgId = params.orgId as string;

  const [currentTab, setCurrentTab] = useState("basic");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    projectKey: "",
    description: "",
    visibility: "private",
    startDate: "",
    targetDate: "",
    status: "planning",
  });

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));

    // Auto-generate project key from name if projectKey is empty
    if (name === "name" && !formData.projectKey) {
      const key = value
        .trim()
        .toUpperCase()
        .replace(/[^A-Z0-9]/g, "")
        .substring(0, 3);

      if (key) {
        setFormData((prev) => ({ ...prev, projectKey: key }));
      }
    }
  };

  const handleRadioChange = (name: string, value: string) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.name.trim()) {
      toast.error("Project name is required");
      return;
    }

    if (!formData.projectKey.trim()) {
      toast.error("Project key is required");
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await projectService.createProject(orgId, formData);
      toast.success("Project created successfully");
      router.push(`/${orgId}/projects/${response.id}`);
    } catch (error) {
      console.error("Failed to create project:", error);
      toast.error("Failed to create project");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="container mx-auto max-w-3xl p-6">
      <Button
        variant="ghost"
        size="sm"
        className="mb-6"
        onClick={() => router.push(`/${orgId}/projects`)}
      >
        <ChevronLeft className="mr-2 h-4 w-4" />
        Back to Projects
      </Button>

      <Card>
        <CardHeader>
          <div className="flex items-center">
            <FolderKanban className="h-6 w-6 mr-3 text-primary" />
            <div>
              <CardTitle className="text-2xl">Create New Project</CardTitle>
              <CardDescription>
                Set up a new project for your team
              </CardDescription>
            </div>
          </div>
        </CardHeader>
        <form onSubmit={handleSubmit}>
          <Tabs value={currentTab} onValueChange={setCurrentTab}>
            <CardContent>
              <TabsList className="mb-6">
                <TabsTrigger value="basic">Basic Info</TabsTrigger>
                <TabsTrigger value="settings">Settings</TabsTrigger>
                <TabsTrigger value="dates">Timeline</TabsTrigger>
              </TabsList>

              <TabsContent value="basic" className="space-y-6">
                <div className="space-y-2">
                  <Label htmlFor="name">Project Name*</Label>
                  <Input
                    id="name"
                    name="name"
                    placeholder="Website Redesign"
                    value={formData.name}
                    onChange={handleChange}
                    required
                    disabled={isSubmitting}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="projectKey">Project Key*</Label>
                  <Input
                    id="projectKey"
                    name="projectKey"
                    placeholder="WEB"
                    value={formData.projectKey}
                    onChange={handleChange}
                    required
                    maxLength={5}
                    className="uppercase"
                    disabled={isSubmitting}
                  />
                  <p className="text-xs text-muted-foreground">
                    A short, unique identifier for your project (e.g., WEB, APP,
                    MKT)
                  </p>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="description">Description</Label>
                  <Textarea
                    id="description"
                    name="description"
                    placeholder="Describe the project purpose and goals"
                    value={formData.description}
                    onChange={handleChange}
                    disabled={isSubmitting}
                  />
                </div>

                <div className="flex justify-end">
                  <Button
                    type="button"
                    onClick={() => setCurrentTab("settings")}
                  >
                    Next
                  </Button>
                </div>
              </TabsContent>

              <TabsContent value="settings" className="space-y-6">
                <div className="space-y-3">
                  <Label>Project Visibility</Label>
                  <RadioGroup
                    value={formData.visibility}
                    onValueChange={(value) =>
                      handleRadioChange("visibility", value)
                    }
                  >
                    <div className="flex items-start space-x-2 mb-2">
                      <RadioGroupItem value="private" id="visibility-private" />
                      <div className="grid gap-1.5">
                        <Label
                          htmlFor="visibility-private"
                          className="font-medium"
                        >
                          Private
                        </Label>
                        <p className="text-sm text-muted-foreground">
                          Only project members can access this project
                        </p>
                      </div>
                    </div>
                    <div className="flex items-start space-x-2 mb-2">
                      <RadioGroupItem
                        value="internal"
                        id="visibility-internal"
                      />
                      <div className="grid gap-1.5">
                        <Label
                          htmlFor="visibility-internal"
                          className="font-medium"
                        >
                          Internal
                        </Label>
                        <p className="text-sm text-muted-foreground">
                          All organization members can access this project
                        </p>
                      </div>
                    </div>
                    <div className="flex items-start space-x-2">
                      <RadioGroupItem value="public" id="visibility-public" />
                      <div className="grid gap-1.5">
                        <Label
                          htmlFor="visibility-public"
                          className="font-medium"
                        >
                          Public
                        </Label>
                        <p className="text-sm text-muted-foreground">
                          Anyone with the link can view this project
                        </p>
                      </div>
                    </div>
                  </RadioGroup>
                </div>

                <div className="space-y-3">
                  <Label>Initial Status</Label>
                  <RadioGroup
                    value={formData.status}
                    onValueChange={(value) =>
                      handleRadioChange("status", value)
                    }
                  >
                    <div className="flex items-start space-x-2 mb-2">
                      <RadioGroupItem value="planning" id="status-planning" />
                      <div className="grid gap-1.5">
                        <Label
                          htmlFor="status-planning"
                          className="font-medium"
                        >
                          Planning
                        </Label>
                        <p className="text-sm text-muted-foreground">
                          Project is in the planning phase
                        </p>
                      </div>
                    </div>
                    <div className="flex items-start space-x-2">
                      <RadioGroupItem value="active" id="status-active" />
                      <div className="grid gap-1.5">
                        <Label htmlFor="status-active" className="font-medium">
                          Active
                        </Label>
                        <p className="text-sm text-muted-foreground">
                          Project is currently active and in progress
                        </p>
                      </div>
                    </div>
                  </RadioGroup>
                </div>

                <div className="flex justify-between">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => setCurrentTab("basic")}
                  >
                    Previous
                  </Button>
                  <Button type="button" onClick={() => setCurrentTab("dates")}>
                    Next
                  </Button>
                </div>
              </TabsContent>

              <TabsContent value="dates" className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-2">
                    <Label htmlFor="startDate">Start Date</Label>
                    <div className="flex">
                      <Calendar className="mr-2 h-4 w-4 mt-3 text-muted-foreground" />
                      <Input
                        id="startDate"
                        name="startDate"
                        type="date"
                        value={formData.startDate}
                        onChange={handleChange}
                        disabled={isSubmitting}
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="targetDate">Target Completion Date</Label>
                    <div className="flex">
                      <Calendar className="mr-2 h-4 w-4 mt-3 text-muted-foreground" />
                      <Input
                        id="targetDate"
                        name="targetDate"
                        type="date"
                        value={formData.targetDate}
                        onChange={handleChange}
                        disabled={isSubmitting}
                      />
                    </div>
                  </div>
                </div>

                <div className="flex justify-between">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => setCurrentTab("settings")}
                  >
                    Previous
                  </Button>
                  <Button type="submit" disabled={isSubmitting}>
                    {isSubmitting ? (
                      <>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        Creating...
                      </>
                    ) : (
                      "Create Project"
                    )}
                  </Button>
                </div>
              </TabsContent>
            </CardContent>
          </Tabs>
        </form>
      </Card>
    </div>
  );
}

export default withAuth(CreateProjectPage);
