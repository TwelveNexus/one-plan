"use client";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarProvider,
  SidebarTrigger,
} from "@/components/ui/sidebar";
import { authService, organizationService } from "@/services";
import type { Organization, User } from "@/types";
import {
  FolderKanban,
  LogOut,
  PlusCircle,
  Settings,
  Users
} from "lucide-react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { toast } from "sonner";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const router = useRouter();
  const pathname = usePathname();
  const [user, setUser] = useState<User | null>(null);
  const [organizations, setOrganizations] = useState<Organization[]>([]);
  const [selectedOrg, setSelectedOrg] = useState<Organization | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Extract organization ID from URL
  const orgId = pathname.split("/")[1];

  useEffect(() => {
    const checkAuth = async () => {
      try {
        if (!authService.isAuthenticated()) {
          router.push("/login");
          return;
        }

        const userData = await authService.getMe();
        setUser(userData);

        const orgs = await organizationService.getOrganizations();
        setOrganizations(orgs);

        // Set selected org based on URL or first available
        if (orgId && orgs.length > 0) {
          const selected = orgs.find((org) => org.id === orgId);
          setSelectedOrg(selected || orgs[0]);
        } else if (orgs.length > 0) {
          setSelectedOrg(orgs[0]);
          router.push(`/${orgs[0].id}/projects`);
        }
      } catch (error) {
        console.error("Auth check failed:", error);
        router.push("/login");
      } finally {
        setIsLoading(false);
      }
    };

    checkAuth();
  }, [orgId, router]);

  const handleLogout = async () => {
    try {
      await authService.logout();
      authService.clearTokens();
      router.push("/login");
      toast.success("Logged out successfully");
    } catch (error) {
      console.error("Logout failed:", error);
    }
  };

  const handleOrganizationChange = (org: Organization) => {
    setSelectedOrg(org);
    router.push(`/${org.id}/projects`);
  };

  if (isLoading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
          <p className="mt-2 text-muted-foreground">Loading...</p>
        </div>
      </div>
    );
  }

  const sidebarItems = [
    {
      title: "Projects",
      href: `/${selectedOrg?.id}/projects`,
      icon: FolderKanban,
    },
    {
      title: "Teams",
      href: `/${selectedOrg?.id}/teams`,
      icon: Users,
    },
    {
      title: "Settings",
      href: `/${selectedOrg?.id}/settings`,
      icon: Settings,
    },
  ];

  return (
    <SidebarProvider>
      <div className="flex h-screen w-full">
        <Sidebar>
          <SidebarHeader className="border-b px-4 py-3">
            <div className="flex items-center gap-2">
              <div className="h-8 w-8 rounded-lg bg-primary/10 flex items-center justify-center">
                <span className="text-primary font-bold">OP</span>
              </div>
              <div>
                <h2 className="font-semibold">One Plan</h2>
                <p className="text-xs text-muted-foreground">
                  {selectedOrg?.name}
                </p>
              </div>
            </div>
          </SidebarHeader>
          <SidebarContent>
            <SidebarMenu>
              {sidebarItems.map((item) => (
                <SidebarMenuItem key={item.href}>
                  <SidebarMenuButton
                    asChild
                    isActive={pathname.includes(item.href)}
                  >
                    <Link href={item.href}>
                      <item.icon className="mr-2 h-4 w-4" />
                      {item.title}
                    </Link>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarContent>
          <SidebarFooter className="border-t p-4">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="w-full justify-start">
                  <Avatar className="h-8 w-8 mr-2">
                    <AvatarImage src={user?.avatar} />
                    <AvatarFallback>
                      {user?.firstName?.[0]}
                      {user?.lastName?.[0]}
                    </AvatarFallback>
                  </Avatar>
                  <div className="flex-1 text-left">
                    <p className="text-sm font-medium">
                      {user?.firstName} {user?.lastName}
                    </p>
                    <p className="text-xs text-muted-foreground">
                      {user?.email}
                    </p>
                  </div>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-56">
                <DropdownMenuLabel>My Account</DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem>Profile Settings</DropdownMenuItem>
                <DropdownMenuItem>User Preferences</DropdownMenuItem>
                <DropdownMenuSeparator />
                {organizations.length > 1 && (
                  <>
                    <DropdownMenuLabel>Organizations</DropdownMenuLabel>
                    {organizations.map((org) => (
                      <DropdownMenuItem
                        key={org.id}
                        onClick={() => handleOrganizationChange(org)}
                      >
                        {org.name}
                        {org.id === selectedOrg?.id && " ✓"}
                      </DropdownMenuItem>
                    ))}
                    <DropdownMenuSeparator />
                  </>
                )}
                <DropdownMenuItem onClick={handleLogout}>
                  <LogOut className="mr-2 h-4 w-4" />
                  Log out
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </SidebarFooter>
        </Sidebar>
        <div className="flex-1 overflow-hidden">
          <header className="flex items-center justify-between border-b px-6 py-3">
            <div className="flex items-center gap-4">
              <SidebarTrigger />
              <nav className="flex items-center space-x-4 text-sm">
                <Link
                  href={`/${selectedOrg?.id}/projects`}
                  className={`font-medium ${
                    pathname.includes("/projects")
                      ? "text-primary"
                      : "text-muted-foreground"
                  }`}
                >
                  Projects
                </Link>
                {pathname.includes("/projects/") && (
                  <>
                    <span className="text-muted-foreground">/</span>
                    <span className="font-medium text-primary">
                      Project Details
                    </span>
                  </>
                )}
              </nav>
            </div>
            <div className="flex items-center gap-2">
              <Button variant="outline" size="sm">
                <PlusCircle className="mr-2 h-4 w-4" />
                New Project
              </Button>
            </div>
          </header>
          <main className="flex-1 overflow-y-auto p-6">{children}</main>
        </div>
      </div>
    </SidebarProvider>
  );
}
