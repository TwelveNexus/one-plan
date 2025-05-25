package com.twelvenexus.oneplan.subscription.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "payment.razorpay")
public class RazorpayConfig {
  private String keyId;
  private String keySecret;
  private String webhookSecret;
  private String apiUrl;
}
