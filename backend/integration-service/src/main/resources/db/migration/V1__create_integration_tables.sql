CREATE TABLE IF NOT EXISTS git_connections (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    project_id VARCHAR(36) NOT NULL,
    provider VARCHAR(20) NOT NULL,
    repository_name VARCHAR(255) NOT NULL,
    repository_full_name VARCHAR(500),
    repository_url VARCHAR(500),
    default_branch VARCHAR(100) DEFAULT 'main',
    access_token TEXT,
    refresh_token TEXT,
    token_expires_at TIMESTAMP NULL,
    webhook_id VARCHAR(100),
    webhook_secret VARCHAR(255),
    webhook_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    last_sync_at TIMESTAMP NULL,
    sync_status VARCHAR(50) DEFAULT 'pending',
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_project_id (project_id),
    INDEX idx_provider (provider),
    UNIQUE KEY uk_project_repo (project_id, provider, repository_full_name)
);

CREATE TABLE IF NOT EXISTS webhook_configurations (
    id VARCHAR(36) PRIMARY KEY,
    connection_id VARCHAR(36) NOT NULL,
    event_types JSON,
    is_active BOOLEAN DEFAULT TRUE,
    filter_branch VARCHAR(100),
    filter_path_pattern VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (connection_id) REFERENCES git_connections(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS oauth_states (
    id VARCHAR(36) PRIMARY KEY,
    state VARCHAR(255) UNIQUE NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    project_id VARCHAR(36),
    provider VARCHAR(20) NOT NULL,
    redirect_url VARCHAR(500),
    metadata JSON,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_state (state),
    INDEX idx_expires (expires_at)
);

CREATE TABLE IF NOT EXISTS git_commits (
    id VARCHAR(36) PRIMARY KEY,
    connection_id VARCHAR(36) NOT NULL,
    commit_sha VARCHAR(40) NOT NULL,
    branch VARCHAR(100),
    message TEXT,
    author_name VARCHAR(255),
    author_email VARCHAR(255),
    author_date TIMESTAMP,
    files_changed INTEGER,
    additions INTEGER,
    deletions INTEGER,
    url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (connection_id) REFERENCES git_connections(id) ON DELETE CASCADE,
    UNIQUE KEY uk_connection_sha (connection_id, commit_sha),
    INDEX idx_connection_id (connection_id),
    INDEX idx_author_date (author_date)
);

CREATE TABLE IF NOT EXISTS pull_requests (
    id VARCHAR(36) PRIMARY KEY,
    connection_id VARCHAR(36) NOT NULL,
    pr_number INTEGER NOT NULL,
    title VARCHAR(500),
    description TEXT,
    state VARCHAR(20),
    source_branch VARCHAR(100),
    target_branch VARCHAR(100),
    author_name VARCHAR(255),
    author_email VARCHAR(255),
    url VARCHAR(500),
    is_merged BOOLEAN DEFAULT FALSE,
    merge_commit_sha VARCHAR(40),
    created_date TIMESTAMP,
    updated_date TIMESTAMP,
    closed_date TIMESTAMP NULL,
    merged_date TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (connection_id) REFERENCES git_connections(id) ON DELETE CASCADE,
    UNIQUE KEY uk_connection_pr (connection_id, pr_number),
    INDEX idx_connection_id (connection_id),
    INDEX idx_state (state)
);

CREATE TABLE IF NOT EXISTS webhook_events (
    id VARCHAR(36) PRIMARY KEY,
    connection_id VARCHAR(36) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_id VARCHAR(100),
    payload JSON,
    headers JSON,
    received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    status VARCHAR(20) DEFAULT 'pending',
    error_message TEXT,
    FOREIGN KEY (connection_id) REFERENCES git_connections(id) ON DELETE CASCADE,
    INDEX idx_connection_event (connection_id, event_type),
    INDEX idx_status (status),
    INDEX idx_received_at (received_at)
);
