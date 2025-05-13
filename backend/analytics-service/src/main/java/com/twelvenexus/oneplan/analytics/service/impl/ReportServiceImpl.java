package com.twelvenexus.oneplan.analytics.service.impl;

import com.twelvenexus.oneplan.analytics.model.Report;
import com.twelvenexus.oneplan.analytics.model.Metric;
import com.twelvenexus.oneplan.analytics.model.AggregatedMetric;
import com.twelvenexus.oneplan.analytics.enums.ReportType;
import com.twelvenexus.oneplan.analytics.enums.MetricType;
import com.twelvenexus.oneplan.analytics.enums.AggregationPeriod;
import com.twelvenexus.oneplan.analytics.repository.ReportRepository;
import com.twelvenexus.oneplan.analytics.service.ReportService;
import com.twelvenexus.oneplan.analytics.service.MetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final MetricService metricService;

    @Override
    public Report createReport(UUID tenantId, String name, ReportType type,
                              Map<String, String> parameters, UUID createdBy,
                              String schedule) {
        Report report = new Report();
        report.setTenantId(tenantId);
        report.setName(name);
        report.setType(type);
        report.setParameters(parameters);
        report.setCreatedBy(createdBy);
        report.setSchedule(schedule);
        report.setActive(true);

        if (schedule != null) {
            report.setNextRunAt(calculateNextRunTime(schedule));
        }

        log.info("Creating report: {} of type {}", name, type);
        return reportRepository.save(report);
    }

    @Override
    public Report updateReport(UUID reportId, String name, Map<String, String> parameters,
                              String schedule) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        report.setName(name);
        report.setParameters(parameters);
        report.setSchedule(schedule);

        if (schedule != null) {
            report.setNextRunAt(calculateNextRunTime(schedule));
        }

        return reportRepository.save(report);
    }

    @Override
    public Report runReport(UUID reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        log.info("Running report: {}", report.getName());

        try {
            String result = generateReport(report);
            report.setLastRunResult(result);
            report.setLastRunAt(LocalDateTime.now());

            if (report.getSchedule() != null) {
                report.setNextRunAt(calculateNextRunTime(report.getSchedule()));
            }

            return reportRepository.save(report);
        } catch (Exception e) {
            log.error("Error running report {}: {}", report.getName(), e.getMessage());
            report.setLastRunResult("Error: " + e.getMessage());
            report.setLastRunAt(LocalDateTime.now());
            return reportRepository.save(report);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> getUserReports(UUID tenantId, UUID userId) {
        return reportRepository.findByTenantIdAndCreatedBy(tenantId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> getScheduledReports(UUID tenantId) {
        return reportRepository.findByTenantIdAndActiveTrue(tenantId);
    }

    @Override
    public void processScheduledReports() {
        LocalDateTime now = LocalDateTime.now();
        List<Report> scheduledReports = reportRepository.findScheduledReportsToRun(now);

        log.info("Processing {} scheduled reports", scheduledReports.size());

        for (Report report : scheduledReports) {
            try {
                runReport(report.getId());
            } catch (Exception e) {
                log.error("Failed to run scheduled report {}: {}", report.getName(), e.getMessage());
            }
        }
    }

    @Override
    public void deleteReport(UUID reportId) {
        reportRepository.deleteById(reportId);
    }

    @Override
    public byte[] exportReport(UUID reportId, String format) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        String content = report.getLastRunResult();
        if (content == null) {
            content = generateReport(report);
        }

        return switch (format.toLowerCase()) {
            case "csv" -> convertToCSV(content);
            case "json" -> content.getBytes();
            case "pdf" -> convertToPDF(content);
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }

    private String generateReport(Report report) {
        return switch (report.getType()) {
            case PROJECT_SUMMARY -> generateProjectSummaryReport(report);
            case TEAM_PERFORMANCE -> generateTeamPerformanceReport(report);
            case USER_ACTIVITY -> generateUserActivityReport(report);
            case TASK_ANALYTICS -> generateTaskAnalyticsReport(report);
            case TIMELINE_ACCURACY -> generateTimelineAccuracyReport(report);
            case RESOURCE_UTILIZATION -> generateResourceUtilizationReport(report);
            case SYSTEM_HEALTH -> generateSystemHealthReport(report);
            case BUSINESS_METRICS -> generateBusinessMetricsReport(report);
            case CUSTOM -> generateCustomReport(report);
        };
    }

    private String generateProjectSummaryReport(Report report) {
        Map<String, String> params = report.getParameters();
        UUID projectId = UUID.fromString(params.get("projectId"));
        LocalDateTime startDate = LocalDateTime.parse(params.get("startDate"));
        LocalDateTime endDate = LocalDateTime.parse(params.get("endDate"));

        Map<String, Object> reportData = new HashMap<>();

        // Get project metrics
        List<Metric> projectMetrics = metricService.getMetrics(
            report.getTenantId(), projectId, MetricType.PROJECT_HEALTH_SCORE, startDate, endDate);

        // Get aggregated task metrics
        List<AggregatedMetric> taskMetrics = metricService.getAggregatedMetrics(
            report.getTenantId(), projectId, MetricType.TASK_COMPLETED,
            AggregationPeriod.DAY, startDate, endDate);

        // Build report data
        reportData.put("projectId", projectId);
        reportData.put("period", Map.of("start", startDate, "end", endDate));
        reportData.put("healthScore", calculateAverageMetricValue(projectMetrics));
        reportData.put("tasksCompleted", taskMetrics.stream()
            .mapToDouble(AggregatedMetric::getSumValue).sum());
        reportData.put("dailyTaskCompletion", taskMetrics.stream()
            .map(m -> Map.of(
                "date", m.getPeriodStart(),
                "count", m.getSumValue()
            ))
            .collect(Collectors.toList()));

        return convertToJson(reportData);
    }

    private String generateTeamPerformanceReport(Report report) {
        Map<String, String> params = report.getParameters();
        UUID teamId = UUID.fromString(params.get("teamId"));
        LocalDateTime startDate = LocalDateTime.parse(params.get("startDate"));
        LocalDateTime endDate = LocalDateTime.parse(params.get("endDate"));

        Map<String, Object> reportData = new HashMap<>();

        // Get team velocity
        List<AggregatedMetric> velocityMetrics = metricService.getAggregatedMetrics(
            report.getTenantId(), teamId, MetricType.TEAM_VELOCITY,
            AggregationPeriod.WEEK, startDate, endDate);

        // Get team efficiency
        List<AggregatedMetric> efficiencyMetrics = metricService.getAggregatedMetrics(
            report.getTenantId(), teamId, MetricType.TEAM_EFFICIENCY,
            AggregationPeriod.WEEK, startDate, endDate);

        reportData.put("teamId", teamId);
        reportData.put("period", Map.of("start", startDate, "end", endDate));
        reportData.put("averageVelocity", calculateAverageAggregatedValue(velocityMetrics));
        reportData.put("averageEfficiency", calculateAverageAggregatedValue(efficiencyMetrics));
        reportData.put("weeklyMetrics", buildWeeklyMetrics(velocityMetrics, efficiencyMetrics));

        return convertToJson(reportData);
    }

    private LocalDateTime calculateNextRunTime(String cronExpression) {
        // TODO: Simplified - in a real implementation, use a proper cron library
        // For now, just schedule for next day at midnight
        return LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0);
    }

    private double calculateAverageMetricValue(List<Metric> metrics) {
        return metrics.stream()
            .mapToDouble(Metric::getValue)
            .average()
            .orElse(0.0);
    }

    private double calculateAverageAggregatedValue(List<AggregatedMetric> metrics) {
        return metrics.stream()
            .mapToDouble(AggregatedMetric::getAvgValue)
            .average()
            .orElse(0.0);
    }

    private List<Map<String, Object>> buildWeeklyMetrics(List<AggregatedMetric> velocity,
                                                        List<AggregatedMetric> efficiency) {
        Map<LocalDateTime, Map<String, Object>> weeklyData = new HashMap<>();

        velocity.forEach(m -> {
            weeklyData.computeIfAbsent(m.getPeriodStart(), k -> new HashMap<>())
                .put("velocity", m.getAvgValue());
        });

        efficiency.forEach(m -> {
            weeklyData.computeIfAbsent(m.getPeriodStart(), k -> new HashMap<>())
                .put("efficiency", m.getAvgValue());
        });

        return weeklyData.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                Map<String, Object> week = new HashMap<>(entry.getValue());
                week.put("weekStart", entry.getKey());
                return week;
            })
            .collect(Collectors.toList());
    }

    private String convertToJson(Object data) {
        // TODO: In real implementation, use Jackson or Gson
        return data.toString();
    }

    private byte[] convertToCSV(String jsonContent) {
        // TODO: Implement JSON to CSV conversion
        return jsonContent.getBytes();
    }

    private byte[] convertToPDF(String content) {
        // TODO: Implement PDF generation (e.g., using iText or Apache PDFBox)
        return content.getBytes();
    }

    private String generateUserActivityReport(Report report) {
        // TODO: Implementation for user activity report
        return "{}";
    }

    private String generateTaskAnalyticsReport(Report report) {
        // TODO: Implementation for task analytics report
        return "{}";
    }

    private String generateTimelineAccuracyReport(Report report) {
        // TODO: Implementation for timeline accuracy report
        return "{}";
    }

    private String generateResourceUtilizationReport(Report report) {
        // TODO: Implementation for resource utilization report
        return "{}";
    }

    private String generateSystemHealthReport(Report report) {
        // TODO: Implementation for system health report
        return "{}";
    }

    private String generateBusinessMetricsReport(Report report) {
        //TODO: Implementation for business metrics report
        return "{}";
    }

    private String generateCustomReport(Report report) {
        //TODO: Implementation for custom reports
        return "{}";
    }
}
