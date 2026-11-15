package org.vttish.gymapplication.core.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class AuthorizationHeaderInterceptorTest {

  @Mock
  private HttpRequest httpRequest;

  @Mock
  private HttpHeaders httpHeaders;

  @Mock
  private ClientHttpRequestExecution execution;

  @Mock
  private ClientHttpResponse response;

  @Mock
  private HttpServletRequest servletRequest;

  private AuthorizationHeaderInterceptor interceptor;

  private static final byte[] BODY = new byte[0];

  @BeforeEach
  void setUp() {
    interceptor = new AuthorizationHeaderInterceptor();
    RequestContextHolder.resetRequestAttributes();
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void intercept_ShouldForwardAuthorizationHeader_WhenPresentInContext() throws IOException {
    ServletRequestAttributes attributes = new ServletRequestAttributes(servletRequest);
    RequestContextHolder.setRequestAttributes(attributes);

    when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer test-token-123");
    when(httpRequest.getHeaders()).thenReturn(httpHeaders);
    when(execution.execute(httpRequest, BODY)).thenReturn(response);

    ClientHttpResponse result = interceptor.intercept(httpRequest, BODY, execution);

    assertNotNull(result);
    verify(httpHeaders).set(HttpHeaders.AUTHORIZATION, "Bearer test-token-123");
    verify(execution).execute(httpRequest, BODY);
  }

  @Test
  void intercept_ShouldNotSetAuthorizationHeader_WhenRequestContextAttributesIsNull() throws IOException {
    when(execution.execute(httpRequest, BODY)).thenReturn(response);

    ClientHttpResponse result = interceptor.intercept(httpRequest, BODY, execution);

    assertNotNull(result);
    verifyNoInteractions(httpRequest);
    verify(execution).execute(httpRequest, BODY);
  }

  @Test
  void intercept_ShouldNotSetAuthorizationHeader_WhenAuthHeaderIsNull() throws IOException {
    ServletRequestAttributes attributes = new ServletRequestAttributes(servletRequest);
    RequestContextHolder.setRequestAttributes(attributes);

    when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
    when(execution.execute(httpRequest, BODY)).thenReturn(response);

    ClientHttpResponse result = interceptor.intercept(httpRequest, BODY, execution);

    assertNotNull(result);
    verifyNoInteractions(httpRequest);
    verify(execution).execute(httpRequest, BODY);
  }

  @Test
  void intercept_ShouldNotSetAuthorizationHeader_WhenAuthHeaderIsBlank() throws IOException {
    ServletRequestAttributes attributes = new ServletRequestAttributes(servletRequest);
    RequestContextHolder.setRequestAttributes(attributes);

    when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("   ");
    when(execution.execute(httpRequest, BODY)).thenReturn(response);

    ClientHttpResponse result = interceptor.intercept(httpRequest, BODY, execution);

    assertNotNull(result);
    verifyNoInteractions(httpRequest);
    verify(execution).execute(httpRequest, BODY);
  }
}
