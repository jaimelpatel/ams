/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity.challonge;

import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.superentity.SuperEntity;
import com.ltlogic.pojo.challonge.ChallongeMatchPojo;
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
            name = "MatchResponse.getMatchResponseById",
            query = "SELECT m FROM MatchResponse m WHERE m.match.id = :id"
    ),
    @NamedQuery(
            name = "MatchResponse.getNextMatchByCurrentMatchId",
            query = "SELECT m FROM MatchResponse m WHERE m.match.prerequisite_match_ids_csv LIKE :matchResponseId"
    )
})
@Entity
public class MatchResponse extends SuperEntity{

    @Embedded
    private ChallongeMatchPojo match;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "matchPk")
    Match ourMatch;

    public Match getMatch() {
        return ourMatch;
    }

    public void setMatch(Match match) {
        this.ourMatch = match;
    }
    
    public ChallongeMatchPojo getMatchPojo() {
        return match;
    }

    public void setMatchPojo(ChallongeMatchPojo match) {
        this.match = match;
    }
      
}
