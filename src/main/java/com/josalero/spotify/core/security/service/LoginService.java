package com.josalero.spotify.core.security.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.josalero.spotify.core.security.CustomAuthoritiesOpaqueTokenIntrospector;
import com.josalero.spotify.core.security.domain.LoginRequest;
import com.josalero.spotify.core.security.domain.LoginResponse;
import com.josalero.spotify.core.user.domain.UserCreateRequest;
import com.josalero.spotify.core.user.domain.UserType;
import com.josalero.spotify.core.user.model.User;
import com.josalero.spotify.core.user.service.UserService;
import com.nimbusds.jwt.proc.BadJWTException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class LoginService {

  @Autowired
  WebClient authClient;

  @Autowired
  JwtDecoder jwtDecoder;

  @Autowired
  OpaqueTokenIntrospector opaqueTokenIntrospector;

  @Autowired
  UserService service;

  @Value("${auth.client.id}")
  String clientId;

  @Value("${auth.client.secret}")
  String clientSecret;

  public LoginResponse login(LoginRequest loginRequest) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("username", loginRequest.getUsername());
    formData.add("password", loginRequest.getPassword());
    formData.add("grant_type", "password");
    formData.add("scope", "openid");
    formData.add("client_id", clientId);
    formData.add("client_secret", clientSecret);

    JsonNode response = authClient
            .post()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .exchange()
            .block()
            .bodyToMono(JsonNode.class)
            .block();

    User user = getUserInfo(response);

    var loginResponse =  LoginResponse.builder()
            .accessToken(response.get("access_token").asText())
            .refreshToken(response.get("refresh_token").asText())
            .id(user.getId())
            .idToken(response.get("id_token").asText())
            .username(user.getUsername()).build();

    return loginResponse;

  }

  private User getUserInfo(JsonNode response) {
    User user = new User();
    try {
      Jwt jwt = jwtDecoder.decode(response.get("access_token").asText());
      if(jwt.getClaims()!= null) {

        List<String> aud = jwt.getAudience();
        if(aud==null || !aud.contains("find-job")) {
          throw new BadJWTException("Invalid Keycloak Resource!");
        }

        UserType userType = determineUserType(CustomAuthoritiesOpaqueTokenIntrospector.extractResourceRoles(jwt));
        UserCreateRequest request = UserCreateRequest
                .builder()
                .username(jwt.getClaim("preferred_username"))
                .email(jwt.getClaim("email"))
                .firstname(jwt.getClaim("given_name"))
                .lastname(jwt.getClaim("family_name"))
                .externalId(UUID.fromString(jwt.getSubject()))
                .build();

        user = service.getUser(request);

        log.info("User Int {} - Ext {} ", user.getId(), user.getExternalId());

      }

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
    return user;
  }

  public boolean isTokenExpired(String accessToken){
    try {
      Jwt jwt = jwtDecoder.decode(accessToken);

      return jwt.getExpiresAt().isBefore(Instant.now());

    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return true;
  }

  private UserType determineUserType(Collection<GrantedAuthority> authorities) {
    boolean isAdmin = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .filter(a -> a.contains(UserType.ADMIN.name()))
            .findFirst()
            .isPresent();
    if (isAdmin) {
      return UserType.ADMIN;
    }

    boolean isRecruiter = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .filter(a -> a.contains(UserType.RECRUITER.name()))
            .findFirst()
            .isPresent();
    if (isRecruiter) {
      return UserType.RECRUITER;
    }

    return UserType.OTHER;
  }

}
