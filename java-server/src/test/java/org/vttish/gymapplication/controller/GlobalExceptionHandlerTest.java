package org.vttish.gymapplication.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.vttish.gymapplication.dto.error.ErrorResponseDto;
import org.vttish.gymapplication.exception.BadRequestException;
import org.vttish.gymapplication.exception.ForbiddenException;
import org.vttish.gymapplication.exception.ResourceNotFoundException;
import org.vttish.gymapplication.exception.TooManyAttemptsException;
import org.vttish.gymapplication.exception.UnauthenticatedException;

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
  void handleValidationException_ShouldReturnBadRequest_WithFieldAndGlobalErrors() {
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
    bindingResult.addError(new FieldError("target", "name", "Name is mandatory"));
    bindingResult.addError(new ObjectError("target", "Date range is invalid"));

    when(ex.getBindingResult()).thenReturn(bindingResult);

    ResponseEntity<ErrorResponseDto> response =
        handler.handleValidationException(ex, request);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().status());
    assertEquals("Bad Request", response.getBody().error());
    assertEquals("Validation failed", response.getBody().message());
    assertEquals("/test/path", response.getBody().path());
    assertNotNull(response.getBody().timestamp());

    assertEquals(2, response.getBody().validationErrors().size());
    assertEquals("name", response.getBody().validationErrors().get(0).field());
    assertEquals(
        "Name is mandatory",
        response.getBody().validationErrors().get(0).message()
    );
    assertEquals("target", response.getBody().validationErrors().get(1).field());
    assertEquals(
        "Date range is invalid",
        response.getBody().validationErrors().get(1).message()
    );
  }

  @Test
  void handleHttpMessageNotReadableException_ShouldReturnBadRequest() {
    HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
    ResponseEntity<ErrorResponseDto> response =
        handler.handleHttpMessageNotReadableException(ex, request);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().status());
    assertEquals("Bad Request", response.getBody().error());
    assertEquals("Malformed JSON request body", response.getBody().message());
    assertEquals("/test/path", response.getBody().path());
    assertNotNull(response.getBody().timestamp());
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
  void handleNotFoundException_ShouldReturnNotFound_WhenResourceNotFoundExceptionThrown() {
    ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");

    ResponseEntity<ErrorResponseDto> response = handler.handleNotFoundException(ex, request);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(404, response.getBody().status());
    assertEquals("Not Found", response.getBody().error());
    assertEquals("Resource not found", response.getBody().message());
    assertEquals("/test/path", response.getBody().path());
    assertNotNull(response.getBody().timestamp());
  }

  @Test
  void handleNotFoundException_ShouldReturnNotFound_WhenNoResourceFoundExceptionThrown() {
    NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/test/path");

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
  void handleBadRequestException_ShouldReturnBadRequest() {
    BadRequestException ex = new BadRequestException("Custom bad request");

    ResponseEntity<ErrorResponseDto> response = handler.handleBadRequestException(ex, request);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().status());
    assertEquals("Bad Request", response.getBody().error());
    assertEquals("Custom bad request", response.getBody().message());
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
  void handleForbiddenException_ShouldReturnForbidden() {
    ForbiddenException ex = new ForbiddenException("Access denied");

    ResponseEntity<ErrorResponseDto> response = handler.handleForbiddenException(ex, request);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(403, response.getBody().status());
    assertEquals("Forbidden", response.getBody().error());
    assertEquals("Access denied", response.getBody().message());
    assertEquals("/test/path", response.getBody().path());
    assertNotNull(response.getBody().timestamp());
  }

  @Test
  void handleTooManyAttemptsException_ShouldReturnTooManyRequests() {
    TooManyAttemptsException ex =
        new TooManyAttemptsException("Too many failed attempts. Try again after 300s");

    ResponseEntity<ErrorResponseDto> response =
        handler.handleTooManyAttemptsException(ex, request);

    assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(429, response.getBody().status());
    assertEquals("Too Many Requests", response.getBody().error());
    assertEquals("Too many failed attempts. Try again after 300s", response.getBody().message());
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
