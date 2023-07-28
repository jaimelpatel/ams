/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.core.test.team;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.core.test.team.mwr.MWRTeamComponent;
import com.ltlogic.core.test.user.UserComponent;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.RegistrationPojo;
import com.ltlogic.pojo.TeamPojo;
import com.ltlogic.service.core.TeamService;
import com.ltlogic.service.springsecurity.TeamValidator;
import com.ltlogic.core.test.user.UserTest;
import com.ltlogic.db.entity.TeamInvite;
import com.ltlogic.iws.TeamInviteIWS;
import com.ltlogic.service.core.TeamInviteService;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.BeforeClass;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

/**
 *
 * @author Hoang
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TransactionConfiguration(defaultRollback = false)
@Service
public class TeamTest {

    @Autowired
    TeamService teamService;

    @Autowired
    TeamIWS teamIWS;
    
    @Autowired
    TeamValidator teamValidator;

    @Autowired
    UserIWS userIWS;
    
    @Autowired
    TeamComponent teamComponent;
    
    @Autowired
    UserComponent userComponent;
    
    @Autowired
    TeamInviteIWS teamInviteIWS;

    private static final Logger LOG = LoggerFactory.getLogger(TeamTest.class);

//    @BeforeClass 
//    public void createUsersForTeams() {      
//     userTest.createDefaultUsersTest();
//    }
    
    
    @Ignore
    @Test
    public void testSinglesTeamCreation(){
        int numOfUsersAndTeams = 10;
        teamComponent.createTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH, TeamSizeEnum.SINGLES);
    }
    
    @Ignore
    @Test
    public void testDoublesTeamCreation(){
        int numOfUsersAndTeams = 10;
        teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.XP);
    }
    
    @Ignore
    @Test
    public void test3sOr4sTeamCreation(){
        int numOfUsersAndTeams = 10;
        teamComponent.createAndAssociateThreesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.XP);
    }
}
