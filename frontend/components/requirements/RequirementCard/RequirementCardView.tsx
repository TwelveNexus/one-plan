import React from "react";
import RequirementCard from "./RequirementCard";
import type { Requirement } from "@/types";

interface RequirementCardViewProps {
  requirements: Requirement[];
  onAnalyzeRequirement: (id: string) => void;
}

const RequirementCardView: React.FC<RequirementCardViewProps> = ({
  requirements,
  onAnalyzeRequirement,
}) => {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {requirements.map((requirement) => (
        <RequirementCard
          key={requirement.id}
          requirement={requirement}
          onAnalyzeRequirement={onAnalyzeRequirement}
        />
      ))}
    </div>
  );
};

export default RequirementCardView;
