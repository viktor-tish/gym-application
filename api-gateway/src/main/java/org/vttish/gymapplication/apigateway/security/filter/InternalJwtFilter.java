package org.vttish.gymapplication.apigateway.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.vttish.gymapplication.apigateway.security.ClientIpResolver;
import org.vttish.gymapplication.apigateway.security.InternalJwtScopePolicy;
import org.vttish.gymapplication.apigateway.security.InternalJwtService;
import org.vttish.gymapplication.apigateway.security.model.HeaderOverrideRequest;
import org.vttish.gymapplication.apigateway.security.model.JwtAuthenticationToken;

@Component
@RequiredArgsConstructor
public class InternalJwtFilter extends OncePerRequestFilter {

  private static final String CLIENT_IP_HEADER = "X-Client-Ip";
  private static final String BEARER_PREFIX = "Bearer ";

  private final InternalJwtScopePolicy policy;
  private final InternalJwtService internalJwtService;
  private final ClientIpResolver clientIpResolver;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain
  ) throws ServletException, IOException {
    Map<String, String> overrides = new HashMap<>();

    overrides.put(HttpHeaders.AUTHORIZATION, null);
    overrides.put(CLIENT_IP_HEADER, clientIpResolver.resolve(request));

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth instanceof JwtAuthenticationToken jwtAuth && jwtAuth.isAuthenticated()) {
      policy.match(request.getMethod(), request.getRequestURI())
          .ifPresent(rule -> {
            String internalJwt = internalJwtService.generate(
                jwtAuth.userPrincipal(),
                jwtAuth.jwtMetadata(),
                rule
            );

            overrides.put(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + internalJwt);
          });
    }

    chain.doFilter(new HeaderOverrideRequest(request, overrides), response);
  }
}
