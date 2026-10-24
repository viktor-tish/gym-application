package org.vttish.gymapplication.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.vttish.gymapplication.exception.AuthenticationException;
import org.vttish.gymapplication.exception.ForbiddenException;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
  private final JwtService jwtService;
  private final List<PublicEndpoint> whiteList;

  private static final String BEARER_PREFIX = "Bearer ";
  private static final String USERNAME_PATH_VARIABLE = "username";

  @Override
  @SuppressWarnings("java:S3516")
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler
  ) {
    if (HttpMethod.OPTIONS.matches(request.getMethod())) {
      return true;
    }

    if (isPublicEndpoint(request)) {
      return true;
    }

    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      throw new AuthenticationException("Missing or invalid Authorization header");
    }

    String token = authHeader.substring(BEARER_PREFIX.length()).trim();
    if (token.isBlank()) {
      throw new AuthenticationException("JWT token is empty");
    }

    UserPrincipal principal = jwtService.parseToken(token);
    request.setAttribute("userPrincipal", principal);

    if (handler instanceof HandlerMethod handlerMethod) {
      checkRole(handlerMethod, principal);
      checkOwnership(request, principal);
    }

    return true;
  }

  private boolean isPublicEndpoint(HttpServletRequest request) {
    if (whiteList == null) {
      return false;
    }

    for (PublicEndpoint endpoint : whiteList) {
      if (endpoint.matches(request)) {
        return true;
      }
    }

    return false;
  }

  private void checkRole(HandlerMethod handlerMethod, UserPrincipal principal) {
    RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);

    if (requireRole == null) {
      requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
    }

    if (requireRole != null) {
      List<Role> allowedRoles = List.of(requireRole.value());

      if (!allowedRoles.contains(principal.role())) {
        throw new ForbiddenException("Access denied");
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void checkOwnership(HttpServletRequest request, UserPrincipal principal) {
    Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(
        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
    );

    if (pathVariables != null && pathVariables.containsKey(USERNAME_PATH_VARIABLE)) {
      String pathUsername = pathVariables.get(USERNAME_PATH_VARIABLE);

      if (pathUsername != null && !pathUsername.equals(principal.username())) {
        throw new ForbiddenException("Access denied");
      }
    }
  }
}
