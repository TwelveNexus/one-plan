-- Insert default plans
INSERT INTO plans (id, code, name, description, type, base_price, currency, trial_days, active, popular, sort_order) VALUES
(UUID(), 'starter', 'Starter Plan', 'Perfect for small teams just getting started', 'STARTER', 999.00, 'INR', 14, true, false, 1),
(UUID(), 'professional', 'Professional Plan', 'For growing teams with advanced needs', 'PROFESSIONAL', 2999.00, 'INR', 14, true, true, 2),
(UUID(), 'enterprise', 'Enterprise Plan', 'For large organizations with complex requirements', 'ENTERPRISE', 9999.00, 'INR', 14, true, false, 3);

-- Get plan IDs for reference
SET @starter_id = (SELECT id FROM plans WHERE code = 'starter');
SET @professional_id = (SELECT id FROM plans WHERE code = 'professional');
SET @enterprise_id = (SELECT id FROM plans WHERE code = 'enterprise');

-- Insert billing cycle prices
INSERT INTO plan_billing_cycles (plan_id, billing_cycle, price) VALUES
(@starter_id, 'MONTHLY', 999.00),
(@starter_id, 'QUARTERLY', 2697.00),  -- 10% discount
(@starter_id, 'HALF_YEARLY', 5094.00), -- 15% discount
(@starter_id, 'YEARLY', 9588.00),     -- 20% discount

(@professional_id, 'MONTHLY', 2999.00),
(@professional_id, 'QUARTERLY', 8097.00),  -- 10% discount
(@professional_id, 'HALF_YEARLY', 15294.00), -- 15% discount
(@professional_id, 'YEARLY', 28788.00),     -- 20% discount

(@enterprise_id, 'MONTHLY', 9999.00),
(@enterprise_id, 'QUARTERLY', 26997.00),  -- 10% discount
(@enterprise_id, 'HALF_YEARLY', 50994.00), -- 15% discount
(@enterprise_id, 'YEARLY', 95988.00);     -- 20% discount

-- Insert plan features
INSERT INTO plan_features (plan_id, feature_name, feature_value) VALUES
-- Starter features
(@starter_id, 'basic_reporting', 'true'),
(@starter_id, 'email_support', 'true'),
(@starter_id, 'api_access', 'false'),
(@starter_id, 'custom_branding', 'false'),
(@starter_id, 'advanced_analytics', 'false'),
(@starter_id, 'priority_support', 'false'),
(@starter_id, 'git_integration', 'basic'),
(@starter_id, 'ai_assistance', 'basic'),

-- Professional features
(@professional_id, 'basic_reporting', 'true'),
(@professional_id, 'email_support', 'true'),
(@professional_id, 'api_access', 'true'),
(@professional_id, 'custom_branding', 'true'),
(@professional_id, 'advanced_analytics', 'true'),
(@professional_id, 'priority_support', 'true'),
(@professional_id, 'git_integration', 'advanced'),
(@professional_id, 'ai_assistance', 'advanced'),

-- Enterprise features
(@enterprise_id, 'basic_reporting', 'true'),
(@enterprise_id, 'email_support', 'true'),
(@enterprise_id, 'api_access', 'true'),
(@enterprise_id, 'custom_branding', 'true'),
(@enterprise_id, 'advanced_analytics', 'true'),
(@enterprise_id, 'priority_support', 'true'),
(@enterprise_id, 'git_integration', 'enterprise'),
(@enterprise_id, 'ai_assistance', 'enterprise'),
(@enterprise_id, 'dedicated_support', 'true'),
(@enterprise_id, 'sso_integration', 'true'),
(@enterprise_id, 'audit_logs', 'true');

-- Insert plan limits
INSERT INTO plan_limits (plan_id, limit_name, limit_value) VALUES
-- Starter limits
(@starter_id, 'users', 10),
(@starter_id, 'projects', 5),
(@starter_id, 'storage_gb', 10),
(@starter_id, 'api_calls_per_month', 10000),
(@starter_id, 'ai_requests_per_month', 100),
(@starter_id, 'integrations', 2),

-- Professional limits
(@professional_id, 'users', 50),
(@professional_id, 'projects', 50),
(@professional_id, 'storage_gb', 100),
(@professional_id, 'api_calls_per_month', 100000),
(@professional_id, 'ai_requests_per_month', 1000),
(@professional_id, 'integrations', 10),

-- Enterprise limits
(@enterprise_id, 'users', -1),  -- Unlimited
(@enterprise_id, 'projects', -1),  -- Unlimited
(@enterprise_id, 'storage_gb', 1000),
(@enterprise_id, 'api_calls_per_month', -1),  -- Unlimited
(@enterprise_id, 'ai_requests_per_month', -1),  -- Unlimited
(@enterprise_id, 'integrations', -1);  -- Unlimited
