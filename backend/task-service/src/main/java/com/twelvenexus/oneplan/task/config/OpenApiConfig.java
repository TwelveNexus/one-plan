package com.twelvenexus.oneplan.task.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI taskServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Service API")
                        .description("Task management service for One Plan")
                        .version("1.0"));
    }
}
