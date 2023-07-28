/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.fe.models.form.MatchForm;
import com.ltlogic.configs.WebSecurityConfig;
import com.ltlogic.constants.BestOfEnum;
import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.GameModeEnum;
import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.Dispute;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.MatchCancellationRequest;
import com.ltlogic.db.entity.MatchInvite;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.iw.IWMatch;
import com.ltlogic.db.entity.mwr.MWRMatch;
import com.ltlogic.db.entity.mwr.MWRTeam;
import com.ltlogic.db.entity.ww2.WW2Match;
import com.ltlogic.fe.models.form.DisputeForm;
import com.ltlogic.fe.models.form.DisputeFormValidator;
import com.ltlogic.fe.models.form.MatchFormValidator;
import com.ltlogic.fe.models.form.TeamForm;
import com.ltlogic.fe.models.form.TeamFormValidator;
import com.ltlogic.iws.DisputeIWS;
import com.ltlogic.iws.MatchCancellationRequestIWS;
import com.ltlogic.iws.MatchIWS;
import com.ltlogic.iws.MatchInviteIWS;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.iws.TeamInviteIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.MatchPojo;
import com.ltlogic.pojo.TeamPojo;
import com.ltlogic.pojo.iw.IWMatchPojo;
import com.ltlogic.pojo.mwr.MWRMatchPojo;
import com.ltlogic.pojo.ww2.WW2MatchPojo;
import com.ltlogic.service.springsecurity.MatchValidator;
import com.ltlogic.service.springsecurity.SecurityContextAccessorImpl;
import com.ltlogic.service.springsecurity.TeamValidator;
import com.ltlogic.web.exception.CustomErrorException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Hoang
 */
@Controller
public class MatchesController {

    @Autowired
    WebSecurityConfig webSecurityConfig;

    @Autowired
    UserIWS userIWS;

    @Autowired
    MatchIWS matchIWS;

    @Autowired
    TeamIWS teamIWS;

    @Autowired
    MatchFormValidator matchFormValidator;

    @Autowired
    TeamValidator teamValidator;

    @Autowired
    MatchInviteIWS matchInviteIWS;

    @Autowired
    DisputeIWS disputeIWS;

    @Autowired
    MatchValidator matchValidator;

    @Autowired
    TeamInviteIWS teamInviteIWS;

    @Autowired
    TeamFormValidator teamCreationFormValidator;

    @Autowired
    MatchCancellationRequestIWS matchCancellationRequestIWS;

    @Autowired
    DisputeFormValidator disputeFormValidator;

    private boolean isAnonymous() {
        SecurityContextAccessorImpl securityContextAccessor = webSecurityConfig.securityContextAccessorImpl();
        return securityContextAccessor.isCurrentAuthenticationAnonymous();
    }

    @RequestMapping(value = "/matches", method = RequestMethod.GET)
    public String getAllPublicMatches(@RequestParam(value = "game", required = false) Integer game,
            @RequestParam(value = "platform", required = false) Integer platform, ModelMap model, Principal p) {

        GameEnum gameEnum = null;
        if (game != null) {
            gameEnum = GameEnum.getGameEnumById(game);
        }

        PlatformEnum platformEnum = null;
        if (platform != null) {
            platformEnum = PlatformEnum.getPlatformEnumById(platform);
        }

        List<Match> matches = matchIWS.getMatchesByGameAndPlatform(gameEnum, platformEnum);

        model.addAttribute("matches", matches);
        model.addAttribute("isLoggedIn", false);
        if (!isAnonymous()) {
            User u = userIWS.getUserByUsername(p.getName());
            model.addAttribute("user", u);
            model.addAttribute("isLoggedIn", true);
        }

        return "matches/index";
    }

    @RequestMapping(value = "/{username}/matches", method = RequestMethod.GET)
    public String myMatches(@PathVariable String username, ModelMap model, Principal p) {

        if (username != null && username.equalsIgnoreCase(p.getName()) == false) {
            throw new CustomErrorException();
        }

        User user = userIWS.getUserByUsername(p.getName());
        List<Match> matches = matchIWS.findMatchesByUserPk(user.getPk());

        model.addAttribute("matches", matches);
        model.addAttribute("user", user);
        return "matches/my-matches";
    }

    /*
     public - everyone can visit here to check out this match details
     */
    @RequestMapping(value = "/matches/{matchPk}", method = RequestMethod.GET)
    public String getMatch(@PathVariable Long matchPk, ModelMap model, Principal p) {

        Match match = matchIWS.findMatchByPk(matchPk);
        MWRMatch mWRMatch = null;
        IWMatch iwMatch = null;
        WW2Match ww2Match = null;
        if (match.getMatchInfo().getGameEnum() == GameEnum.COD_MWR) {
            mWRMatch = matchIWS.getMWRMatchByMatchPk(matchPk);
            model.addAttribute("mWRMatch", mWRMatch);
        } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_IW) {
            iwMatch = matchIWS.getIWMatchByMatchPk(matchPk);
            model.addAttribute("iwMatch", iwMatch);
        } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_WW2) {
            ww2Match = matchIWS.getWW2MatchByMatchPk(matchPk);
            model.addAttribute("ww2Match", ww2Match);
        }
        //IWMatch iWMatch = matchIWS.getIWMatchByMatchPk(matchPk);
        List<String> mapsToPlay = null;
        List<String> hostNames = null;

        User user = null;
        if (p != null) {
            user = userIWS.getUserByUsername(p.getName());
        }

        if (match == null) {
            throw new CustomErrorException();
        }

        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
            Tournament tournament = match.getTournament();
            if (tournament.getTournamentInfo().getMaxRounds() == match.getMatchResponse().getMatchPojo().getRound()) {
                match.getMatchInfo().setBestOfEnum(tournament.getTournamentInfo().getBestOfEnumFinals());
            }
        }

        //For host names && maps
        if (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.READY_TO_PLAY || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ACTIVE
                || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ENDED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.DISPUTED) {
            //set maps to play && hostnames
            if (match.getMatchInfo().getGameEnum() == GameEnum.COD_MWR) {
                if (mWRMatch != null && mWRMatch.getMwrMatchInfo() != null && mWRMatch.getMwrMatchInfo().getMapsToPlayInMatchInOrder() != null) {
                    if (match != null && match.getMatchInfo() != null) {
                        if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_1) {
                            mapsToPlay = mWRMatch.getMwrMatchInfo().getMapsToPlayInMatchInOrder().subList(0, 1);
                            hostNames = mWRMatch.getMwrMatchInfo().getHostNamesInOrder().subList(1, 2);
                        } else if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_3 && match.getMatchInfo().getMatchType() != MatchTypeEnum.XP) {
                            mapsToPlay = mWRMatch.getMwrMatchInfo().getMapsToPlayInMatchInOrder().subList(0, 3);
                            hostNames = mWRMatch.getMwrMatchInfo().getHostNamesInOrder().subList(0, 2);
                            hostNames.add("TBD");   //third one to be announced;
                        } else if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_3 && match.getMatchInfo().getMatchType() == MatchTypeEnum.XP) {
                            mapsToPlay = mWRMatch.getMwrMatchInfo().getMapsToPlayInMatchInOrder().subList(0, 3);
                            hostNames = mWRMatch.getMwrMatchInfo().getHostNamesInOrder().subList(0, 3);
                        }
                    }
                }
            } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_IW) {
                if (iwMatch != null && iwMatch.getIwMatchInfo() != null && iwMatch.getIwMatchInfo().getMapsToPlayInMatchInOrder() != null) {
                    if (match != null && match.getMatchInfo() != null) {
                        if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_1) {
                            mapsToPlay = iwMatch.getIwMatchInfo().getMapsToPlayInMatchInOrder().subList(0, 1);
                            hostNames = iwMatch.getIwMatchInfo().getHostNamesInOrder().subList(1, 2);
                        } else if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_3 && match.getMatchInfo().getMatchType() != MatchTypeEnum.XP) {
                            mapsToPlay = iwMatch.getIwMatchInfo().getMapsToPlayInMatchInOrder().subList(0, 3);
                            hostNames = iwMatch.getIwMatchInfo().getHostNamesInOrder().subList(0, 2);
                            hostNames.add("TBD");   //third one to be announced;
                        } else if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_3 && match.getMatchInfo().getMatchType() == MatchTypeEnum.XP) {
                            mapsToPlay = iwMatch.getIwMatchInfo().getMapsToPlayInMatchInOrder().subList(0, 3);
                            hostNames = iwMatch.getIwMatchInfo().getHostNamesInOrder().subList(0, 3);
                        }
                    }
                }
            } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_WW2) {
                if (ww2Match != null && ww2Match.getWw2MatchInfo() != null && ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder() != null) {
                    if (ww2Match.getWw2MatchInfo().getGameModeEnum() == GameModeEnum.SearchAndDestroy) {
                        if (match != null && match.getMatchInfo() != null) {
                            if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_1) {
                                mapsToPlay = ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder().subList(0, 1);
                                hostNames = ww2Match.getWw2MatchInfo().getHostNamesInOrder().subList(1, 2);
                            } else if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_3 && match.getMatchInfo().getMatchType() != MatchTypeEnum.XP) {
                                boolean doBelow = true;
                                if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
                                    Tournament tournament = match.getTournament();
                                    if (tournament.getTournamentInfo().getBestOfEnum() == BestOfEnum.BEST_OF_1) {
                                        hostNames = ww2Match.getWw2MatchInfo().getHostNamesInOrder().subList(0, 2);
                                        hostNames.add("TBD");
                                        List<String> maps = ww2Match.getWw2MatchInfo().getVariantMaps();
                                        maps.remove("Gibraltar");
                                        Collections.shuffle(maps);
                                        mapsToPlay = maps.subList(0, 3);
                                        doBelow = false;
                                    }
                                }
                                if (doBelow) {
                                    mapsToPlay = ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder().subList(0, 3);
                                    hostNames = ww2Match.getWw2MatchInfo().getHostNamesInOrder().subList(0, 2);
                                    hostNames.add("TBD");   //third one to be announced;
                                }
                            } else if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_3 && match.getMatchInfo().getMatchType() == MatchTypeEnum.XP) {
                                mapsToPlay = ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder().subList(0, 3);
                                hostNames = ww2Match.getWw2MatchInfo().getHostNamesInOrder().subList(0, 3);
                            } else if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_5) {
                                mapsToPlay = ww2Match.getWw2MatchInfo().getBestOf5MapsToPlay().subList(0, 5);
                                hostNames = ww2Match.getWw2MatchInfo().getBestOf5HostNames().subList(0, 4);
                                hostNames.add("TBD");   //third one to be announced;
                            }
                        }
                    } else {
                        if (match != null && match.getMatchInfo() != null) {
                            String mapNameWithMode = "";
                            mapsToPlay = new ArrayList<String>();
                            if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_1) {
                                mapsToPlay = ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder().subList(0, 1);
                                hostNames = ww2Match.getWw2MatchInfo().getHostNamesInOrder().subList(1, 2);
                            } else if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_3 && match.getMatchInfo().getMatchType() != MatchTypeEnum.XP) {
                                boolean doBelow = true;
                                if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
                                    Tournament tournament = match.getTournament();
                                    if (tournament.getTournamentInfo().getBestOfEnum() == BestOfEnum.BEST_OF_1) {
                                        hostNames = ww2Match.getWw2MatchInfo().getHostNamesInOrder().subList(0, 2);
                                        hostNames.add("TBD");
                                        List<String> maps = ww2Match.getWw2MatchInfo().getVariantMaps();
                                        List<String> modes = ww2Match.getWw2MatchInfo().getVariantModes();
                                        Collections.shuffle(maps);
                                        Collections.shuffle(modes);
                                        for(int i = 0 ; i < 3 ; i++){
                                            String mapAndMode = maps.get(i) + " - " + modes.get(i);
                                            mapsToPlay.add(mapAndMode);
                                        }
                                        doBelow = false;
                                    }
                                }
                                if (doBelow) {
                                    mapsToPlay = ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder().subList(0, 3);
                                    hostNames = ww2Match.getWw2MatchInfo().getHostNamesInOrder().subList(0, 2);
                                    hostNames.add("TBD");   //third one to be announced;
                                }
                            } else if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_3 && match.getMatchInfo().getMatchType() == MatchTypeEnum.XP) {
                                mapsToPlay = ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder().subList(0, 3);
                                hostNames = ww2Match.getWw2MatchInfo().getHostNamesInOrder().subList(0, 3);
                            } else if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_5) {
                                List<String> mapsToPlay1 = ww2Match.getWw2MatchInfo().getBestOf5MapsToPlay().subList(0, 5);
                                hostNames = ww2Match.getWw2MatchInfo().getBestOf5HostNames().subList(0, 4);
                                hostNames.add("TBD");   //third one to be announced;
                                for (int i = 0; i <= 4; i++) {
                                    String mapName = mapsToPlay1.get(i);
                                    mapNameWithMode = mapName + " - " + ww2Match.getWw2MatchInfo().getBestOf5ModesToPlay().get(i);
                                    System.out.println("VARIANT MAP AND MODE: " + mapNameWithMode);
                                    mapsToPlay.add(mapNameWithMode);
                                }
                            }
                        }
                    }
                }
            }

        }

        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
            model.addAttribute("isTournamentMatch", true);
            int matchRound = match.getMatchResponse().getMatchPojo().getRound();
            model.addAttribute("matchRound", matchRound);
            if (matchRound == match.getTournament().getTournamentInfo().getMaxRounds()) {
                model.addAttribute("finals", true);
            } else if (matchRound == (match.getTournament().getTournamentInfo().getMaxRounds() - 1)) {
                model.addAttribute("semifinals", true);
            } else if (matchRound == (match.getTournament().getTournamentInfo().getMaxRounds() - 2)) {
                model.addAttribute("quarterfinals", true);
            } else {
                int round = match.getMatchResponse().getMatchPojo().getRound();
                model.addAttribute("round", round);
                model.addAttribute("isNotFinals", true);
            }
        } else {
            model.addAttribute("isTournamentMatch", false);
        }
        List<Team> teamsInMatch = teamIWS.findTeamsByMatchPk(matchPk);

        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                System.out.println("LIST SIZE: " + teamsInMatch.size());
                System.out.println("TEAM PK: " + teamsInMatch.get(0).getPk());
                System.out.println("null PK: " + match.getPkOfTeamThatCreatedMatch());
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }

        if (match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT && match.getUsersInMatch().contains(user) && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_SECOND_ACCEPT || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT)) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }

        if (match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ACTIVE || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.DISPUTED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.READY_TO_PLAY || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ENDED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.EXPIRED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.CANCELLED)) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }

        if (match.getMatchInfo().isIsMatchPublic() && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_SECOND_ACCEPT || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT)) {
            if (p == null) {
                model.addAttribute("showTeams", false);
            } else {
                if (!match.getUsersInMatch().contains(user)) {
                    model.addAttribute("showTeams", false);
                } else {
                    model.addAttribute("showTeams", true);
                }
            }
        } else {
            model.addAttribute("showTeams", true);
        }

        Boolean isEligibleToJoin = false;

        if (match.getMatchInfo()
                .getMatchStatus() == MatchStatusEnum.PENDING && match.getMatchInfo().isIsMatchPublic()) {
            isEligibleToJoin = true;
        }

        if (!match.getMatchInfo().isIsMatchPublic() && match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING && match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT) {
            List<MatchInvite> matchInvites = matchInviteIWS.findAllMatchInvitesByMatchPk(match.getPk());
            MatchInvite teamLeaderMatchInvite = null;
            for (MatchInvite mi : matchInvites) {
                if (mi.isIsPrivateMatchInviteToLeader()) {
                    teamLeaderMatchInvite = mi;
                }
            }

            if (match.getUsersInMatch().contains(user) || user.getPk() == teamLeaderMatchInvite.getUser().getPk()) {
                System.out.println("User is in match or invited to match.");
            } else {
                throw new CustomErrorException();
            }

            if (user.getPk() == teamLeaderMatchInvite.getUser().getPk()) {
                isEligibleToJoin = true;
            }
        }

        if (p != null) {
            if (user != null) {

                Match userInTheMatch = matchIWS.findUserMatchByMatchPKAndUsername(matchPk, user.getUsername());

                if (userInTheMatch != null) {

                    Team team = null;
                    for (Team t : teamsInMatch) {
//                        if (t.getMembers().contains(user)) {
//                            team = t;
//                        }
                        if (t.getPk() == match.getPkOfTeamThatCreatedMatch()) {
                            if (match.getPksOfCreatorTeamMembersPlaying().contains(user.getPk())) {
                                long teamPk = match.getPkOfTeamThatCreatedMatch();
                                team = teamIWS.getTeamByPk(teamPk);
                            }
                        } else if (t.getPk() == match.getPkOfTeamThatAcceptedMatch()) {
                            if (match.getPksOfAcceptorTeamMembersPlaying().contains(user.getPk())) {
                                long teamPk = match.getPkOfTeamThatAcceptedMatch();
                                team = teamIWS.getTeamByPk(teamPk);
                            }
                        }
                    }

                    Dispute dispute = null;
                    if (team != null) {
                        dispute = disputeIWS.findDisputeForMatchAndTeam(match.getPk(), team.getPk());
                        model.addAttribute("teamDispute", dispute);
//                        MWRTeam mWRteam = teamIWS.getMWRTeamByTeamPk(team.getPk());
//                        model.addAttribute("mWRteam", mWRteam);
                    }

                    if (!match.getDisputes().isEmpty()) {
                        model.addAttribute("isDisputed", true);
                    }

                    //dispute = null means team has not made a dispute yet
                    if (dispute == null && (MatchStatusEnum.ACTIVE == match.getMatchInfo().getMatchStatus() || MatchStatusEnum.DISPUTED == match.getMatchInfo().getMatchStatus())) {
                        model.addAttribute("canDisputeMatch", true);
                    }

                    if (dispute != null && (MatchStatusEnum.ACTIVE == match.getMatchInfo().getMatchStatus() || MatchStatusEnum.DISPUTED == match.getMatchInfo().getMatchStatus())) {
                        model.addAttribute("viewDispute", true);
                        model.addAttribute("dispute", dispute);
                    }

                    if (MatchStatusEnum.ACTIVE == match.getMatchInfo().getMatchStatus()) {
                        model.addAttribute("canReportScore", true);
                    }

                    MatchCancellationRequest request = matchCancellationRequestIWS.getMatchCancellationRequestByMatchPk(match.getPk());

                    if (user.getPk() == team.getTeamLeaderPk()) {
                        if (request != null) {
                            model.addAttribute("canRequestMatchCancel", false);
                        } else {
                            //allow user to request cancel match
                            if (MatchStatusEnum.READY_TO_PLAY == match.getMatchInfo().getMatchStatus()) {
                                model.addAttribute("canRequestMatchCancel", true);
                            }
                        }
                    }

                    //enable creator to cancel match
                    if (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING) {

                        if (match.getPkOfUserThatCreatedMatch() == user.getPk()) {
                            model.addAttribute("canCancelMatchForCreator", true);
                        }
                    }

                    //reported score
                    if (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ACTIVE) {
                        if (match.getPksOfCreatorTeamMembersPlaying() != null && match.getPksOfCreatorTeamMembersPlaying().contains(user.getPk())) {
                            if (match.getReportedScoreOfTeamCreated() != null && (match.getReportedScoreOfTeamCreated() == true || match.getReportedScoreOfTeamCreated() == false)) {
                                model.addAttribute("reportedScoreByMyTeam", 0);   //creator reported
                                model.addAttribute("canDisputeMatch", false);
                                model.addAttribute("canReportScore", false);
                            }
                        } else if (match.getPksOfAcceptorTeamMembersPlaying() != null && match.getPksOfAcceptorTeamMembersPlaying().contains(user.getPk())) {
                            if (match.getReportedScoreOfTeamAccepted() != null && (match.getReportedScoreOfTeamAccepted() == true || match.getReportedScoreOfTeamAccepted() == false)) {
                                model.addAttribute("reportedScoreByMyTeam", 1);   //acceptor reported
                                model.addAttribute("canDisputeMatch", false);
                                model.addAttribute("canReportScore", false);
                            }
                        }
                    }

                    if (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ENDED) {
                        if (match.getPksOfCreatorTeamMembersPlaying() != null && match.getPksOfCreatorTeamMembersPlaying().contains(user.getPk())) {
                            if (match.getPkOfTeamThatCreatedMatch() == match.getPkOfTeamWonMatch()) {
                                model.addAttribute("isWinnerTeam", 1);   //creator team winner
                            } else {
                                model.addAttribute("isWinnerTeam", 0);  //creator team loser
                            }

                        } else if (match.getPksOfAcceptorTeamMembersPlaying() != null && match.getPksOfAcceptorTeamMembersPlaying().contains(user.getPk())) {
                            if (match.getPkOfTeamThatAcceptedMatch() == match.getPkOfTeamWonMatch()) {
                                model.addAttribute("isWinnerTeam", 1);   //acceptor team winner
                            } else {
                                model.addAttribute("isWinnerTeam", 0);  //acceptor team loser
                            }
                        }

//                        if (match.getReportedScoreOfTeamCreated() != null && match.getReportedScoreOfTeamAccepted() != null) {
//                            if (match.getReportedScoreOfTeamCreated() == true && match.getReportedScoreOfTeamAccepted() == true) {
//                                model.addAttribute("isWinnerTeam", -1);   //both teams claim winner
//                            }
//                        }
                    }

                    isEligibleToJoin = false;
                }

                model.addAttribute("user", user);
            } else {
                model.addAttribute("user", null);
            }
        }

        model.addAttribute("isEligibleToJoin", isEligibleToJoin);
        model.addAttribute("match", match);
        model.addAttribute("teamsInMatch", teamsInMatch);
        model.addAttribute("mapsToPlay", mapsToPlay);
        model.addAttribute("hostNames", hostNames);

        return "matches/match-details";
    }

    /*
     only creator may cancel a match
     */
    @RequestMapping(value = "/matches/{matchPk}/cancel-match-game", method = RequestMethod.GET)
    public String cancelMatchGame(@PathVariable Long matchPk, ModelMap model,
            Principal p,
            RedirectAttributes redirectAttributes
    ) {

        Match match = matchIWS.findMatchByPk(matchPk);
        User user = userIWS.getUserByUsername(p.getName());

        if (match == null) {
            throw new CustomErrorException();
        }

        if (match.getMatchInfo().getMatchStatus() != MatchStatusEnum.WAITING_ON_FIRST_ACCEPT && match.getMatchInfo().getMatchStatus() != MatchStatusEnum.PENDING) {
            redirectAttributes.addFlashAttribute("error", "You cannot cancel this match anymore.");
            return "redirect:/matches/" + match.getPk();
        }

        if (match.getPkOfUserThatCreatedMatch() != user.getPk()) {
            throw new CustomErrorException(403, "You're not authorized to be here.");
        }

        matchIWS.cancelMatch(match.getPk());
        redirectAttributes.addFlashAttribute("message", "You have cancelled the match game.");
        return "redirect:/matches/" + match.getPk();
    }

    /*
     public - show report score form... allow user to pick who won/lost
     */
    @RequestMapping(value = "/matches/{matchPk}/report-score", method = RequestMethod.GET)
    public String getReportScoreForm(@PathVariable Long matchPk, ModelMap model,
            Principal p,
            RedirectAttributes redirectAttributes
    ) {

        Match match = matchIWS.findMatchByPk(matchPk);
        User user = userIWS.getUserByUsername(p.getName());
        List<Match> matches = matchIWS.findMatchesByUserPk(user.getPk());
        if (match == null || matches.contains(match) == false) {
            throw new CustomErrorException();
        }

        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
            model.addAttribute("isTournamentMatch", true);
        } else {
            model.addAttribute("isTournamentMatch", false);
        }
        List<Team> teamsInMatch = teamIWS.findTeamsByMatchPk(matchPk);

        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }

        if (match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT && match.getUsersInMatch().contains(user) && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_SECOND_ACCEPT || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT)) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }

        if (match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ACTIVE || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.DISPUTED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.READY_TO_PLAY || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ENDED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.EXPIRED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.CANCELLED)) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }

        if (match.getMatchInfo().getMatchStatus() != MatchStatusEnum.ACTIVE) {
            redirectAttributes.addFlashAttribute("error", "Match is not in Active status. You are not allowed to report score.");
            return "redirect:/matches/" + match.getPk();
        }

        model.addAttribute("match", match);
        return "matches/report-score";
    }

    /*
     public - save report score form... allow user to pick who won/lost
     */
    @RequestMapping(value = "/matches/{matchPk}/report-score", method = RequestMethod.POST)
    public String saveReportScore(@PathVariable Long matchPk, ModelMap model,
            Principal p,
            @RequestParam("matchScore") Boolean matchScore, RedirectAttributes redirectAttributes
    ) {

        Match match = matchIWS.findMatchByPk(matchPk);
        User user = userIWS.getUserByUsername(p.getName());
        List<Match> matches = matchIWS.findMatchesByUserPk(user.getPk());
        if (match == null || matches.contains(match) == false) {
            throw new CustomErrorException();
        }

        if (match.getMatchInfo().getMatchStatus() != MatchStatusEnum.ACTIVE) {
            redirectAttributes.addFlashAttribute("error", "Match is not in Active status. You are not allowed to report score.");
            return "redirect:/matches/" + match.getPk();
        }

        if (matchScore == null) {
            model.addAttribute("error", "Please select an option from the dropdown.");
            model.addAttribute("match", match);
            return "matches/report-score";
        }

        //Find my team in this match
        Team team = null;
        for (Team t : match.getTeamsInMatch()) {
            if (t.getPk() == match.getPkOfTeamThatCreatedMatch()) {
                if (match.getPksOfCreatorTeamMembersPlaying().contains(user.getPk())) {
                    long teamPk = match.getPkOfTeamThatCreatedMatch();
                    team = teamIWS.getTeamByPk(teamPk);
                }
            } else if (t.getPk() == match.getPkOfTeamThatAcceptedMatch()) {
                if (match.getPksOfAcceptorTeamMembersPlaying().contains(user.getPk())) {
                    long teamPk = match.getPkOfTeamThatAcceptedMatch();
                    team = teamIWS.getTeamByPk(teamPk);
                }
            }
        }

        if (p != null) {
            if (!hasPermission(team.getPk(), p.getName())) {
                redirectAttributes.addFlashAttribute("error", "You do not have permissions to report the outcome for this match.");
                return "redirect:/matches/" + matchPk;
            }
        }

        try {
            matchIWS.reportMatchScore(user, match, team, matchScore, false);
            redirectAttributes.addFlashAttribute("message", "Thank you for reporting match score.");
            return "redirect:/matches/" + match.getPk();
        } catch (Exception ex) {
            ex.printStackTrace();
            model.addAttribute("erorr", ex.getMessage());
            model.addAttribute("match", match);
            return "matches/report-score";
        }
    }

    @RequestMapping(value = "/matches/{matchPk}/team/create", method = RequestMethod.GET)
    public String createTournamentTeam(@ModelAttribute("teamCreationForm") TeamForm teamCreationForm, ModelMap model,
            Principal p,
            @PathVariable Long matchPk
    ) {

        try {
            Match match = matchIWS.findMatchByPk(matchPk);

            if (match == null) {
                throw new CustomErrorException();
            }

            teamCreationForm.setGame(match.getMatchInfo().getGameEnum().getGameEnumId());
            teamCreationForm.setPlatform(match.getMatchInfo().getPlatform().getPlatformEnumId());
            teamCreationForm.setTeamSize(match.getMatchInfo().getTeamSizeEnum().getTeamSizeEnumId());
            teamCreationForm.setTeamType(match.getMatchInfo().getTeamTypeEnum().getTeamTypeEnumId());
            model.addAttribute("pk", matchPk);
            return "matches/team-create";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "matches/team-create";
        }
    }

    @RequestMapping(value = "/matches/{matchPk}/team/create", method = RequestMethod.POST)
    public String saveTournamentTeam(@Valid
            @ModelAttribute("teamCreationForm") TeamForm teamCreationForm, BindingResult bindingResult,
            ModelMap model,
            Principal p,
            @PathVariable Long matchPk, RedirectAttributes redirectAttributes
    ) {

        Match match = matchIWS.findMatchByPk(matchPk);
        if (match == null) {
            throw new CustomErrorException();
        }

        try {
            String username = p.getName();
            User user = userIWS.getUserByUsername(username);

            teamCreationForm.setUsername(username);
            TeamPojo teamPojo = TeamForm.toTeamPojo(teamCreationForm, user);

            teamCreationFormValidator.validate(teamPojo, bindingResult);

            teamValidator.inviteUserToTeamValidationOnTeamCreation(teamPojo, bindingResult);

            if (bindingResult.hasErrors()) {
                model.addAttribute("pk", matchPk);
                return "matches/team-create";
            }

            Team newTeam = teamIWS.createTeam(teamPojo, username);

            for (String un : teamPojo.getUsersToInviteToTeam()) {
                teamInviteIWS.inviteUserToTeam(username, un, newTeam.getPk());
            }

            redirectAttributes.addFlashAttribute("message", "You have created a team.");

            return "redirect:/matches/" + matchPk + "/accept/";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "matches/team-create";
        }
    }

    @RequestMapping(value = "/matches/{matchPk}/disputes", method = RequestMethod.GET)
    public String matchDisputes(@PathVariable Long matchPk, ModelMap model,
            Principal p
    ) {

        Match match = matchIWS.findMatchByPk(matchPk);
        User user = userIWS.getUserByUsername(p.getName());
        List<Match> matchesInUser = matchIWS.findMatchesByUserPk(user.getPk());

        if (match == null || matchesInUser.contains(match) == false) {
            throw new CustomErrorException();
        }

        List<Dispute> disputes = disputeIWS.getAllMatchDisputesByMatchPk(match.getPk());

        if (disputes.isEmpty()) {
            return "redirect:/matches/" + match.getPk();
        }

        model.addAttribute("match", match);
        model.addAttribute("disputes", disputes);
        return "matches/match-disputes";
    }

    @RequestMapping(value = "/matches/{matchPk}/accept", method = RequestMethod.GET)
    public String showTeamInviteToMatchForm(@ModelAttribute("form") Object form,
            @PathVariable Long matchPk,
            ModelMap model,
            Principal p, RedirectAttributes redirectAttributes
    ) {

        Match match = matchIWS.findMatchByPk(matchPk);

        if (match == null) {
            throw new CustomErrorException();
        }

        if (match.getMatchInfo().getMatchStatus() != MatchStatusEnum.PENDING) {
            redirectAttributes.addFlashAttribute("error", "This match is no longer available.");
            return "redirect:/matches/" + match.getPk();
        }

        List<User> userList = new ArrayList<>();
        model.addAttribute("invitedUsers", userList);

        User user = userIWS.getUserByUsername(p.getName());

        //user already in this match
        if (user != null && match != null) {
            Match m = matchIWS.findUserMatchByMatchPKAndUsername(match.getPk(), user.getUsername());
            if (m != null) {
                redirectAttributes.addFlashAttribute("error", "You are already participating in the match.");
                return "redirect:/matches/" + match.getPk();
            }
        }

//        if (match.getMatchInfo() != null && match.getMatchInfo().getMatchType() == MatchTypeEnum.XP) {
//
//            //Get my team matching this match details
//            List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(p.getName(),
//                    match.getMatchInfo().getGameEnum(),
//                    match.getMatchInfo().getPlatform(),
//                    match.getMatchInfo().getTeamSizeEnum(),
//                    match.getMatchInfo().getTeamTypeEnum(),
//                    user.getUserInfo().getRegion());
//
//            Set<Team> teamsInMatch = match.getTeamsInMatch();
//
//            Boolean isEligibleToJoin = false;
//
//            if (teams != null && !teams.isEmpty()) {
//                for (Team team : teams) {
//                    //Team is not in match yet
//                    if (!teamsInMatch.contains(team)) {
//                        isEligibleToJoin = true;
//                        Team myTeam = teams.get(0);
//                        model.addAttribute("receiverTeam", myTeam);
//
//                        List<User> teamMembers = new ArrayList<>();
//                        teamMembers = userIWS.getAllUsersOnTeam(myTeam.getPk());
//                        model.addAttribute("teamMembers", teamMembers);
//                    }
//                }
//            }
//
//            model.addAttribute("isEligibleToJoin", isEligibleToJoin);
//        } else {
        //Cash game and public
        if (match.getMatchInfo().isIsMatchPublic()) {
            System.out.println("PUBLIC");
            List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(p.getName(),
                    match.getMatchInfo().getGameEnum(),
                    match.getMatchInfo().getPlatform(),
                    match.getMatchInfo().getTeamSizeEnum(),
                    match.getMatchInfo().getTeamTypeEnum(),
                    user.getUserInfo().getRegion());
            model.addAttribute("teams", teams);
            model.addAttribute("isCashGameAndPublic", true);
            if (teams != null && teams.size() > 0) {
                model.addAttribute("isEligibleToJoin", true);
            } else {
                model.addAttribute("isEligibleToJoin", false);
            }
        } else {
            //cash game and private
            long pkOfAcceptingTeamAcceptingPrivateMatch = match.getPkOfTeamThatAcceptedMatch();
            Team teamThatAccepted = teamIWS.getTeamByPk(pkOfAcceptingTeamAcceptingPrivateMatch);
            model.addAttribute("receiverTeam", teamThatAccepted);
            List<User> teamMembers = new ArrayList<>();
            teamMembers = userIWS.getAllUsersOnTeam(teamThatAccepted.getPk());
            model.addAttribute("teamMembers", teamMembers);
            if (teamThatAccepted != null) {
                model.addAttribute("isEligibleToJoin", true);
            } else {
                model.addAttribute("isEligibleToJoin", false);
            }

            if (!match.getMatchInfo().isIsMatchPublic() && match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING) {
                List<MatchInvite> matchInvites = matchInviteIWS.findAllMatchInvitesByMatchPk(match.getPk());
                MatchInvite teamLeaderMatchInvite = null;
                for (MatchInvite mi : matchInvites) {
                    if (mi.isIsPrivateMatchInviteToLeader()) {
                        teamLeaderMatchInvite = mi;
                    }
                }

                if (match.getUsersInMatch().contains(user) || user.getPk() == teamLeaderMatchInvite.getUser().getPk()) {
                    System.out.println("User is in match or invited to match.");
                } else {
                    throw new CustomErrorException();
                }

                if (user.getPk() == teamLeaderMatchInvite.getUser().getPk()) {
                    model.addAttribute("isEligibleToJoin", true);
                } else {
                    model.addAttribute("isEligibleToJoin", false);
                }
            }
//            }

        }

        model.addAttribute("user", user);
        model.addAttribute("match", match);

        return "matches/accept-match";
    }

    @RequestMapping(value = "/matches/accept/{teamPk}/{matchPk}", method = RequestMethod.POST)
    public String createTeamInviteToMatch(@ModelAttribute("form") Object form, BindingResult bindingResult,
            @PathVariable Long teamPk,
            @RequestParam(value = "userPks[]", required = false) Long[] userPks,
            @PathVariable Long matchPk, ModelMap model,
            Principal p, Errors errors,
            RedirectAttributes redirectAttributes
    ) {

        Match match = matchIWS.findMatchByPk(matchPk);
        Team receiverTeam = teamIWS.getTeamByPk(teamPk);

        if (match == null || receiverTeam == null) {
            throw new CustomErrorException();
        }

        if (match.getMatchInfo().getMatchStatus() != MatchStatusEnum.PENDING) {
            redirectAttributes.addFlashAttribute("error", "This match is no longer available.");
            return "redirect:/matches/" + match.getPk();
        }

        if (p != null) {
            if (!hasPermission(teamPk, p.getName())) {
                redirectAttributes.addFlashAttribute("error", "You do not have permissions to accept a match for this team.");
                return "redirect:/matches/" + matchPk;
            }
        }

        User user = userIWS.getUserByUsername(p.getName());
        List<User> userList = new ArrayList<>();

        if (userPks != null) {
            for (Long userPk : userPks) {
                User user1 = userIWS.getUserByPk(userPk);
                userList.add(user1);
            }
        }

        model.addAttribute("invitedUsers", userList);

        matchFormValidator.joinMatchValidationGoToMatchDetails(userList, match, receiverTeam, bindingResult);

        matchFormValidator.joinMatchValidationGoToCreateNewTeamPage(userList, match, receiverTeam, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("match", match);
            model.addAttribute("user", user);

//            if (match.getMatchInfo() != null && match.getMatchInfo().getMatchType() == MatchTypeEnum.XP) {
//                System.out.println("XP");
//                //Get my team matching this match details
//                List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(p.getName(),
//                        match.getMatchInfo().getGameEnum(),
//                        match.getMatchInfo().getPlatform(),
//                        match.getMatchInfo().getTeamSizeEnum(),
//                        match.getMatchInfo().getTeamTypeEnum(),
//                        user.getUserInfo().getRegion());
//
//                Set<Team> teamsInMatch = match.getTeamsInMatch();
//
//                Boolean isEligibleToJoin = false;
//                if (teams != null && !teams.isEmpty()) {
//                    for (Team team : teams) {
//                        //Team is not in match yet
//                        if (!teamsInMatch.contains(team)) {
//                            isEligibleToJoin = true;
//                            Team myTeam = teams.get(0);
//                            model.addAttribute("receiverTeam", myTeam);
//
//                            List<User> teamMembers = new ArrayList<>();
//                            teamMembers = userIWS.getAllUsersOnTeam(myTeam.getPk());
//                            model.addAttribute("teamMembers", teamMembers);
//                        }
//                    }
//                }
//
//                model.addAttribute("isEligibleToJoin", isEligibleToJoin);
//            } else {
            System.out.println("CASH");
            //Cash game and public
            if (match.getMatchInfo().isIsMatchPublic()) {
                System.out.println("PUBLIC");
                List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(p.getName(),
                        match.getMatchInfo().getGameEnum(),
                        match.getMatchInfo().getPlatform(),
                        match.getMatchInfo().getTeamSizeEnum(),
                        match.getMatchInfo().getTeamTypeEnum(),
                        user.getUserInfo().getRegion());
                model.addAttribute("teams", teams);
                model.addAttribute("isCashGameAndPublic", true);
                if (teams != null && teams.size() > 0) {
                    model.addAttribute("isEligibleToJoin", true);
                }
            } else {
                //cash game and private
                long pkOfAcceptingTeamAcceptingPrivateMatch = match.getPkOfTeamThatAcceptedMatch();
                Team teamThatAccepted = teamIWS.getTeamByPk(pkOfAcceptingTeamAcceptingPrivateMatch);
                model.addAttribute("receiverTeam", teamThatAccepted);
                List<User> teamMembers = new ArrayList<>();
                teamMembers = userIWS.getAllUsersOnTeam(teamThatAccepted.getPk());
                model.addAttribute("teamMembers", teamMembers);
                if (teamThatAccepted != null) {
                    model.addAttribute("isEligibleToJoin", true);
                }
            }

//            }
        }

        if (receiverTeam.getTeamPojo().getTeamType() == TeamTypeEnum.CASH) {
            matchFormValidator.validateMatchAcceptForWager(userList.get(0), match, bindingResult);
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("match", match);
            //model.addAttribute("receiverTeam", receiverTeam);
            boolean isEligibleToJoin = false;
            if (match.getMatchInfo() != null) {
                List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(p.getName(),
                        match.getMatchInfo().getGameEnum(),
                        match.getMatchInfo().getPlatform(),
                        match.getMatchInfo().getTeamSizeEnum(),
                        match.getMatchInfo().getTeamTypeEnum(),
                        user.getUserInfo().getRegion());

                Set<Match> matches = user.getMatches();
                Set<Team> teamsInMatch = match.getTeamsInMatch();

                if (teams != null && !teams.isEmpty()) {
                    for (Team team : teams) {
                        if (match.getPkOfTeamThatAcceptedMatch() == team.getPk()) {
                            model.addAttribute("receiverTeam", team);
                        }
                    }
                }
                isEligibleToJoin = true;

            }
            model.addAttribute("isEligibleToJoin", isEligibleToJoin);
            model.addAttribute("user", user);

            return "matches/accept-match";
        }

        try {

            model.addAttribute("isEligibleToJoin", false);

            matchIWS.acceptToJoinPublicOrPrivateMatch(match, receiverTeam, userList, user);
            model.addAttribute("user", user);
            redirectAttributes.addFlashAttribute("message", "You have joined the match.");
            return "redirect:/matches/" + match.getPk();
        } catch (Exception ex) {
            System.out.println("ERROR HERE!!!!!");
            ex.printStackTrace();
            model.addAttribute("user", user);
            model.addAttribute("isEligibleToJoin", true);
            model.addAttribute("match", match);
            model.addAttribute("receiverTeam", receiverTeam);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("invitedUsers", userList);

//            if (match.getMatchInfo() != null && match.getMatchInfo().getMatchType() == MatchTypeEnum.XP) {
//                //Get my team matching this match details
//                List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(p.getName(),
//                        match.getMatchInfo().getGameEnum(),
//                        match.getMatchInfo().getPlatform(),
//                        match.getMatchInfo().getTeamSizeEnum(),
//                        match.getMatchInfo().getTeamTypeEnum(),
//                        user.getUserInfo().getRegion());
//
//                Set<Team> teamsInMatch = match.getTeamsInMatch();
//                List<User> teamMembers = new ArrayList<>();
//                Boolean isEligibleToJoin = false;
//                if (teams != null && !teams.isEmpty()) {
//                    for (Team team : teams) {
//                        //Team is not in match yet
//                        if (!teamsInMatch.contains(team)) {
//                            isEligibleToJoin = true;
//                            Team myTeam = teams.get(0);
//                            model.addAttribute("receiverTeam", myTeam);
//
//                            teamMembers = userIWS.getAllUsersOnTeam(myTeam.getPk());
//                        }
//                    }
//                }
//
//                model.addAttribute("teamMembers", teamMembers);
//                model.addAttribute("isEligibleToJoin", isEligibleToJoin);
//            } else {
            //Cash game and public
            if (match.getMatchInfo().isIsMatchPublic()) {
                System.out.println("PUBLIC");
                List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(p.getName(),
                        match.getMatchInfo().getGameEnum(),
                        match.getMatchInfo().getPlatform(),
                        match.getMatchInfo().getTeamSizeEnum(),
                        match.getMatchInfo().getTeamTypeEnum(),
                        user.getUserInfo().getRegion());
                model.addAttribute("teams", teams);
                model.addAttribute("isCashGameAndPublic", true);
                if (teams != null && teams.size() > 0) {
                    model.addAttribute("isEligibleToJoin", true);
                }
            } else {
                //cash game and private
                long pkOfAcceptingTeamAcceptingPrivateMatch = match.getPkOfTeamThatAcceptedMatch();
                Team teamThatAccepted = teamIWS.getTeamByPk(pkOfAcceptingTeamAcceptingPrivateMatch);
                model.addAttribute("receiverTeam", teamThatAccepted);
                List<User> teamMembers = new ArrayList<>();
                teamMembers = userIWS.getAllUsersOnTeam(teamThatAccepted.getPk());
                model.addAttribute("teamMembers", teamMembers);
                if (teamThatAccepted != null) {
                    model.addAttribute("isEligibleToJoin", true);
                }
            }

//            }
            return "matches/accept-match";
        }
    }

    @RequestMapping(value = "/matches/create/team-select", method = RequestMethod.GET)
    public String teamSelectForCreateMatch(ModelMap model, Principal p
    ) {
        String username = p.getName();
        List<Team> playoffTeams = teamIWS.getAllTeamsOfUserByTeamType(username, TeamTypeEnum.XP);
        List<Team> cashoutTeams = teamIWS.getAllTeamsOfUserByTeamType(username, TeamTypeEnum.CASH);
        List<Team> teams = teamIWS.getAllTeamsOfUser(username);

        model.addAttribute("teams", teams);
        model.addAttribute("playoffTeams", playoffTeams);
        model.addAttribute("cashoutTeams", cashoutTeams);
        return "matches/team-select";
    }

    @RequestMapping(value = "/matches/create/{teamPk}", method = RequestMethod.GET)
    public String createMatchForm(@ModelAttribute("matchForm") MatchForm matchForm,
            @PathVariable Long teamPk,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes, ModelMap model,
            Principal p
    ) {

        String username = p.getName();
        Team team = teamIWS.getTeamByPk(teamPk);
        System.out.println("USERNAME: " + username);
        if (team != null) {
            System.out.println("TEAM PK: " + team.getPk());
        }
        List<Team> teams = teamIWS.getAllTeamsOfUser(username);
        System.out.println("TEAM SIZE: " + teams.size());

        if (team == null || teams.contains(team) == false) {
            throw new CustomErrorException();
        }

        //check team member permissions
        //before allowing to create match
        if (!hasPermission(teamPk, username)) {
            redirectAttributes.addFlashAttribute("messageFailure", "You do not have permissions to create a match with this team.");
            return "redirect:/matches/create/team-select";
        }

        List<User> teamMembers = new ArrayList<>();
        teamMembers = userIWS.getAllUsersOnTeam(teamPk);

        model.addAttribute("teamMembers", teamMembers);
        model.addAttribute("teamPk", teamPk);
        model.addAttribute("team", team);

        if (team != null && team.getTeamPojo() != null) {
            if (team.getTeamPojo().getTeamType() == TeamTypeEnum.XP) {
                model.addAttribute("isTeamTypeXP", true);
            } else if (team.getTeamPojo().getTeamType() == TeamTypeEnum.CASH) {
                model.addAttribute("isTeamTypeXP", false);
            }
        }

        return "matches/create-match";
    }

    @RequestMapping(value = "/matches/create/{teamPk}", method = RequestMethod.POST)
    public String saveMatch(@Valid
            @ModelAttribute("matchForm") MatchForm matchForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            ModelMap model,
            @PathVariable Long teamPk, Principal p
    ) {
        String username = p.getName();
        User u = userIWS.getUserByUsername(username);
        Team senderTeam = teamIWS.getTeamByPk(teamPk);

        List<Team> teams = teamIWS.getAllTeamsOfUser(username);

        if (senderTeam == null || teams.contains(senderTeam) == false) {
            throw new CustomErrorException();
        }

        matchFormValidator.matchCreationValidation(matchForm, senderTeam, u, bindingResult);
        Team receiverTeam = null;
        String receiverTeamName;
        if (senderTeam.getTeamPojo().getTeamType() == TeamTypeEnum.CASH) {
            if (matchForm != null && matchForm.getMatchPublic() != null && matchForm.getMatchPublic() == false) {
                receiverTeamName = matchForm.getOpponentTeamName();
                if (receiverTeamName != null && !receiverTeamName.isEmpty()) {
                    receiverTeam = teamValidator.findTeamForMatchValidation(receiverTeamName, senderTeam.getTeamPojo(), bindingResult);
                }
            }
        }

        model.addAttribute("teamPk", teamPk);
        model.addAttribute("team", senderTeam);
        if (senderTeam != null) {
            List<User> teamMembers = userIWS.getAllUsersOnTeam(senderTeam.getPk());
            model.addAttribute("teamMembers", teamMembers);
            if (senderTeam.getTeamPojo() != null) {
                if (senderTeam.getTeamPojo().getTeamType() == TeamTypeEnum.XP) {
                    model.addAttribute("isTeamTypeXP", true);
                } else if (senderTeam.getTeamPojo().getTeamType() == TeamTypeEnum.CASH) {
                    model.addAttribute("isTeamTypeXP", false);
                }
            }
        }

        if (bindingResult.hasErrors()) {

            return "matches/create-match";
        }

        try {
            List<User> users = new ArrayList<>();

            for (long userPk : matchForm.getUsersInMatch()) {
                User user = userIWS.getUserByPk(userPk);

                if (user != null) {
                    users.add(user);
                    System.out.println("Username being added-------------------------------: " + user.getUsername());
                }
            }
            Match match = null;
            MatchPojo matchInfo = MatchForm.toMatchPojo(u, matchForm, senderTeam);
            if (matchInfo.getGameEnum() == GameEnum.COD_MWR) {
                MWRMatchPojo mwrMatchInfo = MatchForm.toMWRMatchPojo(matchForm);
                match = matchIWS.createMatch(matchInfo, mwrMatchInfo, senderTeam, users, receiverTeam, u);
            } else if (matchInfo.getGameEnum() == GameEnum.COD_IW) {
                IWMatchPojo iwMatchInfo = MatchForm.toIWMatchPojo(matchForm);
                match = matchIWS.createMatch(matchInfo, iwMatchInfo, senderTeam, users, receiverTeam, u);
            } else if (matchInfo.getGameEnum() == GameEnum.COD_WW2) {
                WW2MatchPojo ww2MatchInfo = MatchForm.toWW2MatchPojo(matchForm);
                match = matchIWS.createMatch(matchInfo, ww2MatchInfo, senderTeam, users, receiverTeam, u);
            }
            redirectAttributes.addFlashAttribute("message", "You have created a match.");
            return "redirect:/matches/" + match.getPk();

        } catch (Exception ex) {
            System.out.println("ERROR HERE!!!!!!!");
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            model.addAttribute("error", ex.getMessage());

            Team team = teamIWS.getTeamByPk(teamPk);

            if (team != null) {
                List<User> teamMembers = new ArrayList<>();
                teamMembers = userIWS.getAllUsersOnTeam(teamPk);

                model.addAttribute("teamMembers", teamMembers);
            }
            return "matches/create-match";
        }
    }

    @RequestMapping(value = "/matches/{matchPk}/create-dispute", method = RequestMethod.GET)
    public String createMatchDispute(@ModelAttribute("createDisputeForm") DisputeForm createDisputeForm,
            @PathVariable Long matchPk, ModelMap model,
            Principal p,
            RedirectAttributes redirectAttributes
    ) {
        User user = userIWS.getUserByUsername(p.getName());
        Match match = matchIWS.findMatchByPk(matchPk);

        if (MatchStatusEnum.ACTIVE == match.getMatchInfo().getMatchStatus()) {
            model.addAttribute("canReportScore", true);
        }

        //reported score
        if (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ACTIVE) {
            if (match.getPksOfCreatorTeamMembersPlaying() != null && match.getPksOfCreatorTeamMembersPlaying().contains(user.getPk())) {
                if (match.getReportedScoreOfTeamCreated() != null && (match.getReportedScoreOfTeamCreated() == true || match.getReportedScoreOfTeamCreated() == false)) {
                    model.addAttribute("reportedScoreByMyTeam", 0);   //creator reported
                    model.addAttribute("canDisputeMatch", false);
                    model.addAttribute("canReportScore", false);
                }
            } else if (match.getPksOfAcceptorTeamMembersPlaying() != null && match.getPksOfAcceptorTeamMembersPlaying().contains(user.getPk())) {
                if (match.getReportedScoreOfTeamAccepted() != null && (match.getReportedScoreOfTeamAccepted() == true || match.getReportedScoreOfTeamAccepted() == false)) {
                    model.addAttribute("reportedScoreByMyTeam", 1);   //acceptor reported
                    model.addAttribute("canDisputeMatch", false);
                    model.addAttribute("canReportScore", false);
                }
            }
        }

        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
            model.addAttribute("isTournamentMatch", true);
        } else {
            model.addAttribute("isTournamentMatch", false);
        }
        List<Team> teamsInMatch = teamIWS.findTeamsByMatchPk(matchPk);

        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }

        if (match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT && match.getUsersInMatch().contains(user) && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_SECOND_ACCEPT || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT)) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }

        if (match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ACTIVE || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.DISPUTED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.READY_TO_PLAY || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ENDED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.EXPIRED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.CANCELLED)) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }

        if (match == null) {
            throw new CustomErrorException();
        }

        if (user != null) {
            Long userPk = user.getPk();
            Dispute dispute = disputeIWS.getDisputeForUserAndMatchPk(userPk, matchPk);

            if (dispute != null) {
                //already exist a dispute from current user
                redirectAttributes.addFlashAttribute("error", "You have already created a dispute for the match.");
                return "redirect:/matches/" + matchPk;
            }
        }

        if (match != null) {
            model.addAttribute("match", match);
        }
        model.addAttribute("matchPk", matchPk);
        return "disputes/create-dispute";
    }

    @RequestMapping(value = "/matches/{matchPk}/create-dispute", method = RequestMethod.POST)
    public String saveMatchDispute(@Valid
            @ModelAttribute("createDisputeForm") DisputeForm createDisputeForm, BindingResult bindingResult,
            @PathVariable Long matchPk, ModelMap model,
            Principal p,
            RedirectAttributes redirectAttributes
    ) {

        User user = userIWS.getUserByUsername(p.getName());
        Match match = matchIWS.findMatchByPk(matchPk);

        if (match == null) {
            throw new CustomErrorException();
        }

        if (match.getMatchInfo().getMatchStatus() != MatchStatusEnum.ACTIVE && match.getMatchInfo().getMatchStatus() != MatchStatusEnum.DISPUTED) {
            redirectAttributes.addFlashAttribute("error", "This match has already ended.");
            return "redirect:/matches/" + matchPk;
        }

        if (user != null) {
            Long userPk = user.getPk();
            Dispute dispute = disputeIWS.getDisputeForUserAndMatchPk(userPk, matchPk);

            if (dispute != null) {
                //already exist a dispute from current user
                redirectAttributes.addFlashAttribute("error", "You have already created a dispute for the match.");
                return "redirect:/matches/" + matchPk;
            }
        }

        disputeFormValidator.validate(createDisputeForm, bindingResult);

        if (bindingResult.hasErrors()) {
            if (match != null) {
                model.addAttribute("match", match);
            }
            if (MatchStatusEnum.ACTIVE == match.getMatchInfo().getMatchStatus()) {
                model.addAttribute("canReportScore", true);
            }

            return "disputes/create-dispute";
        }

        try {
            if (user != null) {
                Long teamPk = null;
                List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(user.getUsername(), match.getMatchInfo().getGameEnum(), match.getMatchInfo().getPlatform(), match.getMatchInfo().getTeamSizeEnum(), match.getMatchInfo().getTeamTypeEnum(), user.getUserInfo().getRegion());
                for (Team t : teams) {
                    if (t.getPk() == match.getPkOfTeamThatCreatedMatch()) {
                        if (match.getPksOfCreatorTeamMembersPlaying().contains(user.getPk())) {
                            teamPk = match.getPkOfTeamThatCreatedMatch();
                        }
                    } else if (t.getPk() == match.getPkOfTeamThatAcceptedMatch()) {
                        if (match.getPksOfAcceptorTeamMembersPlaying().contains(user.getPk())) {
                            teamPk = match.getPkOfTeamThatAcceptedMatch();
                        }
                    }
                }

                if (p != null) {
                    if (!hasPermission(teamPk, p.getName())) {
                        redirectAttributes.addFlashAttribute("error", "You do not have permissions to create a dispute for this match.");
                        return "redirect:/matches/" + matchPk;
                    }
                }
                List<String> links = new ArrayList<>();
                if (createDisputeForm != null) {
                    if (createDisputeForm.getUrl1() != null && !createDisputeForm.getUrl1().equals("")) {
                        links.add(createDisputeForm.getUrl1());
                    }
                    if (createDisputeForm.getUrl2() != null && !createDisputeForm.getUrl2().equals("")) {
                        links.add(createDisputeForm.getUrl2());
                    }
                    if (createDisputeForm.getUrl3() != null && !createDisputeForm.getUrl3().equals("")) {
                        links.add(createDisputeForm.getUrl3());
                    }
                    if (createDisputeForm.getUrl4() != null && !createDisputeForm.getUrl4().equals("")) {
                        links.add(createDisputeForm.getUrl4());
                    }
                }

                disputeIWS.createDispute(user.getPk(), matchPk, teamPk, createDisputeForm.getReason(), links, false);
            }

            redirectAttributes.addFlashAttribute("message", "You have submitted a dispute for this match.");
            return "redirect:/matches/" + matchPk;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (match != null) {
                model.addAttribute("match", match);
            }
            if (MatchStatusEnum.ACTIVE == match.getMatchInfo().getMatchStatus()) {
                model.addAttribute("canReportScore", true);
            }
            System.out.println(ex.getMessage());
            model.addAttribute("error", ex.getMessage());
            return "disputes/create-dispute";
        }
    }

    @RequestMapping(value = "/matches/{matchPk}/request-cancel", method = RequestMethod.GET)
    public String requestMatchCancel(@PathVariable Long matchPk, ModelMap model,
            Principal p,
            RedirectAttributes redirectAttributes
    ) {

        Match match = matchIWS.findMatchByPk(matchPk);
        User user = userIWS.getUserByUsername(p.getName());
        List<Match> userAllmatches = matchIWS.findMatchesByUserPk(user.getPk());

        if (match == null && userAllmatches.contains(match) == false) {
            throw new CustomErrorException();
        }

        try {
            matchIWS.requestCancellation(matchPk, p.getName());
            redirectAttributes.addFlashAttribute("message", "Your match cancellation request has been sent to the other teams' leader.");
            return "redirect:/matches/" + matchPk;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Error processing cancellation request. " + ex.getMessage());
            return "redirect:/matches/" + matchPk;
        }
    }

    private Boolean hasPermission(Long teamPk, String username) {
        Team team = teamIWS.getTeamByPk(teamPk);
        User user = userIWS.getUserByUsername(username);

        if (team.getTeamLeaderPk() != user.getPk()) {
            return false;
        }
        return true;
    }
}
