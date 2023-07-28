/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.core.test.team.mwr;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.core.test.user.UserComponent;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.TeamInvite;
import com.ltlogic.db.entity.User;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.iws.TeamInviteIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.TeamPojo;
import com.ltlogic.service.core.TeamService;
import com.ltlogic.service.springsecurity.TeamValidator;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author Hoang
 */
@Component
@Transactional
public class MWRTeamComponent {

    @Autowired
    TeamService teamService;

    @Autowired
    TeamIWS teamIWS;

    @Autowired
    TeamInviteIWS teamInviteIWS;

    @Autowired
    TeamValidator teamValidator;

    @Autowired
    UserIWS userIWS;

    @Autowired
    UserComponent userComponent;

    public List<Team> createTeams(List<User> userList, int numberOfTeamsToCreate, GameEnum gameEnum, PlatformEnum platform, TeamTypeEnum teamType, TeamSizeEnum teamSize) {

        List<Team> teamList = new ArrayList<>();

        TeamPojo teamPojo;

        for (int i = 0; i < numberOfTeamsToCreate; i++) {
            teamPojo = new TeamPojo();
            teamPojo.setGame(gameEnum);
            teamPojo.setPlatform(platform);
            teamPojo.setTeamName("Team"+i);
            teamPojo.setTeamSize(teamSize);
            teamPojo.setTeamType(teamType);
            teamPojo.setUsername(userList.get(i).getUsername());
            teamList.add(teamIWS.createTeam(teamPojo, userList.get(i).getUsername()));
        }
        return teamList;
    }

    
}
