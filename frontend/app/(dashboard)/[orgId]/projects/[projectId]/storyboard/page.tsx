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
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Input } from "@/components/ui/input";
import { storyboardService } from "@/services";
import type { Storyboard } from "@/types";
import { format } from "date-fns";
import {
  Calendar,
  ChevronLeft,
  Eye,
  FileQuestion,
  Filter,
  MoreHorizontal,
  Pencil,
  Plus,
  Search,
  Share2,
  Sparkles,
  Trash2,
  Users,
} from "lucide-react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { toast } from "sonner";

export default function StoryboardsPage() {
  const params = useParams();
  const router = useRouter();
  const orgId = params.orgId as string;
  const projectId = params.projectId as string;

  const [storyboards, setStoryboards] = useState<Storyboard[]>([]);
  const [filteredStoryboards, setFilteredStoryboards] = useState<Storyboard[]>(
    []
  );
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [newStoryboard, setNewStoryboard] = useState<Partial<Storyboard>>({
    title: "",
    description: "",
    visibility: "private",
  });
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  useEffect(() => {
    loadStoryboards();
  }, [projectId]);

  useEffect(() => {
    filterStoryboards();
  }, [storyboards, searchQuery]);

  const loadStoryboards = async () => {
    try {
      setIsLoading(true);
      const data = await storyboardService.getStoryboards(projectId);
      setStoryboards(data);
    } catch (error) {
      console.error("Failed to load storyboards:", error);
      toast.error("Failed to load storyboards");
    } finally {
      setIsLoading(false);
    }
  };

  const filterStoryboards = () => {
    if (!searchQuery) {
      setFilteredStoryboards(storyboards);
      return;
    }

    const filtered = storyboards.filter(
      (storyboard) =>
        storyboard.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        storyboard.description
          ?.toLowerCase()
          .includes(searchQuery.toLowerCase())
    );

    setFilteredStoryboards(filtered);
  };

  const handleCreateStoryboard = async () => {
    try {
      if (!newStoryboard.title) {
        toast.error("Title is required");
        return;
      }

      const created = await storyboardService.createStoryboard(
        projectId,
        newStoryboard
      );
      setStoryboards([...storyboards, created]);
      setNewStoryboard({
        title: "",
        description: "",
        visibility: "private",
      });
      setIsDialogOpen(false);
      toast.success("Storyboard created successfully");
    } catch (error) {
      console.error("Failed to create storyboard:", error);
      toast.error("Failed to create storyboard");
    }
  };

  const handleGenerateStoryboard = async (id: string) => {
    try {
      toast.info("Generating storyboard from requirements...");
      const generated = await storyboardService.generateStoryboard(id);

      // Update the storyboard in the list
      setStoryboards(
        storyboards.map((board) => (board.id === id ? generated : board))
      );

      toast.success("Storyboard generated successfully");
    } catch (error) {
      console.error("Failed to generate storyboard:", error);
      toast.error("Failed to generate storyboard");
    }
  };

  const getVisibilityBadge = (visibility: string) => {
    switch (visibility) {
      case "private":
        return (
          <Badge
            variant="outline"
            className="bg-blue-50 text-blue-700 hover:bg-blue-50"
          >
            Private
          </Badge>
        );
      case "team":
        return (
          <Badge
            variant="outline"
            className="bg-green-50 text-green-700 hover:bg-green-50"
          >
            Team
          </Badge>
        );
      case "public":
        return (
          <Badge
            variant="outline"
            className="bg-purple-50 text-purple-700 hover:bg-purple-50"
          >
            Public
          </Badge>
        );
      default:
        return <Badge variant="outline">{visibility}</Badge>;
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "draft":
        return (
          <Badge
            variant="outline"
            className="bg-gray-50 text-gray-700 hover:bg-gray-50"
          >
            Draft
          </Badge>
        );
      case "published":
        return (
          <Badge
            variant="outline"
            className="bg-green-50 text-green-700 hover:bg-green-50"
          >
            Published
          </Badge>
        );
      case "archived":
        return (
          <Badge
            variant="outline"
            className="bg-yellow-50 text-yellow-700 hover:bg-yellow-50"
          >
            Archived
          </Badge>
        );
      default:
        return <Badge variant="outline">{status}</Badge>;
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
          <p className="mt-2 text-muted-foreground">Loading storyboards...</p>
        </div>
      </div>
    );
  }

  return (
    <div>
      <div className="flex items-center gap-2 mb-6">
        <Button variant="outline" size="sm" asChild className="gap-1">
          <Link href={`/${orgId}/projects/${projectId}`}>
            <ChevronLeft className="h-4 w-4" />
            Back to Project
          </Link>
        </Button>
        <div className="w-px h-6 bg-border"></div>
        <h1 className="text-2xl font-bold">Storyboards</h1>
      </div>

      <div className="flex items-center justify-between mb-6">
        <div className="relative flex-1 max-w-sm">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground h-4 w-4" />
          <Input
            placeholder="Search storyboards..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10"
          />
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline">
            <Filter className="mr-2 h-4 w-4" />
            Filter
          </Button>
          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
              <Button>
                <Plus className="mr-2 h-4 w-4" />
                New Storyboard
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[525px]">
              <DialogHeader>
                <DialogTitle>Create New Storyboard</DialogTitle>
                <DialogDescription>
                  Create a new storyboard to visualize your project
                  requirements.
                </DialogDescription>
              </DialogHeader>
              <div className="grid gap-4 py-4">
                <div className="grid gap-2">
                  <label htmlFor="title">Title</label>
                  <Input
                    id="title"
                    value={newStoryboard.title}
                    onChange={(e) =>
                      setNewStoryboard({
                        ...newStoryboard,
                        title: e.target.value,
                      })
                    }
                    placeholder="Enter storyboard title"
                  />
                </div>
                <div className="grid gap-2">
                  <label htmlFor="description">Description (Optional)</label>
                  <Input
                    id="description"
                    value={newStoryboard.description || ""}
                    onChange={(e) =>
                      setNewStoryboard({
                        ...newStoryboard,
                        description: e.target.value,
                      })
                    }
                    placeholder="Enter storyboard description"
                  />
                </div>
                <div className="grid gap-2">
                  <label htmlFor="visibility">Visibility</label>
                  <select
                    id="visibility"
                    className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                    value={newStoryboard.visibility}
                    onChange={(e) =>
                      setNewStoryboard({
                        ...newStoryboard,
                        visibility: e.target.value as Storyboard["visibility"],
                      })
                    }
                  >
                    <option value="private">Private</option>
                    <option value="team">Team</option>
                    <option value="public">Public</option>
                  </select>
                </div>
              </div>
              <DialogFooter>
                <Button
                  variant="outline"
                  onClick={() => setIsDialogOpen(false)}
                >
                  Cancel
                </Button>
                <Button onClick={handleCreateStoryboard}>
                  Create Storyboard
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>
      </div>

      {filteredStoryboards.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <FileQuestion className="h-12 w-12 text-muted-foreground mb-4" />
            <h3 className="text-lg font-semibold mb-2">No storyboards found</h3>
            <p className="text-muted-foreground mb-4 text-center max-w-md">
              {searchQuery
                ? "No storyboards match your search criteria. Try adjusting your filters."
                : "Get started by creating your first storyboard. Storyboards help visualize requirements as user stories."}
            </p>
            {!searchQuery && (
              <Dialog>
                <DialogTrigger asChild>
                  <Button>
                    <Plus className="mr-2 h-4 w-4" />
                    Create First Storyboard
                  </Button>
                </DialogTrigger>
              </Dialog>
            )}
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredStoryboards.map((storyboard) => (
            <Card
              key={storyboard.id}
              className="hover:shadow-md transition-shadow"
            >
              <CardHeader className="pb-3">
                <div className="flex justify-between">
                  <CardTitle className="text-lg">
                    <Link
                      href={`/${orgId}/projects/${projectId}/storyboard/${storyboard.id}`}
                      className="hover:text-primary transition-colors"
                    >
                      {storyboard.title}
                    </Link>
                  </CardTitle>
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <Button variant="ghost" size="sm">
                        <MoreHorizontal className="h-4 w-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem
                        onClick={() =>
                          router.push(
                            `/${orgId}/projects/${projectId}/storyboard/${storyboard.id}`
                          )
                        }
                      >
                        <Eye className="mr-2 h-4 w-4" />
                        View Storyboard
                      </DropdownMenuItem>
                      <DropdownMenuItem>
                        <Pencil className="mr-2 h-4 w-4" />
                        Edit Details
                      </DropdownMenuItem>
                      <DropdownMenuItem>
                        <Share2 className="mr-2 h-4 w-4" />
                        Share Storyboard
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        onClick={() => handleGenerateStoryboard(storyboard.id)}
                      >
                        <Sparkles className="mr-2 h-4 w-4" />
                        Generate from Requirements
                      </DropdownMenuItem>
                      <DropdownMenuSeparator />
                      <DropdownMenuItem className="text-destructive">
                        <Trash2 className="mr-2 h-4 w-4" />
                        Delete
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                </div>
                <CardDescription className="line-clamp-2 mt-1">
                  {storyboard.description || "No description provided"}
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="flex items-center gap-2 mb-4">
                  {getStatusBadge(storyboard.status || "draft")}
                  {getVisibilityBadge(storyboard.visibility)}
                </div>
                <div className="flex flex-col gap-2 text-sm">
                  <div className="flex items-center gap-2 text-muted-foreground">
                    <Calendar className="h-4 w-4" />
                    <span>
                      Updated{" "}
                      {format(new Date(storyboard.updatedAt), "MMM d, yyyy")}
                    </span>
                  </div>
                  <div className="flex items-center gap-2 text-muted-foreground">
                    <Users className="h-4 w-4" />
                    <span>
                      {storyboard.shareToken ? "Shared" : "Not shared"}
                    </span>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
