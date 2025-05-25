package com.twelvenexus.oneplan.identity.dto;

import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private String avatar;
  private String status;
  private Set<String> roles;
}
