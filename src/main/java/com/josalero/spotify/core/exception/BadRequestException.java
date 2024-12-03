package com.josalero.spotify.core.exception;

public class BadRequestException extends RuntimeException{

  public BadRequestException() {
  }

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(String message, Throwable cause) {
    super(message, cause);
  }

}