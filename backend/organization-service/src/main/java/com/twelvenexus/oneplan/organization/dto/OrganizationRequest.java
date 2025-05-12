package com.twelvenexus.oneplan.organization.dto;

import com.twelvenexus.oneplan.organization.model.Organization;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationRequest {
    @NotNull(message = "Tenant ID is required")
    private UUID tenantId;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String displayName;
    private String description;
    private String logo;
    private String website;
    private String industry;
    private Organization.OrganizationSize size;
    private String settings;
}
