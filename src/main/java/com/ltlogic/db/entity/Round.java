/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity;

import com.ltlogic.db.superentity.SuperEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;

/**
 * 
 * @author Hoang
 */
@Entity
public class Round extends SuperEntity {
    
    private int number;
    
    ArrayList<TournamentTeam> tournamentTeamsInRound;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ArrayList<TournamentTeam> getTournamentTeamsInRound() {
        return tournamentTeamsInRound;
    }

    public void setTournamentTeamsInRound(ArrayList<TournamentTeam> tournamentTeamsInRound) {
        this.tournamentTeamsInRound = tournamentTeamsInRound;
    }
    
    
}
