package com.josalero.spotify.core.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josalero.spotify.core.exception.CustomErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");

    CustomErrorResponse errorResponse = CustomErrorResponse.builder()
            .errorCode(HttpStatus.UNAUTHORIZED.value())
            .timestamp(LocalDateTime.now().toString())
            .message(authException.getMessage())
            .build();

    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getWriter(), errorResponse);
  }
}
