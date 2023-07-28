/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.service.core;

import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.repository.TournamentRepository;
import com.ltlogic.db.repository.TournamentTeamRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Hoang
 */
@Service
@Transactional
public class TournamentTeamService {
    
    @Autowired
    TournamentTeamRepository tournamentTeamRepo;
    
    @Autowired
    TournamentRepository tournamentRepo;
    
    
    public void persistTournamentTeam(TournamentTeam t){
        tournamentTeamRepo.persistTournamentTeam(t);
    }
    
    public TournamentTeam findByPk(long pk){
        return tournamentTeamRepo.findByPk(pk);
    }
    
    public TournamentTeam createTournamentTeam(Team team, long tournamentPk, List<Long> pksOfTeamMembersPlaying){
        TournamentTeam tournamentTeam = new TournamentTeam();
        Tournament tournament = tournamentRepo.findByPk(tournamentPk);
        associateTeamToTournamentTeam(tournamentTeam, team);
        associateTournamentToTournamentTeam(tournamentTeam, tournament);
        tournamentTeam.getPksOfTeamMembersPlaying().addAll(pksOfTeamMembersPlaying);
        persistTournamentTeam(tournamentTeam);
        return tournamentTeam;
    }
    
    public void associateTeamToTournamentTeam(TournamentTeam tournamentTeam, Team team){
        tournamentTeam.setTeam(team);
        team.getTournamentTeams().add(tournamentTeam);
    }
    
    public void associateTournamentToTournamentTeam(TournamentTeam tournamentTeam, Tournament tournament){
        tournamentTeam.setTournament(tournament);
        tournament.getTournamentTeams().add(tournamentTeam);
    }
    
    public void disassociateTournamentTeamFromTournament(TournamentTeam tournamentTeam, Tournament tournament){
        tournamentTeam.setTournament(null);
        tournament.getTournamentTeams().remove(tournamentTeam);
    }
    
    public List<TournamentTeam> getAllTournamentTeamsOfTeam(long pk){
       return tournamentTeamRepo.getAllTournamentTeamsByTeam(pk);
    }
    
    public List<TournamentTeam> getAllEligibleTournamentTeamsForTournament(long pk){
        return tournamentTeamRepo.getAllEligibleTournamentTeamsForTournament(pk);
    }
    
    public List<TournamentTeam> getAllTournamentTeamsForTournament(long pk){
        return tournamentTeamRepo.getAllTournamentTeamsForTournament(pk);
    }
    
    public TournamentTeam getTournamentTeamByTournamentPkAndUserPk(long tournamentPk, long userPk){
        List<TournamentTeam> tournamentTeams = tournamentTeamRepo.getAllTournamentTeamsForTournament(tournamentPk);
        for(TournamentTeam t : tournamentTeams){
            if(t.getPksOfTeamMembersPlaying().contains(userPk)){
                return t;
            }
        }
        return null;
    }
    
    public TournamentTeam getTournamentTeamByNameAndTournament(String teamName, Tournament tournament){
        return tournamentTeamRepo.getTournamentTeamByNameAndTournament(teamName, tournament);
    }
}