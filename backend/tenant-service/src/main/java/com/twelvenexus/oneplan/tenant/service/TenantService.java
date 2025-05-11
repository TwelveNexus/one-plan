package com.twelvenexus.oneplan.tenant.service;

import com.twelvenexus.oneplan.tenant.dto.TenantRequest;
import com.twelvenexus.oneplan.tenant.dto.TenantResponse;

import java.util.List;
import java.util.UUID;

public interface TenantService {
    TenantResponse createTenant(TenantRequest request);
    TenantResponse getTenantById(UUID id);
    TenantResponse getTenantByDomain(String domain);
    List<TenantResponse> getAllTenants();
    TenantResponse updateTenant(UUID id, TenantRequest request);
    void deleteTenant(UUID id);
}
