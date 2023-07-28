/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.core.test.team;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamStatusEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.core.test.user.UserComponent;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.TeamInvite;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.TeamRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.iws.TeamInviteIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.TeamPojo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import junit.framework.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Hoang
 */
@Component
@Transactional
public class TeamComponent {

    @Autowired
    TeamIWS teamIWS;
    @Autowired
    TeamInviteIWS teamInviteIWS;
    @Autowired
    UserComponent userComponent;
    @Autowired
    UserIWS userIWS;
    @Autowired
    TeamRepository teamRepo;
    @Autowired
    UserRepository userRepo;

    public List<Team> createTeams(int numberOfTeamsToCreate, GameEnum gameEnum, PlatformEnum platform, TeamTypeEnum teamType, TeamSizeEnum teamSize) {
        List<User> userList = userComponent.createListOfUsers(0, numberOfTeamsToCreate);
        List<Team> teamList = new ArrayList<>();
        TeamPojo teamPojo;
        for (int i = 0; i < numberOfTeamsToCreate; i++) {
            teamPojo = new TeamPojo();
            teamPojo.setGame(gameEnum);
            teamPojo.setPlatform(platform);
            teamPojo.setTeamName("team" + i);
            teamPojo.setTeamSize(teamSize);
            teamPojo.setTeamType(teamType);
            teamPojo.setTeamStatus(TeamStatusEnum.LIVE);
            teamPojo.setRegion(RegionEnum.NA);
            teamPojo.setUsername(userList.get(i).getUsername());
            if(userList.isEmpty() || userList == null){
            System.out.println("USERLIST IS NULL");
            }
            teamList.add(teamIWS.createTeam(teamPojo, userList.get(i).getUsername()));
        }
        return teamList;
    }

    public List<Team> createAndAssociateDoublesTeams(int numOfUsersAndTeams, GameEnum gameEnum, PlatformEnum platformEnum, TeamTypeEnum teamTypeEnum) {
        List<Team> teamList = createTeams(numOfUsersAndTeams, gameEnum, platformEnum, teamTypeEnum, TeamSizeEnum.DOUBLES);
        List<User> userToAddToTeams = userComponent.createListOfUsers(numOfUsersAndTeams, numOfUsersAndTeams);
        List<TeamInvite> teamInviteList = new ArrayList<>();

        for (int i = 0; i < teamList.size(); i++) {
            if (teamList.get(i).getTeamPojo().getTeamName().equalsIgnoreCase("team" + i)) {
                Team team = teamList.get(i);
                User user = userIWS.getUserByUsername("user" + i);
                int userNumber = i + numOfUsersAndTeams;
                String userToInviteName = "user" + userNumber;
                User userToInvite = userIWS.getUserByUsername(userToInviteName);
                TeamInvite invite = teamInviteIWS.inviteUserToTeam(user.getUsername(), userToInvite.getUsername(), team.getPk());
                teamInviteList.add(invite);
            }
        }

        for (int j = 0; j < teamInviteList.size(); j++) {

            TeamInvite teamInvite = teamInviteList.get(j);
            User user = teamInvite.getUserReceiver();
            Team team = teamInvite.getTeam();
            teamInviteIWS.acceptInvite(user, team, teamInvite);
        }
        
        for (int i = 0; i < numOfUsersAndTeams; i++) {
            String teamName = "team" + i;
            int userNumber = numOfUsersAndTeams + i;
            String usernameOfUserBelongingToTeam = "user" + userNumber;
            System.out.println("Team name: " + teamName);
            System.out.println("User name: " + usernameOfUserBelongingToTeam);
            Team team = teamRepo.findTeamByNameAndGameAndPlatformAndTypeAndSizeAndStatus(teamName, gameEnum, platformEnum, TeamSizeEnum.DOUBLES, teamTypeEnum, RegionEnum.NA, TeamStatusEnum.LIVE);
            User user = userRepo.findByUsername(usernameOfUserBelongingToTeam);
            Assert.assertTrue(team.getMembers().contains(user));
        }
        return teamList;
    }

    public List<Team> createAndAssociateThreesTeams(int numOfUsersAndTeams, GameEnum gameEnum, PlatformEnum platformEnum, TeamTypeEnum teamTypeEnum) {
        List<Team> teamList = createTeams(numOfUsersAndTeams, gameEnum, platformEnum, teamTypeEnum, TeamSizeEnum.TEAM);
        List<User> firstUsersToAddToTeams = userComponent.createListOfUsers(numOfUsersAndTeams, numOfUsersAndTeams);
        int indexToStartAtForSecondList = numOfUsersAndTeams+numOfUsersAndTeams;
        List<User> secondUsersToAddToTeams = userComponent.createListOfUsers(indexToStartAtForSecondList, numOfUsersAndTeams);
        int indexToStartAtForThirdList = indexToStartAtForSecondList+numOfUsersAndTeams;
        List<User> thirdUsersToAddToTeams = userComponent.createListOfUsers(indexToStartAtForThirdList, numOfUsersAndTeams);
        
        List<TeamInvite> teamInviteList = new ArrayList<>();

        Map<String, Team> teamMap = new HashMap<>();
        
        //invite 3 people to a team, to make 4's team
        for (int i = 0; i < teamList.size(); i++) {
            if (teamList.get(i).getTeamPojo().getTeamName().equalsIgnoreCase("team" + i)) {
                Team team = teamList.get(i);
                User user = userIWS.getUserByUsername("user" + i);

                int userNumber1 = i + numOfUsersAndTeams;
                int userNumber2 = i + 1 + numOfUsersAndTeams;
                int userNumber3 = i + 2 + numOfUsersAndTeams;
                String userToInviteName1 = "user" + userNumber1;
                String userToInviteName2 = "user" + userNumber2;
                String userToInviteName3 = "user" + userNumber3;
                User userToInvite1 = userIWS.getUserByUsername(userToInviteName1);
                User userToInvite2 = userIWS.getUserByUsername(userToInviteName2);
                User userToInvite3 = userIWS.getUserByUsername(userToInviteName3);
                TeamInvite invite1 = teamInviteIWS.inviteUserToTeam(user.getUsername(), userToInvite1.getUsername(), team.getPk());
                TeamInvite invite2 = teamInviteIWS.inviteUserToTeam(user.getUsername(), userToInvite2.getUsername(), team.getPk());
                TeamInvite invite3 = teamInviteIWS.inviteUserToTeam(user.getUsername(), userToInvite3.getUsername(), team.getPk());
                teamInviteList.add(invite1);
                teamInviteList.add(invite2);
                teamInviteList.add(invite3);
            }
        }
        //accept all the team invites
        for (int j = 0; j < teamInviteList.size(); j++) {

            TeamInvite teamInvite = teamInviteList.get(j);
            User user = teamInvite.getUserReceiver();
            Team team = teamInvite.getTeam();
            teamInviteIWS.acceptInvite(user, team, teamInvite);
        }

        for (int i = 0; i < numOfUsersAndTeams; i++) {
            String teamName = "team" + i;
            int userNumber1 = numOfUsersAndTeams + i;
            int userNumber2 = numOfUsersAndTeams + i+1;
            int userNumber3 = numOfUsersAndTeams + i+2;
            String usernameOfUserBelongingToTeam1 = "user" + userNumber1;
            String usernameOfUserBelongingToTeam2 = "user" + userNumber2;
            String usernameOfUserBelongingToTeam3 = "user" + userNumber3;
            Team team = teamRepo.findTeamByNameAndGameAndPlatformAndTypeAndSizeAndStatus(teamName, gameEnum, platformEnum, TeamSizeEnum.TEAM, teamTypeEnum, RegionEnum.NA, TeamStatusEnum.LIVE);
            User user1 = userRepo.findByUsername(usernameOfUserBelongingToTeam1);
            User user2 = userRepo.findByUsername(usernameOfUserBelongingToTeam2);
            User user3 = userRepo.findByUsername(usernameOfUserBelongingToTeam3);
            Assert.assertTrue(team.getMembers().contains(user1));
            Assert.assertTrue(team.getMembers().contains(user2));
            Assert.assertTrue(team.getMembers().contains(user3));
        }
        return teamList;
    }

}
