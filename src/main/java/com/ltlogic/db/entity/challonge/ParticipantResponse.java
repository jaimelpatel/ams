/*
 * To change this license header choose License Headers in Project Properties.
 * To change this template file choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity.challonge;

import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.superentity.SuperEntity;
import com.ltlogic.pojo.challonge.ChallongeParticipantPojo;
import java.time.LocalDateTime;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 * 
 * @author Hoang
 */
@NamedQueries({
    @NamedQuery(
            name = "ParticipantResponse.getParticipantResponseById",
            query = "SELECT p FROM ParticipantResponse p WHERE p.participant.id = :id"
    )
})
@Entity
public class ParticipantResponse extends SuperEntity{
    
    @Embedded
    ChallongeParticipantPojo participant;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "tournamentTeamPk")
    TournamentTeam tournamentTeam;

    public TournamentTeam getTournamentTeam() {
        return tournamentTeam;
    }

    public void setTournamentTeam(TournamentTeam tournamentTeam) {
        this.tournamentTeam = tournamentTeam;
    }
    
    public ChallongeParticipantPojo getParticipant() {
        return participant;
    }

    public void setParticipant(ChallongeParticipantPojo participant) {
        this.participant = participant;
    }
    
    
}
