import React, { useState } from 'react';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import { format } from 'date-fns';
import { MessageSquare, Send } from 'lucide-react';

// Define the Comment type for this component
interface Comment {
  id: string;
  content: string;
  authorId: string;
  author?: {
    id: string;
    firstName: string;
    lastName: string;
    avatar?: string;
  };
  createdAt: string;
  isEdited: boolean;
}

interface RequirementCommentsProps {
  requirementId: string;
  comments: Comment[];
}

const RequirementComments: React.FC<RequirementCommentsProps> = ({
  requirementId,
  comments = []
}) => {
  const [newComment, setNewComment] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmitComment = async () => {
    if (!newComment.trim()) return;

    setIsSubmitting(true);

    try {
      // API call to submit the comment would go here
      // For now, we'll just simulate a delay
      await new Promise(resolve => setTimeout(resolve, 1000));

      // Reset the comment field on success
      setNewComment('');
      console.log('Comment submitted:', requirementId, newComment);

      // In a real app, you would update the comments list with the new comment
      // from the API response
    } catch (error) {
      console.error('Failed to submit comment:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const getInitials = (author: Comment['author']) => {
    if (!author) return '??';
    return `${author.firstName?.[0] || ''}${author.lastName?.[0] || ''}`;
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>Comments</CardTitle>
        <CardDescription>
          Collaborate with your team on this requirement
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {comments.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-8">
            <MessageSquare className="h-12 w-12 text-muted-foreground mb-4" />
            <h3 className="text-lg font-semibold mb-2">No comments yet</h3>
            <p className="text-muted-foreground mb-4 text-center max-w-md">
              Be the first to leave a comment on this requirement.
            </p>
          </div>
        ) : (
          <div className="space-y-6">
            {comments.map((comment) => (
              <div key={comment.id} className="flex gap-4">
                <Avatar>
                  <AvatarImage src={comment.author?.avatar} />
                  <AvatarFallback>{getInitials(comment.author)}</AvatarFallback>
                </Avatar>
                <div className="flex-1">
                  <div className="flex items-center justify-between">
                    <div className="font-medium">
                      {comment.author ? `${comment.author.firstName} ${comment.author.lastName}` : 'Unknown User'}
                    </div>
                    <div className="text-xs text-muted-foreground">
                      {format(new Date(comment.createdAt), 'MMM d, yyyy h:mm a')}
                      {comment.isEdited && ' (edited)'}
                    </div>
                  </div>
                  <div className="mt-2 text-sm">
                    {comment.content}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </CardContent>
      <CardFooter>
        <div className="flex w-full gap-2">
          <Textarea
            placeholder="Add a comment..."
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            className="flex-1"
          />
          <Button
            onClick={handleSubmitComment}
            disabled={isSubmitting || !newComment.trim()}
          >
            {isSubmitting ? (
              <div className="animate-spin h-4 w-4 border-2 border-current border-t-transparent rounded-full"></div>
            ) : (
              <Send className="h-4 w-4" />
            )}
          </Button>
        </div>
      </CardFooter>
    </Card>
  );
};

export default RequirementComments;
