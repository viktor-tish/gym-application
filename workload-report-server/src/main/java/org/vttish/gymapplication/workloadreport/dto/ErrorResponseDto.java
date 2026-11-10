package org.vttish.gymapplication.workloadreport.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDto(
    int status,
    String error,
    String message,
    String path,
    Instant timestamp,
    List<ValidationError> validationErrors
) {
  public record ValidationError(String field, String message) {
  }

  public static ErrorResponseDto of(int status, String error, String message, String path) {
    return new ErrorResponseDto(status, error, message, path, Instant.now(), null);
  }

  public static ErrorResponseDto ofValidation(
      int status,
      String error,
      String message,
      String path,
      List<ValidationError> validationErrors
  ) {
    return new ErrorResponseDto(status, error, message, path, Instant.now(), validationErrors);
  }
}
