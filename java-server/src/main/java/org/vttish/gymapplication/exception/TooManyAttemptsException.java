package org.vttish.gymapplication.exception;

public class TooManyAttemptsException extends RuntimeException {
  public TooManyAttemptsException(String message) {
    super(message);
  }
}
