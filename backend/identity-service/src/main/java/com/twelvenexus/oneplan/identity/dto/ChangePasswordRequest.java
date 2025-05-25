package com.twelvenexus.oneplan.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
  @NotBlank private String currentPassword;

  @NotBlank
  @Size(min = 8, max = 40)
  private String newPassword;
}
