package com.twelvenexus.oneplan.identity.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors;

    public ValidationErrorResponse(LocalDateTime timestamp, int status, String message, String path,
                                   Map<String, String> errors) {
        super(timestamp, status, message, path);
        this.errors = errors;
    }
}
