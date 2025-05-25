package com.twelvenexus.oneplan.identity.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvironmentVariableValidator implements InitializingBean {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Value("${spring.datasource.username}")
  private String dbUsername;

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Override
  public void afterPropertiesSet() {
    validateEnvironmentVariable("DB_URL", dbUrl);
    validateEnvironmentVariable("DB_USERNAME", dbUsername);
    validateEnvironmentVariable("JWT_SECRET", jwtSecret);

    // Validate JWT secret length for security
    if (jwtSecret.length() < 32) {
      throw new IllegalStateException(
          "JWT_SECRET must be at least 32 characters long for adequate security");
    }
  }

  private void validateEnvironmentVariable(String name, String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalStateException("Environment variable " + name + " is not set");
    }
  }
}
