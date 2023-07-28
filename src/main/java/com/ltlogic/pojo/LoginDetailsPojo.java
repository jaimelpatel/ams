/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.pojo;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 
 * @author Hoang
 */
public class LoginDetailsPojo {
    
    private String username;
    private String password;
    private Long teamPk;//testing team invites

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

    public Long getTeamPk() {
        return teamPk;
    }

    public void setTeamPk(Long teamPk) {
        this.teamPk = teamPk;
    }
}
