CREATE TABLE notifications (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    tenant_id VARCHAR(36) NOT NULL,
    type VARCHAR(50) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    recipient VARCHAR(255),
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    read_at TIMESTAMP,
    retry_count INT DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    INDEX idx_user_tenant (user_id, tenant_id),
    INDEX idx_status (status),
    INDEX idx_scheduled (scheduled_at),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notification_metadata (
    notification_id VARCHAR(36) NOT NULL,
    key_ VARCHAR(255) NOT NULL,
    value VARCHAR(1000),
    PRIMARY KEY (notification_id, key_),
    FOREIGN KEY (notification_id) REFERENCES notifications(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notification_preferences (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    tenant_id VARCHAR(36) NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    digest_enabled BOOLEAN DEFAULT FALSE,
    digest_schedule VARCHAR(255),
    quiet_time_start TIMESTAMP,
    quiet_time_end TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE KEY uk_user_tenant_type (user_id, tenant_id, notification_type),
    INDEX idx_user_tenant_prefs (user_id, tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE preference_channels (
    notification_preference_id VARCHAR(36) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    PRIMARY KEY (notification_preference_id, channel),
    FOREIGN KEY (notification_preference_id) REFERENCES notification_preferences(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notification_templates (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    language VARCHAR(10) NOT NULL DEFAULT 'en',
    subject VARCHAR(255) NOT NULL,
    body_template TEXT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE KEY uk_type_channel_lang (type, channel, language, active),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE template_variables (
    notification_template_id VARCHAR(36) NOT NULL,
    variable_name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    PRIMARY KEY (notification_template_id, variable_name),
    FOREIGN KEY (notification_template_id) REFERENCES notification_templates(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notification_digests (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    tenant_id VARCHAR(36) NOT NULL,
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL,
    sent BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    INDEX idx_user_tenant_digest (user_id, tenant_id),
    INDEX idx_period (period_start, period_end),
    INDEX idx_sent (sent)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
