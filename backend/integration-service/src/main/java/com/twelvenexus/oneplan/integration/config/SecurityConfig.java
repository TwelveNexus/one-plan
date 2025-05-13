package com.twelvenexus.oneplan.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // Allow webhook endpoints without authentication
                        .requestMatchers("/api/v1/webhooks/**").permitAll()
                        // Allow OAuth callback
                        .requestMatchers("/api/v1/integrations/oauth/callback").permitAll()
                        // Allow health and docs
                        .requestMatchers("/api/v1/health", "/api/v1/swagger-ui/**", "/api/v1/api-docs/**").permitAll()
                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .oauth2Client();
        
        return http.build();
    }
}
