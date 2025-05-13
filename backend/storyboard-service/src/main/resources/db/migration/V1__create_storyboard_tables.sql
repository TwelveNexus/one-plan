CREATE TABLE IF NOT EXISTS storyboards (
    id VARCHAR(36) PRIMARY KEY,
    project_id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    visibility VARCHAR(20) DEFAULT 'private',
    status VARCHAR(50) DEFAULT 'draft',
    share_token VARCHAR(100) UNIQUE,
    share_password VARCHAR(255),
    share_expires_at TIMESTAMP NULL,
    is_password_protected BOOLEAN DEFAULT FALSE,
    version INTEGER DEFAULT 1,
    created_by VARCHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_project_id (project_id),
    INDEX idx_share_token (share_token),
    INDEX idx_created_by (created_by)
);

CREATE TABLE IF NOT EXISTS story_cards (
    id VARCHAR(36) PRIMARY KEY,
    storyboard_id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    acceptance_criteria TEXT,
    story_points INTEGER,
    priority VARCHAR(20) DEFAULT 'medium',
    position_x INTEGER DEFAULT 0,
    position_y INTEGER DEFAULT 0,
    width INTEGER DEFAULT 200,
    height INTEGER DEFAULT 150,
    color VARCHAR(7) DEFAULT '#FFFFFF',
    status VARCHAR(50) DEFAULT 'todo',
    requirement_id VARCHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (storyboard_id) REFERENCES storyboards(id),
    INDEX idx_storyboard_id (storyboard_id),
    INDEX idx_requirement_id (requirement_id)
);

CREATE TABLE IF NOT EXISTS story_relationships (
    id VARCHAR(36) PRIMARY KEY,
    storyboard_id VARCHAR(36) NOT NULL,
    from_story_id VARCHAR(36) NOT NULL,
    to_story_id VARCHAR(36) NOT NULL,
    relationship_type VARCHAR(50) DEFAULT 'depends_on',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (storyboard_id) REFERENCES storyboards(id),
    FOREIGN KEY (from_story_id) REFERENCES story_cards(id),
    FOREIGN KEY (to_story_id) REFERENCES story_cards(id),
    UNIQUE KEY uk_story_relationship (from_story_id, to_story_id)
);

CREATE TABLE IF NOT EXISTS share_access_logs (
    id VARCHAR(36) PRIMARY KEY,
    storyboard_id VARCHAR(36) NOT NULL,
    share_token VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    accessed_by VARCHAR(36),
    access_type VARCHAR(20),
    accessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (storyboard_id) REFERENCES storyboards(id),
    INDEX idx_storyboard_access (storyboard_id, accessed_at)
);
