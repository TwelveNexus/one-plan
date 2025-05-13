package com.twelvenexus.oneplan.analytics.dto;

import lombok.Data;

@Data
public class CreateDashboardDto {
    private String name;
    private String description;
    private boolean isPublic;
}
