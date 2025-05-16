import React from "react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Timeline,
  TimelineItem,
  TimelineConnector,
  TimelineContent,
  TimelineDot,
  TimelineOppositeContent,
  TimelineSeparator,
} from "@/components/ui/timeline";
import { Button } from "@/components/ui/button";
import { format } from "date-fns";
import { History, GitCompare } from "lucide-react";
import type { RequirementVersion } from "@/types";

interface RequirementHistoryProps {
  versions: RequirementVersion[];
}

// Creating a simplified Timeline component for this example
// In a real project, you would create proper UI components for Timeline
const Timeline = ({ children }: { children: React.ReactNode }) => (
  <div className="relative">{children}</div>
);

const TimelineItem = ({ children }: { children: React.ReactNode }) => (
  <div className="mb-8 flex">{children}</div>
);

const TimelineSeparator = ({ children }: { children: React.ReactNode }) => (
  <div className="flex flex-col items-center mr-4">{children}</div>
);

const TimelineDot = ({ className }: { className?: string }) => (
  <div className={`rounded-full h-3 w-3 ${className || "bg-primary"}`}></div>
);

const TimelineConnector = () => (
  <div className="h-full w-px bg-border grow my-1"></div>
);

const TimelineContent = ({ children }: { children: React.ReactNode }) => (
  <div className="p-3 border rounded-md flex-1">{children}</div>
);

const TimelineOppositeContent = ({
  children,
}: {
  children: React.ReactNode;
}) => (
  <div className="w-28 text-right text-xs text-muted-foreground self-center mr-4">
    {children}
  </div>
);

const RequirementHistory: React.FC<RequirementHistoryProps> = ({
  versions,
}) => {
  // Sort versions by versionNumber in descending order
  const sortedVersions = [...versions].sort(
    (a, b) => b.versionNumber - a.versionNumber
  );

  if (versions.length === 0) {
    return (
      <Card>
        <CardContent className="flex flex-col items-center justify-center py-12">
          <History className="h-12 w-12 text-muted-foreground mb-4" />
          <h3 className="text-lg font-semibold mb-2">No version history</h3>
          <p className="text-muted-foreground mb-4 text-center max-w-md">
            There&apos;s only one version of this requirement.
          </p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Version History</CardTitle>
        <CardDescription>
          Track changes made to this requirement over time
        </CardDescription>
      </CardHeader>
      <CardContent>
        <Timeline>
          {sortedVersions.map((version, index) => (
            <TimelineItem key={version.id}>
              <TimelineOppositeContent>
                {format(new Date(version.createdAt), "MMM d, yyyy")}
              </TimelineOppositeContent>
              <TimelineSeparator>
                <TimelineDot
                  className={index === 0 ? "bg-primary" : "bg-muted"}
                />
                {index < sortedVersions.length - 1 && <TimelineConnector />}
              </TimelineSeparator>
              <TimelineContent>
                <div className="flex justify-between items-start">
                  <div>
                    <h4 className="text-sm font-medium">
                      Version {version.versionNumber}
                    </h4>
                    <p className="text-sm text-muted-foreground mt-1">
                      {version.changeSummary || "No change summary provided"}
                    </p>
                    <p className="text-xs text-muted-foreground mt-2">
                      Changed by {version.changedBy || "Unknown"}
                    </p>
                  </div>
                  {index > 0 && (
                    <Button variant="outline" size="sm">
                      <GitCompare className="h-3 w-3 mr-1" />
                      Compare
                    </Button>
                  )}
                </div>
              </TimelineContent>
            </TimelineItem>
          ))}
        </Timeline>
      </CardContent>
    </Card>
  );
};

export default RequirementHistory;
