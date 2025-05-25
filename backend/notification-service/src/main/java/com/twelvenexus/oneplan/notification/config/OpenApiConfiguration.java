package com.twelvenexus.oneplan.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

  @Value("${server.port}")
  private String serverPort;

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("One Plan - Notification Service API")
                .version("1.0.0")
                .description("Notification Service for the One Plan project management platform")
                .contact(new Contact().name("One Plan Team").email("support@oneplan.com")))
        .servers(
            List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Local development server")));
  }
}
