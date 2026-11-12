package org.vttish.gymapplication.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vttish.gymapplication.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.dto.auth.LoginRequestDto;
import org.vttish.gymapplication.dto.auth.LoginResponseDto;
import org.vttish.gymapplication.exception.ForbiddenException;
import org.vttish.gymapplication.facade.GymFacade;
import org.vttish.gymapplication.security.ClientIpResolver;
import org.vttish.gymapplication.security.model.UserPrincipal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final GymFacade gymFacade;
  private final ClientIpResolver clientIpResolver;

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(
      @Valid @RequestBody LoginRequestDto request,
      @Parameter(hidden = true) HttpServletRequest httpRequest
  ) {
    String ip = clientIpResolver.resolve(httpRequest);

    return ResponseEntity.ok(gymFacade.login(request, ip));
  }

  @PostMapping("/change-password")
  @PreAuthorize("hasAnyRole('TRAINEE', 'TRAINER')")
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
