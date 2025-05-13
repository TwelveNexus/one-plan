package com.twelvenexus.oneplan.analytics.dto;

import java.util.Map;

import com.twelvenexus.oneplan.analytics.enums.ReportType;

import lombok.Data;

@Data
public class CreateReportDto {
    private String name;
    private ReportType type;
    private Map<String, String> parameters;
    private String schedule;
}
