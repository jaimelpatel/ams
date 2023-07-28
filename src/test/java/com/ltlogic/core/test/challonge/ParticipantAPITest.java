/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.core.test.challonge;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.core.test.team.TeamComponent;
import com.ltlogic.core.test.team.mwr.MWRTeamComponent;
import com.ltlogic.core.test.user.UserComponent;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.entity.User;
import com.ltlogic.iws.TournamentServiceIWS;
import com.ltlogic.pojo.TournamentPojo;
import com.ltlogic.service.challonge.ChallongeParticipantService;
import com.ltlogic.service.challonge.ChallongeTournamentService;
import com.ltlogic.service.core.TournamentService;
import com.ltlogic.service.core.TournamentTeamService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 *
 * @author Hoang
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TransactionConfiguration(defaultRollback = false)
@Service
public class ParticipantAPITest {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantAPITest.class);

    @Autowired
    ChallongeParticipantService challongeParticipantService;

    @Autowired
    TournamentTeamService tournamentTeamService;

    @Autowired
    TeamComponent teamComponent;

    @Autowired
    UserComponent userComponent;

    @Autowired
    ChallongeTournamentService challongeTournamentService;

    @Autowired
    TournamentServiceIWS tournamentIWS;

    @Ignore
    @Test
    public void createChallongeParticipants() {

        TournamentPojo tournamentPojo = new TournamentPojo();

        Tournament tournament = tournamentIWS.createTournament(tournamentPojo);
        challongeTournamentService.createChallongeTournament(tournament);

        List<Team> teams = teamComponent.createAndAssociateDoublesTeams(10, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);

        for (Team team : teams) {
            List<User> usersPlayingOnTeam = new ArrayList<>(team.getMembers());
            List<Long> usersPlayingPks = new ArrayList<>();
            for (User user : usersPlayingOnTeam) {
                usersPlayingPks.add(user.getPk());
            }
            TournamentTeam tournamentTeam = tournamentTeamService.createTournamentTeam(team, tournament.getPk(), usersPlayingPks);
            challongeParticipantService.createParticipantForTournament(tournament, tournamentTeam);
        }
    }
}
