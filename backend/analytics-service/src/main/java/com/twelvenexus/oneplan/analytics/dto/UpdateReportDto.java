package com.twelvenexus.oneplan.analytics.dto;

import java.util.Map;

import lombok.Data;

@Data
public class UpdateReportDto {
    private String name;
    private Map<String, String> parameters;
    private String schedule;
}

