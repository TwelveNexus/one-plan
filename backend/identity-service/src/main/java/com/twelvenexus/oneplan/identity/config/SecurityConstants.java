package com.twelvenexus.oneplan.identity.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityConstants {
  public static final String[] PUBLIC_ROUTES = {
    "/",
    "/error",
    "/auth/**",
    "/actuator/**",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/api-docs.yaml",
    "/api-docs/**",
  };
}
