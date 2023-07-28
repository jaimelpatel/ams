/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.pojo;

import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.customannotations.ValidEmail;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Jaimel
 */
public class RegistrationPojo {

    /*
    @NotNull
    @NotEmpty
    @NotBlank 
    
    annotation explanation with examples
    
    String name = null;
    @NotNull: false
    @NotEmpty: false
    @NotBlank: false
    
    String name = "";
    @NotNull: true
    @NotEmpty: false
    @NotBlank: false
    
    String name = " ";
    @NotNull: true
    @NotEmpty: true
    @NotBlank: false
    
    String name = "Great answer!";
    @NotNull: true
    @NotEmpty: true
    @NotBlank: true
     */
    @ValidEmail(message="Email is invalid.")
    private String email;

    private String username;

    private String password;

    private String passwordConfirm;
    
    @NotEmpty(message="Region is required.")
    private String regionId;
    
    private int timeZoneEnumId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public int getTimeZoneEnumId() {
        return timeZoneEnumId;
    }

    public void setTimeZoneEnumId(int timeZoneEnumId) {
        this.timeZoneEnumId = timeZoneEnumId;
    }

//    public int getRegionId() {
//        return regionId;
//    }
//
//    public void setRegionId(int regionId) {
//        this.regionId = regionId;
//    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
}
