package org.vttish.gymapplication.core.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.vttish.gymapplication.core.exception.TooManyAttemptsException;
import org.vttish.gymapplication.core.security.ClientIpResolver;
import org.vttish.gymapplication.core.security.LoginAttemptService;

@Component
public class LoginAttemptFilter extends OncePerRequestFilter {

  private final LoginAttemptService loginAttemptService;
  private final ClientIpResolver clientIpResolver;
  private final HandlerExceptionResolver exceptionResolver;

  private static final String LOGIN_URI = "/api/v1/auth/login";

  public LoginAttemptFilter(
      LoginAttemptService loginAttemptService,
      ClientIpResolver clientIpResolver,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver
  ) {
    this.loginAttemptService = loginAttemptService;
    this.clientIpResolver = clientIpResolver;
    this.exceptionResolver = exceptionResolver;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    if (isLoginRequest(request)) {
      String ip = clientIpResolver.resolve(request);

      if (loginAttemptService.isBanned(ip)) {
        Duration remaining = loginAttemptService.remainingBanTime(ip);

        TooManyAttemptsException ex = new TooManyAttemptsException(
            String.format("Too many failed attempts. Try again after %ds", remaining.toSeconds())
        );

        exceptionResolver.resolveException(request, response, null, ex);
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private boolean isLoginRequest(HttpServletRequest request) {
    return HttpMethod.POST.matches(request.getMethod())
        && LOGIN_URI.equals(request.getRequestURI());
  }
}
