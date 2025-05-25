package com.twelvenexus.oneplan.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
  private String accessToken;
  private String refreshToken;
  private final String tokenType = "Bearer";
  private Long expiresIn;
  private UserDto user;
}
