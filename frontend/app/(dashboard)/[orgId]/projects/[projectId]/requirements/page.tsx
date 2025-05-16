"use client";

import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { requirementService } from "@/services";
import type { Requirement } from "@/types";
import { FileQuestion } from "lucide-react";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { toast } from "sonner";

import RequirementCardView from "@/components/requirements/RequirementCard/RequirementCardView";
import RequirementCreateDialog from "@/components/requirements/RequirementCreateDialog";
import RequirementFilters from "@/components/requirements/RequirementFilters";
import RequirementHeader from "@/components/requirements/RequirementHeader";
import RequirementTableView from "@/components/requirements/RequirementTable/RequirementTableView";

export default function RequirementsPage() {
  const params = useParams();
  const orgId = params.orgId as string;
  const projectId = params.projectId as string;

  const [requirements, setRequirements] = useState<Requirement[]>([]);
  const [filteredRequirements, setFilteredRequirements] = useState<
    Requirement[]
  >([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");
  const [categoryFilter, setCategoryFilter] = useState("all");
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  useEffect(() => {
    loadRequirements();
  }, [projectId]);

  useEffect(() => {
    filterRequirements();
  }, [requirements, searchQuery, statusFilter, categoryFilter]);

  const loadRequirements = async () => {
    try {
      setIsLoading(true);
      const data = await requirementService.getRequirements(projectId);
      setRequirements(data);
    } catch (error) {
      console.error("Failed to load requirements:", error);
      toast.error("Failed to load requirements");
    } finally {
      setIsLoading(false);
    }
  };

  const filterRequirements = () => {
    let filtered = [...requirements];

    if (searchQuery) {
      filtered = filtered.filter(
        (requirement) =>
          requirement.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
          requirement.description
            ?.toLowerCase()
            .includes(searchQuery.toLowerCase())
      );
    }

    if (statusFilter !== "all") {
      filtered = filtered.filter(
        (requirement) => requirement.status === statusFilter
      );
    }

    if (categoryFilter !== "all") {
      filtered = filtered.filter(
        (requirement) => requirement.category === categoryFilter
      );
    }

    setFilteredRequirements(filtered);
  };

  const handleCreateRequirement = async (
    newRequirement: Partial<Requirement>
  ) => {
    try {
      if (!newRequirement.title) {
        toast.error("Title is required");
        return;
      }

      const created = await requirementService.createRequirement(
        projectId,
        newRequirement
      );
      setRequirements([...requirements, created]);
      setIsDialogOpen(false);
      toast.success("Requirement created successfully");
    } catch (error) {
      console.error("Failed to create requirement:", error);
      toast.error("Failed to create requirement");
    }
  };

  const handleAnalyzeRequirement = async (id: string) => {
    try {
      toast.info("Analyzing requirement...");
      const analyzed = await requirementService.analyzeRequirement(id);

      // Update the requirement in the list
      setRequirements(
        requirements.map((req) => (req.id === id ? analyzed : req))
      );

      toast.success("Requirement analyzed successfully");
    } catch (error) {
      console.error("Failed to analyze requirement:", error);
      toast.error("Failed to analyze requirement");
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
          <p className="mt-2 text-muted-foreground">Loading requirements...</p>
        </div>
      </div>
    );
  }

  // Get unique categories for the filter
  const categories = [
    "all",
    ...new Set(requirements.map((req) => req.category).filter(Boolean)),
  ];

  return (
    <div>
      <RequirementHeader orgId={orgId} projectId={projectId} />

      <div className="flex items-center justify-between mb-6">
        <RequirementFilters
          searchQuery={searchQuery}
          setSearchQuery={setSearchQuery}
          statusFilter={statusFilter}
          setStatusFilter={setStatusFilter}
          categoryFilter={categoryFilter}
          setCategoryFilter={setCategoryFilter}
          categories={categories as string[]}
        />
        <RequirementCreateDialog
          isOpen={isDialogOpen}
          onOpenChange={setIsDialogOpen}
          onCreateRequirement={handleCreateRequirement}
        />
      </div>

      {filteredRequirements.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <FileQuestion className="h-12 w-12 text-muted-foreground mb-4" />
            <h3 className="text-lg font-semibold mb-2">
              No requirements found
            </h3>
            <p className="text-muted-foreground mb-4 text-center max-w-md">
              {searchQuery || statusFilter !== "all" || categoryFilter !== "all"
                ? "No requirements match your search criteria. Try adjusting your filters."
                : "Get started by creating your first requirement. Requirements define what needs to be built."}
            </p>
            {!searchQuery &&
              statusFilter === "all" &&
              categoryFilter === "all" && (
                <Button onClick={() => setIsDialogOpen(true)}>
                  Create First Requirement
                </Button>
              )}
          </CardContent>
        </Card>
      ) : (
        <Tabs defaultValue="table" className="mb-6">
          <TabsList className="mb-4">
            <TabsTrigger value="table">Table View</TabsTrigger>
            <TabsTrigger value="card">Card View</TabsTrigger>
          </TabsList>

          <TabsContent value="table">
            <RequirementTableView
              requirements={filteredRequirements}
              onAnalyzeRequirement={handleAnalyzeRequirement}
            />
          </TabsContent>

          <TabsContent value="card">
            <RequirementCardView
              requirements={filteredRequirements}
              onAnalyzeRequirement={handleAnalyzeRequirement}
            />
          </TabsContent>
        </Tabs>
      )}
    </div>
  );
}
