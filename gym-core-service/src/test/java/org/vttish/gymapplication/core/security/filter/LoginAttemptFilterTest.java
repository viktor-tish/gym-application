package org.vttish.gymapplication.core.security.filter;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.vttish.gymapplication.core.exception.TooManyAttemptsException;
import org.vttish.gymapplication.core.security.ClientIpResolver;
import org.vttish.gymapplication.core.security.LoginAttemptService;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginAttemptFilter Unit Tests")
class LoginAttemptFilterTest {

  @Mock
  private LoginAttemptService loginAttemptService;

  @Mock
  private ClientIpResolver clientIpResolver;

  @Mock
  private HandlerExceptionResolver exceptionResolver;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @InjectMocks
  private LoginAttemptFilter filter;

  private static final String CLIENT_IP = "127.0.0.1";
  private static final String LOGIN_URI = "/api/v1/auth/login";

  @Test
  void doFilterInternal_ShouldProceedWithoutChecks_WhenNotPostRequest() throws Exception {
    when(request.getMethod()).thenReturn(HttpMethod.GET.name());

    filter.doFilterInternal(request, response, filterChain);

    verifyNoInteractions(clientIpResolver, loginAttemptService, exceptionResolver);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ShouldProceed_WhenPostRequestToNonLoginUri() throws Exception {
    when(request.getMethod()).thenReturn(HttpMethod.POST.name());
    when(request.getRequestURI()).thenReturn("/api/v1/trainee");

    filter.doFilterInternal(request, response, filterChain);

    verifyNoInteractions(clientIpResolver, loginAttemptService, exceptionResolver);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ShouldProceed_WhenLoginRequestAndIpIsNotBanned() throws Exception {
    when(request.getMethod()).thenReturn(HttpMethod.POST.name());
    when(request.getRequestURI()).thenReturn(LOGIN_URI);
    when(clientIpResolver.resolve(request)).thenReturn(CLIENT_IP);
    when(loginAttemptService.isBanned(CLIENT_IP)).thenReturn(false);

    filter.doFilterInternal(request, response, filterChain);

    verify(clientIpResolver).resolve(request);
    verify(loginAttemptService).isBanned(CLIENT_IP);
    verifyNoInteractions(exceptionResolver);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ShouldResolveException_WhenLoginRequestAndIpIsBanned() throws Exception {
    when(request.getMethod()).thenReturn(HttpMethod.POST.name());
    when(request.getRequestURI()).thenReturn(LOGIN_URI);
    when(clientIpResolver.resolve(request)).thenReturn(CLIENT_IP);
    when(loginAttemptService.isBanned(CLIENT_IP)).thenReturn(true);
    when(loginAttemptService.remainingBanTime(CLIENT_IP)).thenReturn(Duration.ofSeconds(120));

    filter.doFilterInternal(request, response, filterChain);

    verify(clientIpResolver).resolve(request);
    verify(loginAttemptService).isBanned(CLIENT_IP);
    verify(loginAttemptService).remainingBanTime(CLIENT_IP);

    ArgumentCaptor<TooManyAttemptsException> exceptionCaptor =
        ArgumentCaptor.forClass(TooManyAttemptsException.class);

    verify(exceptionResolver).resolveException(
        eq(request),
        eq(response),
        eq(null),
        exceptionCaptor.capture()
    );

    verify(filterChain, never()).doFilter(request, response);
  }
}
