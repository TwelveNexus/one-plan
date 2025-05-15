export interface Storyboard {
  id: string;
  projectId: string;
  title: string;
  description?: string;
  visibility: "private" | "team" | "public";
  status: "draft" | "published" | "archived";
  shareToken?: string;
  sharePassword?: string;
  shareExpiresAt?: string;
  isPasswordProtected: boolean;
  version: number;
  createdBy?: string;
  createdAt: string;
  updatedAt: string;
  cards?: StoryCard[];
  relationships?: StoryRelationship[];
}

export interface StoryCard {
  id: string;
  storyboardId: string;
  title: string;
  description?: string;
  acceptanceCriteria?: string;
  storyPoints?: number;
  priority: "low" | "medium" | "high" | "critical";
  positionX: number;
  positionY: number;
  width: number;
  height: number;
  color: string;
  status: "todo" | "in_progress" | "done" | "blocked";
  requirementId?: string;
  createdAt: string;
  updatedAt: string;
}

export interface StoryRelationship {
  id: string;
  storyboardId: string;
  fromStoryId: string;
  toStoryId: string;
  relationshipType: "depends_on" | "blocks" | "relates_to";
  createdAt: string;
}

export interface ShareAccessLog {
  id: string;
  storyboardId: string;
  shareToken?: string;
  ipAddress?: string;
  userAgent?: string;
  accessedBy?: string;
  accessType?: string;
  accessedAt: string;
}

export interface StoryboardCanvas {
  storyboardId: string;
  canvasData: {
    zoom: number;
    centerX: number;
    centerY: number;
    layout: "freeform" | "grid" | "hierarchy";
  };
  updatedAt: string;
}
