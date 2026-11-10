package org.vttish.gymapplication.apigateway.exception;

public class UnauthenticatedException extends RuntimeException {
  public UnauthenticatedException(String message) {
    super(message);
  }
}
