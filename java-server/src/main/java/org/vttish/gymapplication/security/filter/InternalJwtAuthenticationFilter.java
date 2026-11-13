package org.vttish.gymapplication.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.vttish.gymapplication.exception.UnauthenticatedException;
import org.vttish.gymapplication.security.InternalJwtService;
import org.vttish.gymapplication.security.model.InternalJwtMetadata;
import org.vttish.gymapplication.security.model.ParsedInternalJwt;
import org.vttish.gymapplication.security.model.UserPrincipal;

@Slf4j
@Component
public class InternalJwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String ROLE_PREFIX = "ROLE_";
  private static final String SCOPE_PREFIX = "SCOPE_";
  private static final String BEARER_PREFIX = "Bearer ";

  private final String applicationName;
  private final InternalJwtService internalJwtService;

  public InternalJwtAuthenticationFilter(
      @Value("${spring.application.name}") String applicationName,
      InternalJwtService internalJwtService
  ) {
    this.applicationName = applicationName;
    this.internalJwtService = internalJwtService;
  }

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
    ParsedInternalJwt parsedToken = internalJwtService.parse(token);
    UserPrincipal principal = parsedToken.userPrincipal();
    InternalJwtMetadata metadata = parsedToken.internalJwtMetadata();

    if (!metadata.audiences().contains(applicationName)) {
      throw new UnauthenticatedException("Internal JWT is not intended for this service");
    }

    Collection<GrantedAuthority> authorities = buildAuthorities(principal, metadata);
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        principal, null, authorities
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private Collection<GrantedAuthority> buildAuthorities(
      UserPrincipal principal,
      InternalJwtMetadata metadata
  ) {
    Set<GrantedAuthority> authorities = new LinkedHashSet<>();

    authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + principal.role().name()));

    for (String scope : metadata.scopes()) {
      if (scope != null && !scope.isBlank()) {
        authorities.add(new SimpleGrantedAuthority(SCOPE_PREFIX + scope.trim()));
      }
    }

    return List.copyOf(authorities);
  }
}
