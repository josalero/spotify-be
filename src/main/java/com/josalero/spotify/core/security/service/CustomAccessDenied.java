package com.josalero.spotify.core.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josalero.spotify.core.exception.CustomErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDenied implements AccessDeniedHandler {
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    CustomErrorResponse errorResponse = CustomErrorResponse.builder()
            .errorCode(HttpStatus.FORBIDDEN.value())
            .timestamp(LocalDateTime.now().toString())
            .message(accessDeniedException.getMessage())
            .build();

    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getWriter(), errorResponse);
  }
}
