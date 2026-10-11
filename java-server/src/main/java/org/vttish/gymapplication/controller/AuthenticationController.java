package org.vttish.gymapplication.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vttish.gymapplication.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.dto.auth.LoginRequestDto;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

  @PostMapping("/login")
  public ResponseEntity<Void> login(@Valid @RequestBody LoginRequestDto request) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @PostMapping("/change-password")
  public ResponseEntity<Void> changePassword(
      @Valid @RequestBody ChangePasswordRequestDto request
  ) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }
}
