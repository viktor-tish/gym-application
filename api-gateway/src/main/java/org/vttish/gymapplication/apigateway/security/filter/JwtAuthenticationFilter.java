package org.vttish.gymapplication.apigateway.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.vttish.gymapplication.apigateway.exception.UnauthenticatedException;
import org.vttish.gymapplication.apigateway.security.JwtBlacklistService;
import org.vttish.gymapplication.apigateway.security.JwtService;
import org.vttish.gymapplication.apigateway.security.model.JwtAuthenticationToken;
import org.vttish.gymapplication.apigateway.security.model.JwtMetadata;
import org.vttish.gymapplication.apigateway.security.model.ParsedJwt;
import org.vttish.gymapplication.apigateway.security.model.UserPrincipal;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtService jwtService;
  private final JwtBlacklistService jwtBlacklistService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
      String token = authHeader.substring(BEARER_PREFIX.length()).trim();

      if (!token.isBlank()) {
        try {
          authenticate(token);
        } catch (UnauthenticatedException ex) {
          log.info("JWT authentication rejected: {}", ex.getMessage());
          SecurityContextHolder.clearContext();
        } catch (Exception ex) {
          log.error("Unexpected error during JWT authentication", ex);
          SecurityContextHolder.clearContext();
        }
      }
    }

    filterChain.doFilter(request, response);
  }

  private void authenticate(String token) {
    ParsedJwt parsedJwt = jwtService.parse(token);
    UserPrincipal principal = parsedJwt.userPrincipal();
    JwtMetadata metadata = parsedJwt.jwtMetadata();

    if (jwtBlacklistService.isBlacklisted(metadata.jwtId())) {
      log.info("Rejected blacklisted JWT with JTI: {}", metadata.jwtId());
      return;
    }

    List<SimpleGrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority("ROLE_" + principal.role().name())
    );

    JwtAuthenticationToken authentication = new JwtAuthenticationToken(
        principal, metadata, authorities
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
