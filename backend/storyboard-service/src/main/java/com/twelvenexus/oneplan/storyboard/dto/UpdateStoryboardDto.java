package com.twelvenexus.oneplan.storyboard.dto;

import lombok.Data;

@Data
public class UpdateStoryboardDto {
    private String title;
    private String description;
    private String visibility;
    private String status;
}
