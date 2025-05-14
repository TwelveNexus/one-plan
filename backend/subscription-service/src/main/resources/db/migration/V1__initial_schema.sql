-- Plans table
CREATE TABLE plans (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(20) NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    trial_days INT NOT NULL DEFAULT 14,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    popular BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    INDEX idx_plan_code (code),
    INDEX idx_active_plans (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Plan billing cycles
CREATE TABLE plan_billing_cycles (
    plan_id VARCHAR(36) NOT NULL,
    billing_cycle VARCHAR(20) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (plan_id, billing_cycle),
    FOREIGN KEY (plan_id) REFERENCES plans(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Plan features
CREATE TABLE plan_features (
    plan_id VARCHAR(36) NOT NULL,
    feature_name VARCHAR(255) NOT NULL,
    feature_value VARCHAR(255) NOT NULL,
    PRIMARY KEY (plan_id, feature_name),
    FOREIGN KEY (plan_id) REFERENCES plans(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Plan limits
CREATE TABLE plan_limits (
    plan_id VARCHAR(36) NOT NULL,
    limit_name VARCHAR(255) NOT NULL,
    limit_value INT NOT NULL,
    PRIMARY KEY (plan_id, limit_name),
    FOREIGN KEY (plan_id) REFERENCES plans(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Subscriptions table
CREATE TABLE subscriptions (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    plan_id VARCHAR(36) NOT NULL,
    billing_cycle VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    current_period_start TIMESTAMP NOT NULL,
    current_period_end TIMESTAMP NOT NULL,
    trial_start TIMESTAMP,
    trial_end TIMESTAMP,
    cancelled_at TIMESTAMP,
    cancellation_reason VARCHAR(500),
    auto_renew BOOLEAN NOT NULL DEFAULT TRUE,
    coupon_code VARCHAR(50),
    discount_percentage DECIMAL(5,2),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (plan_id) REFERENCES plans(id),
    INDEX idx_sub_tenant (tenant_id),
    INDEX idx_sub_status (status),
    INDEX idx_sub_renewal (status, current_period_end, auto_renew)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Subscription metadata
CREATE TABLE subscription_metadata (
    subscription_id VARCHAR(36) NOT NULL,
    key_ VARCHAR(255) NOT NULL,
    value VARCHAR(1000),
    PRIMARY KEY (subscription_id, key_),
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payments table
CREATE TABLE payments (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    subscription_id VARCHAR(36),
    status VARCHAR(20) NOT NULL,
    gateway VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    gateway_order_id VARCHAR(255),
    gateway_payment_id VARCHAR(255),
    gateway_signature VARCHAR(500),
    description VARCHAR(500),
    failure_reason VARCHAR(500),
    attempt_count INT NOT NULL DEFAULT 1,
    paid_at TIMESTAMP,
    refunded_amount DECIMAL(10,2),
    refunded_at TIMESTAMP,
    refund_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id),
    INDEX idx_payment_tenant (tenant_id),
    INDEX idx_payment_status (status),
    INDEX idx_payment_gateway_order (gateway_order_id),
    INDEX idx_payment_gateway_payment (gateway_payment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payment metadata
CREATE TABLE payment_metadata (
    payment_id VARCHAR(36) NOT NULL,
    key_ VARCHAR(255) NOT NULL,
    value VARCHAR(1000),
    PRIMARY KEY (payment_id, key_),
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Invoices table
CREATE TABLE invoices (
    id VARCHAR(36) PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    tenant_id VARCHAR(36) NOT NULL,
    subscription_id VARCHAR(36) NOT NULL,
    payment_id VARCHAR(36),
    status VARCHAR(20) NOT NULL,
    invoice_date DATE NOT NULL,
    due_date DATE NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 18.00,
    tax_amount DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    notes VARCHAR(500),
    paid_at TIMESTAMP,
    pdf_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id),
    FOREIGN KEY (payment_id) REFERENCES payments(id),
    INDEX idx_invoice_tenant (tenant_id),
    INDEX idx_invoice_status (status),
    INDEX idx_invoice_date (invoice_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Billing address (embedded in invoices)
ALTER TABLE invoices ADD COLUMN company_name VARCHAR(255);
ALTER TABLE invoices ADD COLUMN contact_name VARCHAR(255);
ALTER TABLE invoices ADD COLUMN address_line1 VARCHAR(255);
ALTER TABLE invoices ADD COLUMN address_line2 VARCHAR(255);
ALTER TABLE invoices ADD COLUMN city VARCHAR(100);
ALTER TABLE invoices ADD COLUMN state VARCHAR(100);
ALTER TABLE invoices ADD COLUMN postal_code VARCHAR(20);
ALTER TABLE invoices ADD COLUMN country VARCHAR(50) DEFAULT 'India';
ALTER TABLE invoices ADD COLUMN gst_number VARCHAR(50);
ALTER TABLE invoices ADD COLUMN pan_number VARCHAR(20);
ALTER TABLE invoices ADD COLUMN phone VARCHAR(20);
ALTER TABLE invoices ADD COLUMN email VARCHAR(255);

-- Invoice items
CREATE TABLE invoice_items (
    id VARCHAR(36) PRIMARY KEY,
    invoice_id VARCHAR(36) NOT NULL,
    description VARCHAR(500) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    hsa_code VARCHAR(20),
    period_start TIMESTAMP,
    period_end TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payment methods
CREATE TABLE payment_methods (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    gateway VARCHAR(20) NOT NULL,
    type VARCHAR(50) NOT NULL,
    token_id VARCHAR(255),
    card_last4 VARCHAR(4),
    card_brand VARCHAR(50),
    upi_id VARCHAR(255),
    bank_name VARCHAR(100),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    INDEX idx_payment_method_tenant (tenant_id),
    INDEX idx_payment_method_default (tenant_id, is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payment method metadata
CREATE TABLE payment_method_metadata (
    payment_method_id VARCHAR(36) NOT NULL,
    key_ VARCHAR(255) NOT NULL,
    value VARCHAR(1000),
    PRIMARY KEY (payment_method_id, key_),
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
