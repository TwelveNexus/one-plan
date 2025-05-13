package com.twelvenexus.oneplan.analytics.controller;

import com.twelvenexus.oneplan.analytics.dto.ReportDto;
import com.twelvenexus.oneplan.analytics.dto.CreateReportDto;
import com.twelvenexus.oneplan.analytics.dto.UpdateReportDto;
import com.twelvenexus.oneplan.analytics.model.Report;
import com.twelvenexus.oneplan.analytics.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Analytics reports management")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "Create a report")
    public ResponseEntity<ReportDto> createReport(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateReportDto dto) {
        Report report = reportService.createReport(
            tenantId,
            dto.getName(),
            dto.getType(),
            dto.getParameters(),
            userId,
            dto.getSchedule()
        );

        return new ResponseEntity<>(toDto(report), HttpStatus.CREATED);
    }

    @PutMapping("/{reportId}")
    @Operation(summary = "Update a report")
    public ResponseEntity<ReportDto> updateReport(
            @PathVariable UUID reportId,
            @Valid @RequestBody UpdateReportDto dto) {
        Report report = reportService.updateReport(
            reportId,
            dto.getName(),
            dto.getParameters(),
            dto.getSchedule()
        );

        return ResponseEntity.ok(toDto(report));
    }

    @PostMapping("/{reportId}/run")
    @Operation(summary = "Run a report")
    public ResponseEntity<ReportDto> runReport(@PathVariable UUID reportId) {
        Report report = reportService.runReport(reportId);
        return ResponseEntity.ok(toDto(report));
    }

    @GetMapping
    @Operation(summary = "Get user reports")
    public ResponseEntity<List<ReportDto>> getUserReports(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestHeader("X-User-Id") UUID userId) {
        List<Report> reports = reportService.getUserReports(tenantId, userId);
        return ResponseEntity.ok(
            reports.stream().map(this::toDto).collect(Collectors.toList())
        );
    }

    @GetMapping("/scheduled")
    @Operation(summary = "Get scheduled reports")
    public ResponseEntity<List<ReportDto>> getScheduledReports(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        List<Report> reports = reportService.getScheduledReports(tenantId);
        return ResponseEntity.ok(
            reports.stream().map(this::toDto).collect(Collectors.toList())
        );
    }

    @GetMapping("/{reportId}/export")
    @Operation(summary = "Export a report")
    public ResponseEntity<byte[]> exportReport(
            @PathVariable UUID reportId,
            @RequestParam(defaultValue = "csv") String format) {
        byte[] content = reportService.exportReport(reportId, format);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getMediaType(format));
        headers.setContentDispositionFormData("attachment", "report." + format);

        return ResponseEntity.ok()
            .headers(headers)
            .body(content);
    }

    @DeleteMapping("/{reportId}")
    @Operation(summary = "Delete a report")
    public ResponseEntity<Void> deleteReport(@PathVariable UUID reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }

    private ReportDto toDto(Report report) {
        ReportDto dto = new ReportDto();
        dto.setId(report.getId());
        dto.setName(report.getName());
        dto.setType(report.getType());
        dto.setParameters(report.getParameters());
        dto.setSchedule(report.getSchedule());
        dto.setActive(report.isActive());
        dto.setLastRunAt(report.getLastRunAt());
        dto.setNextRunAt(report.getNextRunAt());
        dto.setLastRunResult(report.getLastRunResult());
        return dto;
    }

    private MediaType getMediaType(String format) {
        return switch (format.toLowerCase()) {
            case "csv" -> MediaType.TEXT_PLAIN;
            case "json" -> MediaType.APPLICATION_JSON;
            case "pdf" -> MediaType.APPLICATION_PDF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}
