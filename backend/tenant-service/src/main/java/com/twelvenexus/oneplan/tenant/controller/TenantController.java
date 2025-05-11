package com.twelvenexus.oneplan.tenant.controller;

import com.twelvenexus.oneplan.tenant.dto.TenantRequest;
import com.twelvenexus.oneplan.tenant.dto.TenantResponse;
import com.twelvenexus.oneplan.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenant Management", description = "APIs for managing tenants")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @Operation(summary = "Create a new tenant")
    public ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody TenantRequest request) {
        return new ResponseEntity<>(tenantService.createTenant(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by ID")
    public ResponseEntity<TenantResponse> getTenantById(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }

    @GetMapping("/domain/{domain}")
    @Operation(summary = "Get tenant by domain")
    public ResponseEntity<TenantResponse> getTenantByDomain(@PathVariable String domain) {
        return ResponseEntity.ok(tenantService.getTenantByDomain(domain));
    }

    @GetMapping
    @Operation(summary = "Get all tenants")
    public ResponseEntity<List<TenantResponse>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a tenant")
    public ResponseEntity<TenantResponse> updateTenant(@PathVariable UUID id, 
                                                      @Valid @RequestBody TenantRequest request) {
        return ResponseEntity.ok(tenantService.updateTenant(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a tenant")
    public ResponseEntity<Void> deleteTenant(@PathVariable UUID id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
}
