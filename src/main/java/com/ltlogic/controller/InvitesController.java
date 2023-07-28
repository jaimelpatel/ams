/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.MatchInvite;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.TeamInvite;
import com.ltlogic.db.entity.TournamentInvite;
import com.ltlogic.db.entity.User;
import com.ltlogic.fe.helpers.Paginator;
import com.ltlogic.fe.models.form.MatchFormValidator;
import com.ltlogic.fe.models.form.TeamForm;
import com.ltlogic.fe.models.form.TeamFormValidator;
import com.ltlogic.fe.models.form.TournamentFormValidator;
import com.ltlogic.iws.MatchIWS;
import com.ltlogic.iws.MatchInviteIWS;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.iws.TeamInviteIWS;
import com.ltlogic.iws.TournamentInviteIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.service.springsecurity.TeamValidator;
import com.ltlogic.web.exception.CustomErrorException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author raymond
 */
@Controller
public class InvitesController {

    @Autowired
    private UserIWS userIWS;

    @Autowired
    private TeamIWS teamIWS;

    @Autowired
    TeamFormValidator teamCreationFormValidator;

    @Autowired
    TournamentFormValidator tournamentCreationFormValidator;

    @Autowired
    TeamValidator teamValidator;

    @Autowired
    MatchFormValidator matchFormValidator;

    @Autowired
    TeamInviteIWS teamInviteIWS;

    @Autowired
    MatchInviteIWS matchInviteIWS;

    @Autowired
    TournamentInviteIWS tournamentInviteIWS;

    @Autowired
    MatchIWS matchIWS;
    
    @Autowired
    Paginator paginator;

    @RequestMapping(value = "{username}/invites", method = RequestMethod.GET)
    public String viewInvites(@PathVariable String username, ModelMap model, Principal p,
            @RequestParam(value = "page", required = false) Integer page) {

        if (p == null || (username != null && username.equalsIgnoreCase(p.getName()) == false)) {
            throw new CustomErrorException();
        }
        
        Integer currentPage = 1;
        
        if(page != null) {
            currentPage = page;
        }

        User user = userIWS.getUserByUsername(p.getName());
        List<TeamInvite> pendingInvites = new ArrayList<>();
        List<MatchInvite> matchInvites = new ArrayList<>();
        List<TournamentInvite> tournamentInvites = new ArrayList<>();
        if (user != null) {
            pendingInvites = teamInviteIWS.getPendingInvitesByUserPk(user.getPk(), currentPage-1);
            if (pendingInvites.size() == 0) {
                model.addAttribute("noTeamInvites", true);
            }
            matchInvites = matchInviteIWS.findAllMatchInvitesByStatusForUser(user.getPk(), InvitesEnum.PENDING);
            if (matchInvites.size() == 0) {
                model.addAttribute("noMatchInvites", true);
            }
            tournamentInvites = tournamentInviteIWS.findAllTournamentInvitesByStatusForUser(user.getPk(), InvitesEnum.PENDING);
            if (tournamentInvites.size() == 0) {
                model.addAttribute("noTournamentInvites", true);
            }
        }

        boolean isEmpty = pendingInvites.isEmpty() && matchInvites.isEmpty() && tournamentInvites.isEmpty();

        
        Integer totalTeamInvites = teamInviteIWS.getTotalPendingInvitesByUserPk(user.getPk());
        
        paginator.setPagination(null,currentPage, totalTeamInvites, model);
        
        model.addAttribute("user", user);
        model.addAttribute("pendingInvites", pendingInvites);
        model.addAttribute("matchInvites", matchInvites);
        model.addAttribute("tournamentInvites", tournamentInvites);
        model.addAttribute("isEmpty", isEmpty);

        return "invites/index";
    }

    @RequestMapping(value = "/invites/{tournamentInvitePk}/accept/tournament", method = RequestMethod.GET)
    public String acceptTournamentInvite(@ModelAttribute("acceptTeamInviteForm") Object acceptTeamInviteForm, @PathVariable long tournamentInvitePk,
            BindingResult bindingResult, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {
        User user = userIWS.getUserByUsername(p.getName());
        TournamentInvite invite = tournamentInviteIWS.findInviteByPk(tournamentInvitePk);

        List<TournamentInvite> tournamentInvites = new ArrayList<>();
        if (user != null) {
            tournamentInvites = tournamentInviteIWS.findAllTournamentInvitesByStatusForUser(user.getPk(), InvitesEnum.PENDING);
        }

        if (invite == null) {
            throw new CustomErrorException();
        }
//        
//        teamValidator.userAcceptTeamInviteValidation(user, team, bindingResult);
//        
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("user", user);
//            model.addAttribute("pendingInvites", pendingInvites);
//            return "invites/index";
//        }

        try {
            if (invite.getTournament().getUsersInTournament().contains(user)) {
                throw new Exception("You are already in this tournament on a different team.");
            }
            tournamentCreationFormValidator.validateTournamentAccept(user, invite.getTournament(), bindingResult);
            tournamentInviteIWS.acceptTournamentInvite(tournamentInvitePk);
            redirectAttributes.addFlashAttribute("message", "You have accepted a tournament invite.");
            return "redirect:/tournaments/" + invite.getTournament().getPk();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            //model.addAttribute("error", ex.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("tournamentInvites", tournamentInvites);
            return "redirect:/" + user.getUsername() + "/invites";
        }
    }

    @RequestMapping(value = "/invites/{tournamentInvitePk}/decline/tournament", method = RequestMethod.GET)
    public String declineTournamentInvite(@ModelAttribute("acceptTeamInviteForm") Object acceptTeamInviteForm, @PathVariable long tournamentInvitePk,
            BindingResult bindingResult, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {
        User user = userIWS.getUserByUsername(p.getName());
        TournamentInvite invite = tournamentInviteIWS.findInviteByPk(tournamentInvitePk);

        List<TournamentInvite> tournamentInvites = new ArrayList<>();
        if (user != null) {
            tournamentInvites = tournamentInviteIWS.findAllTournamentInvitesByStatusForUser(user.getPk(), InvitesEnum.PENDING);
        }

        if (invite == null) {
            throw new CustomErrorException();
        }

        //teamValidator.userAcceptTeamInviteValidation(user, team, bindingResult);
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("user", user);
//            model.addAttribute("pendingInvites", pendingInvites);
//            return "invites/index";
//        }
        try {
//            if (invite.getTournament().getUsersInTournament().contains(user)) {
//                throw new Exception("You are already in this tournament on a different team.");
//            }
            tournamentInviteIWS.declineTournamentInvite(tournamentInvitePk);
            redirectAttributes.addFlashAttribute("message", "You have declined a tournament invite.");
            return "redirect:/" + user.getUsername() + "/invites";
        } catch (Exception ex) {
            model.addAttribute("user", user);
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            model.addAttribute("tournamentInvites", tournamentInvites);
            return "redirect:/" + user.getUsername() + "/invites";
        }
    }

    @RequestMapping(value = "/invites/{teamInvitePk}/accept/team", method = RequestMethod.GET)
    public String acceptTeamInvite(@ModelAttribute("acceptTeamInviteForm") Object acceptTeamInviteForm, @PathVariable long teamInvitePk,
            BindingResult bindingResult, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {
        User user = userIWS.getUserByUsername(p.getName());
        TeamInvite invite = teamInviteIWS.findInviteByPk(teamInvitePk);

        if (invite == null) {
            throw new CustomErrorException();
        }

        Team team = teamIWS.getTeamByPk(invite.getTeam().getPk());

        List<TeamInvite> pendingInvites = new ArrayList<>();
        if (user != null) {
            pendingInvites = teamInviteIWS.getPendingInvitesByUserPk(user.getPk(), 0);
        }
//
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("user", user);
//            model.addAttribute("pendingInvites", pendingInvites);
//            return "invites/index";
//        }

        try {
            teamValidator.userAcceptTeamInviteValidation(user, team, bindingResult);
            teamInviteIWS.acceptInvite(user, team, invite);
            redirectAttributes.addFlashAttribute("message", "You have joined the team.");
            return "redirect:/teams/" + team.getPk();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("pendingInvites", pendingInvites);
            return "redirect:/" + user.getUsername() + "/invites";
        }
    }

    @RequestMapping(value = "/invites/{teamInvitePk}/decline/team", method = RequestMethod.GET)
    public String declineTeamInvite(@ModelAttribute("acceptTeamInviteForm") Object acceptTeamInviteForm, @PathVariable long teamInvitePk,
            BindingResult bindingResult, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {

        User user = userIWS.getUserByUsername(p.getName());
        TeamInvite invite = teamInviteIWS.findInviteByPk(teamInvitePk);

        if (invite == null) {
            throw new CustomErrorException();
        }

        Team team = teamIWS.getTeamByPk(invite.getTeam().getPk());

        List<TeamInvite> pendingInvites = new ArrayList<>();
        if (user != null) {
            pendingInvites = teamInviteIWS.getPendingInvitesByUserPk(user.getPk(), 0);
        }

//        if (bindingResult.hasErrors()) {
//            model.addAttribute("user", user);
//            model.addAttribute("pendingInvites", pendingInvites);
//            return "invites/index";
//        }
        try {
            teamValidator.userDeclineTeamInviteValidation(user, team, bindingResult);
            teamInviteIWS.declineInvite(user, team, invite);
            redirectAttributes.addFlashAttribute("message", "You have declined a team invite.");
            return "redirect:/" + user.getUsername() + "/invites";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("pendingInvites", pendingInvites);
            return "redirect:/" + user.getUsername() + "/invites";
        }
    }

    //for accepting individual cash match invites -- check matchInvite.isPrivateMatchInviteToLeader on fe, if this is true call @RequestMapping(value="/matches/accept/{teamPk}/{matchPk}", method=RequestMethod.POST) from MatchesController instead of this
    @RequestMapping(value = "/invites/{matchInvitePk}/accept/match", method = RequestMethod.GET)
    public String acceptMatchInvite(@ModelAttribute("acceptMatchForm") Object acceptMatchForm, @PathVariable long matchInvitePk,
            BindingResult bindingResult, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {

        User user = userIWS.getUserByUsername(p.getName());
        MatchInvite invite = matchInviteIWS.findInvitebyPk(matchInvitePk);

        if (invite == null) {
            throw new CustomErrorException();
        }

        Match match = invite.getMatch();

        List<MatchInvite> matchInvites = new ArrayList<>();
        if (user != null) {
            matchInvites = matchInviteIWS.findAllMatchInvitesByStatusForUser(user.getPk(), InvitesEnum.PENDING);
        }

        try {
            matchFormValidator.validateMatchAcceptForWager2(user, match, bindingResult);

            matchInviteIWS.acceptMatchInvite(matchInvitePk);
            redirectAttributes.addFlashAttribute("message", "You have accepted a match invite.");
            return "redirect:/matches/" + invite.getMatch().getPk();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            //model.addAttribute("error", ex.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("matchInvites", matchInvites);
            return "redirect:/" + user.getUsername() + "/invites";
        }

    }

    @RequestMapping(value = "/invites/{matchInvitePk}/decline/match", method = RequestMethod.GET)
    public String declineMatchInvite(@ModelAttribute("acceptMatchForm") Object acceptMatchForm, @PathVariable long matchInvitePk,
            BindingResult bindingResult, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {

        User user = userIWS.getUserByUsername(p.getName());
        MatchInvite invite = matchInviteIWS.findInvitebyPk(matchInvitePk);

        if (invite == null) {
            throw new CustomErrorException();
        }

        Match match = invite.getMatch();

        List<MatchInvite> matchInvites = new ArrayList<>();
        if (user != null) {
            matchInvites = matchInviteIWS.findAllMatchInvitesByStatusForUser(user.getPk(), InvitesEnum.PENDING);
        }

//        if (bindingResult.hasErrors()) {
//            model.addAttribute("user", user);
//            model.addAttribute("matchInvites", matchInvites);
//            return "invites/index";
//        }
        try {
            matchFormValidator.validateDeclineMatchInvite(user, match, bindingResult);

            matchInviteIWS.declineMatchInvite(matchInvitePk);
            redirectAttributes.addFlashAttribute("message", "You have declined a match invite.");
            return "redirect:/" + user.getUsername() + "/invites";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());

            model.addAttribute("user", user);
            model.addAttribute("matchInvites", matchInvites);
            return "redirect:/" + user.getUsername() + "/invites";
        }
    }
}
