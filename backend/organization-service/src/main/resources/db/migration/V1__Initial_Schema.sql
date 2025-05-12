CREATE TABLE organization (
    id BINARY(16) NOT NULL,
    tenant_id BINARY(16) NOT NULL,
    name VARCHAR(100) NOT NULL,
    display_name VARCHAR(100),
    description TEXT,
    logo VARCHAR(255),
    website VARCHAR(255),
    industry VARCHAR(100),
    size VARCHAR(20),
    settings JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_organization_tenant (tenant_id)
);

CREATE TABLE team (
    id BINARY(16) NOT NULL,
    organization_id BINARY(16) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    avatar VARCHAR(255),
    visibility VARCHAR(20) NOT NULL,
    settings JSON,
    metadata JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_team_organization (organization_id),
    CONSTRAINT fk_team_organization FOREIGN KEY (organization_id) REFERENCES organization (id) ON DELETE CASCADE
);

CREATE TABLE team_member (
    id BINARY(16) NOT NULL,
    team_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    role VARCHAR(20) NOT NULL,
    permissions JSON,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    invited_by BINARY(16),
    status VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_team_member (team_id, user_id),
    INDEX idx_team_member_team (team_id),
    INDEX idx_team_member_user (user_id),
    CONSTRAINT fk_team_member_team FOREIGN KEY (team_id) REFERENCES team (id) ON DELETE CASCADE
);
