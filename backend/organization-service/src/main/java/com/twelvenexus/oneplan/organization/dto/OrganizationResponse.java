package com.twelvenexus.oneplan.organization.dto;

import com.twelvenexus.oneplan.organization.model.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponse {
    private UUID id;
    private UUID tenantId;
    private String name;
    private String displayName;
    private String description;
    private String logo;
    private String website;
    private String industry;
    private Organization.OrganizationSize size;
    private String settings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static OrganizationResponse fromEntity(Organization organization) {
        return OrganizationResponse.builder()
                .id(organization.getId())
                .tenantId(organization.getTenantId())
                .name(organization.getName())
                .displayName(organization.getDisplayName())
                .description(organization.getDescription())
                .logo(organization.getLogo())
                .website(organization.getWebsite())
                .industry(organization.getIndustry())
                .size(organization.getSize())
                .settings(organization.getSettings())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .build();
    }
}
