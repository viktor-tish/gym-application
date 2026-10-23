package org.vttish.gymapplication.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vttish.gymapplication.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.dto.auth.LoginRequestDto;
import org.vttish.gymapplication.dto.auth.LoginResponseDto;
import org.vttish.gymapplication.exception.ForbiddenException;
import org.vttish.gymapplication.facade.GymFacade;
import org.vttish.gymapplication.security.RequireRole;
import org.vttish.gymapplication.security.Role;
import org.vttish.gymapplication.security.UserPrincipal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final GymFacade gymFacade;

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
    return ResponseEntity.ok(gymFacade.login(request));
  }

  @PostMapping("/change-password")
  @RequireRole({ Role.TRAINEE, Role.TRAINER })
  public ResponseEntity<Void> changePassword(
      @Valid @RequestBody ChangePasswordRequestDto request,
      @Parameter(hidden = true) UserPrincipal userPrincipal
  ) {
    if (userPrincipal == null || !request.username().equals(userPrincipal.username())) {
      throw new ForbiddenException("Access denied");
    }

    gymFacade.changePassword(request);

    return ResponseEntity.ok().build();
  }
}
