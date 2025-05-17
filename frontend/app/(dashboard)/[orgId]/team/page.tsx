"use client";

import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
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
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Separator } from "@/components/ui/separator";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { organizationService } from "@/services";
import { Team } from "@/types";
import { zodResolver } from "@hookform/resolvers/zod";
import { UserPlus, Users } from "lucide-react";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

// Define visibility as a union type for better type safety
type Visibility = "private" | "organization" | "public";

// Define visibility options with proper typing
const VISIBILITY_OPTIONS: { value: Visibility; label: string }[] = [
  { value: "private", label: "Private - Only team members" },
  { value: "organization", label: "Organization - All organization members" },
  { value: "public", label: "Public - Anyone with link" },
];

// Form schema with improved validation
const formSchema = z.object({
  name: z.string().min(2, {
    message: "Team name must be at least 2 characters.",
  }),
  description: z.string().optional(),
  visibility: z.enum(["private", "organization", "public"]).default("organization"),
});

type FormValues = z.infer<typeof formSchema>;

export default function TeamsPage() {
  const params = useParams();
  const orgId = params.orgId as string;
  const [teams, setTeams] = useState<Team[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  // Simplified form with only the fields we need for submission
  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: "",
      description: "",
      visibility: "organization",
    },
  });

  // Fetch teams on component mount
  useEffect(() => {
    const fetchTeams = async () => {
      if (!orgId) return;

      try {
        setIsLoading(true);
        const fetchedTeams = await organizationService.getTeams(orgId);
        setTeams(fetchedTeams);
      } catch (error) {
        console.error("Failed to fetch teams:", error);
        toast.error("Failed to load teams");
      } finally {
        setIsLoading(false);
      }
    };

    fetchTeams();
  }, [orgId]);

  // Handle form submission
  const onSubmit = async (values: FormValues) => {
    try {
      const newTeam = await organizationService.createTeam(orgId, {
        name: values.name,
        description: values.description || "",
        visibility: values.visibility,
      });

      setTeams((prevTeams) => [...prevTeams, newTeam]);
      toast.success(`${values.name} team has been created`);
      form.reset();
      setIsDialogOpen(false);
    } catch (error) {
      console.error("Failed to create team:", error);
      toast.error("Failed to create team");
    }
  };

  // Render loading state
  const renderLoading = () => (
    <div className="flex justify-center p-6">
      <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
    </div>
  );

  // Render empty state
  const renderEmptyState = () => (
    <div className="flex flex-col items-center justify-center p-10 text-center">
      <Users className="h-12 w-12 text-muted-foreground mb-4" />
      <h3 className="text-xl font-semibold mb-2">No teams found</h3>
      <p className="text-muted-foreground mb-4">
        You haven&apos;t created any teams yet for this organization.
      </p>
      <Button onClick={() => setIsDialogOpen(true)}>
        <UserPlus className="mr-2 h-4 w-4" />
        Create Your First Team
      </Button>
    </div>
  );

  // Render team cards
  const renderTeamCards = () => (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {teams.map((team) => (
        <Card key={team.id} className="overflow-hidden">
          <CardContent className="p-6">
            <div className="flex items-start justify-between">
              <div>
                <h3 className="text-lg font-semibold">{team.name}</h3>
                <p className="text-sm text-muted-foreground mt-1">
                  {team.description || "No description"}
                </p>
              </div>
              <div className="bg-muted flex items-center justify-center h-10 w-10 rounded-full">
                {team.avatar ? (
                  // eslint-disable-next-line @next/next/no-img-element
                  <img
                    src={team.avatar}
                    alt={team.name}
                    className="h-10 w-10 rounded-full object-cover"
                  />
                ) : (
                  <Users className="h-5 w-5 text-muted-foreground" />
                )}
              </div>
            </div>
            <div className="mt-4 flex items-center gap-2 text-sm text-muted-foreground">
              <span className="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold">
                {team.visibility === "private"
                  ? "Private"
                  : team.visibility === "organization"
                  ? "Organization"
                  : "Public"}
              </span>
              <span>•</span>
              <span>
                Created{" "}
                {new Date(team.createdAt).toLocaleDateString(undefined, {
                  year: "numeric",
                  month: "short",
                  day: "numeric",
                })}
              </span>
            </div>
            <div className="mt-4 flex gap-2">
              <Button variant="outline" size="sm" className="flex-1">
                View
              </Button>
              <Button variant="outline" size="sm" className="flex-1">
                Manage
              </Button>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );

  // Render form fields
  const renderFormFields = () => (
    <>
      <FormField
        control={form.control}
        name="name"
        render={({ field }) => (
          <FormItem>
            <FormLabel>Team Name</FormLabel>
            <FormControl>
              <Input placeholder="Engineering" {...field} />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />
      <FormField
        control={form.control}
        name="description"
        render={({ field }) => (
          <FormItem>
            <FormLabel>Description</FormLabel>
            <FormControl>
              <Textarea
                placeholder="Front-end development team"
                {...field}
                value={field.value || ""}
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />
      <FormField
        control={form.control}
        name="visibility"
        render={({ field }) => (
          <FormItem>
            <FormLabel>Visibility</FormLabel>
            <FormControl>
              <Select
                onValueChange={field.onChange}
                defaultValue={field.value}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select visibility" />
                </SelectTrigger>
                <SelectContent>
                  {VISIBILITY_OPTIONS.map((option) => (
                    <SelectItem key={option.value} value={option.value}>
                      {option.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />
    </>
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Teams</h2>
          <p className="text-muted-foreground">
            Manage your organization&apos;s teams and members
          </p>
        </div>
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button>
              <UserPlus className="mr-2 h-4 w-4" />
              Create Team
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Create New Team</DialogTitle>
              <DialogDescription>
                Add a new team to your organization.
              </DialogDescription>
            </DialogHeader>
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                {renderFormFields()}
                <DialogFooter>
                  <Button
                    variant="outline"
                    onClick={() => setIsDialogOpen(false)}
                    type="button"
                  >
                    Cancel
                  </Button>
                  <Button type="submit">Create Team</Button>
                </DialogFooter>
              </form>
            </Form>
          </DialogContent>
        </Dialog>
      </div>
      <Separator />

      {isLoading
        ? renderLoading()
        : teams.length === 0
        ? renderEmptyState()
        : renderTeamCards()}
    </div>
  );
}
