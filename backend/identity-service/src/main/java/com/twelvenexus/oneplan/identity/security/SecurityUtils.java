package com.twelvenexus.oneplan.identity.security;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

  public static String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }
    return authentication.getName();
  }

  public static boolean isCurrentUser(UUID userId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof CustomUserDetails) {
      return ((CustomUserDetails) principal).getId().equals(userId);
    }

    return false;
  }
}
