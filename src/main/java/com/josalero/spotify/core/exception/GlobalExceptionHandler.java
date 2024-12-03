package com.josalero.spotify.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.UnsupportedEncodingException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<CustomErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
    CustomErrorResponse response = CustomErrorResponse.builder()
            .errorCode(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .build();

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UnsupportedEncodingException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<CustomErrorResponse> handleUnsupportedEncodingException(UnsupportedEncodingException ex) {
    CustomErrorResponse response = CustomErrorResponse.builder()
            .errorCode(HttpStatus.SERVICE_UNAVAILABLE.value())
            .message(ex.getMessage())
            .build();

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ResourceAlreadyExistException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<CustomErrorResponse> handleResourceAlreadyExistException(ResourceAlreadyExistException ex) {
    CustomErrorResponse response = CustomErrorResponse.builder()
            .errorCode(HttpStatus.CONFLICT.value())
            .message(ex.getMessage())
            .build();
    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<CustomErrorResponse> handleResourceAlreadyExistException(BadRequestException ex) {
    CustomErrorResponse response = CustomErrorResponse.builder()
            .errorCode(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidTokenException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<CustomErrorResponse> handleResourceInvalidTokenException(InvalidTokenException ex) {
    CustomErrorResponse response = CustomErrorResponse.builder()
            .errorCode(HttpStatus.UNAUTHORIZED.value())
            .message(ex.getMessage())
            .build();

    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<CustomErrorResponse> handleResourceException(Exception ex) {
    CustomErrorResponse response = CustomErrorResponse.builder()
            .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message(ex.getMessage())
            .build();

    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}