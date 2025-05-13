package com.twelvenexus.oneplan.analytics.service.impl;

import com.twelvenexus.oneplan.analytics.model.Dashboard;
import com.twelvenexus.oneplan.analytics.model.DashboardWidget;
import com.twelvenexus.oneplan.analytics.repository.DashboardRepository;
import com.twelvenexus.oneplan.analytics.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;

    @Override
    public Dashboard createDashboard(UUID tenantId, String name, String description,
                                    UUID ownerId, boolean isPublic) {
        Dashboard dashboard = new Dashboard();
        dashboard.setTenantId(tenantId);
        dashboard.setName(name);
        dashboard.setDescription(description);
        dashboard.setOwnerId(ownerId);
        dashboard.setPublic(isPublic);
        dashboard.setDefault(false);
        dashboard.setWidgets(new ArrayList<>());

        log.info("Creating dashboard: {} for user {}", name, ownerId);
        return dashboardRepository.save(dashboard);
    }

    @Override
    public Dashboard updateDashboard(UUID dashboardId, String name, String description,
                                    boolean isPublic) {
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
            .orElseThrow(() -> new IllegalArgumentException("Dashboard not found"));

        dashboard.setName(name);
        dashboard.setDescription(description);
        dashboard.setPublic(isPublic);

        return dashboardRepository.save(dashboard);
    }

    @Override
    @Transactional(readOnly = true)
    public Dashboard getDashboard(UUID dashboardId, UUID tenantId) {
        return dashboardRepository.findByIdWithWidgets(dashboardId, tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Dashboard not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Dashboard> getUserDashboards(UUID tenantId, UUID userId) {
        return dashboardRepository.findAccessibleDashboards(tenantId, userId);
    }

    @Override
    public DashboardWidget addWidget(UUID dashboardId, String title, String type,
                                    Map<String, String> configuration, Integer position,
                                    Integer width, Integer height) {
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
            .orElseThrow(() -> new IllegalArgumentException("Dashboard not found"));

        DashboardWidget widget = new DashboardWidget();
        widget.setDashboard(dashboard);
        widget.setTitle(title);
        widget.setType(type);
        widget.setConfiguration(configuration);
        widget.setPosition(position != null ? position : dashboard.getWidgets().size());
        widget.setWidth(width != null ? width : 6);
        widget.setHeight(height != null ? height : 4);

        dashboard.getWidgets().add(widget);
        dashboardRepository.save(dashboard);

        log.info("Added widget '{}' to dashboard {}", title, dashboardId);
        return widget;
    }

    @Override
    public DashboardWidget updateWidget(UUID widgetId, String title,
                                       Map<String, String> configuration) {
        Dashboard dashboard = dashboardRepository.findAll().stream()
            .filter(d -> d.getWidgets().stream()
                .anyMatch(w -> w.getId().equals(widgetId)))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Widget not found"));

        DashboardWidget widget = dashboard.getWidgets().stream()
            .filter(w -> w.getId().equals(widgetId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Widget not found"));

        widget.setTitle(title);
        widget.setConfiguration(configuration);

        dashboardRepository.save(dashboard);
        return widget;
    }

    @Override
    public void removeWidget(UUID widgetId) {
        Dashboard dashboard = dashboardRepository.findAll().stream()
            .filter(d -> d.getWidgets().stream()
                .anyMatch(w -> w.getId().equals(widgetId)))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Widget not found"));

        dashboard.getWidgets().removeIf(w -> w.getId().equals(widgetId));

        // Reorder remaining widgets
        for (int i = 0; i < dashboard.getWidgets().size(); i++) {
            dashboard.getWidgets().get(i).setPosition(i);
        }

        dashboardRepository.save(dashboard);
        log.info("Removed widget {} from dashboard", widgetId);
    }

    @Override
    public void deleteDashboard(UUID dashboardId) {
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
            .orElseThrow(() -> new IllegalArgumentException("Dashboard not found"));

        if (dashboard.isDefault()) {
            throw new IllegalStateException("Cannot delete default dashboard");
        }

        dashboardRepository.deleteById(dashboardId);
        log.info("Deleted dashboard {}", dashboardId);
    }

    @Override
    public Dashboard duplicateDashboard(UUID dashboardId, String newName, UUID ownerId) {
        Dashboard original = getDashboard(dashboardId, null);

        Dashboard duplicate = new Dashboard();
        duplicate.setTenantId(original.getTenantId());
        duplicate.setName(newName);
        duplicate.setDescription(original.getDescription());
        duplicate.setOwnerId(ownerId);
        duplicate.setPublic(false);
        duplicate.setDefault(false);
        duplicate.setWidgets(new ArrayList<>());

        Dashboard savedDuplicate = dashboardRepository.save(duplicate);

        // Duplicate widgets
        for (DashboardWidget originalWidget : original.getWidgets()) {
            DashboardWidget duplicateWidget = new DashboardWidget();
            duplicateWidget.setDashboard(savedDuplicate);
            duplicateWidget.setTitle(originalWidget.getTitle());
            duplicateWidget.setType(originalWidget.getType());
            duplicateWidget.setPosition(originalWidget.getPosition());
            duplicateWidget.setWidth(originalWidget.getWidth());
            duplicateWidget.setHeight(originalWidget.getHeight());
            duplicateWidget.setConfiguration(new HashMap<>(originalWidget.getConfiguration()));

            savedDuplicate.getWidgets().add(duplicateWidget);
        }

        return dashboardRepository.save(savedDuplicate);
    }
}
