package com.josalero.spotify.core.exception;

public class ResourceAlreadyExistException extends RuntimeException{

  public ResourceAlreadyExistException() {
  }

  public ResourceAlreadyExistException(String message) {
    super(message);
  }

  public ResourceAlreadyExistException(String message, Throwable cause) {
    super(message, cause);
  }

}
