package com.twelvenexus.oneplan.requirement.controller;

import com.twelvenexus.oneplan.requirement.dto.CreateRequirementDto;
import com.twelvenexus.oneplan.requirement.dto.RequirementResponseDto;
import com.twelvenexus.oneplan.requirement.dto.UpdateRequirementDto;
import com.twelvenexus.oneplan.requirement.mapper.RequirementMapper;
import com.twelvenexus.oneplan.requirement.model.Requirement;
import com.twelvenexus.oneplan.requirement.service.RequirementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/requirements")
@RequiredArgsConstructor
@Tag(name = "Requirements", description = "Requirement management API")
public class RequirementController {
    
    private final RequirementService requirementService;
    private final RequirementMapper requirementMapper;
    
    @PostMapping
    @Operation(summary = "Create a new requirement")
    public ResponseEntity<RequirementResponseDto> createRequirement(@Valid @RequestBody CreateRequirementDto dto) {
        Requirement requirement = requirementMapper.toEntity(dto);
        Requirement created = requirementService.createRequirement(requirement);
        return ResponseEntity.status(HttpStatus.CREATED).body(requirementMapper.toDto(created));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get requirement by ID")
    public ResponseEntity<RequirementResponseDto> getRequirement(@PathVariable String id) {
        Requirement requirement = requirementService.getRequirement(id);
        return ResponseEntity.ok(requirementMapper.toDto(requirement));
    }
    
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all requirements for a project")
    public ResponseEntity<List<RequirementResponseDto>> getProjectRequirements(@PathVariable String projectId) {
        List<Requirement> requirements = requirementService.getRequirementsByProject(projectId);
        List<RequirementResponseDto> dtos = requirements.stream()
                .map(requirementMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a requirement")
    public ResponseEntity<RequirementResponseDto> updateRequirement(
            @PathVariable String id,
            @Valid @RequestBody UpdateRequirementDto dto) {
        Requirement requirement = requirementService.getRequirement(id);
        requirementMapper.updateEntity(requirement, dto);
        Requirement updated = requirementService.updateRequirement(id, requirement);
        return ResponseEntity.ok(requirementMapper.toDto(updated));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a requirement")
    public ResponseEntity<Void> deleteRequirement(@PathVariable String id) {
        requirementService.deleteRequirement(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/analyze")
    @Operation(summary = "Analyze requirement with AI")
    public ResponseEntity<RequirementResponseDto> analyzeRequirement(@PathVariable String id) {
        Requirement analyzed = requirementService.analyzeRequirement(id);
        return ResponseEntity.ok(requirementMapper.toDto(analyzed));
    }
    
    @GetMapping("/project/{projectId}/search")
    @Operation(summary = "Search requirements in a project")
    public ResponseEntity<List<RequirementResponseDto>> searchRequirements(
            @PathVariable String projectId,
            @RequestParam String q) {
        List<Requirement> requirements = requirementService.searchRequirements(projectId, q);
        List<RequirementResponseDto> dtos = requirements.stream()
                .map(requirementMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
