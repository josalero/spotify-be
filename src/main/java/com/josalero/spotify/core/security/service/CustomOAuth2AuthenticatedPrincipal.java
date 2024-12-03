package com.josalero.spotify.core.security.service;

import com.josalero.spotify.core.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2AuthenticatedPrincipal implements OAuth2AuthenticatedPrincipal {
  private User user;
  private OAuth2AuthenticatedPrincipal principal;

  private Map<String, Object> attributes;

  public CustomOAuth2AuthenticatedPrincipal(OAuth2AuthenticatedPrincipal principal, User user) {
    this.user = user;
    this.principal = principal;
  }

  public User getUser(){
    return user;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return principal.getAttributes();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return principal.getAuthorities();
  }

  @Override
  public String getName() {
    return principal.getName();
  }
}
