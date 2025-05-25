package com.twelvenexus.oneplan.identity.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Data
public class JwtConfig {
  private String secret = "PLACEHOLDER_SECRET_KEY";
  private long tokenValidityInSeconds = 86400; // 24 hours
  private long refreshTokenValidityInSeconds = 604800; // 7 days
}
