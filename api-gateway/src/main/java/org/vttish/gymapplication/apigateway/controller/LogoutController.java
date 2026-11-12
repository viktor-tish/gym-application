package org.vttish.gymapplication.apigateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class LogoutController {

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    // Intercepted by Spring Security's LogoutFilter before reaching here
    return ResponseEntity.noContent().build();
  }
}
