package com.josalero.spotify.core.security.service;

import com.josalero.spotify.core.exception.ForbiddenAccessException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrincipalService {

  public String getPrincipal() {
    return getAuth2AuthenticatedPrincipal()
            .map(t -> t.getUser().getUsername())
            .orElse("");
  }

  public UUID getUserId() {
    return getAuth2AuthenticatedPrincipal()
            .map(t -> t.getUser().getId())
            .orElse(UUID.randomUUID());
  }

  public String getFullname() {
    return getAuth2AuthenticatedPrincipal()
            .map(t -> t.getUser().getFullname())
            .orElse("Confidential");
  }

  public String getEmail() {
    return getAuth2AuthenticatedPrincipal()
            .map(t -> t.getUser().getEmail())
            .orElse("");
  }

  public List<String> getAuthorities() {
    return getAuth2AuthenticatedPrincipal()
            .map(t -> t.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()))
            .orElse(List.of());
  }

  public boolean isMe(String username) {
    return getPrincipal().equals(username);
  }

  private Optional<CustomOAuth2AuthenticatedPrincipal> getAuth2AuthenticatedPrincipal() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(a -> (BearerTokenAuthentication) a)
            .map(a -> (CustomOAuth2AuthenticatedPrincipal) a.getPrincipal());
  }

}
