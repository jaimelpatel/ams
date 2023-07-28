/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;

import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.service.core.TournamentTeamService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Jaimel
 */
@Service
public class TournamentTeamIWS {

    @Autowired
    TournamentTeamService tournamentTeamService;

    public List<TournamentTeam> getAllEligibleTournamentTeamsForTournament(long tournamentPk) {
        return tournamentTeamService.getAllEligibleTournamentTeamsForTournament(tournamentPk);
    }

    public List<TournamentTeam> getAllTournamentTeamsForTournament(long tournamentPk){
        return tournamentTeamService.getAllTournamentTeamsForTournament(tournamentPk);
    }
    
    public TournamentTeam getTournamentTeamByTournamentPkAndUserPk(long tournamentPk, long userPk){
        return tournamentTeamService.getTournamentTeamByTournamentPkAndUserPk(tournamentPk, userPk);
    }
    
    public TournamentTeam getTournamentTeamByNameAndTournament(String teamName, Tournament tournament){
        return tournamentTeamService.getTournamentTeamByNameAndTournament(teamName, tournament);
    }
}
