/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.core.test.match;

import javax.transaction.Transactional;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
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
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Service
@Ignore
public class MatchTest {

    @Autowired
    MatchComponent matchComponent;

    private static final Logger LOG = LoggerFactory.getLogger(MatchTest.class);

    @Test
    public void createPublicXPMatch() throws Exception {
        matchComponent.createDoublesPublicXPMatch();
    }

    @Test
    public void allAcceptDoublesPublicWagerMatch() throws Exception {
        matchComponent.allAcceptDoublesPublicWagerMatch();
    }

    @Test
    public void creatorTeam1DeclineDoublesPublicWagerMatch() throws Exception {
        matchComponent.creatorTeam1DeclineDoublesPublicWagerMatch();
    }

    @Test
    public void creatorTeamAllAcceptAndAcceptorTeam1DeclineDoublesPublicWagerMatch() throws Exception {
        matchComponent.creatorTeamAllAcceptAndAcceptorTeam1DeclineDoublesPublicWagerMatch();
    }

    @Test
    public void creatorTeamAllAcceptAndAcceptorTeam1DeclineDoublesPrivateWagerMatch() throws Exception {
        matchComponent.creatorTeamAllAcceptAndAcceptorTeam1DeclineDoublesPrivateWagerMatch();
    }

    @Test
    public void creatorTeam1DeclineDoublesPrivateWagerMatch() throws Exception {
        matchComponent.creatorTeam1DeclineDoublesPrivateWagerMatch();
    }

    @Test
    public void allAcceptDoublesPrivateWagerMatch() throws Exception {
        matchComponent.allAcceptDoublesPrivateWagerMatch();
    }

}
