package com.twelvenexus.oneplan.analytics.controller;

import com.twelvenexus.oneplan.analytics.dto.*;
import com.twelvenexus.oneplan.analytics.model.Dashboard;
import com.twelvenexus.oneplan.analytics.model.DashboardWidget;
import com.twelvenexus.oneplan.analytics.service.DashboardService;
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
@RequestMapping("/api/v1/dashboards")
@RequiredArgsConstructor
@Tag(name = "Dashboards", description = "Analytics dashboards management")
public class DashboardController {

    private final DashboardService dashboardService;

    @PostMapping
    @Operation(summary = "Create a dashboard")
    public ResponseEntity<DashboardDto> createDashboard(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateDashboardDto dto) {
        Dashboard dashboard = dashboardService.createDashboard(
            tenantId,
            dto.getName(),
            dto.getDescription(),
            userId,
            dto.isPublic()
        );

        return new ResponseEntity<>(toDto(dashboard), HttpStatus.CREATED);
    }

    @PutMapping("/{dashboardId}")
    @Operation(summary = "Update a dashboard")
    public ResponseEntity<DashboardDto> updateDashboard(
            @PathVariable UUID dashboardId,
            @Valid @RequestBody CreateDashboardDto dto) {
        Dashboard dashboard = dashboardService.updateDashboard(
            dashboardId,
            dto.getName(),
            dto.getDescription(),
            dto.isPublic()
        );

        return ResponseEntity.ok(toDto(dashboard));
    }

    @GetMapping("/{dashboardId}")
    @Operation(summary = "Get a dashboard")
    public ResponseEntity<DashboardDto> getDashboard(
            @PathVariable UUID dashboardId,
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        Dashboard dashboard = dashboardService.getDashboard(dashboardId, tenantId);
        return ResponseEntity.ok(toDto(dashboard));
    }

    @GetMapping
    @Operation(summary = "Get user dashboards")
    public ResponseEntity<List<DashboardDto>> getUserDashboards(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestHeader("X-User-Id") UUID userId) {
        List<Dashboard> dashboards = dashboardService.getUserDashboards(tenantId, userId);
        return ResponseEntity.ok(
            dashboards.stream().map(this::toDto).collect(Collectors.toList())
        );
    }

    @PostMapping("/{dashboardId}/widgets")
    @Operation(summary = "Add a widget to dashboard")
    public ResponseEntity<DashboardWidgetDto> addWidget(
            @PathVariable UUID dashboardId,
            @Valid @RequestBody CreateWidgetDto dto) {
        DashboardWidget widget = dashboardService.addWidget(
            dashboardId,
            dto.getTitle(),
            dto.getType(),
            dto.getConfiguration(),
            dto.getPosition(),
            dto.getWidth(),
            dto.getHeight()
        );

        return new ResponseEntity<>(toWidgetDto(widget), HttpStatus.CREATED);
    }

    @PutMapping("/widgets/{widgetId}")
    @Operation(summary = "Update a widget")
    public ResponseEntity<DashboardWidgetDto> updateWidget(
            @PathVariable UUID widgetId,
            @Valid @RequestBody CreateWidgetDto dto) {
        DashboardWidget widget = dashboardService.updateWidget(
            widgetId,
            dto.getTitle(),
            dto.getConfiguration()
        );

        return ResponseEntity.ok(toWidgetDto(widget));
    }

    @DeleteMapping("/widgets/{widgetId}")
    @Operation(summary = "Remove a widget")
    public ResponseEntity<Void> removeWidget(@PathVariable UUID widgetId) {
        dashboardService.removeWidget(widgetId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{dashboardId}/duplicate")
    @Operation(summary = "Duplicate a dashboard")
    public ResponseEntity<DashboardDto> duplicateDashboard(
            @PathVariable UUID dashboardId,
            @RequestParam String newName,
            @RequestHeader("X-User-Id") UUID userId) {
        Dashboard dashboard = dashboardService.duplicateDashboard(dashboardId, newName, userId);
        return ResponseEntity.ok(toDto(dashboard));
    }

    @DeleteMapping("/{dashboardId}")
    @Operation(summary = "Delete a dashboard")
    public ResponseEntity<Void> deleteDashboard(@PathVariable UUID dashboardId) {
        dashboardService.deleteDashboard(dashboardId);
        return ResponseEntity.noContent().build();
    }

    private DashboardDto toDto(Dashboard dashboard) {
        DashboardDto dto = new DashboardDto();
        dto.setId(dashboard.getId());
        dto.setName(dashboard.getName());
        dto.setDescription(dashboard.getDescription());
        dto.setPublic(dashboard.isPublic());
        dto.setDefault(dashboard.isDefault());

        if (dashboard.getWidgets() != null) {
            dto.setWidgets(dashboard.getWidgets().stream()
                .map(this::toWidgetDto)
                .collect(Collectors.toList()));
        }

        return dto;
    }

    private DashboardWidgetDto toWidgetDto(DashboardWidget widget) {
        DashboardWidgetDto dto = new DashboardWidgetDto();
        dto.setId(widget.getId());
        dto.setTitle(widget.getTitle());
        dto.setType(widget.getType());
        dto.setPosition(widget.getPosition());
        dto.setWidth(widget.getWidth());
        dto.setHeight(widget.getHeight());
        dto.setConfiguration(widget.getConfiguration());
        return dto;
    }
}
