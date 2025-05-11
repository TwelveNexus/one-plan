package com.twelvenexus.oneplan.tenant.service.impl;

import com.twelvenexus.oneplan.tenant.dto.TenantRequest;
import com.twelvenexus.oneplan.tenant.dto.TenantResponse;
import com.twelvenexus.oneplan.tenant.exception.ResourceNotFoundException;
import com.twelvenexus.oneplan.tenant.model.Tenant;
import com.twelvenexus.oneplan.tenant.repository.TenantRepository;
import com.twelvenexus.oneplan.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public TenantResponse createTenant(TenantRequest request) {
        if (tenantRepository.existsByDomain(request.getDomain())) {
            throw new IllegalArgumentException("Tenant with domain " + request.getDomain() + " already exists");
        }

        Tenant tenant = Tenant.builder()
                .name(request.getName())
                .domain(request.getDomain())
                .plan(request.getPlan())
                .status(Tenant.TenantStatus.ACTIVE)
                .settings(request.getSettings())
                .build();

        Tenant savedTenant = tenantRepository.save(tenant);
        return TenantResponse.fromEntity(savedTenant);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponse getTenantById(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
        
        return TenantResponse.fromEntity(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponse getTenantByDomain(String domain) {
        Tenant tenant = tenantRepository.findByDomain(domain)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with domain: " + domain));
        
        return TenantResponse.fromEntity(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantResponse> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(TenantResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TenantResponse updateTenant(UUID id, TenantRequest request) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
        
        // Check if the domain is already taken by another tenant
        if (!tenant.getDomain().equals(request.getDomain()) && 
                tenantRepository.existsByDomain(request.getDomain())) {
            throw new IllegalArgumentException("Domain " + request.getDomain() + " is already in use");
        }
        
        tenant.setName(request.getName());
        tenant.setDomain(request.getDomain());
        tenant.setPlan(request.getPlan());
        tenant.setSettings(request.getSettings());
        
        Tenant updatedTenant = tenantRepository.save(tenant);
        return TenantResponse.fromEntity(updatedTenant);
    }

    @Override
    @Transactional
    public void deleteTenant(UUID id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tenant not found with id: " + id);
        }
        tenantRepository.deleteById(id);
    }
}
