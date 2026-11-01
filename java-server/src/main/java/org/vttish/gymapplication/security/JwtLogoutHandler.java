package org.vttish.gymapplication.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.security.model.JwtMetadata;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtBlacklistService jwtBlacklistService;

  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
    if (authentication != null
        && authentication.getCredentials() instanceof JwtMetadata jwtMetadata
    ) {
      jwtBlacklistService.blacklist(jwtMetadata.jwtId(), jwtMetadata.expiration());
    } else {
      log.warn("Logout invoked without valid JWT metadata in authentication");
    }

    SecurityContextHolder.clearContext();
  }
}
