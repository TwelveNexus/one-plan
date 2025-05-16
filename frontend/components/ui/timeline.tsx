import React from 'react';
import { cn } from '@/lib/utils';

// Timeline Container
export interface TimelineProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
}

export function Timeline({ className, children, ...props }: TimelineProps) {
  return (
    <div className={cn("relative", className)} {...props}>
      {children}
    </div>
  );
}

// Timeline Item
export interface TimelineItemProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
}

export function TimelineItem({ className, children, ...props }: TimelineItemProps) {
  return (
    <div className={cn("mb-8 flex", className)} {...props}>
      {children}
    </div>
  );
}

// Timeline Separator (vertical line + dot)
export interface TimelineSeparatorProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
}

export function TimelineSeparator({ className, children, ...props }: TimelineSeparatorProps) {
  return (
    <div className={cn("flex flex-col items-center mr-4", className)} {...props}>
      {children}
    </div>
  );
}

// Timeline Dot
export interface TimelineDotProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: 'default' | 'primary' | 'secondary' | 'muted';
}

export function TimelineDot({
  className,
  variant = 'default',
  ...props
}: TimelineDotProps) {
  const variantStyles = {
    default: "bg-muted",
    primary: "bg-primary",
    secondary: "bg-secondary",
    muted: "bg-muted",
  };

  return (
    <div
      className={cn(
        "rounded-full h-3 w-3",
        variantStyles[variant],
        className
      )}
      {...props}
    />
  );
}

export function TimelineConnector({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn("h-full w-px bg-border grow my-1", className)}
      {...props}
    />
  );
}

// Timeline Content (main content area)
export interface TimelineContentProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
}

export function TimelineContent({ className, children, ...props }: TimelineContentProps) {
  return (
    <div
      className={cn("p-3 border rounded-md flex-1", className)}
      {...props}
    >
      {children}
    </div>
  );
}

// Timeline Opposite Content (content on the opposite side of the timeline)
export interface TimelineOppositeContentProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
}

export function TimelineOppositeContent({ className, children, ...props }: TimelineOppositeContentProps) {
  return (
    <div
      className={cn("w-28 text-right text-xs text-muted-foreground self-center mr-4", className)}
      {...props}
    >
      {children}
    </div>
  );
}
