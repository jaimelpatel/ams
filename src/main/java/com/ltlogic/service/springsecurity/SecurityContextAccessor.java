/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.springsecurity;

/**
 *
 * @author Jaimel
 */
public interface SecurityContextAccessor {
  boolean isCurrentAuthenticationAnonymous();
}