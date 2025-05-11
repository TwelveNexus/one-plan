CREATE TABLE users
(
    id            BINARY(16)   NOT NULL,
    first_name    VARCHAR(50)  NULL,
    last_name     VARCHAR(50)  NULL,
    email         VARCHAR(50)  NULL,
    password_hash VARCHAR(120) NULL,
    avatar        VARCHAR(255) NULL,
    status        VARCHAR(255) NULL,
    last_login    TIMESTAMP     NULL,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE user_preferences
(
    user_id               BINARY(16)   NOT NULL,
    theme                 VARCHAR(255) DEFAULT 'light',
    language              VARCHAR(255) DEFAULT 'en',
    notification_settings JSON         DEFAULT '{}',
    CONSTRAINT pk_user_preferences PRIMARY KEY (user_id)
);

CREATE TABLE user_roles
(
    user_id BINARY(16)   NOT NULL,
    `role`  VARCHAR(255) NULL
);

ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);

ALTER TABLE user_preferences
    ADD CONSTRAINT FK_USER_PREFERENCES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_on_user FOREIGN KEY (user_id) REFERENCES users (id);