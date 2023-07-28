/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity.challonge;

import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.superentity.SuperEntity;
import com.ltlogic.pojo.challonge.ChallongeTournamentPojo;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@NamedQueries({
    @NamedQuery(
            name = "TournamentResponse.getTournamentResponseByChallongeTournamentId",
            query = "SELECT t FROM TournamentResponse t WHERE t.tournament.id = :id"
    )
})
/**
 * 
 * @author Hoang
 */
@Entity
public class TournamentResponse extends SuperEntity{
    
    @Embedded
    private ChallongeTournamentPojo tournament;
    
    @OneToOne(mappedBy = "tournamentResponse", fetch = FetchType.LAZY)
    private Tournament tourn;

    public Tournament getTournament() {
        return tourn;
    }

    public void setTournament(Tournament tourn) {
        this.tourn = tourn;
    }

    public ChallongeTournamentPojo getTournamentPojo() {
        return tournament;
    }

    public void setTournamentPojo(ChallongeTournamentPojo tournament) {
        this.tournament = tournament;
    }

}
