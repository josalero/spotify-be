package com.josalero.spotify.core.user.domain;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ToString
public class UserCreateRequest {

  @Size(max = 50, min = 1)
  String firstname;

  @Size(max = 50, min = 1)
  String lastname;

  @Size(max = 100, min = 1)
  @EqualsAndHashCode.Include
  String email;

  @Size(max = 100, min = 1)
  @EqualsAndHashCode.Include
  String username;

  @EqualsAndHashCode.Include
  UUID externalId;

}
