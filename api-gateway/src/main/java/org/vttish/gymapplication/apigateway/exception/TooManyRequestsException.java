package org.vttish.gymapplication.apigateway.exception;

public class TooManyRequestsException extends RuntimeException {
  public TooManyRequestsException(String message) {
    super(message);
  }
}
