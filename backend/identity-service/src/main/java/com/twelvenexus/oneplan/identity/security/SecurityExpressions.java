package com.twelvenexus.oneplan.identity.security;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component("securityExpressions")
public class SecurityExpressions {

  public boolean isCurrentUser(UUID userId) {
    return SecurityUtils.isCurrentUser(userId);
  }
}
