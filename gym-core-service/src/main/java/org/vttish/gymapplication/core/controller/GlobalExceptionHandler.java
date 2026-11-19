package org.vttish.gymapplication.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.vttish.gymapplication.core.dto.error.ErrorResponseDto;
import org.vttish.gymapplication.core.exception.BadRequestException;
import org.vttish.gymapplication.core.exception.ForbiddenException;
import org.vttish.gymapplication.core.exception.ResourceNotFoundException;
import org.vttish.gymapplication.core.exception.TooManyAttemptsException;
import org.vttish.gymapplication.core.exception.UnauthenticatedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    List<ErrorResponseDto.ValidationError> validationErrors = new ArrayList<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> validationErrors.add(
        new ErrorResponseDto.ValidationError(error.getField(), error.getDefaultMessage())));
    ex.getBindingResult().getGlobalErrors().forEach(error -> validationErrors.add(
        new ErrorResponseDto.ValidationError(error.getObjectName(), error.getDefaultMessage())));

    return respond(HttpStatus.BAD_REQUEST, ErrorResponseDto.ofValidation(
        HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
        "Validation failed", request.getRequestURI(), validationErrors), ex);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    return respond(HttpStatus.BAD_REQUEST, "Malformed JSON request body", request, ex);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponseDto> handleHttpMethodNotSupportedException(
      HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
    return respond(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request, ex);
  }

  @ExceptionHandler({ NoResourceFoundException.class, ResourceNotFoundException.class })
  public ResponseEntity<ErrorResponseDto> handleNotFoundException(
      Exception ex, HttpServletRequest request) {
    return respond(HttpStatus.NOT_FOUND, ex.getMessage(), request, ex);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponseDto> handleBadRequestException(
      BadRequestException ex, HttpServletRequest request) {
    return respond(HttpStatus.BAD_REQUEST, ex.getMessage(), request, ex);
  }

  @ExceptionHandler({ UnauthenticatedException.class, AuthenticationException.class })
  public ResponseEntity<ErrorResponseDto> handleAuthenticationException(
      Exception ex, HttpServletRequest request) {
    return respond(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, ex);
  }

  @ExceptionHandler({ ForbiddenException.class, AccessDeniedException.class })
  public ResponseEntity<ErrorResponseDto> handleForbiddenException(
      Exception ex, HttpServletRequest request) {
    return respond(HttpStatus.FORBIDDEN, ex.getMessage(), request, ex);
  }

  @ExceptionHandler(TooManyAttemptsException.class)
  public ResponseEntity<ErrorResponseDto> handleTooManyAttemptsException(
      TooManyAttemptsException ex, HttpServletRequest request) {
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
