/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.constants.TournamentStatusEnum;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentInvite;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.entity.User;
import com.ltlogic.fe.models.form.TeamForm;
import com.ltlogic.fe.models.form.TeamFormValidator;
import com.ltlogic.fe.models.form.TournamentFormValidator;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.iws.TeamInviteIWS;
import com.ltlogic.iws.TournamentServiceIWS;
import com.ltlogic.iws.TournamentTeamIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.TeamInvitePojo;
import com.ltlogic.pojo.TeamPojo;
import com.ltlogic.pojo.TournamentInvitePojo;
import com.ltlogic.service.core.TournamentInviteService;
import com.ltlogic.service.core.TournamentService;
import com.ltlogic.service.springsecurity.TeamValidator;
import com.ltlogic.service.springsecurity.UserValidator;
import com.ltlogic.web.exception.CustomErrorException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Valid;
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
 * @author raymond
 */
@Controller
public class TournamentsController {

    @Autowired
    TournamentServiceIWS tournamentIWS;

    @Autowired
    TournamentInviteService tournamentInviteService;

    @Autowired
    TeamIWS teamIWS;

    @Autowired
    UserIWS userIWS;

    @Autowired
    TeamValidator teamValidator;

    @Autowired
    UserValidator userValidator;

    @Autowired
    TeamFormValidator teamCreationFormValidator;

    @Autowired
    TeamInviteIWS teamInviteIWS;

    @Autowired
    TournamentFormValidator tournamentFormValidator;

    @Autowired
    TournamentTeamIWS tournamentTeamIWS;
    
    @Autowired
    TournamentService tournamentService;
    
    @RequestMapping(value = "/tournaments", method = RequestMethod.GET)
    public String index(@RequestParam(value = "game", required = false) Integer game,
            @RequestParam(value = "platform", required = false) Integer platform, ModelMap model, Principal p) {

        GameEnum gameEnum = null;
        if (game != null) {
            gameEnum = GameEnum.getGameEnumById(game);
        }

        PlatformEnum platformEnum = null;
        if (platform != null) {
            platformEnum = PlatformEnum.getPlatformEnumById(platform);
        }
        if (p != null) {
            String username = p.getName();
            User user = userIWS.getUserByUsername(username);
            model.addAttribute("user", user);
        }

        List<Tournament> tournaments = tournamentIWS.getAllTournamentsByGameAndPlatform(gameEnum, platformEnum);
        model.addAttribute("tournaments", tournaments);
        return "tournaments/index";
    }

    @RequestMapping(value = "/tournaments/active", method = RequestMethod.GET)
    public String index2(ModelMap model, Principal p) {

        if (p != null) {
            String username = p.getName();
            User user = userIWS.getUserByUsername(username);
            model.addAttribute("user", user);
        }

        List<Tournament> tournaments = tournamentIWS.getAllTournamentsByStatus(TournamentStatusEnum.ACTIVE);
        model.addAttribute("tournaments", tournaments);
        return "tournaments/index-active";
    }

    @RequestMapping(value = "/tournaments/upcoming", method = RequestMethod.GET)
    public String index5(ModelMap model, Principal p) {
        return "tournaments/index-upcoming";
    }

    @RequestMapping(value = "/tournaments/closed", method = RequestMethod.GET)
    public String index3(ModelMap model, Principal p) {

        if (p != null) {
            String username = p.getName();
            User user = userIWS.getUserByUsername(username);
            model.addAttribute("user", user);
        }

        List<Tournament> tournaments = tournamentIWS.getAllTournamentsByStatus(TournamentStatusEnum.ENDED);
        model.addAttribute("tournaments", tournaments);
        return "tournaments/index-closed";
    }

    @RequestMapping(value = "/tournament/{pk}/invite-users", method = RequestMethod.GET)
    public String inviteUserToTournament(@Valid @ModelAttribute("teamInvitePojo") TeamInvitePojo teamInvitePojo, @PathVariable Long pk, BindingResult bindingResult, ModelMap model, Principal p) {
        Tournament tournament = tournamentIWS.findByPk(pk);

        if (p != null) {
            User user = userIWS.getUserByUsername(p.getName());
            Tournament isUserInTournament = tournamentIWS.findTournamentByTournamentIdAndUserPk(tournament.getTournamentId(), user.getPk());

            if (tournament.getTournamentInfo().getTournamentStatus() == TournamentStatusEnum.PENDING) {
                if (isUserInTournament == null) {
                    //show 'join' tournament button
                    model.addAttribute("canJoinTournament", true);
                } else {
                    model.addAttribute("canJoinTournament", false);
                    //show 'leave' tournament button
                    model.addAttribute("canLeaveTournament", true);
                }
            }
            if (tournament.getUsersInTournament().contains(user) && tournament.getTournamentInfo().getTournamentStatus() == TournamentStatusEnum.PENDING) {
                Team team = tournamentTeamIWS.getTournamentTeamByTournamentPkAndUserPk(tournament.getPk(), user.getPk()).getTeam();
                if (user.getPk() == team.getTeamLeaderPk()) {
                    model.addAttribute("isTeamLeader", true);
                    model.addAttribute("team", team);
                }
            }
            TournamentTeam tt = tournamentTeamIWS.getTournamentTeamByTournamentPkAndUserPk(tournament.getPk(), user.getPk());
            List<TournamentInvitePojo> tip = new ArrayList<>();
            List<TournamentInvite> tiList = tournamentInviteService.finAllTournamentInvitesByTournamentTeamPkNotCancelled(tt.getPk());
            for (TournamentInvite ti : tiList) {
                TournamentInvitePojo pojo = new TournamentInvitePojo();
                pojo.setUsername(ti.getUser().getUsername());
                pojo.setTournamentInviteStatus(ti.getInviteEnum().getInvitesDesc());
                tip.add(pojo);
            }
            model.addAttribute("statusList", tip);
            String numOfPlayers = Integer.toString(tt.getNumOfPlayers());
            if (tt.isAllPlayersAccepted()) {
                model.addAttribute("fraction", numOfPlayers + "/" + numOfPlayers);
                model.addAttribute("eligible", true);
            } else {
                int numberOfPlayersAccepted = tt.getNumOfPlayers() - tt.getNumOfPlayersNeedingToAccept();
                model.addAttribute("fraction", Integer.toString(numberOfPlayersAccepted) + "/" + numOfPlayers);
                model.addAttribute("eligible", false);
            }

        }

        model.addAttribute("tournament", tournament);
        return "tournaments/invite-users";
    }

    @RequestMapping(value = "/{username}/tournaments", method = RequestMethod.GET)
    public String getTournamentsByUserPk(@PathVariable String username, ModelMap model, Principal p) {

        if (p == null || (username != null && username.equalsIgnoreCase(p.getName()) == false)) {
            throw new CustomErrorException();
        }
        String uname = p.getName();
        User user = userIWS.getUserByUsername(uname);
        List<Tournament> tournaments = tournamentIWS.getTournamentsByUserPk(user.getPk());

        model.addAttribute("user", user);
        model.addAttribute("tournaments", tournaments);
        return "tournaments/my-tournaments";
    }

    @RequestMapping(value = "/tournaments/{pk}", method = RequestMethod.GET)
    public String tournamentDetails(ModelMap model, Principal p, @PathVariable Long pk) {

        Tournament tournament = tournamentIWS.findByPk(pk);

        if (tournament == null) {
            throw new CustomErrorException();
        }

        //default joinable for everyone
        if (tournament.getTournamentInfo().getTournamentStatus() == TournamentStatusEnum.PENDING) {
            model.addAttribute("canJoinTournament", true);
        }

        if (p != null) {
            User user = userIWS.getUserByUsername(p.getName());

            //check if user is participating tournament
            Tournament isUserInTournament = tournamentIWS.findTournamentByTournamentIdAndUserPk(tournament.getTournamentId(), user.getPk());

            if (tournament.getTournamentInfo().getTournamentStatus() == TournamentStatusEnum.PENDING) {
                if (isUserInTournament == null) {
                    //show 'join' tournament button
                    model.addAttribute("canJoinTournament", true);
                } else {
                    model.addAttribute("canJoinTournament", false);
                    //show 'leave' tournament button
                    model.addAttribute("canLeaveTournament", true);
                }
            }

            if (tournament.getUsersInTournament().contains(user) && tournament.getTournamentInfo().getTournamentStatus() == TournamentStatusEnum.PENDING) {
                Team team = tournamentTeamIWS.getTournamentTeamByTournamentPkAndUserPk(tournament.getPk(), user.getPk()).getTeam();
                if (user.getPk() == team.getTeamLeaderPk()) {
                    model.addAttribute("isTeamLeader", true);
                }
            }
        }

        if (tournament.getTournamentInfo().getTournamentStatus() == TournamentStatusEnum.ENDED) {
            long teamWinnerPk = tournament.getListOfTeamsRemainingInTournament().get(0);
            Team tournamentWinner = teamIWS.getTeamByPk(teamWinnerPk);
            model.addAttribute("tournamentWinner", tournamentWinner);
        }

        List<TournamentTeam> tournamentTeams = tournamentTeamIWS.getAllTournamentTeamsForTournament(pk);
        List<TournamentTeam> eligibleTournamentTeams = tournamentTeamIWS.getAllEligibleTournamentTeamsForTournament(pk);

        model.addAttribute("tournament", tournament);
        model.addAttribute("tournamentTeams", tournamentTeams);
        model.addAttribute("eligibleTournamentTeams", eligibleTournamentTeams);
        return "tournaments/tournament-details";
    }

    @RequestMapping(value = "/tournaments/{pk}/bracket", method = RequestMethod.GET)
    public String tournamentBracket(ModelMap model, Principal p, @PathVariable Long pk) {

        Tournament tournament = tournamentIWS.findByPk(pk);

        if (tournament == null) {
            throw new CustomErrorException();
        }

        if (p != null) {
            User user = userIWS.getUserByUsername(p.getName());

            //check if user is participating tournament
            Tournament isUserInTournament = tournamentIWS.findTournamentByTournamentIdAndUserPk(tournament.getTournamentId(), user.getPk());

            if (tournament.getTournamentInfo().getTournamentStatus() == TournamentStatusEnum.PENDING) {
                if (isUserInTournament == null) {
                    //show 'join' tournament button
                    model.addAttribute("canJoinTournament", true);
                } else {
                    model.addAttribute("canJoinTournament", false);
                    //show 'leave' tournament button
                    model.addAttribute("canLeaveTournament", true);
                }
            }

            if (tournament.getUsersInTournament().contains(user) && tournament.getTournamentInfo().getTournamentStatus() == TournamentStatusEnum.PENDING) {
                Team team = tournamentTeamIWS.getTournamentTeamByTournamentPkAndUserPk(tournament.getPk(), user.getPk()).getTeam();
                if (user.getPk() == team.getTeamLeaderPk()) {
                    model.addAttribute("isTeamLeader", true);
                }
            }
        }

        if (tournament.getTournamentInfo().getTournamentStatus() == TournamentStatusEnum.ENDED) {
            long teamWinnerPk = tournament.getListOfTeamsRemainingInTournament().get(0);
            Team tournamentWinner = teamIWS.getTeamByPk(teamWinnerPk);
            model.addAttribute("tournamentWinner", tournamentWinner);
        }

        tournament.getTournamentResponse().getTournamentPojo().getUrl();
        model.addAttribute("tournament", tournament);
        model.addAttribute("bracketUrl", tournament.getTournamentResponse().getTournamentPojo().getUrl());
        model.addAttribute("bracketFullUrl", tournament.getTournamentResponse().getTournamentPojo().getFull_challonge_url());
        return "tournaments/tournament-bracket";
    }

    @RequestMapping(value = "/tournaments/{pk}/leave", method = RequestMethod.GET)
    public String tournamentLeave(ModelMap model, Principal p, @PathVariable Long pk, RedirectAttributes redirectAttributes) {

        Tournament tournament = tournamentIWS.findByPk(pk);

        if (tournament == null) {
            throw new CustomErrorException();
        }
        String username = p.getName();
        User user = userIWS.getUserByUsername(username);

        if (DateTimeUtil.getDefaultLocalDateTimeNow().isAfter(tournament.getTournamentInfo().getScheduledTournamentTime())) {
            redirectAttributes.addFlashAttribute("error", "You cannot leave this tournament because it has already started.");
            return "redirect:/tournaments/" + tournament.getPk();
        }

        TournamentTeam tournamentTeam = tournamentTeamIWS.getTournamentTeamByTournamentPkAndUserPk(tournament.getPk(), user.getPk());
        if (tournamentTeam != null) {
            try {
                if (!tournament.getUsersInTournament().contains(user)) {
                    throw new Exception("You are not in this tournament.");
                }
                tournamentIWS.leaveTournament(user, tournamentTeam);
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("error", ex.getMessage());
                //tournament.getTournamentResponse().getTournamentPojo().getUrl();
                model.addAttribute("tournament", tournament);
                model.addAttribute("bracketUrl", tournament.getTournamentResponse().getTournamentPojo().getUrl());
                model.addAttribute("bracketFullUrl", tournament.getTournamentResponse().getTournamentPojo().getFull_challonge_url());
                return "redirect:/tournaments/" + tournament.getPk();
            }
        }
        redirectAttributes.addFlashAttribute("message", "You have left this tournament.");
        tournament.getTournamentResponse().getTournamentPojo().getUrl();
        model.addAttribute("tournament", tournament);
        model.addAttribute("bracketUrl", tournament.getTournamentResponse().getTournamentPojo().getUrl());
        model.addAttribute("bracketFullUrl", tournament.getTournamentResponse().getTournamentPojo().getFull_challonge_url());
        return "redirect:/tournaments/" + tournament.getPk();
    }

    @RequestMapping(value = "/tournaments/{pk}/team/create", method = RequestMethod.GET)
    public String createTournamentTeam(@ModelAttribute("teamCreationForm") TeamForm teamCreationForm, ModelMap model, Principal p, @PathVariable Long pk) {

        try {
            Tournament tournament = tournamentIWS.findByPk(pk);

            if (tournament == null) {
                throw new CustomErrorException();
            }

            teamCreationForm.setGame(tournament.getTournamentInfo().getGameEnum().ordinal());
            teamCreationForm.setPlatform(tournament.getTournamentInfo().getPlatform().ordinal());
            teamCreationForm.setTeamSize(tournament.getTournamentInfo().getTeamSizeEnum().ordinal());
            teamCreationForm.setTeamType(TeamTypeEnum.CASH.ordinal());

            User user = userIWS.getUserByUsername(p.getName());
            addUserAttributesToModel(model, user);

            model.addAttribute("pk", pk);
            return "tournaments/team-create";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("pk", pk);
            return "tournaments/team-create";
        }
    }

    @RequestMapping(value = "/tournaments/{pk}/team/create", method = RequestMethod.POST)
    public String saveTournamentTeam(@Valid @ModelAttribute("teamCreationForm") TeamForm teamCreationForm, BindingResult bindingResult, @ModelAttribute("createTournamentForm") Object createTournamentForm, ModelMap model,
            Principal p, @PathVariable Long pk, RedirectAttributes redirectAttributes) {
        String username = p.getName();
        try {

            User user = userIWS.getUserByUsername(username);
            teamCreationForm.setUsername(username);
            TeamPojo teamPojo = TeamForm.toTeamPojo(teamCreationForm, user);
            Tournament tournament = tournamentIWS.findByPk(pk);
            System.out.println("TEAMNAME: " + teamPojo.getTeamName());
            teamCreationFormValidator.validate(teamPojo, bindingResult);

            //teamValidator.inviteUserToTeamValidationOnTeamCreation(teamPojo, bindingResult);
            if (bindingResult.hasErrors()) {
                addUserAttributesToModel(model, user);
                model.addAttribute("pk", pk);
                return "tournaments/team-create";
            }
            tournamentService.test(teamPojo, username, tournament, user, bindingResult);

            redirectAttributes.addFlashAttribute("message", "You have created a cashout team and joined the tournament. You may now invite users below to become eligible for play.");

            return "redirect:/tournament/" + pk + "/invite-users";
        } catch (Exception ex) {
//            Tournament tournament = tournamentIWS.findByPk(pk);
//            User user = userIWS.getUserByUsername(username);
//            List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(
//                    user.getUsername(), tournament.getTournamentInfo().getGameEnum(),
//                    tournament.getTournamentInfo().getPlatform(),
//                    tournament.getTournamentInfo().getTeamSizeEnum(),
//                    TeamTypeEnum.CASH, user.getUserInfo().getRegion());
//            model.addAttribute("teams", teams);
//            model.addAttribute("tournament", tournament);
            System.out.println("INSIDE EXCEPTION------------- : " + ex.getMessage());
            model.addAttribute("pk", pk);
            model.addAttribute("error", ex.getMessage());
            //model.addAttribute("message", "You have successfully created a cashout team. Please select it below and join the tournament.");
            return "tournaments/team-create";
        }
    }


    @RequestMapping(value = "/tournaments/{pk}/join", method = RequestMethod.GET)
    public String joinTournament(@ModelAttribute("createTournamentForm") Object createTournamentForm, @ModelAttribute("teamCreationForm") TeamForm teamCreationForm, ModelMap model, Principal p, @PathVariable Long pk) {

        Tournament tournament = tournamentIWS.findByPk(pk);

        if (tournament == null) {
            throw new CustomErrorException();
        }

        User user = userIWS.getUserByUsername(p.getName());

        List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(
                user.getUsername(), tournament.getTournamentInfo().getGameEnum(),
                tournament.getTournamentInfo().getPlatform(),
                tournament.getTournamentInfo().getTeamSizeEnum(),
                TeamTypeEnum.CASH, user.getUserInfo().getRegion());

        //No Tournament Team
        if (teams != null && teams.isEmpty()) {
            return "redirect:/tournaments/" + pk + "/team/create";
        }

        model.addAttribute("teams", teams);
        model.addAttribute("tournament", tournament);
        model.addAttribute("usersInTournament", new ArrayList<>());
        return "tournaments/join-tournament";
    }

    //need to add teamPk of team user is joining tournament with, the way we doing it in method wont work
    @RequestMapping(value = "/tournaments/{tournamentPk}/{teamPk}/join", method = RequestMethod.POST)
    public String createTournament(@ModelAttribute("createTournamentForm") Object createTournamentForm, BindingResult bindingResult,
            ModelMap model, @RequestParam(value = "usersInTournament", required = false) List<Integer> usersInTournament, Principal p, @PathVariable Long tournamentPk, @PathVariable Long teamPk,
            RedirectAttributes redirectAttributes) {
        System.out.println("users: " + usersInTournament);
        User user = userIWS.getUserByUsername(p.getName());
        Tournament tournament = tournamentIWS.findByPk(tournamentPk);
        Team team = teamIWS.getTeamByPk(teamPk);

        if (tournament == null || team == null) {
            throw new CustomErrorException();
        }

        List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(
                user.getUsername(), tournament.getTournamentInfo().getGameEnum(),
                tournament.getTournamentInfo().getPlatform(),
                tournament.getTournamentInfo().getTeamSizeEnum(),
                TeamTypeEnum.CASH, user.getUserInfo().getRegion());

        if (p != null) {
            if (!hasPermission(teamPk, p.getName())) {
                model.addAttribute("tournament", tournament);
                model.addAttribute("usersInTournament", usersInTournament);
                model.addAttribute("teams", teams);
                model.addAttribute("error", "You do not have permissions to join a tournament for this team.");
                return "tournaments/join-tournament";
            }
        }

        List<User> usersPlayigInTournament = getUsersByPks(usersInTournament);
        tournamentFormValidator.validateJoinTournament(tournament, usersPlayigInTournament, team, user, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("teams", teams);
            model.addAttribute("tournament", tournament);
            model.addAttribute("usersInTournament", usersInTournament);
            return "tournaments/join-tournament";
        }

        try {
            tournamentIWS.joinTournament(team, tournament, usersPlayigInTournament, user);
            redirectAttributes.addFlashAttribute("message", "You have joined this tournament.");
            return "redirect:/tournaments/" + tournamentPk;
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("team", team);
            model.addAttribute("tournament", tournament);
            model.addAttribute("usersInTournament", new ArrayList<>());
            return "tournaments/join-tournament";
        }
    }

    private void addUserAttributesToModel(ModelMap model, User user) {
        if (model != null && user != null) {
            model.addAttribute("un", user.getUsername());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("userInfo", user);
            model.addAttribute("myMatches", user.getMatches());
        }
    }

    private List<User> getUsersByPks(List<Integer> usersInTournament) {
        List<User> usersPlayigInTournament = new ArrayList<>();
        if (usersInTournament == null) {
            usersInTournament = new ArrayList<>();
        }
        for (Integer userPk : usersInTournament) {
            User user = userIWS.getUserByPk(userPk);
            usersPlayigInTournament.add(user);
        }
        return usersPlayigInTournament;
    }

    private Boolean hasPermission(Long teamPk, String username) {
        Team team = teamIWS.getTeamByPk(teamPk);
        User user = userIWS.getUserByUsername(username);

        if (team.getTeamLeaderPk() != user.getPk()) {
            return false;
        }
        return true;
    }

    @RequestMapping(value = "/tournament/invite-users/{pk}", method = RequestMethod.POST)
    public String tournamentInvite(@Valid @ModelAttribute("teamInvitePojo") TeamInvitePojo teamInvitePojo, @PathVariable long pk,
            BindingResult bindingResult, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {

        String senderUsername = p.getName();
        User user = userIWS.getUserByUsername(p.getName());
        User userToInvite = userIWS.getUserByUsername(teamInvitePojo.getUsernameOfUserBeingInvited());
        if (userToInvite == null) {
            redirectAttributes.addFlashAttribute("error", "User '" + teamInvitePojo.getUsernameOfUserBeingInvited() + "' does not exist.");
            return "redirect:/tournament/" + pk + "/invite-users";
        }
        TournamentTeam tournamentTeam = tournamentTeamIWS.getTournamentTeamByTournamentPkAndUserPk(pk, user.getPk());

        TournamentInvite tournamentInvite = tournamentInviteService.findTournamentInviteForUserInTournamentTeam(tournamentTeam.getPk(), userToInvite.getPk());
        if (tournamentInvite != null) {
            if (tournamentInvite.getInviteEnum() == InvitesEnum.DECLINED) {
                tournamentInviteService.setTournamentInviteStatus(tournamentInvite, InvitesEnum.PENDING);
                redirectAttributes.addFlashAttribute("message", "User '" + teamInvitePojo.getUsernameOfUserBeingInvited() + "' has been invited.");
                return "redirect:/tournament/" + pk + "/invite-users";
            } else if (tournamentInvite.getInviteEnum() == InvitesEnum.PENDING) {
                tournamentInviteService.setTournamentInviteStatus(tournamentInvite, InvitesEnum.PENDING);
                redirectAttributes.addFlashAttribute("message", "User '" + teamInvitePojo.getUsernameOfUserBeingInvited() + "' has been invited.");
                return "redirect:/tournament/" + pk + "/invite-users";
            }
        }

        if (!userValidator.containsOnlyAlphaNumeric(teamInvitePojo.getUsernameOfUserBeingInvited())) {
            redirectAttributes.addFlashAttribute("error", "Usernames only contain letters, digits and underscores.");
            return "redirect:/tournament/" + pk + "/invite-users";
        }
        addUserAttributesToModel(model, user);

        Tournament tournament = tournamentIWS.findByPk(pk);
        try {
            if (tournament.getUsersInTournament().contains(user)) {
                tournamentInviteService.createNewTournamentInvite(tournament, userToInvite, tournamentTeam, user);
                redirectAttributes.addFlashAttribute("message", "User '" + teamInvitePojo.getUsernameOfUserBeingInvited() + "' has been invited.");
                return "redirect:/tournament/" + pk + "/invite-users";
            }

            redirectAttributes.addFlashAttribute("error", "There has been an error while trying to process this request.");
            return "redirect:/tournament/" + pk + "/invite-users";
        } catch (Exception ex) {
//            ex.printStackTrace();
//            model.addAttribute("team", team);
//            model.addAttribute("error", ex.getMessage());
//            model.addAttribute("teamMembers", teamMembers);
            redirectAttributes.addFlashAttribute("error", "There has been an error while trying to process this request.");
            return "redirect:/tournament/" + pk + "/invite-users";
        }
    }
}
