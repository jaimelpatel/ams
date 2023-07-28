/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.springsecurity;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamStatusEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.TeamInvite;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.TeamPermissions;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.TeamInviteRepository;
import com.ltlogic.db.repository.TeamRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.pojo.TeamPojo;
import com.ltlogic.service.core.TeamService;
import com.ltlogic.service.core.UserService;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 *
 * @author jaimel
 */
@Component
public class TeamValidator {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamIWS teamIWS;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private TeamInviteRepository teamInviteRepo;

    public void inviteUserToTeamValidation(String username, Long teamPk, Errors errors) {

        boolean hasReceivedTeamInviteBefore = false;
//        User receiver = userService.findByUsername(username);
        Team team = teamService.getTeamByPk(teamPk);
        User receiver = userRepo.userWithUsernameExistsUser(username.toLowerCase());
        boolean isReceiverAlreadyMemberOfTeam = team.getMembers().contains(receiver);

        if (username == null || username.isEmpty()) {
            errors.reject("usernameField", "Username can\'t be empty.");
        } else {
            if (isReceiverAlreadyMemberOfTeam) {
                errors.reject("team.uniqueness", "User '" + username + "' is already a member of the team.");
            }
            if (receiver == null) {
                errors.reject("team.uniqueness", "User " + "'" + username + "'" + " does not exist.");
            }
        }

        if (receiver != null && team != null) {
            TeamInvite teamInvite = teamInviteRepo.getPendingReceivedInvitesByUserPkAndTeamPk(receiver.getPk(), team.getPk());

            if (teamInvite != null) {
                teamInvite.setRowCreatedTimestamp(DateTimeUtil.getDefaultLocalDateTimeNow());
                errors.reject("team.uniqueness", "User " + "'" + username + "'" + " has already received an invite to join the team.");
            }
        }

        if (!containsOnlyAlphaNumeric(username)) {
            errors.reject("team.uniqueness", "Username may only contain letters, digits and underscores.");
        }
    }

    public boolean containsOnlyAlphaNumeric(String toExamine) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }

    public String inviteUserToTeamValidationOnTeamCreation(TeamPojo tp, Errors errors) {
        String errorMessage = "";
        for (String username : tp.getUsersToInviteToTeam()) {
            if (!userRepo.userWithUsernameExists(username.toLowerCase())) {
                errors.reject("team.uniqueness", "User '" + username + "' does not exist.");
            }
        }

        for (String username : tp.getUsersToInviteToTeam()) {
            if (username.equalsIgnoreCase(tp.getUsername())) {
                errors.reject("team.uniqueness", "You cannot invite yourself to the team.");
            }
        }
        return errorMessage;
    }

    public void doesUserHaveLeaderPermissions(long userPk, long teamPk, Errors errors) {
        TeamPermissions tp = teamRepo.findTeamPermissionsByUserPkAndTeamPk(userPk, teamPk);
        if (tp == null || tp.isHasLeaderPermissions() == false) {
            errors.reject("team.uniqueness", "You do not have permissions to edit this team.");
        }
    }

    public Team findTeamForMatchValidation(String teamName, TeamPojo creatingTeamPojo, Errors errors) {
        String errorMessage = "";
        Team team = null;
        if (creatingTeamPojo != null) {
            User user = userRepo.findByUsername(creatingTeamPojo.getUsername());
            if (teamName != null && !teamName.isEmpty()) {
                GameEnum gameEnum = creatingTeamPojo.getGame();
                PlatformEnum platformEnum = creatingTeamPojo.getPlatform();
                TeamSizeEnum teamSizeEnum = creatingTeamPojo.getTeamSize();
                TeamTypeEnum teamTypeEnum = creatingTeamPojo.getTeamType();
                RegionEnum regionEnum = user.getUserInfo().getRegion();
                team = teamRepo.findTeamByNameAndGameAndPlatformAndTypeAndSizeAndStatus(teamName.toLowerCase(), gameEnum, platformEnum, teamSizeEnum, teamTypeEnum, regionEnum, TeamStatusEnum.LIVE);
                if (team == null) {
                    errors.reject("TeamNotExist", "There is no team with the name " + "'" + teamName + "'" + " in this ladder.");
                }
            }
        }

        return team;
    }

    //not being used
    public String teamCreationValidation(TeamPojo teamPojo, Errors errors) {
        String username = teamPojo.getUsername();
        User user = userRepo.findByUsername(username);
        Set<Team> teamsUserIsAMemberOf = user.getTeams();
        String errorMessage = "";

        for (Team team : teamsUserIsAMemberOf) {
            if (team.getTeamPojo().getGame() == teamPojo.getGame() && team.getTeamPojo().getPlatform() == teamPojo.getPlatform() && team.getTeamPojo().getTeamSize() == teamPojo.getTeamSize() && team.getTeamPojo().getTeamType() == teamPojo.getTeamType()) {
                errors.rejectValue("User is part of a similar team.", "User is part of a similar team.");
            }
        }

        Team team = teamRepo.findTeamByNameAndGameAndPlatformAndTypeAndSizeAndStatus(teamPojo.getTeamName(), teamPojo.getGame(), teamPojo.getPlatform(), teamPojo.getTeamSize(), teamPojo.getTeamType(), user.getUserInfo().getRegion(), TeamStatusEnum.LIVE);
        if (team != null) {
            errors.rejectValue("Teamname in use", "Teamname in use");
            errorMessage = "This team name is already in use.";
        }
        return errorMessage;
    }

    public void userAcceptTeamInviteValidation(User u, Team t, Errors errors) throws Exception {

        if (t.getMembers().contains(u)) {
            throw new Exception("You are already a member of this team.");
        }

        if (t.getTeamPojo().getTeamStatus() == TeamStatusEnum.DISBANDED) {
            errors.reject("team.uniqueness", "This team has been disbanded.");
            throw new Exception("This team has been disbanded.");
        }

        TeamPojo teamPojo = t.getTeamPojo();
        if (t.getTeamPojo().getTeamSize() == TeamSizeEnum.SINGLES) {
            if (t.getMembers().size() >= 1) {
                errors.reject("team.uniqueness", "This team already has the max number of members (1).");
                throw new Exception("This team already has the max number of members (1).");

            }
        }

        if (t.getTeamPojo().getTeamSize() == TeamSizeEnum.DOUBLES) {
            if (t.getMembers().size() >= 6) {
                errors.rejectValue("team.uniqueness", "This team already has the max number of members (4).");
                throw new Exception("This team already has the max number of members (6).");

            }
        }

        if (t.getTeamPojo().getTeamSize() == TeamSizeEnum.TEAM) {
            if (t.getMembers().size() >= 10) {
                errors.rejectValue("team.uniqueness", "This team already has the max number of members (8).");
                throw new Exception("This team already has the max number of members (10).");

            }
        }

        if (teamPojo.getTeamType() == TeamTypeEnum.XP) {
            List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(u.getUsername(), teamPojo.getGame(), teamPojo.getPlatform(), teamPojo.getTeamSize(), teamPojo.getTeamType(), u.getUserInfo().getRegion());
            if (teams.size() == 2) {
                errors.reject("team.uniqueness", "You are already a member of an XP team in the same ladder (" + teamPojo.getGame().getGameEnumDesc() + " - " + teamPojo.getPlatform().getPlatformEnumDesc() + " - " + teamPojo.getTeamSize().getTeamSizeEnumDesc() + " - " + teamPojo.getRegion().getRegionEnumDesc() + "). Max 1 team per ladder for XP teams. Max 3 teams per ladder for Cashout teams.");
                throw new Exception("You are already a member of 2 XP teams in the same ladder (" + teamPojo.getGame().getGameEnumDesc() + " - " + teamPojo.getPlatform().getPlatformEnumDesc() + " - " + teamPojo.getTeamSize().getTeamSizeEnumDesc() + " - " + teamPojo.getRegion().getRegionEnumDesc() + "). Max 2 teams per ladder for XP teams. Max 3 teams per ladder for Cashout teams.");

            }
        } else {
            List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(u.getUsername(), teamPojo.getGame(), teamPojo.getPlatform(), teamPojo.getTeamSize(), teamPojo.getTeamType(), u.getUserInfo().getRegion());
            if (teams.size() == 3) {
                errors.reject("team.uniqueness", "You are already a member of 3 cashout teams in the same ladder (" + teamPojo.getGame().getGameEnumDesc() + " - " + teamPojo.getPlatform().getPlatformEnumDesc() + " - " + teamPojo.getTeamSize().getTeamSizeEnumDesc() + " - " + teamPojo.getRegion().getRegionEnumDesc() + "). Max 3 teams per ladder for Cashout teams.  Max 1 team per ladder for XP teams. ");
                throw new Exception("You are already a member of 3 cashout teams in the same ladder (" + teamPojo.getGame().getGameEnumDesc() + " - " + teamPojo.getPlatform().getPlatformEnumDesc() + " - " + teamPojo.getTeamSize().getTeamSizeEnumDesc() + " - " + teamPojo.getRegion().getRegionEnumDesc() + "). Max 3 teams per ladder for Cashout teams.  Max 2 teams per ladder for XP teams. ");

            }
        }

    }

    public void userDeclineTeamInviteValidation(User u, Team t, Errors errors) throws Exception {

        if (t.getTeamPojo().getTeamStatus() == TeamStatusEnum.DISBANDED) {
            errors.reject("team.uniqueness", "This team has been disbanded.");
            throw new Exception("This team has been disbanded.");

        }
    }
}
