CREATE TABLE projects (
    id BINARY(16) NOT NULL PRIMARY KEY,
    organization_id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    project_key VARCHAR(50) NOT NULL,
    visibility VARCHAR(50) NOT NULL DEFAULT 'private',
    start_date DATE,
    target_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'planning',
    settings JSON,
    metadata JSON,
    created_by BINARY(16) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_org_id (organization_id),
    UNIQUE KEY uk_org_project_key (organization_id, project_key)
);
