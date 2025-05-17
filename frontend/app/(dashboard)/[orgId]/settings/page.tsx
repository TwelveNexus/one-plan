"use client";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Form,
  FormControl,
  FormDescription,
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
import { Organization } from "@/types";
import { zodResolver } from "@hookform/resolvers/zod";
import { Building, CreditCard, Info, Lock, RefreshCw, Save, Trash } from "lucide-react";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

const generalFormSchema = z.object({
  name: z.string().min(2, {
    message: "Organization name must be at least 2 characters.",
  }),
  displayName: z.string().min(2, {
    message: "Display name must be at least 2 characters.",
  }),
  description: z.string().optional(),
  website: z.string().url({ message: "Please enter a valid URL" }).optional().or(z.literal("")),
  industry: z.string().optional(),
});

type GeneralFormValues = z.infer<typeof generalFormSchema>;

export default function OrganizationSettingsPage() {
  const params = useParams();
  const orgId = params.orgId as string;
  const [organization, setOrganization] = useState<Organization | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);

  const generalForm = useForm<GeneralFormValues>({
    resolver: zodResolver(generalFormSchema),
    defaultValues: {
      name: "",
      displayName: "",
      description: "",
      website: "",
      industry: "",
    },
  });

  useEffect(() => {
    const fetchOrganization = async () => {
      try {
        setIsLoading(true);
        const fetchedOrg = await organizationService.getOrganization(orgId);
        setOrganization(fetchedOrg);

        // Populate form with organization data
        generalForm.reset({
          name: fetchedOrg.name || "",
          displayName: fetchedOrg.displayName || "",
          description: fetchedOrg.description || "",
          website: fetchedOrg.website || "",
          industry: fetchedOrg.industry || "",
        });
      } catch (error) {
        console.error("Failed to fetch organization:", error);
        toast.error("Failed to load organization information");
      } finally {
        setIsLoading(false);
      }
    };

    if (orgId) {
      fetchOrganization();
    }
  }, [orgId, generalForm]);

  const onGeneralSubmit = async (values: GeneralFormValues) => {
    try {
      setIsSaving(true);
      const updatedOrg = await organizationService.updateOrganization(orgId, values);
      setOrganization(updatedOrg);
      toast.success("Organization updated successfully");
    } catch (error) {
      console.error("Failed to update organization:", error);
      toast.error("Failed to update organization");
    } finally {
      setIsSaving(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center p-6">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Organization Settings</h2>
        <p className="text-muted-foreground">
          Manage your organization preferences and configurations
        </p>
      </div>
      <Separator />

      <Tabs defaultValue="general" className="w-full">
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="general">
            <Info className="mr-2 h-4 w-4" />
            General
          </TabsTrigger>
          <TabsTrigger value="billing">
            <CreditCard className="mr-2 h-4 w-4" />
            Billing
          </TabsTrigger>
          <TabsTrigger value="security">
            <Lock className="mr-2 h-4 w-4" />
            Security
          </TabsTrigger>
          <TabsTrigger value="advanced">
            <RefreshCw className="mr-2 h-4 w-4" />
            Advanced
          </TabsTrigger>
        </TabsList>

        <TabsContent value="general" className="space-y-4 mt-6">
          <Card>
            <CardHeader>
              <CardTitle>General Information</CardTitle>
              <CardDescription>
                Update your organization&apos;s basic information
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Form {...generalForm}>
                <form onSubmit={generalForm.handleSubmit(onGeneralSubmit)} className="space-y-4">
                  <FormField
                    control={generalForm.control}
                    name="name"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Organization Name</FormLabel>
                        <FormControl>
                          <Input placeholder="Acme Inc." {...field} />
                        </FormControl>
                        <FormDescription>
                          This is your organization&apos;s unique identifier.
                        </FormDescription>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={generalForm.control}
                    name="displayName"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Display Name</FormLabel>
                        <FormControl>
                          <Input placeholder="Acme Corporation" {...field} />
                        </FormControl>
                        <FormDescription>
                          This is how your organization will be displayed.
                        </FormDescription>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={generalForm.control}
                    name="description"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Description</FormLabel>
                        <FormControl>
                          <Textarea
                            placeholder="A brief description of your organization"
                            {...field}
                            value={field.value || ""}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <FormField
                      control={generalForm.control}
                      name="website"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Website</FormLabel>
                          <FormControl>
                            <Input placeholder="https://example.com" {...field} value={field.value || ""} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <FormField
                      control={generalForm.control}
                      name="industry"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Industry</FormLabel>
                          <FormControl>
                            <Input placeholder="Technology" {...field} value={field.value || ""} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>

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
              <CardTitle>Organization Logo</CardTitle>
              <CardDescription>
                Upload or update your organization logo
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="flex items-center gap-4">
                <div className="h-24 w-24 rounded-lg border flex items-center justify-center bg-muted">
                  {organization?.logo ? (
                    // eslint-disable-next-line @next/next/no-img-element
                    <img
                      src={organization.logo}
                      alt={organization.name}
                      className="h-24 w-24 object-contain rounded-lg"
                    />
                  ) : (
                    <Building className="h-12 w-12 text-muted-foreground" />
                  )}
                </div>
                <div className="space-y-2">
                  <Button variant="outline">
                    Upload Logo
                  </Button>
                  <p className="text-xs text-muted-foreground">
                    Recommended size: 256x256 pixels. PNG or JPG.
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="billing" className="space-y-4 mt-6">
          <Card>
            <CardHeader>
              <CardTitle>Subscription Plan</CardTitle>
              <CardDescription>
                Manage your subscription and billing information
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="bg-muted p-4 rounded-lg">
                <div className="flex justify-between items-center mb-2">
                  <h3 className="font-medium">Current Plan</h3>
                  <span className="bg-primary/10 text-primary text-xs py-1 px-2 rounded-full">
                    {organization?.settings?.subscription?.plan || "Free"}
                  </span>
                </div>
                <p className="text-sm text-muted-foreground">
                  {organization?.settings?.subscription?.status === "active" ?
                    "Your subscription is active and will renew automatically." :
                    "You are currently on the free plan with limited features."}
                </p>
              </div>

              <div className="space-y-2">
                <h3 className="font-medium">Payment Method</h3>
                {organization?.settings?.subscription?.hasPaymentMethod ? (
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <CreditCard className="h-5 w-5 mr-2 text-muted-foreground" />
                      <span>•••• •••• •••• 4242</span>
                    </div>
                    <Button variant="outline" size="sm">
                      Update
                    </Button>
                  </div>
                ) : (
                  <Button>
                    Add Payment Method
                  </Button>
                )}
              </div>

              <Separator />

              <div className="space-y-2">
                <h3 className="font-medium">Billing History</h3>
                <div className="text-center p-4 text-muted-foreground">
                  No billing history available.
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="security" className="space-y-4 mt-6">
          <Card>
            <CardHeader>
              <CardTitle>Security Settings</CardTitle>
              <CardDescription>
                Configure security options for your organization
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between py-2">
                <div>
                  <h3 className="font-medium">Two-Factor Authentication</h3>
                  <p className="text-sm text-muted-foreground">
                    Require 2FA for organization members
                  </p>
                </div>
                <Button variant="outline">Configure</Button>
              </div>

              <Separator />

              <div className="flex items-center justify-between py-2">
                <div>
                  <h3 className="font-medium">Single Sign-On (SSO)</h3>
                  <p className="text-sm text-muted-foreground">
                    Enable SAML or OAuth based SSO
                  </p>
                </div>
                <Button variant="outline">Configure</Button>
              </div>

              <Separator />

              <div className="flex items-center justify-between py-2">
                <div>
                  <h3 className="font-medium">Session Management</h3>
                  <p className="text-sm text-muted-foreground">
                    Control session duration and security
                  </p>
                </div>
                <Button variant="outline">Configure</Button>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="advanced" className="space-y-4 mt-6">
          <Card>
            <CardHeader>
              <CardTitle>Danger Zone</CardTitle>
              <CardDescription>
                These actions are irreversible and potentially destructive
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="rounded-lg border border-destructive/50 p-4">
                <h3 className="font-medium text-destructive mb-2">Delete Organization</h3>
                <p className="text-sm text-muted-foreground mb-4">
                  Permanently delete this organization and all its data. This action cannot be undone.
                </p>
                <Button variant="destructive">
                  <Trash className="mr-2 h-4 w-4" />
                  Delete Organization
                </Button>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
