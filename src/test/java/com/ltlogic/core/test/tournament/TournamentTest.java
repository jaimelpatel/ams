/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.core.test.tournament;

import com.ltlogic.core.test.match.MatchTest;
import javax.transaction.Transactional;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Service
@Ignore
public class TournamentTest {

    @Autowired
    TournamentComponent tournamentComponent;

    private static final Logger LOG = LoggerFactory.getLogger(TournamentTest.class);

    //##########################################################################################################################################
    //Not enough tournament teawms Accept cases
    //FE
    @Test
    public void oneTeamsFullyAccepts() throws Exception {
        tournamentComponent.oneTeamsFullyAccepts();
    }

    //FE

    @Test
    public void oneTeamsFullyAcceptsFiveTeamsPartiallyAccept() throws Exception {
        tournamentComponent.oneTeamsFullyAcceptsFiveTeamsPartiallyAccept();
    }

    //FE

    @Test
    public void twoTeamsFullyAcceptOneTeamLeaves() throws Exception {
        tournamentComponent.twoTeamsFullyAcceptOneTeamLeaves();
    }

    //FE

    @Test
    public void fiveTeamsPartiallyAccept() throws Exception {
        tournamentComponent.fiveTeamsPartiallyAccept();
    }

    //##########################################################################################################################################
    //Enough tournamnent teams Accept cases
    @Test
    public void twoTeamsFullyAcceptOneTeamPartiallyAcceptsThenLeaves() throws Exception {
        tournamentComponent.twoTeamsFullyAcceptOneTeamPartiallyAcceptsThenLeaves();
    }

    //FE

    @Test
    public void twoTeamsFullyAcceptTwoTeamsPartiallyAccepts() throws Exception {
        tournamentComponent.twoTeamsFullyAcceptTwoTeamsPartiallyAccepts();
    }

    //FE
    @Test
    public void singleMemberDeclinesTournamentInvite() throws Exception {
        tournamentComponent.singleMemberDeclinesTournamentInvite();
    }

    @Test
    public void teamJoinLeaveRejoinTournament() throws Exception {
        tournamentComponent.teamJoinLeaveRejoinTournament();
    }

    @Test
    public void fiveTeamsFullyAccept() throws Exception {
        tournamentComponent.fiveTeamsFullyAccept();
    }

}
