package com.twelvenexus.oneplan.organization.service;

import com.twelvenexus.oneplan.organization.dto.OrganizationRequest;
import com.twelvenexus.oneplan.organization.dto.OrganizationResponse;

import java.util.List;
import java.util.UUID;

public interface OrganizationService {
    OrganizationResponse createOrganization(OrganizationRequest request);
    OrganizationResponse getOrganizationById(UUID id);
    List<OrganizationResponse> getOrganizationsByTenantId(UUID tenantId);
    OrganizationResponse updateOrganization(UUID id, OrganizationRequest request);
    void deleteOrganization(UUID id);
}