/* eslint-disable @typescript-eslint/no-explicit-any */
import { User } from './auth';

export interface Organization {
  id: string;
  tenantId: string;
  name: string;
  displayName?: string;
  description?: string;
  logo?: string;
  website?: string;
  industry?: string;
  size?: string;
  settings?: Record<string, any>;
  createdAt: string;
  updatedAt: string;
}

export interface Team {
  id: string;
  organizationId: string;
  name: string;
  description?: string;
  avatar?: string;
  visibility: string;
  settings?: Record<string, any>;
  metadata?: Record<string, any>;
  createdAt: string;
  updatedAt: string;
}

export interface TeamMember {
  id: string;
  teamId: string;
  userId: string;
  role: string;
  permissions?: Record<string, any>;
  joinedAt: string;
  invitedBy?: string;
  status: string;
  user?: User;
}
