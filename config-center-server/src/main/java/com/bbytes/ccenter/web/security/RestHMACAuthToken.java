package com.bbytes.ccenter.web.security;

import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;


public class RestHMACAuthToken extends UsernamePasswordAuthenticationToken {

  private static final long serialVersionUID = -9021106139110827313L;
  

  public RestHMACAuthToken(Object principal, RestHMACCredentials credentials) {
    super(principal, credentials,AuthorityUtils.NO_AUTHORITIES);
  }

  @Override
  public String getPrincipal() {
    return (String) super.getPrincipal();
  }

  @Override
  public RestHMACCredentials getCredentials() {
    return (RestHMACCredentials) super.getCredentials();
  }


}
