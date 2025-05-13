package com.twelvenexus.oneplan.storyboard.dto;

import lombok.Data;

@Data
public class ShareStoryboardDto {
    private Boolean passwordProtected = false;
    private String password;
    private Integer expiryDays;
}
