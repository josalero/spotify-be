package com.josalero.spotify.core.exception;

public class ApiCallException extends RuntimeException{

  public ApiCallException() {
  }

  public ApiCallException(String message) {
    super(message);
  }

  public ApiCallException(String message, Throwable cause) {
    super(message, cause);
  }

}