package com.twelvenexus.oneplan.requirement.dto;

import lombok.Data;

@Data
public class UpdateRequirementDto {
    private String title;
    private String description;
    private String status;
    private String priority;
    private String category;
    private String tags;
}
