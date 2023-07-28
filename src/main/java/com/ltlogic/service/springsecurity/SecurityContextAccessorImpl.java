/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.springsecurity;

import com.ltlogic.configs.WebSecurityConfig;
import com.ltlogic.service.core.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Jaimel
 */
@Service
public final class SecurityContextAccessorImpl implements SecurityContextAccessor{


  @Autowired
  WebSecurityConfig webSecurityConfig;
  
  private static final Logger LOG = LoggerFactory.getLogger(SecurityContextAccessorImpl.class);

  @Override
  public boolean isCurrentAuthenticationAnonymous() {
    AuthenticationTrustResolver authenticationTrustResolver = webSecurityConfig.authenticationTrustResolver();
    final Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();
    LOG.info("XX isCurrentAuthenticationAnonymous method called!! XX");
    return authenticationTrustResolver.isAnonymous(authentication);
  }
}
