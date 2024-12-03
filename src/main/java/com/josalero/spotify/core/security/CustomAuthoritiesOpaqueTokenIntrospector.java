package com.josalero.spotify.core.security;

import com.josalero.spotify.core.exception.InvalidTokenException;
import com.josalero.spotify.core.security.service.CustomOAuth2AuthenticatedPrincipal;
import com.josalero.spotify.core.user.model.User;
import com.josalero.spotify.core.user.service.UserService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomAuthoritiesOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    OpaqueTokenIntrospector delegate;
    @Autowired
    JwtDecoder jwtDecoder;

    @Value("${spring.security.oauth2.resourceserver.opaque.introspection-uri}")
    String introspectionUri;
    @Value("${spring.security.oauth2.resourceserver.opaque.introspection-client-id}")
    String clientId;
    @Value("${spring.security.oauth2.resourceserver.opaque.introspection-client-secret}")
    String clientSecret;
    @Autowired
    UserService userService;

    @PostConstruct
    public void setup() {
      delegate = new NimbusOpaqueTokenIntrospector(introspectionUri, clientId, clientSecret);
    }

    public OAuth2AuthenticatedPrincipal introspect(String token) {
      try {
        OAuth2AuthenticatedPrincipal principal = this.delegate.introspect(token);
        Jwt jwt = this.jwtDecoder.decode(token);
        String username = jwt.getClaim("preferred_username");
        User user = userService.getByUsername(username)
                .orElseGet(() -> User.builder().username(username).build());

        return new CustomOAuth2AuthenticatedPrincipal(new DefaultOAuth2AuthenticatedPrincipal(jwt.getClaims(), extractResourceRoles(jwt)), user);
      } catch (Exception ex) {
        throw new InvalidTokenException(ex.getMessage());
      }
    }

    public static Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
      Map<String, Object> realmAccess = jwt.getClaim("realm_access");
      Collection<String> resourceRoles;

      if (realmAccess == null
              || (resourceRoles = (Collection<String> ) realmAccess.get("roles")) == null) {
        return Set.of();
      }
      return resourceRoles.stream()
              .map(role -> new SimpleGrantedAuthority(role))
              .collect(Collectors.toSet());
    }

    public static class ParseOnlyJWTProcessor extends DefaultJWTProcessor<SecurityContext> {
      @SneakyThrows
      @Override
      public JWTClaimsSet process(final SignedJWT signedJWT, final SecurityContext context)
              throws BadJOSEException, JOSEException {
        return signedJWT.getJWTClaimsSet();
      }
    }
}
