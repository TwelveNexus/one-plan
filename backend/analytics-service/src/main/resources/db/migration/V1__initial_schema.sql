CREATE TABLE metrics (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    entity_id VARCHAR(36) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    value DOUBLE NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    INDEX idx_metric_lookup (tenant_id, entity_id, entity_type, type, timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE metric_dimensions (
    metric_id VARCHAR(36) NOT NULL,
    dimension VARCHAR(255) NOT NULL,
    value VARCHAR(1000),
    PRIMARY KEY (metric_id, dimension),
    FOREIGN KEY (metric_id) REFERENCES metrics(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE metric_tags (
    metric_id VARCHAR(36) NOT NULL,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY (metric_id, tag),
    FOREIGN KEY (metric_id) REFERENCES metrics(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE aggregated_metrics (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    entity_id VARCHAR(36) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    period VARCHAR(20) NOT NULL,
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL,
    min_value DOUBLE NOT NULL,
    max_value DOUBLE NOT NULL,
    avg_value DOUBLE NOT NULL,
    sum_value DOUBLE NOT NULL,
    count BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    INDEX idx_aggregated_lookup (tenant_id, entity_id, entity_type, type, period, period_start)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE aggregated_metric_percentiles (
    aggregated_metric_id VARCHAR(36) NOT NULL,
    percentile INT NOT NULL,
    value DOUBLE NOT NULL,
    PRIMARY KEY (aggregated_metric_id, percentile),
    FOREIGN KEY (aggregated_metric_id) REFERENCES aggregated_metrics(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE reports (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_by VARCHAR(36) NOT NULL,
    schedule VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    last_run_at TIMESTAMP,
    next_run_at TIMESTAMP,
    last_run_result TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    INDEX idx_report_tenant (tenant_id),
    INDEX idx_scheduled_reports (active, next_run_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE report_parameters (
    report_id VARCHAR(36) NOT NULL,
    param_name VARCHAR(255) NOT NULL,
    param_value VARCHAR(1000),
    PRIMARY KEY (report_id, param_name),
    FOREIGN KEY (report_id) REFERENCES reports(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE dashboards (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    owner_id VARCHAR(36) NOT NULL,
    is_public BOOLEAN DEFAULT FALSE,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    INDEX idx_dashboard_tenant_owner (tenant_id, owner_id),
    INDEX idx_dashboard_public (tenant_id, is_public)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE dashboard_widgets (
    id VARCHAR(36) PRIMARY KEY,
    dashboard_id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    position INT NOT NULL,
    width INT NOT NULL DEFAULT 6,
    height INT NOT NULL DEFAULT 4,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (dashboard_id) REFERENCES dashboards(id) ON DELETE CASCADE,
    INDEX idx_widget_position (dashboard_id, position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE widget_configuration (
    dashboard_widget_id VARCHAR(36) NOT NULL,
    config_key VARCHAR(255) NOT NULL,
    config_value VARCHAR(1000),
    PRIMARY KEY (dashboard_widget_id, config_key),
    FOREIGN KEY (dashboard_widget_id) REFERENCES dashboard_widgets(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
