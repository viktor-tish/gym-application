package org.vttish.gymapplication.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.vttish.gymapplication.dto.error.ErrorResponseDto;
import org.vttish.gymapplication.exception.AuthenticationException;
import org.vttish.gymapplication.exception.BadRequestException;
import org.vttish.gymapplication.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidationException(
      MethodArgumentNotValidException ex,
      HttpServletRequest request
  ) {
    List<ErrorResponseDto.ValidationError> validationErrors = new ArrayList<>();

    ex.getBindingResult().getFieldErrors().forEach(error ->
        validationErrors.add(new ErrorResponseDto.ValidationError(
            error.getField(),
            error.getDefaultMessage())
        )
    );

    ex.getBindingResult().getGlobalErrors().forEach(error ->
        validationErrors.add(new ErrorResponseDto.ValidationError(
            error.getObjectName(),
            error.getDefaultMessage())
        )
    );

    ErrorResponseDto errorResponse = ErrorResponseDto.ofValidation(
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        "Validation failed",
        request.getRequestURI(),
        validationErrors
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(
      HttpServletRequest request
  ) {
    ErrorResponseDto errorResponse = ErrorResponseDto.of(
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        "Malformed JSON request body",
        request.getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponseDto> handleHttpMethodNotSupportedException(
      HttpRequestMethodNotSupportedException ex,
      HttpServletRequest request
  ) {
    ErrorResponseDto errorResponse = ErrorResponseDto.of(
        HttpStatus.METHOD_NOT_ALLOWED.value(),
        HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
        ex.getMessage(),
        request.getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
  }

  @ExceptionHandler({ NoResourceFoundException.class, ResourceNotFoundException.class })
  public ResponseEntity<ErrorResponseDto> handleNotFoundException(
      Exception ex,
      HttpServletRequest request
  ) {
    ErrorResponseDto errorResponse = ErrorResponseDto.of(
        HttpStatus.NOT_FOUND.value(),
        HttpStatus.NOT_FOUND.getReasonPhrase(),
        ex.getMessage(),
        request.getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponseDto> handleBadRequestException(
      BadRequestException ex,
      HttpServletRequest request
  ) {
    ErrorResponseDto errorResponse = ErrorResponseDto.of(
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        ex.getMessage(),
        request.getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponseDto> handleAuthenticationException(
      AuthenticationException ex,
      HttpServletRequest request
  ) {
    ErrorResponseDto errorResponse = ErrorResponseDto.of(
        HttpStatus.UNAUTHORIZED.value(),
        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
        ex.getMessage(),
        request.getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleUnexpectedException(
      HttpServletRequest request
  ) {
    ErrorResponseDto errorResponse = ErrorResponseDto.of(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
        "An unexpected error occurred",
        request.getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
