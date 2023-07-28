/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamPermissionsEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamStatusEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.TeamInvite;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.mwr.MWRTeam;
import com.ltlogic.pojo.TeamPojo;
import com.ltlogic.service.core.TeamInviteService;
import com.ltlogic.service.core.TeamService;
import com.ltlogic.service.core.UserService;
import com.ltlogic.service.springsecurity.TeamValidator;
import com.ltlogic.service.springsecurity.UserValidator;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;


/**
 *
 * @author jaimel
 */

@Service
public class TeamIWS {
    
    @Autowired
    private TeamService teamService;
    
    @Autowired
    private TeamValidator teamValidator;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TeamInviteService teamInviteService;
    
    //call this method before calling the "createTeam" method to validate that the team can be created without any errors or issues
    public String teamCreationvalidation(TeamPojo teamPojo, Errors errors){
        return teamValidator.teamCreationValidation(teamPojo, errors);
    }
    
    //creates and persists the team with the values given in the teamPojo object, returns created team object
    public Team createTeam(TeamPojo teamPojo, String username){
        return teamService.createTeam(teamPojo, username);
    }
    
    //call this method before calling "inviteUserToTeam" to see if there are any errors with inviting that user to the team
    public void inviteUserToTeamValidation(String usernameOfInviteReceiver, Long teamPk, Errors errors){
         teamValidator.inviteUserToTeamValidation(usernameOfInviteReceiver, teamPk, errors);
    }
    
    //invites the user with username "usernameOfInviteReceiver" to team with pk of "teamPk". Invite is sent by user with username "usernameOfInviteSender"
//    public TeamInvite inviteUserToTeam(String usernameOfInviteSender, String usernameOfInviteReceiver, Long teamPk){
//        TeamInvite teamInvite = teamInviteService.inviteUserToTeam(usernameOfInviteSender, usernameOfInviteReceiver, teamPk);
//        return teamInvite;
//                
//    }
    
    public Team getTeamByPk(long pk) {
        return teamService.getTeamByPk(pk);
    }
    //given a user's username, gets all teams of that user
    public List<Team> getAllTeamsOfUser(String username){
        return teamService.getAllTeamsOfUser(username);
    }
    
    public void promoteTeammateToLeader(long userPk, long teamPk) {
        teamService.promoteTeammateToLeader(userPk, teamPk);
    }
    
    public List<Team> getAllTeamsOfUserByTeamType(String username, TeamTypeEnum teamType){
        return teamService.getAllTeamsOfUserByTeamType(username, teamType);
    }
    
    public Team findTeamByNameAndGameAndPlatformAndTypeAndSize(String teamName, GameEnum gameEnum, PlatformEnum platformEnum, TeamSizeEnum teamSizeEnum, TeamTypeEnum teamTypeEnum, RegionEnum regionEnum, TeamStatusEnum teamStatusEnum) {
        return teamService.findTeamByNameAndGameAndPlatformAndTypeAndSize(teamName, gameEnum, platformEnum, teamSizeEnum, teamTypeEnum, regionEnum, teamStatusEnum);
    }
    
    public List<Team> findTeamByUsernameAndGameAndPlatformAndTypeAndSize(String username, GameEnum gameEnum, PlatformEnum platformEnum, TeamSizeEnum teamSizeEnum, TeamTypeEnum teamTypeEnum, RegionEnum regionEnum) {
        return teamService.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(username, gameEnum, platformEnum, teamSizeEnum, teamTypeEnum, regionEnum);
    }
    
    public void giveUserLeaderPermissions(long userPk, long teamPk){
        teamService.giveUserLeaderPermissions(userPk, teamPk);
    }
    
    public void removeLeaderPermissionsFromUser(long userPk, long teamPk){
        teamService.removeLeaderPermissionsfromUser(userPk, teamPk);
    }
    
    public void removeUserFromTeam(long userPk, long teamPk){
        teamService.removeUserFromTeam(userPk, teamPk);
    }
    
    public void disband(long teamPk, long userPk) throws Exception {
        teamService.disbandTeam(teamPk, userPk);
    }
    
    public MWRTeam getMWRTeamByTeamPk(long teamPk) {
        return teamService.getMWRTeamByTeamPk(teamPk);
    }
    
    public List<Team> findTeamsByMatchPk(long matchPk) {
        return teamService.findTeamsByMatchPk(matchPk);
    }
    
    public boolean isTeamInMatchOrTournament(Team team) {
        return teamService.isTeamInMatchOrTournament(team);
    }
    
    public boolean isUserOnTeamInMatchOrTournament(Team team, User user) {
        return teamService.isUserOnTeamInMatchOrTournament(team, user);
    }
    
    public void turnXpGainOn(long teamPk) {
        teamService.turnXpGainOn(teamPk);
    }
    
    public void turnXpGainOff(long teamPk) {
        teamService.turnXpGainOff(teamPk);
    }

}
