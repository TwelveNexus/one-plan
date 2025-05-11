-- V1__Initial_Schema.sql

-- Users Table
CREATE TABLE users (
                       id VARCHAR(36) PRIMARY KEY,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       email VARCHAR(50) NOT NULL UNIQUE,
                       password_hash VARCHAR(120) NOT NULL,
                       avatar VARCHAR(255),
                       status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                       last_login TIMESTAMP,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- User Roles Table
CREATE TABLE user_roles (
                            user_id VARCHAR(36) NOT NULL,
                            role VARCHAR(50) NOT NULL,
                            PRIMARY KEY (user_id, role),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- User Preferences Table
CREATE TABLE user_preferences (
                                  user_id VARCHAR(36) PRIMARY KEY,
                                  theme VARCHAR(50) DEFAULT 'light',
                                  language VARCHAR(10) DEFAULT 'en',
                                  notification_settings TEXT DEFAULT '{}',
                                  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users(email);