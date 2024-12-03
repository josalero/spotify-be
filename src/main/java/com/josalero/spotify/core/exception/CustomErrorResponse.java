package com.josalero.spotify.core.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomErrorResponse {
  int errorCode;
  String message;
  @Builder.Default
  String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
}
