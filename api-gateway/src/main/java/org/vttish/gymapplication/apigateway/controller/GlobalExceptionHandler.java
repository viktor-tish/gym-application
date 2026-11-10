package org.vttish.gymapplication.apigateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.vttish.gymapplication.apigateway.dto.ErrorResponseDto;
import org.vttish.gymapplication.apigateway.exception.TooManyRequestsException;
import org.vttish.gymapplication.apigateway.exception.UnauthenticatedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponseDto> handleHttpMethodNotSupportedException(
      HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
    return respond(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request, ex);
  }

  @ExceptionHandler({ NoResourceFoundException.class, NoHandlerFoundException.class })
  public ResponseEntity<ErrorResponseDto> handleNotFoundException(
      Exception ex, HttpServletRequest request) {
    return respond(HttpStatus.NOT_FOUND, ex.getMessage(), request, ex);
  }

  @ExceptionHandler({ UnauthenticatedException.class, AuthenticationException.class })
  public ResponseEntity<ErrorResponseDto> handleAuthenticationException(
      Exception ex, HttpServletRequest request) {
    return respond(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, ex);
  }

  @ExceptionHandler(TooManyRequestsException.class)
  public ResponseEntity<ErrorResponseDto> handleTooManyAttemptsException(
      TooManyRequestsException ex, HttpServletRequest request) {
    return respond(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), request, ex);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleUnexpectedException(
      Exception ex, HttpServletRequest request) {
    return respond(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request, ex);
  }

  private ResponseEntity<ErrorResponseDto> respond(
      HttpStatus status, String message, HttpServletRequest request, Exception ex) {
    return respond(status, ErrorResponseDto.of(
        status.value(), status.getReasonPhrase(), message, request.getRequestURI()), ex);
  }

  private ResponseEntity<ErrorResponseDto> respond(
      HttpStatus status, ErrorResponseDto body, Exception ex) {

    if (status.is5xxServerError()) {
      log.error("Request failed: {}", body, ex);
    } else {
      log.info("Request rejected: {} ({})", body, ex.getClass().getSimpleName());
    }

    return ResponseEntity.status(status).body(body);
  }
}
