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
public class TournamentInvitePojo {

    private String username;

    private String tournamentInviteStatus;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTournamentInviteStatus() {
        return tournamentInviteStatus;
    }

    public void setTournamentInviteStatus(String tournamentInviteStatus) {
        this.tournamentInviteStatus = tournamentInviteStatus;
    }

}