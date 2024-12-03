package com.josalero.spotify.core.exception;

public class ForbiddenAccessException extends RuntimeException{
  public ForbiddenAccessException() {
  }

  public ForbiddenAccessException(String message) {
    super(message);
  }

  public ForbiddenAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
