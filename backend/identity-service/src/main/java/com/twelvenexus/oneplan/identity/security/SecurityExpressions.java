package com.twelvenexus.oneplan.identity.security;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("securityExpressions")
public class SecurityExpressions {

    public boolean isCurrentUser(UUID userId) {
        return SecurityUtils.isCurrentUser(userId);
    }
}
