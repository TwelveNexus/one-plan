-- Email Templates
INSERT INTO notification_templates (id, name, type, channel, language, subject, body_template, active) VALUES
(UUID(), 'Task Assigned Email', 'TASK_ASSIGNED', 'EMAIL', 'en', 
 'You have been assigned to task: {{taskTitle}}',
 '<h2>Hello {{userName}},</h2><p>You have been assigned to a new task:</p><h3>{{taskTitle}}</h3><p>{{taskDescription}}</p><p>Due Date: {{dueDate}}</p><p><a href="{{taskUrl}}">View Task</a></p>',
 true),

(UUID(), 'Comment Mention Email', 'COMMENT_MENTION', 'EMAIL', 'en',
 '{{mentionedBy}} mentioned you in a comment',
 '<h2>Hello {{userName}},</h2><p>{{mentionedBy}} mentioned you in a comment:</p><blockquote>{{commentText}}</blockquote><p><a href="{{commentUrl}}">View Comment</a></p>',
 true),

(UUID(), 'Project Invitation Email', 'PROJECT_INVITATION', 'EMAIL', 'en',
 'You have been invited to join project: {{projectName}}',
 '<h2>Hello {{userName}},</h2><p>{{invitedBy}} has invited you to join the project:</p><h3>{{projectName}}</h3><p>{{projectDescription}}</p><p><a href="{{inviteUrl}}">Accept Invitation</a></p>',
 true);

-- In-App Templates
INSERT INTO notification_templates (id, name, type, channel, language, subject, body_template, active) VALUES
(UUID(), 'Task Assigned In-App', 'TASK_ASSIGNED', 'IN_APP', 'en',
 'New task assigned',
 'You have been assigned to {{taskTitle}}',
 true),

(UUID(), 'Comment Mention In-App', 'COMMENT_MENTION', 'IN_APP', 'en',
 'You were mentioned',
 '{{mentionedBy}} mentioned you in a comment',
 true);

-- Template Variables
INSERT INTO template_variables (notification_template_id, variable_name, description) 
SELECT id, 'userName', 'Recipient user name' FROM notification_templates WHERE type = 'TASK_ASSIGNED';

INSERT INTO template_variables (notification_template_id, variable_name, description) 
SELECT id, 'taskTitle', 'Task title' FROM notification_templates WHERE type = 'TASK_ASSIGNED';

INSERT INTO template_variables (notification_template_id, variable_name, description) 
SELECT id, 'taskDescription', 'Task description' FROM notification_templates WHERE type = 'TASK_ASSIGNED';

INSERT INTO template_variables (notification_template_id, variable_name, description) 
SELECT id, 'taskUrl', 'Direct link to task' FROM notification_templates WHERE type = 'TASK_ASSIGNED';

INSERT INTO template_variables (notification_template_id, variable_name, description) 
SELECT id, 'dueDate', 'Task due date' FROM notification_templates WHERE type = 'TASK_ASSIGNED';
