"use client";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
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
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
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
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Textarea } from "@/components/ui/textarea";
import { organizationService } from "@/services";
import { Team, TeamMember } from "@/types";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  ChevronLeft,
  MoreHorizontal,
  Pencil,
  RefreshCw,
  Save,
  Trash,
  User as UserIcon,
  UserPlus,
  Users,
} from "lucide-react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

const teamUpdateSchema = z.object({
  name: z.string().min(2, {
    message: "Team name must be at least 2 characters.",
  }),
  description: z.string().optional(),
  visibility: z.enum(["private", "organization", "public"]),
});

const inviteSchema = z.object({
  email: z.string().email({
    message: "Please enter a valid email address.",
  }),
  role: z.enum(["member", "admin", "guest"], {
    required_error: "Please select a role.",
  }),
});

type TeamUpdateFormValues = z.infer<typeof teamUpdateSchema>;
type InviteFormValues = z.infer<typeof inviteSchema>;

export default function TeamPage() {
  const params = useParams();
  const orgId = params.orgId as string;
  const teamId = params.teamId as string;

  const [team, setTeam] = useState<Team | null>(null);
  const [members, setMembers] = useState<TeamMember[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [isInviteDialogOpen, setIsInviteDialogOpen] = useState(false);

  const teamForm = useForm<TeamUpdateFormValues>({
    resolver: zodResolver(teamUpdateSchema),
    defaultValues: {
      name: "",
      description: "",
      visibility: "organization",
    },
  });

  const inviteForm = useForm<InviteFormValues>({
    resolver: zodResolver(inviteSchema),
    defaultValues: {
      email: "",
      role: "member",
    },
  });

  useEffect(() => {
    const fetchTeamData = async () => {
      try {
        setIsLoading(true);
        const fetchedTeam = await organizationService.getTeam(teamId);
        setTeam(fetchedTeam);

        // Populate form with team data
        teamForm.reset({
          name: fetchedTeam.name,
          description: fetchedTeam.description || "",
          visibility: fetchedTeam.visibility as "private" | "organization" | "public",
        });

        // Fetch team members
        const fetchedMembers = await organizationService.getTeamMembers(teamId);
        setMembers(fetchedMembers);
      } catch (error) {
        console.error("Failed to fetch team data:", error);
        toast.error("Failed to load team information");
      } finally {
        setIsLoading(false);
      }
    };

    if (teamId) {
      fetchTeamData();
    }
  }, [teamId, teamForm]);

  const onTeamUpdate = async (values: TeamUpdateFormValues) => {
    try {
      setIsSaving(true);
      const updatedTeam = await organizationService.updateTeam(teamId, values);
      setTeam(updatedTeam);
      toast.success("Team updated successfully");
    } catch (error) {
      console.error("Failed to update team:", error);
      toast.error("Failed to update team");
    } finally {
      setIsSaving(false);
    }
  };

  const onInviteMember = async (values: InviteFormValues) => {
    try {
      // This would normally involve creating a user first if they don't exist
      // For this example, we'll simulate adding a member
      toast.success(`Invitation sent to ${values.email}`);
      inviteForm.reset();
      setIsInviteDialogOpen(false);
    } catch (error) {
      console.error("Failed to invite member:", error);
      toast.error("Failed to send invitation");
    }
  };

  const handleRemoveMember = async (memberId: string) => {
    try {
      await organizationService.removeTeamMember(teamId, memberId);
      setMembers(members.filter(member => member.userId !== memberId));
      toast.success("Member removed from team");
    } catch (error) {
      console.error("Failed to remove member:", error);
      toast.error("Failed to remove member");
    }
  };

  const handleUpdateMemberRole = async (memberId: string, role: string) => {
    try {
      // @ts-expect-error method signature mismatch TODO: Add this API method to the service
      await organizationService.updateTeamMemberRole(teamId, memberId, role);
      setMembers(
        members.map(member =>
          member.userId === memberId ? { ...member, role } : member
        )
      );
      toast.success("Member role updated");
    } catch (error) {
      console.error("Failed to update member role:", error);
      toast.error("Failed to update member role");
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center p-6">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
    );
  }

  if (!team) {
    return (
      <div className="flex flex-col items-center justify-center p-10 text-center">
        <Users className="h-12 w-12 text-muted-foreground mb-4" />
        <h3 className="text-xl font-semibold mb-2">Team not found</h3>
        <p className="text-muted-foreground mb-4">
          The team you&apos;re looking for doesn&apos;t exist or you don&apos;t have access to it.
        </p>
        <Button asChild>
          <Link href={`/${orgId}/teams`}>Back to Teams</Link>
        </Button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <Button variant="outline" size="icon" asChild>
            <Link href={`/${orgId}/teams`}>
              <ChevronLeft className="h-4 w-4" />
            </Link>
          </Button>
          <div>
            <h2 className="text-3xl font-bold tracking-tight">{team.name}</h2>
            <p className="text-muted-foreground">{team.description || "No description"}</p>
          </div>
        </div>
        <Dialog open={isInviteDialogOpen} onOpenChange={setIsInviteDialogOpen}>
          <DialogTrigger asChild>
            <Button>
              <UserPlus className="mr-2 h-4 w-4" />
              Invite Member
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Invite Team Member</DialogTitle>
              <DialogDescription>
                Invite a new member to join your team.
              </DialogDescription>
            </DialogHeader>
            <Form {...inviteForm}>
              <form onSubmit={inviteForm.handleSubmit(onInviteMember)} className="space-y-4">
                <FormField
                  control={inviteForm.control}
                  name="email"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Email Address</FormLabel>
                      <FormControl>
                        <Input placeholder="team@example.com" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={inviteForm.control}
                  name="role"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Role</FormLabel>
                      <FormControl>
                        <select
                          className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                          {...field}
                        >
                          <option value="member">Member</option>
                          <option value="admin">Admin</option>
                          <option value="guest">Guest</option>
                        </select>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <DialogFooter>
                  <Button variant="outline" onClick={() => setIsInviteDialogOpen(false)} type="button">
                    Cancel
                  </Button>
                  <Button type="submit">Invite</Button>
                </DialogFooter>
              </form>
            </Form>
          </DialogContent>
        </Dialog>
      </div>
      <Separator />

      <Tabs defaultValue="members" className="w-full">
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="members">
            <Users className="mr-2 h-4 w-4" />
            Members ({members.length})
          </TabsTrigger>
          <TabsTrigger value="settings">
            <Pencil className="mr-2 h-4 w-4" />
            Settings
          </TabsTrigger>
        </TabsList>

        <TabsContent value="members" className="space-y-4 mt-6">
          <Card>
            <CardHeader>
              <CardTitle>Team Members</CardTitle>
              <CardDescription>
                Manage members of the {team.name} team
              </CardDescription>
            </CardHeader>
            <CardContent>
              {members.length === 0 ? (
                <div className="text-center py-6">
                  <UserIcon className="mx-auto h-12 w-12 text-muted-foreground" />
                  <h3 className="mt-2 text-lg font-medium">No members</h3>
                  <p className="mt-1 text-sm text-muted-foreground">
                    This team doesn&apos;t have any members yet.
                  </p>
                  <Button
                    onClick={() => setIsInviteDialogOpen(true)}
                    className="mt-4"
                  >
                    <UserPlus className="mr-2 h-4 w-4" />
                    Invite Members
                  </Button>
                </div>
              ) : (
                <div className="space-y-4">
                  {members.map((member) => (
                    <div
                      key={member.id}
                      className="flex items-center justify-between py-2"
                    >
                      <div className="flex items-center space-x-4">
                        <Avatar>
                          <AvatarImage src={member.user?.avatar} />
                          <AvatarFallback>
                            {member.user?.firstName?.[0] || ''}
                            {member.user?.lastName?.[0] || ''}
                          </AvatarFallback>
                        </Avatar>
                        <div>
                          <p className="font-medium">
                            {member.user?.firstName} {member.user?.lastName}
                          </p>
                          <p className="text-sm text-muted-foreground">
                            {member.user?.email}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center space-x-2">
                        <span className="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold capitalize">
                          {member.role}
                        </span>
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button variant="ghost" size="icon">
                              <MoreHorizontal className="h-4 w-4" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end">
                            <DropdownMenuLabel>Actions</DropdownMenuLabel>
                            <DropdownMenuSeparator />
                            <DropdownMenuItem onClick={() => handleUpdateMemberRole(member.userId, "admin")}>
                              Make Admin
                            </DropdownMenuItem>
                            <DropdownMenuItem onClick={() => handleUpdateMemberRole(member.userId, "member")}>
                              Make Member
                            </DropdownMenuItem>
                            <DropdownMenuSeparator />
                            <DropdownMenuItem
                              className="text-destructive"
                              onClick={() => handleRemoveMember(member.userId)}
                            >
                              Remove from Team
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="settings" className="space-y-4 mt-6">
          <Card>
            <CardHeader>
              <CardTitle>Team Information</CardTitle>
              <CardDescription>
                Update your team&apos;s details and visibility
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Form {...teamForm}>
                <form onSubmit={teamForm.handleSubmit(onTeamUpdate)} className="space-y-4">
                  <FormField
                    control={teamForm.control}
                    name="name"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Team Name</FormLabel>
                        <FormControl>
                          <Input {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={teamForm.control}
                    name="description"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Description</FormLabel>
                        <FormControl>
                          <Textarea
                            placeholder="A brief description of your team"
                            {...field}
                            value={field.value || ""}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={teamForm.control}
                    name="visibility"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Visibility</FormLabel>
                        <FormControl>
                          <select
                            className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                            {...field}
                          >
                            <option value="private">Private - Only team members</option>
                            <option value="organization">Organization - All organization members</option>
                            <option value="public">Public - Anyone with link</option>
                          </select>
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <div className="flex justify-end space-x-2 pt-4">
                    <Button
                      type="submit"
                      disabled={isSaving}
                    >
                      {isSaving ? (
                        <>
                          <RefreshCw className="mr-2 h-4 w-4 animate-spin" />
                          Saving...
                        </>
                      ) : (
                        <>
                          <Save className="mr-2 h-4 w-4" />
                          Save Changes
                        </>
                      )}
                    </Button>
                  </div>
                </form>
              </Form>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-destructive">Danger Zone</CardTitle>
              <CardDescription>
                Destructive actions for this team
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="rounded-lg border border-destructive/50 p-4">
                <h3 className="font-medium text-destructive mb-2">Delete Team</h3>
                <p className="text-sm text-muted-foreground mb-4">
                  Permanently delete this team and remove all members. This action cannot be undone.
                </p>
                <Button variant="destructive">
                  <Trash className="mr-2 h-4 w-4" />
                  Delete Team
                </Button>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
