CREATE TABLE tasks (
    id BINARY(16) NOT NULL PRIMARY KEY,
    project_id BINARY(16) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'TODO',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    assignee_id BINARY(16),
    reporter_id BINARY(16) NOT NULL,
    story_points INT,
    start_date DATE,
    due_date DATE,
    estimated_hours FLOAT,
    actual_hours FLOAT,
    parent_id BINARY(16),
    tags JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_project_id (project_id),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_status (status),
    FOREIGN KEY (parent_id) REFERENCES tasks(id) ON DELETE CASCADE
);

CREATE TABLE task_attachments (
    id BINARY(16) NOT NULL PRIMARY KEY,
    task_id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL,
    content_type VARCHAR(100),
    size BIGINT,
    uploaded_by BINARY(16) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

CREATE TABLE task_dependencies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BINARY(16) NOT NULL,
    depends_on_id BINARY(16) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_task_dependency (task_id, depends_on_id),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (depends_on_id) REFERENCES tasks(id) ON DELETE CASCADE
);

CREATE TABLE comments (
    id BINARY(16) NOT NULL PRIMARY KEY,
    task_id BINARY(16) NOT NULL,
    content TEXT NOT NULL,
    author_id BINARY(16) NOT NULL,
    parent_id BINARY(16),
    is_edited BOOLEAN DEFAULT FALSE,
    is_resolved BOOLEAN DEFAULT FALSE,
    resolved_by BINARY(16),
    resolved_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE,
    INDEX idx_task_id (task_id)
);

CREATE TABLE comment_attachments (
    id BINARY(16) NOT NULL PRIMARY KEY,
    comment_id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL,
    content_type VARCHAR(100),
    size BIGINT,
    FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE
);

CREATE TABLE comment_reactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    emoji VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_comment_user_emoji (comment_id, user_id, emoji),
    FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE
);
