package com.twelvenexus.oneplan.identity.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

  @RequestMapping("/error")
  public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    String path = (String) request.getAttribute("javax.servlet.error.request_uri");

    if (statusCode == null) {
      statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    if (path == null) {
      path = request.getRequestURI();
    }

    String message;
    if (statusCode == HttpStatus.NOT_FOUND.value()) {
      message = "The requested resource was not found";
    } else {
      message = "An error occurred processing your request";
    }

    ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), statusCode, message, path);

    return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(statusCode));
  }
}
