package org.vttish.gymapplication.apigateway.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.vttish.gymapplication.apigateway.dto.ErrorResponseDto;
import org.vttish.gymapplication.apigateway.exception.TooManyRequestsException;
import org.vttish.gymapplication.apigateway.exception.UnauthenticatedException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
  private GlobalExceptionHandler handler;

  @Mock
  private HttpServletRequest request;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
    when(request.getRequestURI()).thenReturn("/test/path");
  }

  @Test
  void handleHttpMethodNotSupportedException_ShouldReturnMethodNotAllowed() {
    HttpRequestMethodNotSupportedException ex =
        new HttpRequestMethodNotSupportedException("POST");

    ResponseEntity<ErrorResponseDto> response =
        handler.handleHttpMethodNotSupportedException(ex, request);

    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(405, response.getBody().status());
    assertEquals("Method Not Allowed", response.getBody().error());
    assertEquals(ex.getMessage(), response.getBody().message());
    assertEquals("/test/path", response.getBody().path());
    assertNotNull(response.getBody().timestamp());
  }

  @Test
  void handleNotFoundException_ShouldReturnNotFound_WhenNoResourceFoundExceptionThrown() {
    NoResourceFoundException ex = new NoResourceFoundException(
        HttpMethod.GET,
        "/test/path",
        "/test/path"
    );

    ResponseEntity<ErrorResponseDto> response = handler.handleNotFoundException(ex, request);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(404, response.getBody().status());
    assertEquals("Not Found", response.getBody().error());
    assertEquals(ex.getMessage(), response.getBody().message());
    assertEquals("/test/path", response.getBody().path());
    assertNotNull(response.getBody().timestamp());
  }

  @Test
  void handleAuthenticationException_ShouldReturnUnauthorized() {
    UnauthenticatedException ex = new UnauthenticatedException("Unauthorized access");

    ResponseEntity<ErrorResponseDto> response =
        handler.handleAuthenticationException(ex, request);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(401, response.getBody().status());
    assertEquals("Unauthorized", response.getBody().error());
    assertEquals("Unauthorized access", response.getBody().message());
    assertEquals("/test/path", response.getBody().path());
    assertNotNull(response.getBody().timestamp());
  }

  @Test
  void handleAuthenticationException_ShouldReturnUnauthorized_ForSpringSecurityException() {
    BadCredentialsException ex = new BadCredentialsException("Bad credentials");

    ResponseEntity<ErrorResponseDto> response =
        handler.handleAuthenticationException(ex, request);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(401, response.getBody().status());
    assertEquals("Unauthorized", response.getBody().error());
    assertEquals("Bad credentials", response.getBody().message());
    assertEquals("/test/path", response.getBody().path());
    assertNotNull(response.getBody().timestamp());
  }

  @Test
  void handleTooManyAttemptsException_ShouldReturnTooManyRequests() {
    TooManyRequestsException ex =
        new TooManyRequestsException("Too many requests. Try again later");

    ResponseEntity<ErrorResponseDto> response =
        handler.handleTooManyAttemptsException(ex, request);

    assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(429, response.getBody().status());
    assertEquals("Too Many Requests", response.getBody().error());
    assertEquals("Too many requests. Try again later", response.getBody().message());
    assertEquals("/test/path", response.getBody().path());
    assertNotNull(response.getBody().timestamp());
  }

  @Test
  void handleUnexpectedException_ShouldReturnInternalServerError() {
    RuntimeException ex = new RuntimeException("Unexpected exception");
    ResponseEntity<ErrorResponseDto> response = handler.handleUnexpectedException(ex, request);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(500, response.getBody().status());
    assertEquals("Internal Server Error", response.getBody().error());
    assertEquals("An unexpected error occurred", response.getBody().message());
    assertEquals("/test/path", response.getBody().path());
    assertNotNull(response.getBody().timestamp());
  }
}
