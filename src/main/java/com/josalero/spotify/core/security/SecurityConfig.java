package com.josalero.spotify.core.security;


import com.josalero.spotify.core.security.service.CustomAccessDenied;
import com.josalero.spotify.core.security.service.CustomAuthenticationEntryPoint;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import java.util.List;

@Configuration
@ComponentScan(basePackages = {"com.josalero.spotify.core.security"})
@EntityScan(basePackages = {"com.josalero.spotify.core.security"})
@EnableMethodSecurity
@EnableWebSecurity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(
          HttpSecurity httpSecurity,
          CustomAccessDenied customAccessDenied,
          OpaqueTokenIntrospector introspector,
          CustomAuthenticationEntryPoint customAuthenticationEntryPoint
  )
      throws Exception {
    return httpSecurity
        .csrf(csrf -> csrf.disable())
        .securityMatcher(
            new NegatedRequestMatcher(
                new OrRequestMatcher(
                        List.of(
                                new AntPathRequestMatcher("/public/**")
                        )
                )
            )
        )
        .authorizeHttpRequests((requests) -> {
          requests.anyRequest().authenticated();
        })
        .exceptionHandling(configurer -> configurer.accessDeniedHandler(customAccessDenied)
                .authenticationEntryPoint(customAuthenticationEntryPoint))
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        //.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)))
        .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken
                (token -> token.introspector(introspector)))
        //.addFilterBefore(tokenAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return new NimbusJwtDecoder(new CustomAuthoritiesOpaqueTokenIntrospector.ParseOnlyJWTProcessor());
  }

  @Bean
  public OpenAPI customOpenAPI() {
    final String securitySchemeName = "bearerAuth";
    return new OpenAPI()
        .components(
            new Components()
                .addSecuritySchemes(
                    securitySchemeName,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .security(List.of(new SecurityRequirement().addList(securitySchemeName)))
        .tags(List.of(new Tag().name("App Access"),
                new Tag().name("Public")))
        .info(new Info().title("Spotify Metadata Search App").version("1.0.0"));
  }

}
