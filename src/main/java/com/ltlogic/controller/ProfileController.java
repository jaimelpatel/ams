/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.User;
import com.ltlogic.fe.helpers.Paginator;
import com.ltlogic.fe.models.form.GeneralAccountSettingsForm;
import com.ltlogic.iws.MatchIWS;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.iws.TournamentServiceIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.web.exception.CustomErrorException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author raymond
 */
@Controller
public class ProfileController {

    @Autowired
    UserIWS userIWS;

    @Autowired
    TeamIWS teamIWS;

    @Autowired
    MatchIWS matchIWS;

    @Autowired
    TournamentServiceIWS tournamentIWS;

    @Autowired
    Paginator paginator;

    @RequestMapping(value = "/{username}/profile", method = RequestMethod.GET)
    public String account(@PathVariable String username, ModelMap model, Principal p,
            @RequestParam(value = "match_page", required = false) Integer matchPage,
            @RequestParam(value = "tournament_page", required = false) Integer tournamentPage) {

        List<Match> matches = new ArrayList<>();

        List<Team> teams = new ArrayList<>();
        List<Tournament> tournaments = new ArrayList<>();
        User user = userIWS.getUserByUsername(username);
        int totalMatches = 0, totalTournaments = 0;

        Integer matchCurrentPage = 1;
        Integer tournamentCurrentPage = 1;

        if (matchPage != null) {
            matchCurrentPage = matchPage;
        }

        if (tournamentPage != null) {
            tournamentCurrentPage = tournamentPage;
        }

        List<Team> playoffTeams = new ArrayList<>();
        List<Team> cashoutTeams = new ArrayList<>();

        if (user.getUserInfo().getFacebookUrl() != null) {
            if (!user.getUserInfo().getFacebookUrl().isEmpty()) {
                model.addAttribute("facebook", user.getUserInfo().getFacebookUrl());
            }
        }
        if (user.getUserInfo().getTwitchUrl() != null) {
            if (!user.getUserInfo().getTwitchUrl().isEmpty()) {
                model.addAttribute("twitch", user.getUserInfo().getTwitchUrl());
            }
        }
        if (user.getUserInfo().getYoutubeUrl() != null) {
            if (!user.getUserInfo().getYoutubeUrl().isEmpty()) {
                model.addAttribute("youtube", user.getUserInfo().getYoutubeUrl());
            }
        }
        if (user.getUserInfo().getTwitterUrl() != null) {
            if (!user.getUserInfo().getTwitterUrl().isEmpty()) {
                model.addAttribute("twitter", user.getUserInfo().getTwitterUrl());
            }
        }

        if (user != null) {
            playoffTeams = teamIWS.getAllTeamsOfUserByTeamType(username, TeamTypeEnum.XP);
            cashoutTeams = teamIWS.getAllTeamsOfUserByTeamType(username, TeamTypeEnum.CASH);
            matches = matchIWS.findMatchesByUserPkForPaginate(user.getPk(), matchCurrentPage - 1);
            totalMatches = matchIWS.findMatchesByUserPk(user.getPk()).size();
            tournaments = tournamentIWS.getTournamentsByUserPkForPaginate(user.getPk(), tournamentCurrentPage - 1);
            totalTournaments = tournamentIWS.getTournamentsByUserPk(user.getPk()).size();
        }

        paginator.setPagination("match", matchCurrentPage, totalMatches, model);
        paginator.setPagination("tournament", tournamentCurrentPage, totalTournaments, model);

        String[] timezone = user.getUserInfo().getTimeZone().getTimeZonesEnumDesc().split(" ");

        model.addAttribute("timezone", timezone[1]);
        model.addAttribute("playoffTeams", playoffTeams);
        model.addAttribute("cashoutTeams", cashoutTeams);
        model.addAttribute("matches", matches);
        model.addAttribute("tournaments", tournaments);
        model.addAttribute("user", user);
        return "profile/details";
    }

}
