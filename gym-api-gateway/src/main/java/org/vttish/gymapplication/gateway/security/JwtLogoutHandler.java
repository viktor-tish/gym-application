package org.vttish.gymapplication.gateway.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.gateway.security.model.JwtAuthenticationToken;
import org.vttish.gymapplication.gateway.security.model.JwtMetadata;

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
    if (authentication instanceof JwtAuthenticationToken authenticationToken) {
      JwtMetadata metadata = authenticationToken.jwtMetadata();
      jwtBlacklistService.blacklist(metadata.jwtId(), metadata.expiration());
    } else {
      log.warn("Logout invoked without valid JWT metadata in authentication");
    }

    SecurityContextHolder.clearContext();
  }
}
