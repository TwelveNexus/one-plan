package com.twelvenexus.oneplan.analytics.service;

import com.twelvenexus.oneplan.analytics.enums.ReportType;
import com.twelvenexus.oneplan.analytics.model.Report;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReportService {

  Report createReport(
      UUID tenantId,
      String name,
      ReportType type,
      Map<String, String> parameters,
      UUID createdBy,
      String schedule);

  Report updateReport(UUID reportId, String name, Map<String, String> parameters, String schedule);

  Report runReport(UUID reportId);

  List<Report> getUserReports(UUID tenantId, UUID userId);

  List<Report> getScheduledReports(UUID tenantId);

  void processScheduledReports();

  void deleteReport(UUID reportId);

  byte[] exportReport(UUID reportId, String format);
}
