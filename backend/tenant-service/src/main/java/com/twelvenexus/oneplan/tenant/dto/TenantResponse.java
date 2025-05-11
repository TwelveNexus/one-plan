package com.twelvenexus.oneplan.tenant.dto;

import com.twelvenexus.oneplan.tenant.model.Tenant;
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
public class TenantResponse {
    private UUID id;
    private String name;
    private String domain;
    private String plan;
    private Tenant.TenantStatus status;
    private String settings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static TenantResponse fromEntity(Tenant tenant) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .domain(tenant.getDomain())
                .plan(tenant.getPlan())
                .status(tenant.getStatus())
                .settings(tenant.getSettings())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .build();
    }
}