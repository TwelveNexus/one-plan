package com.twelvenexus.oneplan.analytics.dto;

import java.util.Map;

import lombok.Data;

@Data
public class CreateWidgetDto {
    private String title;
    private String type;
    private Integer position;
    private Integer width;
    private Integer height;
    private Map<String, String> configuration;
}
