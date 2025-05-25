package com.twelvenexus.oneplan.identity.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

  @GetMapping({"", "/"})
  public ResponseEntity<Map<String, String>> home() {
    Map<String, String> response = new HashMap<>();
    response.put("service", "Identity Service");
    response.put("status", "UP");
    response.put("version", "1.0.0");
    return ResponseEntity.ok(response);
  }
}
