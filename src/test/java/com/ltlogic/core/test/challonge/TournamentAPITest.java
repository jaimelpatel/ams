/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.core.test.challonge;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.core.test.match.MatchTest;
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
public class TournamentAPITest {

    private static final Logger LOG = LoggerFactory.getLogger(TournamentAPITest.class);

    @Autowired
    ChallongeTournamentService challongeTournamentService;

    @Autowired
    TournamentService tournamentService;

    @Autowired
    ChallongeParticipantService challongeParticipantService;

    @Autowired
    TournamentTeamService tournamentTeamService;
    
    @Autowired
    TournamentServiceIWS tournamentIWS;

    @Autowired
    MWRTeamComponent teamComponent;

    @Autowired
    UserComponent userComponent;

    @Ignore
    @Test
    public void createChallongeTournament() {
        TournamentPojo tournamentPojo = new TournamentPojo();

        Tournament tournament = tournamentIWS.createTournament(tournamentPojo);
        challongeTournamentService.createChallongeTournament(tournament);
    }

}
