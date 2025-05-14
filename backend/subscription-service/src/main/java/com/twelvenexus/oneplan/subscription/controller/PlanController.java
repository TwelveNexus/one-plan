package com.twelvenexus.oneplan.subscription.controller;

import com.twelvenexus.oneplan.subscription.dto.*;
import com.twelvenexus.oneplan.subscription.model.Plan;
import com.twelvenexus.oneplan.subscription.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
@Tag(name = "Plans", description = "Subscription plan management")
public class PlanController {

    private final PlanService planService;

    @PostMapping
    @Operation(summary = "Create a new plan")
    public ResponseEntity<PlanDto> createPlan(@Valid @RequestBody CreatePlanDto dto) {
        Plan plan = planService.createPlan(
            dto.getCode(),
            dto.getName(),
            dto.getDescription(),
            dto.getType(),
            dto.getBasePrice(),
            dto.getBillingCyclePrices(),
            dto.getFeatures(),
            dto.getLimits()
        );

        return new ResponseEntity<>(toDto(plan), HttpStatus.CREATED);
    }

    @PutMapping("/{planId}")
    @Operation(summary = "Update a plan")
    public ResponseEntity<PlanDto> updatePlan(
            @PathVariable UUID planId,
            @Valid @RequestBody UpdatePlanDto dto) {
        Plan plan = planService.updatePlan(
            planId,
            dto.getName(),
            dto.getDescription(),
            dto.getBillingCyclePrices(),
            dto.getFeatures(),
            dto.getLimits()
        );

        return ResponseEntity.ok(toDto(plan));
    }

    @GetMapping
    @Operation(summary = "Get all active plans")
    public ResponseEntity<List<PlanDto>> getAllPlans() {
        List<Plan> plans = planService.getAllActivePlans();
        return ResponseEntity.ok(
            plans.stream().map(this::toDto).collect(Collectors.toList())
        );
    }

    @GetMapping("/popular")
    @Operation(summary = "Get popular plans")
    public ResponseEntity<List<PlanDto>> getPopularPlans() {
        List<Plan> plans = planService.getPopularPlans();
        return ResponseEntity.ok(
            plans.stream().map(this::toDto).collect(Collectors.toList())
        );
    }

    @GetMapping("/{planId}")
    @Operation(summary = "Get plan by ID")
    public ResponseEntity<PlanDto> getPlan(@PathVariable UUID planId) {
        Plan plan = planService.getPlan(planId);
        return ResponseEntity.ok(toDto(plan));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get plan by code")
    public ResponseEntity<PlanDto> getPlanByCode(@PathVariable String code) {
        Plan plan = planService.getPlanByCode(code);
        return ResponseEntity.ok(toDto(plan));
    }

    @PutMapping("/{planId}/activate")
    @Operation(summary = "Activate a plan")
    public ResponseEntity<Void> activatePlan(@PathVariable UUID planId) {
        planService.activatePlan(planId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{planId}/deactivate")
    @Operation(summary = "Deactivate a plan")
    public ResponseEntity<Void> deactivatePlan(@PathVariable UUID planId) {
        planService.deactivatePlan(planId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{planId}/popular")
    @Operation(summary = "Mark plan as popular")
    public ResponseEntity<Void> markAsPopular(
            @PathVariable UUID planId,
            @RequestParam boolean popular) {
        planService.markAsPopular(planId, popular);
        return ResponseEntity.ok().build();
    }

    private PlanDto toDto(Plan plan) {
        PlanDto dto = new PlanDto();
        dto.setId(plan.getId());
        dto.setCode(plan.getCode());
        dto.setName(plan.getName());
        dto.setDescription(plan.getDescription());
        dto.setType(plan.getType());
        dto.setBasePrice(plan.getBasePrice());
        dto.setCurrency(plan.getCurrency());
        dto.setBillingCyclePrices(plan.getBillingCyclePrices());
        dto.setFeatures(plan.getFeatures());
        dto.setLimits(plan.getLimits());
        dto.setTrialDays(plan.getTrialDays());
        dto.setActive(plan.isActive());
        dto.setPopular(plan.isPopular());
        return dto;
    }
}
