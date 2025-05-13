package com.twelvenexus.oneplan.integration.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuthCallbackDto {
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotBlank(message = "State is required")
    private String state;
}
