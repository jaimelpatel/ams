/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.mwr.MWRTeam;
import com.ltlogic.db.entity.ww2.WW2Team;
import com.ltlogic.iws.RankIWS;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author raymond
 */
@Controller
public class LeaderBoardsController {

    @Autowired
    RankIWS rankIWS;

    @RequestMapping(value = "/leaderboards", method = RequestMethod.GET)
    public String leaderBoard(ModelMap model) {

        List<User> userRanksByXP = new ArrayList<>();
        userRanksByXP = rankIWS.getTop100UsersByXP();
        List<User> userRanksByEarnings = new ArrayList<>();
        userRanksByEarnings = rankIWS.getTop100UsersByEarnings();

        if (userRanksByXP.size() == 1) {
            model.addAttribute("xpFirst", userRanksByXP.get(0).getUsername());
        } else if (userRanksByXP.size() == 2) {
            model.addAttribute("xpFirst", userRanksByXP.get(0).getUsername());
            model.addAttribute("xpSecond", userRanksByXP.get(1).getUsername());
        } else if (userRanksByXP.size() >= 3) {
            model.addAttribute("xpFirst", userRanksByXP.get(0).getUsername());
            model.addAttribute("xpSecond", userRanksByXP.get(1).getUsername());
            model.addAttribute("xpThird", userRanksByXP.get(2).getUsername());
        }

        if (userRanksByEarnings.size() == 1) {
            model.addAttribute("cashFirst", userRanksByEarnings.get(0).getUsername());
        } else if (userRanksByEarnings.size() == 2) {
            model.addAttribute("cashFirst", userRanksByEarnings.get(0).getUsername());
            model.addAttribute("cashSecond", userRanksByEarnings.get(1).getUsername());
        } else if (userRanksByEarnings.size() >= 3) {
            model.addAttribute("cashFirst", userRanksByEarnings.get(0).getUsername());
            model.addAttribute("cashSecond", userRanksByEarnings.get(1).getUsername());
            model.addAttribute("cashThird", userRanksByEarnings.get(2).getUsername());
        }

        model.addAttribute("userRanksByEarnings", userRanksByEarnings);
        model.addAttribute("userRanksByXP", userRanksByXP);
        return "leaderboards/index";
    }

    @RequestMapping(value = "/leaderboards/earnings", method = RequestMethod.GET)
    public String leaderBoardByEarnings(ModelMap model) {

        List<User> userRanksByEarnings = new ArrayList<>();

        userRanksByEarnings = rankIWS.getTop100UsersByEarnings();

        model.addAttribute("userRanksByEarnings", userRanksByEarnings);
        return "leaderboards/earnings";
    }

    @RequestMapping(value = "/leaderboards/mwr/ps4", method = RequestMethod.GET)
    public String leaderBoardByTeamMWRAndPs4(ModelMap model) {

        List<MWRTeam> xpSingles = rankIWS.getTop100MWRTeamsByXP(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.SINGLES, TeamTypeEnum.XP);
        List<MWRTeam> xpDoubles = rankIWS.getTop100MWRTeamsByXP(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.XP);
        List<MWRTeam> xpTeam = rankIWS.getTop100MWRTeamsByXP(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.TEAM, TeamTypeEnum.XP);
        List<MWRTeam> cashSingles = rankIWS.getTop100MWRTeamsByXP(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.SINGLES, TeamTypeEnum.CASH);
        List<MWRTeam> cashDoubles = rankIWS.getTop100MWRTeamsByXP(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH);
        List<MWRTeam> cashTeam = rankIWS.getTop100MWRTeamsByXP(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.TEAM, TeamTypeEnum.CASH);

        model.addAttribute("xpSingles", xpSingles);
        model.addAttribute("xpDoubles", xpDoubles);
        model.addAttribute("xpTeam", xpTeam);
        model.addAttribute("cashSingles", cashSingles);
        model.addAttribute("cashDoubles", cashDoubles);
        model.addAttribute("cashTeam", cashTeam);

        return "leaderboards/mwr-ps4";
    }

    @RequestMapping(value = "/leaderboards/mwr/xbox1", method = RequestMethod.GET)
    public String leaderBoardByTeamMWRAndXb1(ModelMap model) {

        List<MWRTeam> xpSingles = rankIWS.getTop100MWRTeamsByXP(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.SINGLES, TeamTypeEnum.XP);
        List<MWRTeam> xpDoubles = rankIWS.getTop100MWRTeamsByXP(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.DOUBLES, TeamTypeEnum.XP);
        List<MWRTeam> xpTeam = rankIWS.getTop100MWRTeamsByXP(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.TEAM, TeamTypeEnum.XP);
        List<MWRTeam> cashSingles = rankIWS.getTop100MWRTeamsByXP(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.SINGLES, TeamTypeEnum.CASH);
        List<MWRTeam> cashDoubles = rankIWS.getTop100MWRTeamsByXP(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH);
        List<MWRTeam> cashTeam = rankIWS.getTop100MWRTeamsByXP(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.TEAM, TeamTypeEnum.CASH);

        model.addAttribute("xpSingles", xpSingles);
        model.addAttribute("xpDoubles", xpDoubles);
        model.addAttribute("xpTeam", xpTeam);
        model.addAttribute("cashSingles", cashSingles);
        model.addAttribute("cashDoubles", cashDoubles);
        model.addAttribute("cashTeam", cashTeam);
        return "leaderboards/mwr-xb1";
    }

    @RequestMapping(value = "/leaderboards/ww2/ps4", method = RequestMethod.GET)
    public String leaderBoardByTeamWW2AndPs4(ModelMap model) {
        List<WW2Team> xpSingles = rankIWS.getTop100WW2TeamsByXP(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.SINGLES, TeamTypeEnum.XP);
        List<WW2Team> xpDoubles = rankIWS.getTop100WW2TeamsByXP(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.XP);
        List<WW2Team> xpTeam = rankIWS.getTop100WW2TeamsByXP(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.TEAM, TeamTypeEnum.XP);
        List<WW2Team> cashSingles = rankIWS.getTop100WW2TeamsByXP(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.SINGLES, TeamTypeEnum.CASH);
        List<WW2Team> cashDoubles = rankIWS.getTop100WW2TeamsByXP(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH);
        List<WW2Team> cashTeam = rankIWS.getTop100WW2TeamsByXP(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.TEAM, TeamTypeEnum.CASH);

        model.addAttribute("xpSingles", xpSingles);
        model.addAttribute("xpDoubles", xpDoubles);
        model.addAttribute("xpTeam", xpTeam);
        model.addAttribute("cashSingles", cashSingles);
        model.addAttribute("cashDoubles", cashDoubles);
        model.addAttribute("cashTeam", cashTeam);
        return "leaderboards/ww2-ps4";
    }

    @RequestMapping(value = "/leaderboards/ww2/xbox1", method = RequestMethod.GET)
    public String leaderBoardByTeamWW2AndXb1(ModelMap model) {
        List<WW2Team> xpSingles = rankIWS.getTop100WW2TeamsByXP(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.SINGLES, TeamTypeEnum.XP);
        List<WW2Team> xpDoubles = rankIWS.getTop100WW2TeamsByXP(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.DOUBLES, TeamTypeEnum.XP);
        List<WW2Team> xpTeam = rankIWS.getTop100WW2TeamsByXP(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.TEAM, TeamTypeEnum.XP);
        List<WW2Team> cashSingles = rankIWS.getTop100WW2TeamsByXP(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.SINGLES, TeamTypeEnum.CASH);
        List<WW2Team> cashDoubles = rankIWS.getTop100WW2TeamsByXP(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH);
        List<WW2Team> cashTeam = rankIWS.getTop100WW2TeamsByXP(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.TEAM, TeamTypeEnum.CASH);

        model.addAttribute("xpSingles", xpSingles);
        model.addAttribute("xpDoubles", xpDoubles);
        model.addAttribute("xpTeam", xpTeam);
        model.addAttribute("cashSingles", cashSingles);
        model.addAttribute("cashDoubles", cashDoubles);
        model.addAttribute("cashTeam", cashTeam);
        return "leaderboards/ww2-xb1";
    }
}
