package com.twelvenexus.oneplan.tenant.dto;

import com.twelvenexus.oneplan.tenant.model.Tenant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Domain is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Domain can only contain lowercase letters, numbers, and hyphens")
    private String domain;

    @NotBlank(message = "Plan is required")
    private String plan;

    private String settings;
}
