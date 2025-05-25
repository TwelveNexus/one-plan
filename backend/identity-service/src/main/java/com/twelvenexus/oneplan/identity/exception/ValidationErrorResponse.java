package com.twelvenexus.oneplan.identity.exception;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationErrorResponse extends ErrorResponse {
  private Map<String, String> errors;

  public ValidationErrorResponse(
      LocalDateTime timestamp,
      int status,
      String message,
      String path,
      Map<String, String> errors) {
    super(timestamp, status, message, path);
    this.errors = errors;
  }
}
