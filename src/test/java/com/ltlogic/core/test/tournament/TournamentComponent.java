/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.core.test.tournament;

import com.ltlogic.constants.BestOfEnum;
import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.MatchSizeEnum;
import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamStatusEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.constants.TournamentFormatEnum;
import com.ltlogic.constants.TournamentStatusEnum;
import com.ltlogic.core.test.team.TeamComponent;
import com.ltlogic.core.test.user.UserComponent;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentInvite;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.entity.User;
import com.ltlogic.iws.MatchIWS;
import com.ltlogic.iws.MatchInviteIWS;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.iws.TeamInviteIWS;
import com.ltlogic.iws.TournamentInviteIWS;
import com.ltlogic.iws.TournamentServiceIWS;
import com.ltlogic.iws.TournamentTeamIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.TournamentPojo;
import com.ltlogic.service.challonge.ChallongeMatchService;
import com.ltlogic.service.challonge.ChallongeParticipantService;
import com.ltlogic.service.challonge.ChallongeTournamentService;
import com.ltlogic.service.core.MatchService;
import com.ltlogic.service.core.TeamService;
import com.ltlogic.service.core.TournamentService;
import com.ltlogic.service.core.TournamentTeamService;
import com.ltlogic.service.springsecurity.TeamValidator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Hoang
 */
@Component
@Transactional
public class TournamentComponent {

    @Autowired
    TournamentService tournamentService;
    @Autowired
    TournamentServiceIWS tournamentServiceIWS;
    @Autowired
    TournamentTeamService tournamentTeamService;
    @Autowired
    TournamentTeamIWS tournamentTeamIWS;
    @Autowired
    TournamentInviteIWS tournamentInviteIWS;
    @Autowired
    ChallongeMatchService challongeMatchService;
    @Autowired
    ChallongeParticipantService challongeParticipantService;
    @Autowired
    ChallongeTournamentService challongeTournamentService;
    @Autowired
    TeamService teamService;
    @Autowired
    TeamIWS teamIWS;
    @Autowired
    TeamInviteIWS teamInviteIWS;
    @Autowired
    MatchInviteIWS matchInviteIWS;
    @Autowired
    TeamValidator teamValidator;
    @Autowired
    UserIWS userIWS;
    @Autowired
    MatchService matchService;
    @Autowired
    MatchIWS matchIWS;
    @Autowired
    UserComponent userComponent;
    @Autowired
    TeamComponent teamComponent;

    private static final Logger LOG = LoggerFactory.getLogger(TournamentComponent.class);

    public Tournament oneTeamsFullyAccepts() throws Exception {
        int numOfUsersAndTeams = 10;
        teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        List<User> usersInJoiningTeam = new ArrayList<>();
        usersInJoiningTeam.addAll(team1.getMembers());
        User userJoiningTournamentForTeam = userIWS.getUserByPk(team1.getTeamLeaderPk());

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");
        for (User user : usersInJoiningTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getTournaments().isEmpty());
        }

        Tournament tournament = tournamentService.createTournamentForTest(constructTournamentPojo());
        TournamentPojo tournamentInfo = tournament.getTournamentInfo();

        tournamentService.joinTournament(team1, tournament, usersInJoiningTeam, userJoiningTournamentForTeam);
        TournamentTeam tournamentTeamJoiningTournament = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team1.getTeamPojo().getTeamName(), tournament);

        //assert user joining tournament for team gets deducted wager amount
        Assert.assertTrue(tournament.getUsersInTournament().contains(userJoiningTournamentForTeam));
        Assert.assertTrue(tournament.getTournamentTeams().contains(tournamentTeamJoiningTournament));
        BigDecimal balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
        Assert.assertTrue(balanceAfterJoiningTournament.compareTo(userJoiningTournamentForTeam.getBank().getTotalAmount()) == 0);
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.PENDING);

        //accept tournament invite for everyone on team except except joiner who already accepted
        List<User> usersInTournamentTeamWithoutJoiner = new ArrayList<>(usersInJoiningTeam);
        usersInTournamentTeamWithoutJoiner.remove(userJoiningTournamentForTeam);
        for (int i = 0; i < usersInTournamentTeamWithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeamJoiningTournament.getPk(), usersInTournamentTeamWithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
        }

        //deduct money for all joiners
        for (User user : usersInJoiningTeam) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }

        tournamentService.startTournamentsForTest();

        //since only 1 team joined and there are not enough teams when tournament is about to start, tournament should be cancelled and all team members should get refund
        for (User user : usersInJoiningTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(tournament.getUsersInTournament().contains(user));
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.CANCELLED);

        return tournament;
    }

    public Tournament oneTeamsFullyAcceptsFiveTeamsPartiallyAccept() throws Exception {
        int numOfUsersAndTeams = 10;
        teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team3 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team2", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team4 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team3", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team5 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team4", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        List<User> usersInJoiningTeam1 = new ArrayList<>();
        usersInJoiningTeam1.addAll(team1.getMembers());
        User userJoiningTournamentForTeam1 = userIWS.getUserByPk(team1.getTeamLeaderPk());

        List<User> usersInJoiningTeam2 = new ArrayList<>();
        usersInJoiningTeam2.addAll(team2.getMembers());
        User userJoiningTournamentForTeam2 = userIWS.getUserByPk(team2.getTeamLeaderPk());

        List<User> usersInJoiningTeam3 = new ArrayList<>();
        usersInJoiningTeam3.addAll(team3.getMembers());
        User userJoiningTournamentForTeam3 = userIWS.getUserByPk(team3.getTeamLeaderPk());

        List<User> usersInJoiningTeam4 = new ArrayList<>();
        usersInJoiningTeam4.addAll(team4.getMembers());
        User userJoiningTournamentForTeam4 = userIWS.getUserByPk(team4.getTeamLeaderPk());

        List<User> usersInJoiningTeam5 = new ArrayList<>();
        usersInJoiningTeam5.addAll(team5.getMembers());
        User userJoiningTournamentForTeam5 = userIWS.getUserByPk(team5.getTeamLeaderPk());

        List<User> usersOnTeamsJoiningTournament = new ArrayList<>();
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam1);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam2);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam3);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam4);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam5);

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");

        for (User user : usersOnTeamsJoiningTournament) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getTournaments().isEmpty());
        }

        Tournament tournament = tournamentService.createTournamentForTest(constructTournamentPojo());
        TournamentPojo tournamentInfo = tournament.getTournamentInfo();

        BigDecimal balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
        List<User> usersWhoAcceptedTournamentInvite = new ArrayList<>();

        List<User> teamLeadersJoiningTournament = new ArrayList<>();
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam1);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam2);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam3);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam4);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam5);

        tournamentService.joinTournament(team1, tournament, usersInJoiningTeam1, userJoiningTournamentForTeam1);
        tournamentService.joinTournament(team2, tournament, usersInJoiningTeam2, userJoiningTournamentForTeam2);
        tournamentService.joinTournament(team3, tournament, usersInJoiningTeam3, userJoiningTournamentForTeam3);
        tournamentService.joinTournament(team4, tournament, usersInJoiningTeam4, userJoiningTournamentForTeam4);
        tournamentService.joinTournament(team5, tournament, usersInJoiningTeam5, userJoiningTournamentForTeam5);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam1);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam2);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam3);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam4);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam5);

        TournamentTeam tournamentTeam1 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team1.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam2 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team2.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam3 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team3.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam4 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team4.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam5 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team5.getTeamPojo().getTeamName(), tournament);

        List<TournamentTeam> tournamentTeamsInTournament = new ArrayList<>();
        tournamentTeamsInTournament.add(tournamentTeam1);
        tournamentTeamsInTournament.add(tournamentTeam2);
        tournamentTeamsInTournament.add(tournamentTeam3);
        tournamentTeamsInTournament.add(tournamentTeam4);
        tournamentTeamsInTournament.add(tournamentTeam5);

        //assert users joining tournament for team gets deducted wager amount
        for (User user : teamLeadersJoiningTournament) {
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
            balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
        }
        for (TournamentTeam tournamentTeam : tournamentTeamsInTournament) {
            Assert.assertTrue(tournament.getTournamentTeams().contains(tournamentTeam));
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.PENDING);

        //fully accept tournament invite for everyone on team 1 
        List<User> usersInTournamentTeam1WithoutJoiner = new ArrayList<>(usersInJoiningTeam1);
        usersInTournamentTeam1WithoutJoiner.remove(userJoiningTournamentForTeam1);
        for (int i = 0; i < usersInTournamentTeam1WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam1.getPk(), usersInTournamentTeam1WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam1WithoutJoiner.get(i));
        }

        //accept tournament invite for each team but not fully
        List<User> usersInTournamentTeam2WithoutJoinerAndLastMember = new ArrayList<>(usersInJoiningTeam2);
        usersInTournamentTeam2WithoutJoinerAndLastMember.remove(userJoiningTournamentForTeam2);
        User lastMemberOnTeam2 = usersInTournamentTeam2WithoutJoinerAndLastMember.get(usersInTournamentTeam2WithoutJoinerAndLastMember.size() - 1);
        usersInTournamentTeam2WithoutJoinerAndLastMember.remove(lastMemberOnTeam2);
        for (int i = 0; i < usersInTournamentTeam2WithoutJoinerAndLastMember.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam2.getPk(), usersInTournamentTeam2WithoutJoinerAndLastMember.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam2WithoutJoinerAndLastMember.get(i));
        }
        List<User> usersInTournamentTeam3WithoutJoinerAndLastMember = new ArrayList<>(usersInJoiningTeam3);
        usersInTournamentTeam3WithoutJoinerAndLastMember.remove(userJoiningTournamentForTeam3);
        User lastMemberOnTeam3 = usersInTournamentTeam3WithoutJoinerAndLastMember.get(usersInTournamentTeam3WithoutJoinerAndLastMember.size() - 1);
        usersInTournamentTeam3WithoutJoinerAndLastMember.remove(lastMemberOnTeam3);
        for (int i = 0; i < usersInTournamentTeam3WithoutJoinerAndLastMember.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam3.getPk(), usersInTournamentTeam3WithoutJoinerAndLastMember.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam3WithoutJoinerAndLastMember.get(i));
        }
        List<User> usersInTournamentTeam4WithoutJoinerAndLastMember = new ArrayList<>(usersInJoiningTeam4);
        usersInTournamentTeam4WithoutJoinerAndLastMember.remove(userJoiningTournamentForTeam4);
        User lastMemberOnTeam4 = usersInTournamentTeam4WithoutJoinerAndLastMember.get(usersInTournamentTeam4WithoutJoinerAndLastMember.size() - 1);
        usersInTournamentTeam4WithoutJoinerAndLastMember.remove(lastMemberOnTeam4);
        for (int i = 0; i < usersInTournamentTeam4WithoutJoinerAndLastMember.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam4.getPk(), usersInTournamentTeam4WithoutJoinerAndLastMember.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam4WithoutJoinerAndLastMember.get(i));
        }
        List<User> usersInTournamentTeam5WithoutJoinerAndLastMember = new ArrayList<>(usersInJoiningTeam5);
        usersInTournamentTeam5WithoutJoinerAndLastMember.remove(userJoiningTournamentForTeam5);
        User lastMemberOnTeam5 = usersInTournamentTeam5WithoutJoinerAndLastMember.get(usersInTournamentTeam5WithoutJoinerAndLastMember.size() - 1);
        usersInTournamentTeam5WithoutJoinerAndLastMember.remove(lastMemberOnTeam5);
        for (int i = 0; i < usersInTournamentTeam5WithoutJoinerAndLastMember.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam5.getPk(), usersInTournamentTeam5WithoutJoinerAndLastMember.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam5WithoutJoinerAndLastMember.get(i));
        }

        //deduct money for all joiners
        for (User user : usersWhoAcceptedTournamentInvite) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }

        tournamentService.startTournamentsForTest();

        //since only 1 team fully accepted and there are 4 teams who partially accepted, and there are not enough teams when tournament is about to start, tournament should be cancelled and all team members should get refund
        for (User user : usersWhoAcceptedTournamentInvite) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(tournament.getUsersInTournament().contains(user));
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.CANCELLED);

        return tournament;
    }

    public Tournament fiveTeamsPartiallyAccept() throws Exception {
        int numOfUsersAndTeams = 10;
        teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team3 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team2", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team4 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team3", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team5 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team4", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        List<User> usersInJoiningTeam1 = new ArrayList<>();
        usersInJoiningTeam1.addAll(team1.getMembers());
        User userJoiningTournamentForTeam1 = userIWS.getUserByPk(team1.getTeamLeaderPk());

        List<User> usersInJoiningTeam2 = new ArrayList<>();
        usersInJoiningTeam2.addAll(team2.getMembers());
        User userJoiningTournamentForTeam2 = userIWS.getUserByPk(team2.getTeamLeaderPk());

        List<User> usersInJoiningTeam3 = new ArrayList<>();
        usersInJoiningTeam3.addAll(team3.getMembers());
        User userJoiningTournamentForTeam3 = userIWS.getUserByPk(team3.getTeamLeaderPk());

        List<User> usersInJoiningTeam4 = new ArrayList<>();
        usersInJoiningTeam4.addAll(team4.getMembers());
        User userJoiningTournamentForTeam4 = userIWS.getUserByPk(team4.getTeamLeaderPk());

        List<User> usersInJoiningTeam5 = new ArrayList<>();
        usersInJoiningTeam5.addAll(team5.getMembers());
        User userJoiningTournamentForTeam5 = userIWS.getUserByPk(team5.getTeamLeaderPk());

        List<User> usersOnTeamsJoiningTournament = new ArrayList<>();
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam1);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam2);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam3);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam4);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam5);

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");

        for (User user : usersOnTeamsJoiningTournament) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getTournaments().isEmpty());
        }

        Tournament tournament = tournamentService.createTournamentForTest(constructTournamentPojo());
        TournamentPojo tournamentInfo = tournament.getTournamentInfo();

        BigDecimal balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
        List<User> usersWhoAcceptedTournamentInvite = new ArrayList<>();

        List<User> teamLeadersJoiningTournament = new ArrayList<>();
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam1);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam2);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam3);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam4);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam5);

        tournamentService.joinTournament(team1, tournament, usersInJoiningTeam1, userJoiningTournamentForTeam1);
        tournamentService.joinTournament(team2, tournament, usersInJoiningTeam2, userJoiningTournamentForTeam2);
        tournamentService.joinTournament(team3, tournament, usersInJoiningTeam3, userJoiningTournamentForTeam3);
        tournamentService.joinTournament(team4, tournament, usersInJoiningTeam4, userJoiningTournamentForTeam4);
        tournamentService.joinTournament(team5, tournament, usersInJoiningTeam5, userJoiningTournamentForTeam5);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam1);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam2);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam3);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam4);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam5);

        TournamentTeam tournamentTeam1 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team1.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam2 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team2.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam3 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team3.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam4 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team4.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam5 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team5.getTeamPojo().getTeamName(), tournament);

        List<TournamentTeam> tournamentTeamsInTournament = new ArrayList<>();
        tournamentTeamsInTournament.add(tournamentTeam1);
        tournamentTeamsInTournament.add(tournamentTeam2);
        tournamentTeamsInTournament.add(tournamentTeam3);
        tournamentTeamsInTournament.add(tournamentTeam4);
        tournamentTeamsInTournament.add(tournamentTeam5);

        //assert users joining tournament for team gets deducted wager amount
        for (User user : teamLeadersJoiningTournament) {
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
            balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
        }
        for (TournamentTeam tournamentTeam : tournamentTeamsInTournament) {
            Assert.assertTrue(tournament.getTournamentTeams().contains(tournamentTeam));
        }

        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.PENDING);

        //accept tournament invite for each team but not fully
        List<User> usersInTournamentTeam1WithoutJoinerAndLastMember = usersInJoiningTeam1;
        usersInTournamentTeam1WithoutJoinerAndLastMember.remove(userJoiningTournamentForTeam1);
        User lastMemberOnTeam1 = usersInTournamentTeam1WithoutJoinerAndLastMember.get(usersInTournamentTeam1WithoutJoinerAndLastMember.size() - 1);
        usersInTournamentTeam1WithoutJoinerAndLastMember.remove(lastMemberOnTeam1);
        for (int i = 0; i < usersInTournamentTeam1WithoutJoinerAndLastMember.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam1.getPk(), usersInTournamentTeam1WithoutJoinerAndLastMember.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam1WithoutJoinerAndLastMember.get(i));
        }
        List<User> usersInTournamentTeam2WithoutJoinerAndLastMember = new ArrayList<>(usersInJoiningTeam2);
        usersInTournamentTeam2WithoutJoinerAndLastMember.remove(userJoiningTournamentForTeam2);
        User lastMemberOnTeam2 = usersInTournamentTeam2WithoutJoinerAndLastMember.get(usersInTournamentTeam2WithoutJoinerAndLastMember.size() - 1);
        usersInTournamentTeam2WithoutJoinerAndLastMember.remove(lastMemberOnTeam2);
        for (int i = 0; i < usersInTournamentTeam2WithoutJoinerAndLastMember.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam2.getPk(), usersInTournamentTeam2WithoutJoinerAndLastMember.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam2WithoutJoinerAndLastMember.get(i));
        }
        List<User> usersInTournamentTeam3WithoutJoinerAndLastMember = new ArrayList<>(usersInJoiningTeam3);
        usersInTournamentTeam3WithoutJoinerAndLastMember.remove(userJoiningTournamentForTeam3);
        User lastMemberOnTeam3 = usersInTournamentTeam3WithoutJoinerAndLastMember.get(usersInTournamentTeam3WithoutJoinerAndLastMember.size() - 1);
        usersInTournamentTeam3WithoutJoinerAndLastMember.remove(lastMemberOnTeam3);
        for (int i = 0; i < usersInTournamentTeam3WithoutJoinerAndLastMember.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam3.getPk(), usersInTournamentTeam3WithoutJoinerAndLastMember.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam3WithoutJoinerAndLastMember.get(i));
        }
        List<User> usersInTournamentTeam4WithoutJoinerAndLastMember = new ArrayList<>(usersInJoiningTeam4);
        usersInTournamentTeam4WithoutJoinerAndLastMember.remove(userJoiningTournamentForTeam4);
        User lastMemberOnTeam4 = usersInTournamentTeam4WithoutJoinerAndLastMember.get(usersInTournamentTeam4WithoutJoinerAndLastMember.size() - 1);
        usersInTournamentTeam4WithoutJoinerAndLastMember.remove(lastMemberOnTeam4);
        for (int i = 0; i < usersInTournamentTeam4WithoutJoinerAndLastMember.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam4.getPk(), usersInTournamentTeam4WithoutJoinerAndLastMember.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam4WithoutJoinerAndLastMember.get(i));
        }
        List<User> usersInTournamentTeam5WithoutJoinerAndLastMember = new ArrayList<>(usersInJoiningTeam5);
        usersInTournamentTeam5WithoutJoinerAndLastMember.remove(userJoiningTournamentForTeam5);
        User lastMemberOnTeam5 = usersInTournamentTeam5WithoutJoinerAndLastMember.get(usersInTournamentTeam5WithoutJoinerAndLastMember.size() - 1);
        usersInTournamentTeam5WithoutJoinerAndLastMember.remove(lastMemberOnTeam5);
        for (int i = 0; i < usersInTournamentTeam5WithoutJoinerAndLastMember.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam5.getPk(), usersInTournamentTeam5WithoutJoinerAndLastMember.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam5WithoutJoinerAndLastMember.get(i));
        }

        //deduct money for all joiners
        for (User user : usersWhoAcceptedTournamentInvite) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }

        tournamentService.startTournamentsForTest();

        //since only 1 team fully accepted and there are 4 teams who partially accepted, and there are not enough teams when tournament is about to start, tournament should be cancelled and all team members should get refund
        for (User user : usersWhoAcceptedTournamentInvite) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(tournament.getUsersInTournament().contains(user));
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.CANCELLED);

        return tournament;
    }

    public Tournament fiveTeamsFullyAccept() throws Exception {
        int numOfUsersAndTeams = 10;
        teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team3 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team2", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team4 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team3", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team5 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team4", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        List<User> usersInJoiningTeam1 = new ArrayList<>();
        usersInJoiningTeam1.addAll(team1.getMembers());
        User userJoiningTournamentForTeam1 = userIWS.getUserByPk(team1.getTeamLeaderPk());

        List<User> usersInJoiningTeam2 = new ArrayList<>();
        usersInJoiningTeam2.addAll(team2.getMembers());
        User userJoiningTournamentForTeam2 = userIWS.getUserByPk(team2.getTeamLeaderPk());

        List<User> usersInJoiningTeam3 = new ArrayList<>();
        usersInJoiningTeam3.addAll(team3.getMembers());
        User userJoiningTournamentForTeam3 = userIWS.getUserByPk(team3.getTeamLeaderPk());

        List<User> usersInJoiningTeam4 = new ArrayList<>();
        usersInJoiningTeam4.addAll(team4.getMembers());
        User userJoiningTournamentForTeam4 = userIWS.getUserByPk(team4.getTeamLeaderPk());

        List<User> usersInJoiningTeam5 = new ArrayList<>();
        usersInJoiningTeam5.addAll(team5.getMembers());
        User userJoiningTournamentForTeam5 = userIWS.getUserByPk(team5.getTeamLeaderPk());

        List<User> usersOnTeamsJoiningTournament = new ArrayList<>();
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam1);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam2);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam3);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam4);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam5);

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");

        for (User user : usersOnTeamsJoiningTournament) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getTournaments().isEmpty());
        }

        Tournament tournament = tournamentService.createTournamentForTest(constructTournamentPojo());
        TournamentPojo tournamentInfo = tournament.getTournamentInfo();

        BigDecimal balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
        List<User> usersWhoAcceptedTournamentInvite = new ArrayList<>();

        List<User> teamLeadersJoiningTournament = new ArrayList<>();
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam1);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam2);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam3);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam4);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam5);

        tournamentService.joinTournament(team1, tournament, usersInJoiningTeam1, userJoiningTournamentForTeam1);
        tournamentService.joinTournament(team2, tournament, usersInJoiningTeam2, userJoiningTournamentForTeam2);
        tournamentService.joinTournament(team3, tournament, usersInJoiningTeam3, userJoiningTournamentForTeam3);
        tournamentService.joinTournament(team4, tournament, usersInJoiningTeam4, userJoiningTournamentForTeam4);
        tournamentService.joinTournament(team5, tournament, usersInJoiningTeam5, userJoiningTournamentForTeam5);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam1);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam2);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam3);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam4);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam5);

        TournamentTeam tournamentTeam1 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team1.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam2 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team2.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam3 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team3.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam4 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team4.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam5 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team5.getTeamPojo().getTeamName(), tournament);

        List<TournamentTeam> tournamentTeamsInTournament = new ArrayList<>();
        tournamentTeamsInTournament.add(tournamentTeam1);
        tournamentTeamsInTournament.add(tournamentTeam2);
        tournamentTeamsInTournament.add(tournamentTeam3);
        tournamentTeamsInTournament.add(tournamentTeam4);
        tournamentTeamsInTournament.add(tournamentTeam5);

        //assert users joining tournament for team gets deducted wager amount
        for (User user : teamLeadersJoiningTournament) {
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
            balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
        }
        for (TournamentTeam tournamentTeam : tournamentTeamsInTournament) {
            Assert.assertTrue(tournament.getTournamentTeams().contains(tournamentTeam));
        }

        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.PENDING);

        //fully accept tournament invite for everyone on team 1 
        List<User> usersInTournamentTeam1WithoutJoiner = new ArrayList<>(usersInJoiningTeam1);
        usersInTournamentTeam1WithoutJoiner.remove(userJoiningTournamentForTeam1);
        for (int i = 0; i < usersInTournamentTeam1WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam1.getPk(), usersInTournamentTeam1WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam1WithoutJoiner.get(i));
        }

        //accept tournament invite for each team
        List<User> usersInTournamentTeam2WithoutJoiner = new ArrayList<>(usersInJoiningTeam2);
        usersInTournamentTeam2WithoutJoiner.remove(userJoiningTournamentForTeam2);
        for (int i = 0; i < usersInTournamentTeam2WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam2.getPk(), usersInTournamentTeam2WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam2WithoutJoiner.get(i));
        }
        List<User> usersInTournamentTeam3WithoutJoiner = new ArrayList<>(usersInJoiningTeam3);
        usersInTournamentTeam3WithoutJoiner.remove(userJoiningTournamentForTeam3);
        for (int i = 0; i < usersInTournamentTeam3WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam3.getPk(), usersInTournamentTeam3WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam3WithoutJoiner.get(i));
        }
        List<User> usersInTournamentTeam4WithoutJoiner = new ArrayList<>(usersInJoiningTeam4);
        usersInTournamentTeam4WithoutJoiner.remove(userJoiningTournamentForTeam4);
        for (int i = 0; i < usersInTournamentTeam4WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam4.getPk(), usersInTournamentTeam4WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam4WithoutJoiner.get(i));
        }
        List<User> usersInTournamentTeam5WithoutJoiner = new ArrayList<>(usersInJoiningTeam5);
        usersInTournamentTeam5WithoutJoiner.remove(userJoiningTournamentForTeam5);
        for (int i = 0; i < usersInTournamentTeam5WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam5.getPk(), usersInTournamentTeam5WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam5WithoutJoiner.get(i));
        }

        //deduct money for all joiners
        for (User user : usersWhoAcceptedTournamentInvite) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }

        tournamentService.startTournamentsForTest();

        //all teams should be deducted and tournament should be active
        for (User user : usersWhoAcceptedTournamentInvite) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.ACTIVE);

        return tournament;
    }

    public Tournament twoTeamsFullyAcceptOneTeamLeaves() throws Exception {
        int numOfUsersAndTeams = 10;
        teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        List<User> usersInJoiningTeam1 = new ArrayList<>();
        usersInJoiningTeam1.addAll(team1.getMembers());
        User userJoiningTournamentForTeam1 = userIWS.getUserByPk(team1.getTeamLeaderPk());

        List<User> usersInJoiningTeam2 = new ArrayList<>();
        usersInJoiningTeam2.addAll(team2.getMembers());
        User userJoiningTournamentForTeam2 = userIWS.getUserByPk(team2.getTeamLeaderPk());

        List<User> usersOnTeamsJoiningTournament = new ArrayList<>();
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam1);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam2);

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");

        for (User user : usersOnTeamsJoiningTournament) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getTournaments().isEmpty());
        }

        Tournament tournament = tournamentService.createTournamentForTest(constructTournamentPojo());
        TournamentPojo tournamentInfo = tournament.getTournamentInfo();

        BigDecimal balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
        List<User> usersWhoAcceptedTournamentInvite = new ArrayList<>();

        List<User> teamLeadersJoiningTournament = new ArrayList<>();
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam1);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam2);

        tournamentService.joinTournament(team1, tournament, usersInJoiningTeam1, userJoiningTournamentForTeam1);
        tournamentService.joinTournament(team2, tournament, usersInJoiningTeam2, userJoiningTournamentForTeam2);

        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam1);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam2);

        TournamentTeam tournamentTeam1 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team1.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam2 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team2.getTeamPojo().getTeamName(), tournament);

        List<TournamentTeam> tournamentTeamsInTournament = new ArrayList<>();
        tournamentTeamsInTournament.add(tournamentTeam1);
        tournamentTeamsInTournament.add(tournamentTeam2);

        //assert users joining tournament for team gets deducted wager amount
        for (User user : teamLeadersJoiningTournament) {
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
            balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
        }
        for (TournamentTeam tournamentTeam : tournamentTeamsInTournament) {
            Assert.assertTrue(tournament.getTournamentTeams().contains(tournamentTeam));
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.PENDING);

        //fully accept tournament invite for everyone on team 1 
        List<User> usersInTournamentTeam1WithoutJoiner = new ArrayList<>(usersInJoiningTeam1);
        usersInTournamentTeam1WithoutJoiner.remove(userJoiningTournamentForTeam1);
        for (int i = 0; i < usersInTournamentTeam1WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam1.getPk(), usersInTournamentTeam1WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam1WithoutJoiner.get(i));
        }

        //accept tournament invite for each team
        List<User> usersInTournamentTeam2WithoutJoiner = new ArrayList<>(usersInJoiningTeam2);
        usersInTournamentTeam2WithoutJoiner.remove(userJoiningTournamentForTeam2);
        for (int i = 0; i < usersInTournamentTeam2WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam2.getPk(), usersInTournamentTeam2WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam2WithoutJoiner.get(i));
        }

        //deduct money for all joiners
        for (User user : usersWhoAcceptedTournamentInvite) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }

        tournamentService.leaveTournament(userJoiningTournamentForTeam2, tournamentTeam2);

        //refund team 2's money for all members who joined and dissociated tournament team from tournament
        Assert.assertFalse(tournament.getTournamentTeams().contains(tournamentTeam2));
        for (User user : usersInJoiningTeam2) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(tournament.getUsersInTournament().contains(user));
        }

        tournamentService.startTournamentsForTest();

        //Not enough teams, so refund everyone 
        for (User user : usersInJoiningTeam1) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(tournament.getUsersInTournament().contains(user));
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.CANCELLED);

        return tournament;
    }

    public Tournament twoTeamsFullyAcceptOneTeamPartiallyAcceptsThenLeaves() throws Exception {
        int numOfUsersAndTeams = 10;
        teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team3 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team2", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        List<User> usersInJoiningTeam1 = new ArrayList<>();
        usersInJoiningTeam1.addAll(team1.getMembers());
        User userJoiningTournamentForTeam1 = userIWS.getUserByPk(team1.getTeamLeaderPk());

        List<User> usersInJoiningTeam2 = new ArrayList<>();
        usersInJoiningTeam2.addAll(team2.getMembers());
        User userJoiningTournamentForTeam2 = userIWS.getUserByPk(team2.getTeamLeaderPk());

        List<User> usersInJoiningTeam3 = new ArrayList<>();
        usersInJoiningTeam3.addAll(team3.getMembers());
        User userJoiningTournamentForTeam3 = userIWS.getUserByPk(team3.getTeamLeaderPk());

        List<User> usersOnTeamsJoiningTournament = new ArrayList<>();
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam1);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam2);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam3);

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");

        for (User user : usersOnTeamsJoiningTournament) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getTournaments().isEmpty());
        }

        Tournament tournament = tournamentService.createTournamentForTest(constructTournamentPojo());
        TournamentPojo tournamentInfo = tournament.getTournamentInfo();

        BigDecimal balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
        List<User> usersWhoAcceptedTournamentInvite = new ArrayList<>();

        List<User> teamLeadersJoiningTournament = new ArrayList<>();
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam1);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam2);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam3);

        tournamentService.joinTournament(team1, tournament, usersInJoiningTeam1, userJoiningTournamentForTeam1);
        tournamentService.joinTournament(team2, tournament, usersInJoiningTeam2, userJoiningTournamentForTeam2);
        tournamentService.joinTournament(team3, tournament, usersInJoiningTeam3, userJoiningTournamentForTeam3);

        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam1);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam2);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam3);

        TournamentTeam tournamentTeam1 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team1.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam2 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team2.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam3 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team3.getTeamPojo().getTeamName(), tournament);

        List<TournamentTeam> tournamentTeamsInTournament = new ArrayList<>();
        tournamentTeamsInTournament.add(tournamentTeam1);
        tournamentTeamsInTournament.add(tournamentTeam2);
        tournamentTeamsInTournament.add(tournamentTeam3);

        //assert users joining tournament for team gets deducted wager amount
        for (User user : teamLeadersJoiningTournament) {
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
            balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.PENDING);

        //fully accept tournament invite for everyone on team 1 
        List<User> usersInTournamentTeam1WithoutJoiner = new ArrayList<>(usersInJoiningTeam1);
        usersInTournamentTeam1WithoutJoiner.remove(userJoiningTournamentForTeam1);
        for (int i = 0; i < usersInTournamentTeam1WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam1.getPk(), usersInTournamentTeam1WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam1WithoutJoiner.get(i));
        }

        //accept tournament invite for each team
        List<User> usersInTournamentTeam2WithoutJoiner = new ArrayList<>(usersInJoiningTeam2);
        usersInTournamentTeam2WithoutJoiner.remove(userJoiningTournamentForTeam2);
        for (int i = 0; i < usersInTournamentTeam2WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam2.getPk(), usersInTournamentTeam2WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam2WithoutJoiner.get(i));
        }
        List<User> usersInTournamentTeam3WithoutJoinerAndLastMember = new ArrayList<>(usersInJoiningTeam3);
        usersInTournamentTeam3WithoutJoinerAndLastMember.remove(userJoiningTournamentForTeam3);
        User lastMemberOnTeam3 = usersInTournamentTeam3WithoutJoinerAndLastMember.get(usersInTournamentTeam3WithoutJoinerAndLastMember.size() - 1);
        usersInTournamentTeam3WithoutJoinerAndLastMember.remove(lastMemberOnTeam3);
        for (int i = 0; i < usersInTournamentTeam3WithoutJoinerAndLastMember.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam3.getPk(), usersInTournamentTeam3WithoutJoinerAndLastMember.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam3WithoutJoinerAndLastMember.get(i));
        }

        //deduct money for all joiners
        for (User user : usersWhoAcceptedTournamentInvite) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }

        tournamentService.leaveTournament(userJoiningTournamentForTeam3, tournamentTeam3);

        //refund leaving team's money and remove assoc from tournament and dissociate tournament team from tournament
        Assert.assertFalse(tournament.getTournamentTeams().contains(tournamentTeam3));
        for (User user : usersInJoiningTeam3) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(tournament.getUsersInTournament().contains(user));
            //usersWhoAcceptedTournamentInvite.remove(user);
        }

        tournamentService.startTournamentsForTest();

        //all teams should be deducted and tournament should be active
        for (User user : usersInJoiningTeam1) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }
        for (User user : usersInJoiningTeam2) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.ACTIVE);

        return tournament;
    }

    public Tournament twoTeamsFullyAcceptTwoTeamsPartiallyAccepts() throws Exception {
        int numOfUsersAndTeams = 10;
        teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team3 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team2", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team4 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team3", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        List<User> usersInJoiningTeam1 = new ArrayList<>();
        usersInJoiningTeam1.addAll(team1.getMembers());
        User userJoiningTournamentForTeam1 = userIWS.getUserByPk(team1.getTeamLeaderPk());

        List<User> usersInJoiningTeam2 = new ArrayList<>();
        usersInJoiningTeam2.addAll(team2.getMembers());
        User userJoiningTournamentForTeam2 = userIWS.getUserByPk(team2.getTeamLeaderPk());

        List<User> usersInJoiningTeam3 = new ArrayList<>();
        usersInJoiningTeam3.addAll(team3.getMembers());
        User userJoiningTournamentForTeam3 = userIWS.getUserByPk(team3.getTeamLeaderPk());

        List<User> usersInJoiningTeam4 = new ArrayList<>();
        usersInJoiningTeam4.addAll(team4.getMembers());
        User userJoiningTournamentForTeam4 = userIWS.getUserByPk(team4.getTeamLeaderPk());

        List<User> usersOnTeamsJoiningTournament = new ArrayList<>();
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam1);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam2);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam3);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam4);

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");

        for (User user : usersOnTeamsJoiningTournament) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getTournaments().isEmpty());
        }

        Tournament tournament = tournamentService.createTournamentForTest(constructTournamentPojo());
        TournamentPojo tournamentInfo = tournament.getTournamentInfo();

        BigDecimal balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
        List<User> usersWhoAcceptedTournamentInvite = new ArrayList<>();

        List<User> teamLeadersJoiningTournament = new ArrayList<>();
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam1);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam2);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam3);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam4);

        tournamentService.joinTournament(team1, tournament, usersInJoiningTeam1, userJoiningTournamentForTeam1);
        tournamentService.joinTournament(team2, tournament, usersInJoiningTeam2, userJoiningTournamentForTeam2);
        tournamentService.joinTournament(team3, tournament, usersInJoiningTeam3, userJoiningTournamentForTeam3);
        tournamentService.joinTournament(team4, tournament, usersInJoiningTeam4, userJoiningTournamentForTeam4);

        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam1);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam2);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam3);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam4);

        TournamentTeam tournamentTeam1 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team1.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam2 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team2.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam3 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team3.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam4 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team4.getTeamPojo().getTeamName(), tournament);

        List<TournamentTeam> tournamentTeamsInTournament = new ArrayList<>();
        tournamentTeamsInTournament.add(tournamentTeam1);
        tournamentTeamsInTournament.add(tournamentTeam2);
        tournamentTeamsInTournament.add(tournamentTeam3);
        tournamentTeamsInTournament.add(tournamentTeam4);

        //assert users joining tournament for team gets deducted wager amount
        for (User user : teamLeadersJoiningTournament) {
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
            balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
        }
        for (TournamentTeam tournamentTeam : tournamentTeamsInTournament) {
            Assert.assertTrue(tournament.getTournamentTeams().contains(tournamentTeam));
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.PENDING);

        //fully accept tournament invite for everyone on team 1 
        List<User> usersInTournamentTeam1WithoutJoiner = new ArrayList<>(usersInJoiningTeam1);
        usersInTournamentTeam1WithoutJoiner.remove(userJoiningTournamentForTeam1);
        for (int i = 0; i < usersInTournamentTeam1WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam1.getPk(), usersInTournamentTeam1WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam1WithoutJoiner.get(i));
        }

        //accept tournament invite for each team
        List<User> usersInTournamentTeam2WithoutJoiner = new ArrayList<>(usersInJoiningTeam2);
        usersInTournamentTeam2WithoutJoiner.remove(userJoiningTournamentForTeam2);
        for (int i = 0; i < usersInTournamentTeam2WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam2.getPk(), usersInTournamentTeam2WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam2WithoutJoiner.get(i));
        }
        List<User> usersInTournamentTeam3WithoutJoinerAndLastMember = new ArrayList<>(usersInJoiningTeam3);
        usersInTournamentTeam3WithoutJoinerAndLastMember.remove(userJoiningTournamentForTeam3);
        User lastMemberOnTeam3 = usersInTournamentTeam3WithoutJoinerAndLastMember.get(usersInTournamentTeam3WithoutJoinerAndLastMember.size() - 1);
        usersInTournamentTeam3WithoutJoinerAndLastMember.remove(lastMemberOnTeam3);
        for (int i = 0; i < usersInTournamentTeam3WithoutJoinerAndLastMember.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam3.getPk(), usersInTournamentTeam3WithoutJoinerAndLastMember.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam3WithoutJoinerAndLastMember.get(i));
        }
        List<User> usersInTournamentTeam4WithoutJoinerAndLastMember = new ArrayList<>(usersInJoiningTeam4);
        usersInTournamentTeam4WithoutJoinerAndLastMember.remove(userJoiningTournamentForTeam4);
        User lastMemberOnTeam4 = usersInTournamentTeam4WithoutJoinerAndLastMember.get(usersInTournamentTeam4WithoutJoinerAndLastMember.size() - 1);
        usersInTournamentTeam4WithoutJoinerAndLastMember.remove(lastMemberOnTeam4);
        for (int i = 0; i < usersInTournamentTeam4WithoutJoinerAndLastMember.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam4.getPk(), usersInTournamentTeam4WithoutJoinerAndLastMember.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam4WithoutJoinerAndLastMember.get(i));
        }

        //deduct money for all joiners
        for (User user : usersWhoAcceptedTournamentInvite) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }

        tournamentService.startTournamentsForTest();

        //refund leaving team's money and remove assoc from tournament
        for (User user : usersInJoiningTeam3) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(tournament.getUsersInTournament().contains(user));
        }
        for (User user : usersInJoiningTeam4) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(tournament.getUsersInTournament().contains(user));
        }

        //all teams should be deducted and tournament should be active
        for (User user : usersInJoiningTeam1) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }
        for (User user : usersInJoiningTeam2) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.ACTIVE);

        return tournament;
    }

    public Tournament singleMemberDeclinesTournamentInvite() throws Exception {
        int numOfUsersAndTeams = 10;
        teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team3 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team2", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        List<User> usersInJoiningTeam1 = new ArrayList<>();
        usersInJoiningTeam1.addAll(team1.getMembers());
        User userJoiningTournamentForTeam1 = userIWS.getUserByPk(team1.getTeamLeaderPk());

        List<User> usersInJoiningTeam2 = new ArrayList<>();
        usersInJoiningTeam2.addAll(team2.getMembers());
        User userJoiningTournamentForTeam2 = userIWS.getUserByPk(team2.getTeamLeaderPk());

        List<User> usersInJoiningTeam3 = new ArrayList<>();
        usersInJoiningTeam3.addAll(team3.getMembers());
        User userJoiningTournamentForTeam3 = userIWS.getUserByPk(team3.getTeamLeaderPk());

        List<User> usersOnTeamsJoiningTournament = new ArrayList<>();
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam1);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam2);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam3);

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");

        for (User user : usersOnTeamsJoiningTournament) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getTournaments().isEmpty());
        }

        Tournament tournament = tournamentService.createTournamentForTest(constructTournamentPojo());
        TournamentPojo tournamentInfo = tournament.getTournamentInfo();

        BigDecimal balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
        List<User> usersWhoAcceptedTournamentInvite = new ArrayList<>();

        List<User> teamLeadersJoiningTournament = new ArrayList<>();
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam1);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam2);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam3);

        tournamentService.joinTournament(team1, tournament, usersInJoiningTeam1, userJoiningTournamentForTeam1);
        tournamentService.joinTournament(team2, tournament, usersInJoiningTeam2, userJoiningTournamentForTeam2);
        tournamentService.joinTournament(team3, tournament, usersInJoiningTeam3, userJoiningTournamentForTeam3);

        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam1);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam2);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam3);

        TournamentTeam tournamentTeam1 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team1.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam2 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team2.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam3 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team3.getTeamPojo().getTeamName(), tournament);

        List<TournamentTeam> tournamentTeamsInTournament = new ArrayList<>();
        tournamentTeamsInTournament.add(tournamentTeam1);
        tournamentTeamsInTournament.add(tournamentTeam2);
        tournamentTeamsInTournament.add(tournamentTeam3);

        //assert users joining tournament for team gets deducted wager amount
        for (User user : teamLeadersJoiningTournament) {
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
            balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.PENDING);

        //fully accept tournament invite for everyone on team 1 
        List<User> usersInTournamentTeam1WithoutJoiner = new ArrayList<>(usersInJoiningTeam1);
        usersInTournamentTeam1WithoutJoiner.remove(userJoiningTournamentForTeam1);
        for (int i = 0; i < usersInTournamentTeam1WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam1.getPk(), usersInTournamentTeam1WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam1WithoutJoiner.get(i));
        }

        //accept tournament invite for each team
        List<User> usersInTournamentTeam2WithoutJoiner = new ArrayList<>(usersInJoiningTeam2);
        usersInTournamentTeam2WithoutJoiner.remove(userJoiningTournamentForTeam2);
        for (int i = 0; i < usersInTournamentTeam2WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam2.getPk(), usersInTournamentTeam2WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam2WithoutJoiner.get(i));
        }
        List<User> usersInTournamentTeam3WithoutJoinerAndLastMember = new ArrayList<>(usersInJoiningTeam3);
        usersInTournamentTeam3WithoutJoinerAndLastMember.remove(userJoiningTournamentForTeam3);
        User lastMemberOnTeam3 = usersInTournamentTeam3WithoutJoinerAndLastMember.get(usersInTournamentTeam3WithoutJoinerAndLastMember.size() - 1);
        usersInTournamentTeam3WithoutJoinerAndLastMember.remove(lastMemberOnTeam3);
        for (int i = 0; i < usersInTournamentTeam3WithoutJoinerAndLastMember.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam3.getPk(), usersInTournamentTeam3WithoutJoinerAndLastMember.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam3WithoutJoinerAndLastMember.get(i));
        }

        TournamentInvite tournamentInviteToDecline = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam3.getPk(), lastMemberOnTeam3.getPk());
        tournamentInviteIWS.declineTournamentInvite(tournamentInviteToDecline.getPk());

        for (User user : usersInJoiningTeam3) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(tournament.getUsersInTournament().contains(user));
        }

        tournamentService.startTournamentsForTest();

        //all teams should be deducted and tournament should be active
        for (User user : usersInJoiningTeam1) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }
        for (User user : usersInJoiningTeam2) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.ACTIVE);

        return tournament;
    }

    public Tournament teamJoinLeaveRejoinTournament() throws Exception {
        int numOfUsersAndTeams = 10;
        teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team3 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team2", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        List<User> usersInJoiningTeam1 = new ArrayList<>();
        usersInJoiningTeam1.addAll(team1.getMembers());
        User userJoiningTournamentForTeam1 = userIWS.getUserByPk(team1.getTeamLeaderPk());

        List<User> usersInJoiningTeam2 = new ArrayList<>();
        usersInJoiningTeam2.addAll(team2.getMembers());
        User userJoiningTournamentForTeam2 = userIWS.getUserByPk(team2.getTeamLeaderPk());

        List<User> usersInJoiningTeam3 = new ArrayList<>();
        usersInJoiningTeam3.addAll(team3.getMembers());
        User userJoiningTournamentForTeam3 = userIWS.getUserByPk(team3.getTeamLeaderPk());

        List<User> usersOnTeamsJoiningTournament = new ArrayList<>();
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam1);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam2);
        usersOnTeamsJoiningTournament.addAll(usersInJoiningTeam3);

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");

        for (User user : usersOnTeamsJoiningTournament) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getTournaments().isEmpty());
        }

        Tournament tournament = tournamentService.createTournamentForTest(constructTournamentPojo());
        TournamentPojo tournamentInfo = tournament.getTournamentInfo();

        BigDecimal balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
        List<User> usersWhoAcceptedTournamentInvite = new ArrayList<>();

        List<User> teamLeadersJoiningTournament = new ArrayList<>();
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam1);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam2);
        teamLeadersJoiningTournament.add(userJoiningTournamentForTeam3);

        tournamentService.joinTournament(team1, tournament, usersInJoiningTeam1, userJoiningTournamentForTeam1);
        tournamentService.joinTournament(team2, tournament, usersInJoiningTeam2, userJoiningTournamentForTeam2);
        tournamentService.joinTournament(team3, tournament, usersInJoiningTeam3, userJoiningTournamentForTeam3);

        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam1);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam2);
        usersWhoAcceptedTournamentInvite.add(userJoiningTournamentForTeam3);

        TournamentTeam tournamentTeam1 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team1.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam2 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team2.getTeamPojo().getTeamName(), tournament);
        TournamentTeam tournamentTeam3 = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team3.getTeamPojo().getTeamName(), tournament);

        List<TournamentTeam> tournamentTeamsInTournament = new ArrayList<>();
        tournamentTeamsInTournament.add(tournamentTeam1);
        tournamentTeamsInTournament.add(tournamentTeam2);
        tournamentTeamsInTournament.add(tournamentTeam3);

        //assert users joining tournament for team gets deducted wager amount
        for (User user : teamLeadersJoiningTournament) {
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
            balanceAfterJoiningTournament = originalAmount.subtract(tournamentInfo.getWagerAmountPerMember());
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.PENDING);

        //fully accept tournament invite for everyone on team 1 
        List<User> usersInTournamentTeam1WithoutJoiner = new ArrayList<>(usersInJoiningTeam1);
        usersInTournamentTeam1WithoutJoiner.remove(userJoiningTournamentForTeam1);
        for (int i = 0; i < usersInTournamentTeam1WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam1.getPk(), usersInTournamentTeam1WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam1WithoutJoiner.get(i));
        }

        //accept tournament invite for each team
        List<User> usersInTournamentTeam2WithoutJoiner = new ArrayList<>(usersInJoiningTeam2);
        usersInTournamentTeam2WithoutJoiner.remove(userJoiningTournamentForTeam2);
        for (int i = 0; i < usersInTournamentTeam2WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam2.getPk(), usersInTournamentTeam2WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam2WithoutJoiner.get(i));
        }

        List<User> usersInTournamentTeam3WithoutJoiner = new ArrayList<>(usersInJoiningTeam3);
        usersInTournamentTeam3WithoutJoiner.remove(userJoiningTournamentForTeam3);
        for (int i = 0; i < usersInTournamentTeam3WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeam3.getPk(), usersInTournamentTeam3WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam3WithoutJoiner.get(i));
        }

        //usersInTournamentTeam3WithoutJoiner.add(userJoiningTournamentForTeam3);
        //deduct money for all joiners
        for (User user : usersWhoAcceptedTournamentInvite) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }

        tournamentService.leaveTournament(userJoiningTournamentForTeam3, tournamentTeam3);

        //remove all team members from people who have accepted tournament invite
        for (User user : usersInJoiningTeam3) {
            usersWhoAcceptedTournamentInvite.remove(user);
        }

        //refund all team 3's money
        for (User user : usersInJoiningTeam3) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(tournament.getUsersInTournament().contains(user));
        }

        //rejoin tournament and accept all invites for team 3
        tournamentService.joinTournament(team3, tournament, usersInJoiningTeam3, userJoiningTournamentForTeam3);
        TournamentTeam tournamentTeamRejoin = tournamentTeamIWS.getTournamentTeamByNameAndTournament(team3.getTeamPojo().getTeamName(), tournament);

        for (int i = 0; i < usersInTournamentTeam3WithoutJoiner.size(); i++) {
            TournamentInvite tournamentInviteToAccept = tournamentInviteIWS.findTournamentInviteForUserInTournamentTeam(tournamentTeamRejoin.getPk(), usersInTournamentTeam3WithoutJoiner.get(i).getPk());
            tournamentInviteIWS.acceptTournamentInvite(tournamentInviteToAccept.getPk());
            usersWhoAcceptedTournamentInvite.add(usersInTournamentTeam3WithoutJoiner.get(i));
        }

        tournamentService.startTournamentsForTest();

        //all teams should be deducted and tournament should be active
        for (User user : usersWhoAcceptedTournamentInvite) {
            Assert.assertTrue(balanceAfterJoiningTournament.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(tournament.getUsersInTournament().contains(user));
        }
        Assert.assertTrue(tournamentInfo.getTournamentStatus() == TournamentStatusEnum.ACTIVE);

        return tournament;
    }

    public TournamentPojo constructTournamentPojo() {
        TournamentPojo tournamentPojo = new TournamentPojo();
        tournamentPojo.setAmountAwardedToFirstPlace(new BigDecimal("1000.00"));
        tournamentPojo.setRegion(RegionEnum.NA);
        tournamentPojo.setWagerAmountPerMember(new BigDecimal("5.00"));
        tournamentPojo.setTournamentStatus(TournamentStatusEnum.PENDING);
        tournamentPojo.setTournamentFormat(TournamentFormatEnum.SINGLEELIMINATION);
        tournamentPojo.setBestOfEnum(BestOfEnum.BEST_OF_1);
        tournamentPojo.setGameEnum(GameEnum.COD_MWR);
        tournamentPojo.setMatchSize(MatchSizeEnum.THREES);
        tournamentPojo.setMatchType(MatchTypeEnum.TOURNAMENT);
        tournamentPojo.setPlatform(PlatformEnum.PS4);
        tournamentPojo.setTeamTypeEnum(TeamTypeEnum.CASH);
        tournamentPojo.setTeamSizeEnum(TeamSizeEnum.TEAM);
        tournamentPojo.setScheduledTournamentTime(LocalDateTime.now());

        return tournamentPojo;
    }

}
