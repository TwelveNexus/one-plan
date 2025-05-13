package com.twelvenexus.oneplan.storyboard.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI storyboardServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Storyboard Service API")
                        .description("Visual storyboard management service for One Plan platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Twelve Nexus Team")
                                .email("support@twelvenexus.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8087").description("Local Development")
                ));
    }
}
