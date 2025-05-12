package com.twelvenexus.oneplan.organization.service.impl;

import com.twelvenexus.oneplan.organization.dto.OrganizationRequest;
import com.twelvenexus.oneplan.organization.dto.OrganizationResponse;
import com.twelvenexus.oneplan.organization.exception.ResourceAlreadyExistsException;
import com.twelvenexus.oneplan.organization.exception.ResourceNotFoundException;
import com.twelvenexus.oneplan.organization.model.Organization;
import com.twelvenexus.oneplan.organization.repository.OrganizationRepository;
import com.twelvenexus.oneplan.organization.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public OrganizationResponse createOrganization(OrganizationRequest request) {
        if (organizationRepository.existsByNameAndTenantId(request.getName(), request.getTenantId())) {
            throw new ResourceAlreadyExistsException("Organization with name " + request.getName() + 
                    " already exists for this tenant");
        }

        Organization organization = Organization.builder()
                .tenantId(request.getTenantId())
                .name(request.getName())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .logo(request.getLogo())
                .website(request.getWebsite())
                .industry(request.getIndustry())
                .size(request.getSize())
                .settings(request.getSettings())
                .build();

        Organization savedOrganization = organizationRepository.save(organization);
        return OrganizationResponse.fromEntity(savedOrganization);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationResponse getOrganizationById(UUID id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));
        
        return OrganizationResponse.fromEntity(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationResponse> getOrganizationsByTenantId(UUID tenantId) {
        return organizationRepository.findByTenantId(tenantId).stream()
                .map(OrganizationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrganizationResponse updateOrganization(UUID id, OrganizationRequest request) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));
        
        // Check if the name is already taken by another organization in the same tenant
        if (!organization.getName().equals(request.getName()) && 
                organizationRepository.existsByNameAndTenantId(request.getName(), request.getTenantId())) {
            throw new ResourceAlreadyExistsException("Organization with name " + request.getName() + 
                    " already exists for this tenant");
        }
        
        organization.setName(request.getName());
        organization.setDisplayName(request.getDisplayName());
        organization.setDescription(request.getDescription());
        organization.setLogo(request.getLogo());
        organization.setWebsite(request.getWebsite());
        organization.setIndustry(request.getIndustry());
        organization.setSize(request.getSize());
        organization.setSettings(request.getSettings());
        
        Organization updatedOrganization = organizationRepository.save(organization);
        return OrganizationResponse.fromEntity(updatedOrganization);
    }

    @Override
    @Transactional
    public void deleteOrganization(UUID id) {
        if (!organizationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Organization not found with id: " + id);
        }
        organizationRepository.deleteById(id);
    }
}
