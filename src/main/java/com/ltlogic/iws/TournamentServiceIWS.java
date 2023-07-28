/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.TournamentStatusEnum;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.entity.User;
import com.ltlogic.pojo.TournamentPojo;
import com.ltlogic.service.core.TournamentInviteService;
import com.ltlogic.service.core.TournamentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author raymond
 */
@Component
public class TournamentServiceIWS {

    @Autowired
    TournamentService tournamentService;

    public Tournament findByPk(long pk) {
        return tournamentService.findByPk(pk);
    }

    public Tournament createTournament(TournamentPojo tournamentPojo) {
        return tournamentService.createTournament(tournamentPojo);
    }

    public TournamentTeam joinTournament(Team team, Tournament tournament, List<User> usersPlayingInTournament, User userJoiningTournamentForTeam) throws Exception {
        return tournamentService.joinTournament(team, tournament, usersPlayingInTournament, userJoiningTournamentForTeam);
    }

    public void leaveTournament(User user, TournamentTeam tournamentTeam) throws Exception {
        tournamentService.leaveTournament(user, tournamentTeam);
    }

    public List<Tournament> getAllTournaments() {
        return tournamentService.getAllTournaments();
    }

    public List<Tournament> getTournamentsByUserPk(long userPk) {
        return tournamentService.getTournamentsByUserPk(userPk);
    }

    public List<Tournament> getTournamentsByUserPkForPaginate(long userPk, int pageNumber) {
        return tournamentService.getTournamentsByUserPkForPaginate(userPk, pageNumber);
    }

    public Tournament findTournamentByTournamentIdAndUserPk(int tournamentId, long userPk) {
        return tournamentService.findTournamentByTournamentIdAndUserPk(tournamentId, userPk);
    }

    public List<Tournament> getAllTournamentsByStatus(TournamentStatusEnum tournamentStatus) {
        return tournamentService.getAllTournamentsByStatus(tournamentStatus);
    }

    public List<Tournament> getAllTournamentsByStatusAndPlatform(TournamentStatusEnum tournamentStatus) {
        return tournamentService.getAllTournamentsByStatus(tournamentStatus);
    }

    public List<Tournament> getAllPendingAndActiveTournamentsForUser(long userPk) {
        return tournamentService.getAllPendingAndActiveTournamentsForUser(userPk);
    }

    public List<Tournament> getAllTournamentsByGameAndPlatform(GameEnum gameEnum, PlatformEnum platformEnum) {
        return tournamentService.getAllTournamentsByGameAndPlatform(gameEnum, platformEnum);
    }
}
