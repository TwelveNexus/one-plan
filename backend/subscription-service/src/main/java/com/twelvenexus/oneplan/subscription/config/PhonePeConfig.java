package com.twelvenexus.oneplan.subscription.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "payment.phonepe")
public class PhonePeConfig {
  private String merchantId;
  private String saltKey;
  private String saltIndex;
  private String environment;
  private String apiUrl;
  private String sandboxUrl;
}
