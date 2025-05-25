package com.twelvenexus.oneplan.analytics.service;

import com.twelvenexus.oneplan.analytics.model.Dashboard;
import com.twelvenexus.oneplan.analytics.model.DashboardWidget;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DashboardService {

  Dashboard createDashboard(
      UUID tenantId, String name, String description, UUID ownerId, boolean isPublic);

  Dashboard updateDashboard(UUID dashboardId, String name, String description, boolean isPublic);

  Dashboard getDashboard(UUID dashboardId, UUID tenantId);

  List<Dashboard> getUserDashboards(UUID tenantId, UUID userId);

  DashboardWidget addWidget(
      UUID dashboardId,
      String title,
      String type,
      Map<String, String> configuration,
      Integer position,
      Integer width,
      Integer height);

  DashboardWidget updateWidget(UUID widgetId, String title, Map<String, String> configuration);

  void removeWidget(UUID widgetId);

  void deleteDashboard(UUID dashboardId);

  Dashboard duplicateDashboard(UUID dashboardId, String newName, UUID ownerId);
}
