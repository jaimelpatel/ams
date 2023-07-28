/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.TeamInvite;
import com.ltlogic.db.entity.TeamPermissions;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.iw.IWTeam;
import com.ltlogic.db.entity.mwr.MWRTeam;
import com.ltlogic.db.entity.ww2.WW2Team;
import com.ltlogic.db.repository.MatchRepository;
import com.ltlogic.db.repository.TeamRepository;
import com.ltlogic.db.repository.TournamentRepository;
import com.ltlogic.db.repository.TournamentTeamRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.fe.helpers.Paginator;
import com.ltlogic.fe.models.form.TeamForm;
import com.ltlogic.fe.models.form.TeamFormValidator;
import com.ltlogic.fe.models.form.UserInfo;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.iws.TeamInviteIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.TeamInvitePojo;
import com.ltlogic.pojo.TeamPojo;
import com.ltlogic.service.springsecurity.TeamValidator;
import com.ltlogic.web.exception.CustomErrorException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Hoang
 */
@Controller
public class TeamsController {

    @Autowired
    private UserIWS userIWS;

    @Autowired
    private TeamIWS teamIWS;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private MatchRepository matchRepo;

    @Autowired
    private TournamentRepository tournamentRepo;

    @Autowired
    TeamFormValidator teamCreationFormValidator;

    @Autowired
    TeamValidator teamValidator;

    @Autowired
    TeamInviteIWS teamInviteIWS;

    @Autowired
    Paginator paginator;
    
    @Autowired
    TournamentTeamRepository tournamentTeamRepo;

    private static final Logger log = LoggerFactory.getLogger(TeamsController.class);

    @RequestMapping(value = "{username}/teams/", method = RequestMethod.GET)
    public String myTeams(@PathVariable String username, ModelMap model, Principal p) {

        if (p == null || (username != null && username.equalsIgnoreCase(p.getName()) == false)) {
            throw new CustomErrorException();
        }

        User user = userIWS.getUserByUsername(p.getName());
        List<Team> playoffTeams = new ArrayList<>();
        List<Team> cashoutTeams = new ArrayList<>();
        List<Team> tournamentTeams = new ArrayList<>();
        List<TeamInvite> pendingInvites = new ArrayList<>();

        if (user != null) {
            addUserAttributesToModel(model, user);
            playoffTeams = teamIWS.getAllTeamsOfUserByTeamType(user.getUsername(), TeamTypeEnum.XP);
            cashoutTeams = teamIWS.getAllTeamsOfUserByTeamType(user.getUsername(), TeamTypeEnum.CASH);
            //cashout teams can play tournaments
//            tournamentTeams = teamIWS.getAllTeamsOfUserByTeamType(user.getUsername(), TeamTypeEnum.TOURNAMENT);
            pendingInvites = teamInviteIWS.getPendingInvitesByUserPk(user.getPk(), 0);
        }

        model.addAttribute("playoffTeams", playoffTeams);
        model.addAttribute("cashoutTeams", cashoutTeams);
        model.addAttribute("tournamentTeams", tournamentTeams);
        model.addAttribute("pendingInvites", pendingInvites);

        return "teams/my-teams";

    }

    @RequestMapping(value = "teams/{teamId}/edit", method = RequestMethod.GET)
    public String editTeam(@ModelAttribute("uploadImageForm") Object uploadImageForm, @ModelAttribute("teamInvitePojo") TeamInvitePojo teamInvitePojo,
            BindingResult bindingResult, @PathVariable Long teamId, ModelMap model, Principal p) throws Exception {
        Team team = teamIWS.getTeamByPk(teamId);

        if (p == null) {
            throw new CustomErrorException();
        } else {
            User user = userIWS.getUserByUsername(p.getName());
            if (team.getTeamLeaderPk() != user.getPk()) {
                throw new CustomErrorException();
            }
        }
        User user = userIWS.getUserByUsername(p.getName());

        addUserAttributesToModel(model, user);
        List<User> teamMembers = userRepo.getAllUsersOnTeam(teamId);
        //Set<User> teamMembers = team.getMembers();
        if (team.getTeamPojo().getTeamType() == TeamTypeEnum.CASH && team.getTeamPojo().getGame() == GameEnum.COD_WW2) {
            model.addAttribute("isCashAndWW2", true);
        } else {
            model.addAttribute("isCashAndWW2", false);
        }

        if (team.getTeamPojo().getTeamType() == TeamTypeEnum.XP) {
            model.addAttribute("isXpTeam", true);
        } else {
            model.addAttribute("isXpTeam", false);
        }
        
        TournamentTeam tournamentTeamPending = tournamentTeamRepo.getAllTournamentTeamsForTeamByTournamentPendingStatus(team.getPk());
        if (tournamentTeamPending != null) {
            model.addAttribute("tournamentPk", tournamentTeamPending.getTournament().getPk());
            model.addAttribute("isTeamInTournament", true);
        }else{
            model.addAttribute("isTeamInTournament", false);
        }
        
        model.addAttribute("team", team);
        model.addAttribute("teamMembers", teamMembers);
        return "teams/edit";
    }

    @RequestMapping(value = "/teams/{teamId}", method = RequestMethod.GET)
    public String myTeams(@PathVariable long teamId, ModelMap model, Principal p,
            @RequestParam(value = "match_page", required = false) Integer matchPage,
            @RequestParam(value = "tournament_page", required = false) Integer tournamentPage) {

        Integer matchCurrentPage = 1;
        Integer tournamentCurrentPage = 1;

        if (matchPage != null && matchPage > 1) {
            matchCurrentPage = matchPage;
        }

        if (tournamentPage != null && tournamentPage > 1) {
            tournamentCurrentPage = tournamentPage;
        }

        try {
            if (p != null) {
                User user = userIWS.getUserByUsername(p.getName());
                model.addAttribute("user", user);
            }

            Team t = teamIWS.getTeamByPk(teamId);
            if (p != null) {
                User user = userRepo.findByUsername(p.getName());
                if (t.getTeamLeaderPk() == user.getPk()) {
                    model.addAttribute("isTeamLeader", true);
                } else if (t.getMembers().contains(user)) {
                    model.addAttribute("isMember", true);
                }
            }
            if (t.getTeamPojo().getGame() == GameEnum.COD_MWR) {
                MWRTeam team = teamRepo.getMWRTeamByTeamPk(teamId);
                if (team.getTeamPojo().getTeamType() == TeamTypeEnum.XP) {
                    model.addAttribute("isXpTeam", true);
                } else {
                    model.addAttribute("isXpTeam", false);
                }
                model.addAttribute("team", team);
            } else if (t.getTeamPojo().getGame() == GameEnum.COD_IW) {
                IWTeam team = teamRepo.getIWTeamByTeamPk(teamId);
                if (team.getTeamPojo().getTeamType() == TeamTypeEnum.XP) {
                    model.addAttribute("isXpTeam", true);
                } else {
                    model.addAttribute("isXpTeam", false);
                }
                model.addAttribute("team", team);
            } else if (t.getTeamPojo().getGame() == GameEnum.COD_WW2) {
                WW2Team team = teamRepo.getWW2TeamByTeamPk(teamId);
                if (team.getTeamPojo().getTeamType() == TeamTypeEnum.XP) {
                    model.addAttribute("isXpTeam", true);
                } else {
                    model.addAttribute("isXpTeam", false);
                }
                model.addAttribute("team", team);
            }

            List<User> users = userRepo.getAllUsersOnTeam(teamId);
            List<Match> matches = matchRepo.findMatchesByTeamPkForPaginate(teamId, matchCurrentPage - 1);
            Integer totalMatches = matchRepo.findMatchesByTeamPk(teamId).size();

            if (t.getTeamPojo().getTeamType() == TeamTypeEnum.CASH) {
                List<Tournament> tournaments = tournamentRepo.getTournamentsByTeamPkForPaginate(t.getPk(), tournamentCurrentPage - 1);
                Integer totalTournaments = tournamentRepo.getTournamentsByTeamPk(t.getPk());
                model.addAttribute("tournaments", tournaments);
                paginator.setPagination("tournament", tournamentCurrentPage, totalTournaments, model);
            }

            paginator.setPagination("match", matchCurrentPage, totalMatches, model);

            int matchesWon = t.getMatchesWon();
            int matchesLost = t.getMatchesLost();

            int matchesPlayedTotal = matchesWon + matchesLost;
            BigDecimal winPercentage = null;
            if (matchesPlayedTotal != 0) {
                BigDecimal winDecimal = new BigDecimal(matchesWon).divide(new BigDecimal(matchesPlayedTotal), 2, RoundingMode.HALF_UP);
                winPercentage = winDecimal.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
            } else {
                winPercentage = new BigDecimal(100.00).setScale(0, RoundingMode.HALF_UP);
            }

            model.addAttribute("winPercentage", winPercentage);
            model.addAttribute("matches", matches);
            model.addAttribute("teamMembers", users);
            return "teams/view";
        } catch (Exception ex) {
            ex.printStackTrace();
            model.addAttribute("error", ex.getMessage());
            return "teams/view";
        }
    }

    @RequestMapping(value = "/teams/create", method = RequestMethod.GET)
    public String createTeam(@ModelAttribute("teamCreationForm") TeamForm teamCreationForm, ModelMap model, Principal p) {
        try {
            User user = userIWS.getUserByUsername(p.getName());
            addUserAttributesToModel(model, user);

            return "teams/create-team";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "teams/create-team";
        }

    }

    @RequestMapping(value = "/teams/create", method = RequestMethod.POST)
    public String createNewTeam(@Valid @ModelAttribute("teamCreationForm") TeamForm teamCreationForm, BindingResult bindingResult, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {

        try {
            String username = p.getName();
            User user = userIWS.getUserByUsername(username);
            teamCreationForm.setUsername(username);
            TeamPojo teamPojo = TeamForm.toTeamPojo(teamCreationForm, user);
            teamPojo.setRegion(user.getUserInfo().getRegion());

            teamCreationFormValidator.validate(teamPojo, bindingResult);

            teamValidator.inviteUserToTeamValidationOnTeamCreation(teamPojo, bindingResult);

            if (bindingResult.hasErrors()) {
                addUserAttributesToModel(model, user);
                return "teams/create-team";
            }
            Team newTeam = teamIWS.createTeam(teamPojo, username);

            for (String un : teamPojo.getUsersToInviteToTeam()) {
                teamInviteIWS.inviteUserToTeam(username, un.toLowerCase(), newTeam.getPk());
            }
            redirectAttributes.addFlashAttribute("message", "You have created a new team.");

            return "redirect:/" + username + "/teams/";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "teams/create-team";
        }
    }

    @RequestMapping(value = "/teams/invite/{teamPk}", method = RequestMethod.POST)
    public String teamInvite(@Valid @ModelAttribute("teamInvitePojo") TeamInvitePojo teamInvitePojo, @ModelAttribute("uploadImageForm") Object uploadImageForm, @PathVariable long teamPk,
            BindingResult bindingResult, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {

        String senderUsername = p.getName();
        User user = userIWS.getUserByUsername(p.getName());
        addUserAttributesToModel(model, user);

        Team team = teamIWS.getTeamByPk(teamPk);
        List<Team> allTeamsOfUser = teamIWS.getAllTeamsOfUser(senderUsername);
        List<User> teamMembers = userRepo.getAllUsersOnTeam(teamPk);

        if (team == null || allTeamsOfUser.contains(team) == false) {
            throw new CustomErrorException();
        }

        try {
            String usernameOfUserBeingInvited = teamInvitePojo.getUsernameOfUserBeingInvited();
            teamValidator.inviteUserToTeamValidation(usernameOfUserBeingInvited, teamPk, bindingResult);
            if (bindingResult.hasErrors()) {
                if (team.getTeamPojo().getTeamType() == TeamTypeEnum.CASH && team.getTeamPojo().getGame() == GameEnum.COD_WW2) {
                    model.addAttribute("isCashAndWW2", true);
                } else {
                    model.addAttribute("isCashAndWW2", false);
                }
                model.addAttribute("team", team);
                model.addAttribute("teamMembers", teamMembers);
                return "teams/edit";
            }

            teamInviteIWS.inviteUserToTeam(senderUsername, usernameOfUserBeingInvited.toLowerCase(), teamPk);
            redirectAttributes.addFlashAttribute("message", usernameOfUserBeingInvited + " has been invited.");
            return "redirect:/teams/" + teamPk + "/edit";
        } catch (Exception ex) {
            ex.printStackTrace();
            model.addAttribute("team", team);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("teamMembers", teamMembers);
            return "teams/edit";
        }
    }

    @RequestMapping(value = "/teams/{teamPk}/disband", method = RequestMethod.GET)
    public String teamInvite(@PathVariable Long teamPk, Principal p, RedirectAttributes redirectAttributes) {
        if (p == null) {
            throw new CustomErrorException();
        }

        String username = p.getName();

        User user = userIWS.getUserByUsername(username);
        Team team = teamIWS.getTeamByPk(teamPk);

        if (team.getTeamLeaderPk() != user.getPk()) {
            throw new CustomErrorException();
        }
        String teamName = team.getTeamPojo().getTeamName();
        TeamTypeEnum teamType = team.getTeamPojo().getTeamType();
        List<Team> allTeamsOfUser = teamIWS.getAllTeamsOfUser(username);

        if (team == null || allTeamsOfUser.contains(team) == false) {
            throw new CustomErrorException();
        }

        if (teamIWS.isTeamInMatchOrTournament(team)) {
            redirectAttributes.addFlashAttribute("messageFailure", "You cannot disband a team that is in a current match or tournament.");
            return "redirect:/teams/" + teamPk + "/edit";
        }

        try {
            teamIWS.disband(teamPk, user.getPk());
            if (teamType == TeamTypeEnum.XP) {
                redirectAttributes.addFlashAttribute("message", "XP Team '" + teamName + "' has been disbanded.");
            } else if (teamType == TeamTypeEnum.CASH) {
                redirectAttributes.addFlashAttribute("message", "Cashout Team '" + teamName + "' has been disbanded.");
            }

            return "redirect:/" + username + "/teams/";
        } catch (Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error disbanding team. " + ex.getMessage());
            return "redirect:/" + username + "/teams/";
        }
    }

    @RequestMapping(value = "/teams/{teamPk}/xp-on", method = RequestMethod.GET)
    public String turnXpGainOn(@PathVariable Long teamPk, Principal p, RedirectAttributes redirectAttributes) {
        if (p == null) {
            throw new CustomErrorException();
        }

        String username = p.getName();

        User user = userIWS.getUserByUsername(username);
        Team team = teamIWS.getTeamByPk(teamPk);

        if (team.getTeamLeaderPk() != user.getPk()) {
            throw new CustomErrorException();
        }
        String teamName = team.getTeamPojo().getTeamName();
        TeamTypeEnum teamType = team.getTeamPojo().getTeamType();
        List<Team> allTeamsOfUser = teamIWS.getAllTeamsOfUser(username);

        if (team == null || allTeamsOfUser.contains(team) == false) {
            throw new CustomErrorException();
        }

        if (teamIWS.isTeamInMatchOrTournament(team)) {
            redirectAttributes.addFlashAttribute("messageFailure", "You cannot edit this setting while the team is in a current match or tournament.");
            return "redirect:/teams/" + teamPk + "/edit";
        }

        try {
            teamIWS.turnXpGainOn(teamPk);
            redirectAttributes.addFlashAttribute("message", "You have turned on xp gain/loss for this cashout team.");

            return "redirect:/teams/" + teamPk + "/edit";
        } catch (Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error turning xp gain on. " + ex.getMessage());
            return "redirect:/teams/" + teamPk + "/edit";
        }
    }

    @RequestMapping(value = "/teams/{teamPk}/xp-off", method = RequestMethod.GET)
    public String turnXpGainOff(@PathVariable Long teamPk, Principal p, RedirectAttributes redirectAttributes) {
        if (p == null) {
            throw new CustomErrorException();
        }

        String username = p.getName();

        User user = userIWS.getUserByUsername(username);
        Team team = teamIWS.getTeamByPk(teamPk);

        if (team.getTeamLeaderPk() != user.getPk()) {
            throw new CustomErrorException();
        }
        String teamName = team.getTeamPojo().getTeamName();
        TeamTypeEnum teamType = team.getTeamPojo().getTeamType();
        List<Team> allTeamsOfUser = teamIWS.getAllTeamsOfUser(username);

        if (team == null || allTeamsOfUser.contains(team) == false) {
            throw new CustomErrorException();
        }

        if (teamIWS.isTeamInMatchOrTournament(team)) {
            redirectAttributes.addFlashAttribute("messageFailure", "You cannot edit this setting while the team is in a current match or tournament.");
            return "redirect:/teams/" + teamPk + "/edit";
        }

        try {
            teamIWS.turnXpGainOff(teamPk);
            redirectAttributes.addFlashAttribute("message", "You have turned off xp gain/loss for this cashout team.");

            return "redirect:/teams/" + teamPk + "/edit";
        } catch (Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error turning xp gain off. " + ex.getMessage());
            return "redirect:/teams/" + teamPk + "/edit";
        }
    }

    @RequestMapping(value = "/teams/{teamPk}/leave", method = RequestMethod.GET)
    public String teamLeave(@PathVariable Long teamPk, Principal p, RedirectAttributes redirectAttributes) {
        if (p == null) {
            throw new CustomErrorException();
        }

        String username = p.getName();

        User user = userIWS.getUserByUsername(username);
        Team team = teamIWS.getTeamByPk(teamPk);

        String teamName = team.getTeamPojo().getTeamName();
        List<Team> allTeamsOfUser = teamIWS.getAllTeamsOfUser(username);

        if (team == null || allTeamsOfUser.contains(team) == false) {
            throw new CustomErrorException();
        }

        if (teamIWS.isUserOnTeamInMatchOrTournament(team, user)) {
            redirectAttributes.addFlashAttribute("messageFailure", "You cannot leave a team you are in a current match or tournament with.");
            return "redirect:/teams/" + teamPk;
        }

        try {
            teamIWS.removeUserFromTeam(user.getPk(), teamPk);
            redirectAttributes.addFlashAttribute("message", "You have left team '" + teamName + "'");

            return "redirect:/" + username + "/teams/";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Error leaving team. " + ex.getMessage());
            return "redirect:/" + username + "/teams/";
        }
    }

    @RequestMapping(value = "/teams/{teamPk}/{username}/remove", method = RequestMethod.GET)
    public String userKick(@PathVariable Long teamPk, @PathVariable String username, Principal p, RedirectAttributes redirectAttributes) {
        if (p == null) {
            throw new CustomErrorException();
        }

        User user = userIWS.getUserByUsername(username);
        Team team = teamIWS.getTeamByPk(teamPk);
        User userLoggedIn = userIWS.getUserByUsername(p.getName());
        if (team.getTeamLeaderPk() != userLoggedIn.getPk()) {
            throw new CustomErrorException();
        }
        List<Team> allTeamsOfUser = teamIWS.getAllTeamsOfUser(username);

        if (team == null || allTeamsOfUser.contains(team) == false) {
            throw new CustomErrorException();
        }

        if (teamIWS.isTeamInMatchOrTournament(team)) {
            redirectAttributes.addFlashAttribute("messageFailure", "You cannot remove any players from a team that is in a current match or tournament.");
            return "redirect:/teams/" + teamPk + "/edit";
        }

        try {
            teamIWS.removeUserFromTeam(user.getPk(), teamPk);
            redirectAttributes.addFlashAttribute("message", "You have removed '" + username + "'");
            return "redirect:/teams/" + teamPk + "/edit";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Error removing member. " + ex.getMessage());
            return "redirect:/teams/" + teamPk + "/edit";
        }
    }

    @RequestMapping(value = "/teams/{teamPk}/{username}/promote", method = RequestMethod.GET)
    public String userPromote(@PathVariable Long teamPk, @PathVariable String username, Principal p, RedirectAttributes redirectAttributes) {
        if (p == null) {
            throw new CustomErrorException();
        }

        User user = userIWS.getUserByUsername(username);
        Team team = teamIWS.getTeamByPk(teamPk);

        User userLoggedIn = userIWS.getUserByUsername(p.getName());
        if (team.getTeamLeaderPk() != userLoggedIn.getPk()) {
            throw new CustomErrorException();
        }
        List<Team> allTeamsOfUser = teamIWS.getAllTeamsOfUser(username);

        if (team == null || allTeamsOfUser.contains(team) == false) {
            throw new CustomErrorException();
        }

        if (teamIWS.isTeamInMatchOrTournament(team)) {
            redirectAttributes.addFlashAttribute("messageFailure", "You cannot change leadership of a team that is in a current match or tournament.");
            return "redirect:/teams/" + teamPk + "/edit";
        }

        try {
            teamIWS.promoteTeammateToLeader(user.getPk(), team.getPk());
            redirectAttributes.addFlashAttribute("message", "You have promoted '" + username + "' to leader.");
            return "redirect:/teams/" + teamPk;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Error promoting member to leader. " + ex.getMessage());
            return "redirect:/teams/" + teamPk + "/edit";
        }
    }

    @RequestMapping(value = "/teams/{teamPk}/getAllUsersOnTeam", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<UserInfo> getAllUsersOnTeam(@PathVariable Long teamPk, Principal p) {
        List<UserInfo> users = new ArrayList<>();
        List<User> userEntities = userIWS.getAllUsersOnTeam(teamPk);

        for (User u : userEntities) {
            UserInfo userInfo = new UserInfo();

            userInfo.setPk(u.getPk());
            userInfo.setUsername(u.getUsername());

            users.add(userInfo);
        }

        return users;
    }

    private void addUserAttributesToModel(ModelMap model, User user) {
        if (model != null && user != null) {
            model.addAttribute("un", user.getUsername());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("userInfo", user);
            model.addAttribute("myMatches", user.getMatches());
        }
    }

}
