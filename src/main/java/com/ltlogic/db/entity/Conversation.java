/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.entity;

import com.ltlogic.db.superentity.SuperEntity;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Bishistha
 */
@NamedQueries({
    @NamedQuery(
            name = "Conversation.getAllConversationForTeamAndDispute",
            query = "SELECT c FROM Conversation c WHERE c.dispute.pk = :disputePk AND c.team.pk = :teamPk ORDER BY rowCreatedTimestamp ASC"
    )
})
@Entity
public class Conversation extends SuperEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispute_pk", referencedColumnName = "pk")
    private Dispute dispute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_pk", referencedColumnName = "pk")
    private Team team;

    private String message;

    private boolean isTeam;

    public Dispute getDispute() {
        return dispute;
    }

    public void setDispute(Dispute dispute) {
        this.dispute = dispute;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsTeam() {
        return isTeam;
    }

    public void setIsTeam(boolean isTeam) {
        this.isTeam = isTeam;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
    
}
