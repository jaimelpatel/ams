/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.pojo;

import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Jaimel
 */
public class TeamInvitePojo {
    private String usernameOfUserBeingInvited;
    private Long teamPk;
    private boolean acceptTeamInv;

    public String getUsernameOfUserBeingInvited() {
        return usernameOfUserBeingInvited;
    }

    public void setUsernameOfUserBeingInvited(String usernameOfUserBeingInvited) {
        this.usernameOfUserBeingInvited = usernameOfUserBeingInvited;
    }

    public Long getTeamPk() {
        return teamPk;
    }

    public void setTeamPk(Long teamPk) {
        this.teamPk = teamPk;
    }

    public boolean isAcceptTeamInv() {
        return acceptTeamInv;
    }

    public void setAcceptTeamInv(boolean acceptTeamInv) {
        this.acceptTeamInv = acceptTeamInv;
    }
   
}
