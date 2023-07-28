/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.models.form;

/**
 *
 * @author raymond
 */
public class UserInfo {
    
    private Long pk;
    private String username;
    
    /**
     * @return the pk
     */
    public Long getPk() {
        return pk;
    }

    /**
     * @param pk the pk to set
     */
    public void setPk(Long pk) {
        this.pk = pk;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
