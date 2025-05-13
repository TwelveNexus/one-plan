package com.twelvenexus.oneplan.analytics.config;

import com.twelvenexus.oneplan.analytics.service.AnalyticsEventService;
import com.twelvenexus.oneplan.analytics.service.MetricService;
import com.twelvenexus.oneplan.analytics.service.ReportService;
import com.twelvenexus.oneplan.analytics.enums.AggregationPeriod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfiguration {

    private final AnalyticsEventService eventService;
    private final MetricService metricService;
    private final ReportService reportService;

    @Value("${analytics.retention.raw-data-days}")
    private int rawDataRetentionDays;

    @Value("${analytics.retention.aggregated-data-days}")
    private int aggregatedDataRetentionDays;

    @Scheduled(fixedDelayString = "${analytics.aggregation.interval}")
    public void processEvents() {
        log.debug("Processing analytics events");
        eventService.processEvents();
    }

    @Scheduled(cron = "0 0 * * * ?") // Every hour
    public void aggregateHourlyMetrics() {
        log.info("Aggregating hourly metrics");
        metricService.aggregateMetrics(AggregationPeriod.HOUR);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Daily at midnight
    public void aggregateDailyMetrics() {
        log.info("Aggregating daily metrics");
        metricService.aggregateMetrics(AggregationPeriod.DAY);
    }

    @Scheduled(cron = "0 0 0 * * SUN") // Weekly on Sunday
    public void aggregateWeeklyMetrics() {
        log.info("Aggregating weekly metrics");
        metricService.aggregateMetrics(AggregationPeriod.WEEK);
    }

    @Scheduled(cron = "0 0 0 1 * ?") // Monthly on the 1st
    public void aggregateMonthlyMetrics() {
        log.info("Aggregating monthly metrics");
        metricService.aggregateMetrics(AggregationPeriod.MONTH);
    }

    @Scheduled(cron = "0 0 */4 * * ?") // Every 4 hours
    public void processScheduledReports() {
        log.info("Processing scheduled reports");
        reportService.processScheduledReports();
    }

    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void cleanupOldData() {
        log.info("Cleaning up old analytics data");
        eventService.cleanupOldEvents(rawDataRetentionDays);
        metricService.cleanupOldMetrics(aggregatedDataRetentionDays);
    }
}
