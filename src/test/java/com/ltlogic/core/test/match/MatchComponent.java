/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.core.test.match;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamStatusEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.core.test.team.TeamComponent;
import com.ltlogic.core.test.user.UserComponent;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.MatchInvite;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.mwr.MWRMatch;
import com.ltlogic.iws.MatchIWS;
import com.ltlogic.iws.MatchInviteIWS;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.iws.TeamInviteIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.MatchPojo;
import com.ltlogic.pojo.mwr.MWRMatchPojo;
import com.ltlogic.service.core.MatchService;
import com.ltlogic.service.core.TeamService;
import com.ltlogic.service.springsecurity.TeamValidator;
import java.math.BigDecimal;
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
public class MatchComponent {

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

    private static final Logger LOG = LoggerFactory.getLogger(MatchComponent.class);

    public Match createDoublesPublicXPMatch() throws Exception {

        int numOfUsersAndTeams = 2;
        int numOfPlayersForMatch = 2 * numOfUsersAndTeams;

        teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.XP);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.XP, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.XP, RegionEnum.NA, TeamStatusEnum.LIVE);

        MatchPojo matchInfo = constructMatchPojo(GameEnum.COD_MWR, numOfPlayersForMatch, team1);
        MWRMatchPojo mwrMatchPojo = new MWRMatchPojo();
        mwrMatchPojo.setJuggernaut(true);
        mwrMatchPojo.setLethals(true);

        List<User> usersInCreatingTeam = new ArrayList<>();
        usersInCreatingTeam.addAll(team1.getMembers());
        User userCreatingMatch = userIWS.getUserByPk(team1.getTeamLeaderPk());

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getMatches().isEmpty());
        }

        Match match = matchIWS.createMatch(matchInfo, mwrMatchPojo, team1, usersInCreatingTeam, null, userCreatingMatch);

        //after creating xp match, user should have same amount of money but them and their team membmer should be associated with match
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(match.getUsersInMatch().contains(user));
        }
        Assert.assertTrue(match.getTeamsInMatch().contains(team1));
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING);
        return match;
    }

    public Match allAcceptDoublesPublicWagerMatch() throws Exception {

        int numOfUsersAndTeams = 2;
        int numOfPlayersForMatch = 2 * numOfUsersAndTeams;

        List<Team> teams = teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        //creating matchPojo with wager amount $5.00
        MatchPojo matchInfo = constructMatchPojo(GameEnum.COD_MWR, numOfPlayersForMatch, team1);
        MWRMatchPojo mwrMatchPojo = new MWRMatchPojo();
        mwrMatchPojo.setJuggernaut(true);
        mwrMatchPojo.setLethals(true);

        List<User> usersInCreatingTeam = new ArrayList<>(team1.getMembers());
        List<User> usersInAcceptingTeam = new ArrayList<>(team2.getMembers());
        User userCreatingMatch = userIWS.getUserByPk(team1.getTeamLeaderPk());
        User userAcceptingMatch = userIWS.getUserByPk(team2.getTeamLeaderPk());

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getMatches().isEmpty());
        }

        //create public wager match
        Match match = matchIWS.createMatch(matchInfo, mwrMatchPojo, team1, usersInCreatingTeam, null, userCreatingMatch);

        //until teammates accept public wager match invite, they should still have same balance and not be associated to match
        List<User> usersInMatchWithoutCreator = new ArrayList<>(usersInCreatingTeam);
        usersInMatchWithoutCreator.remove(userCreatingMatch);
        for (User user : usersInMatchWithoutCreator) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }

        //after creating public wager match, match creator should be associated with match and be deducted the wager amount
        Assert.assertTrue(match.getUsersInMatch().contains(userCreatingMatch));
        Assert.assertTrue(match.getTeamsInMatch().contains(team1));
        BigDecimal balanceAfterCreatingOrAcceptingMatch = originalAmount.subtract(matchInfo.getWagerAmountPerMember());
        Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(userCreatingMatch.getBank().getTotalAmount()) == 0);
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT);

        //accept match invite for all team members besides leader since he already accepted
        for (User user : usersInMatchWithoutCreator) {
            MatchInvite matchInviteToAccept = matchInviteIWS.findMatchInviteForUserInMatch(match.getPk(), user.getPk());
            matchInviteIWS.acceptMatchInvite(matchInviteToAccept.getPk());

        }

        //after all teammates accept public wager match invite, they should be deducted wager amount and be associated to match.  Status of match should be Pending
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(match.getUsersInMatch().contains(user));
        }
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING);

        //leader of team 2 finds and accepts the match
        matchIWS.acceptToJoinPublicOrPrivateMatch(match, team2, usersInAcceptingTeam, userAcceptingMatch);

        //After team 2 leader accepts, associate him to the match, and deduct his balance
        Assert.assertTrue(match.getUsersInMatch().contains(userAcceptingMatch));
        Assert.assertTrue(match.getTeamsInMatch().contains(team2));
        Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(userAcceptingMatch.getBank().getTotalAmount()) == 0);
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_SECOND_ACCEPT);

        //Team 2 users should not be associated and deducted until they accept match invite
        List<User> usersInMatchWithoutAcceptor = new ArrayList<>(usersInAcceptingTeam);
        usersInMatchWithoutAcceptor.remove(userAcceptingMatch);
        for (User user : usersInMatchWithoutAcceptor) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }

        //accept match invite for members of team 2
        for (User user : usersInMatchWithoutAcceptor) {
            MatchInvite matchInviteToAccept = matchInviteIWS.findMatchInviteForUserInMatch(match.getPk(), user.getPk());
            matchInviteIWS.acceptMatchInvite(matchInviteToAccept.getPk());
        }

        //after all team 2 teammates accept private wager match invite, they should be deducted wager amount and be associated to match. Status of match should be Pending
        for (User user : usersInAcceptingTeam) {
            Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(match.getUsersInMatch().contains(user));
        }
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.READY_TO_PLAY);

        return match;
    }

    public Match creatorTeam1DeclineDoublesPublicWagerMatch() throws Exception {

        int numOfUsersAndTeams = 2;
        int numOfPlayersForMatch = 2 * numOfUsersAndTeams;

        List<Team> teams = teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        //creating matchPojo with wager amount $5.00
        MatchPojo matchInfo = constructMatchPojo(GameEnum.COD_MWR, numOfPlayersForMatch, team1);
        MWRMatchPojo mwrMatchPojo = new MWRMatchPojo();
        mwrMatchPojo.setJuggernaut(true);
        mwrMatchPojo.setLethals(true);

        List<User> usersInCreatingTeam = new ArrayList<>(team1.getMembers());
        //List<User> usersInAcceptingTeam = new ArrayList<>(team2.getMembers());
        User userCreatingMatch = userIWS.getUserByPk(team1.getTeamLeaderPk());
        //User userAcceptingMatch = userIWS.getUserByPk(team2.getTeamLeaderPk());

        //assert the initial state, users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getMatches().isEmpty());
        }

        //create public wager match 
        Match match = matchIWS.createMatch(matchInfo, mwrMatchPojo, team1, usersInCreatingTeam, null, userCreatingMatch);

        //until teammates accept public wager match invite, they should still have same balance and not be associated to match
        List<User> usersInMatchWithoutCreator = new ArrayList<>(usersInCreatingTeam);
        usersInMatchWithoutCreator.remove(userCreatingMatch);
        for (User user : usersInMatchWithoutCreator) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }

        //after creating public wager match, match creator should be associated with match and be deducted the wager amount
        Assert.assertTrue(match.getUsersInMatch().contains(userCreatingMatch));
        Assert.assertTrue(match.getTeamsInMatch().contains(team1));
        BigDecimal balanceAfterCreatingOrAcceptingMatch = originalAmount.subtract(matchInfo.getWagerAmountPerMember());
        Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(userCreatingMatch.getBank().getTotalAmount()) == 0);
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT);

        //accept match invite for all members of team 1 except match acceptor and last member
        List<User> usersInMatchWithoutCreatorAndLastMember = new ArrayList<>(usersInMatchWithoutCreator);
        User lastMemberOnTeam1 = usersInMatchWithoutCreatorAndLastMember.get(usersInMatchWithoutCreatorAndLastMember.size() - 1);
        usersInMatchWithoutCreatorAndLastMember.remove(lastMemberOnTeam1);
        for (int i = 0; i < usersInMatchWithoutCreatorAndLastMember.size(); i++) {
            MatchInvite matchInviteToAccept = matchInviteIWS.findMatchInviteForUserInMatch(match.getPk(), usersInMatchWithoutCreatorAndLastMember.get(i).getPk());
            matchInviteIWS.acceptMatchInvite(matchInviteToAccept.getPk());
        }

        //everyone on team 1 who accepted should be deducted balance and associated to match
        for (User user : usersInMatchWithoutCreatorAndLastMember) {
            Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(match.getUsersInMatch().contains(user));
        }

        //decline match invite for last guy
        MatchInvite matchInviteToDecline = matchInviteIWS.findMatchInviteForUserInMatch(match.getPk(), lastMemberOnTeam1.getPk());
        matchInviteIWS.declineMatchInvite(matchInviteToDecline.getPk());

        //Everyone on team 1 should get their money back and match status should be cancelled
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }
        Assert.assertFalse(match.getTeamsInMatch().contains(team1));
        Assert.assertFalse(match.getTeamsInMatch().contains(team2));
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.CANCELLED);

        return match;
    }

    public Match creatorTeamAllAcceptAndAcceptorTeam1DeclineDoublesPublicWagerMatch() throws Exception {

        int numOfUsersAndTeams = 2;
        int numOfPlayersForMatch = 2 * numOfUsersAndTeams;

        List<Team> teams = teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        //creating matchPojo with wager amount $5.00
        MatchPojo matchInfo = constructMatchPojo(GameEnum.COD_MWR, numOfPlayersForMatch, team1);
        MWRMatchPojo mwrMatchPojo = new MWRMatchPojo();
        mwrMatchPojo.setJuggernaut(true);
        mwrMatchPojo.setLethals(true);

        List<User> usersInCreatingTeam = new ArrayList<>(team1.getMembers());
        List<User> usersInAcceptingTeam = new ArrayList<>(team2.getMembers());
        User userCreatingMatch = userIWS.getUserByPk(team1.getTeamLeaderPk());
        User userAcceptingMatch = userIWS.getUserByPk(team2.getTeamLeaderPk());

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getMatches().isEmpty());
        }
        for (User user : usersInAcceptingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getMatches().isEmpty());
        }

        Match match = matchIWS.createMatch(matchInfo, mwrMatchPojo, team1, usersInCreatingTeam, null, userCreatingMatch);

        //until teammates accept public wager match invite, they should still have same balance and not be associated to match
        List<User> usersInMatchWithoutCreator = new ArrayList<>(usersInCreatingTeam);
        usersInMatchWithoutCreator.remove(userCreatingMatch);
        for (User user : usersInMatchWithoutCreator) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }

        //after creating public wager match, match creator should be associated with match and be deducted the wager amount
        Assert.assertTrue(match.getUsersInMatch().contains(userCreatingMatch));
        Assert.assertTrue(match.getTeamsInMatch().contains(team1));
        BigDecimal balanceAfterCreatingOrAcceptingMatch = originalAmount.subtract(matchInfo.getWagerAmountPerMember());
        Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(userCreatingMatch.getBank().getTotalAmount()) == 0);
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT);

        //accept match invite for all team members besides leader since he already accepted
        for (User user : usersInMatchWithoutCreator) {
            MatchInvite matchInviteToAccept = matchInviteIWS.findMatchInviteForUserInMatch(match.getPk(), user.getPk());
            matchInviteIWS.acceptMatchInvite(matchInviteToAccept.getPk());

        }
        //after all teammates accept public wager match invite, they should be deducted wager amount and be associated to match.  Status of match should be Pending
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(match.getUsersInMatch().contains(user));
        }
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING);

        //team 2 leader finds and accepts public wager match
        matchIWS.acceptToJoinPublicOrPrivateMatch(match, team2, usersInAcceptingTeam, userAcceptingMatch);

        //After team 2 leader accepts, associate him to the match, and deduct his balance
        Assert.assertTrue(match.getUsersInMatch().contains(userAcceptingMatch));
        Assert.assertTrue(match.getTeamsInMatch().contains(team2));
        Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(userAcceptingMatch.getBank().getTotalAmount()) == 0);
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_SECOND_ACCEPT);

        //Team 2 users should not be associated and deducted until they accept match invite
        List<User> usersInMatchWithoutAcceptor = new ArrayList<>(usersInAcceptingTeam);
        usersInMatchWithoutAcceptor.remove(userAcceptingMatch);
        for (User user : usersInMatchWithoutAcceptor) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }

        //accept match invite for all members of team 2 except match acceptor and last member
        List<User> usersInMatchWithoutAcceptorAndLastMember = new ArrayList<>(usersInMatchWithoutAcceptor);
        User lastMemberOnTeam2 = usersInMatchWithoutAcceptorAndLastMember.get(usersInMatchWithoutAcceptorAndLastMember.size() - 1);
        usersInMatchWithoutAcceptorAndLastMember.remove(lastMemberOnTeam2);
        for (int i = 0; i < usersInMatchWithoutAcceptorAndLastMember.size(); i++) {
            MatchInvite matchInviteToAccept = matchInviteIWS.findMatchInviteForUserInMatch(match.getPk(), usersInMatchWithoutAcceptorAndLastMember.get(i).getPk());
            matchInviteIWS.acceptMatchInvite(matchInviteToAccept.getPk());
        }

        //everyone on team 2 who accepted should be associated to match and have balance deducted
        for (User user : usersInMatchWithoutAcceptorAndLastMember) {
            Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(match.getUsersInMatch().contains(user));
        }
        //Assert last guy has not been deducted money and associated to match
        Assert.assertTrue(originalAmount.compareTo(lastMemberOnTeam2.getBank().getTotalAmount()) == 0);
        Assert.assertFalse(match.getUsersInMatch().contains(lastMemberOnTeam2));

        //decline match invite for last guy
        MatchInvite matchInviteToDecline = matchInviteIWS.findMatchInviteForUserInMatch(match.getPk(), lastMemberOnTeam2.getPk());
        matchInviteIWS.declineMatchInvite(matchInviteToDecline.getPk());

        //Everyone on team 2 should get their money back, everyone on team 1 should still be deducted and associated to the match, and match status should be back to pending
        for (User user : usersInAcceptingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(match.getUsersInMatch().contains(user));
        }
        Assert.assertTrue(match.getTeamsInMatch().contains(team1));
        Assert.assertFalse(match.getTeamsInMatch().contains(team2));
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING);

        return match;
    }

    public Match allAcceptDoublesPrivateWagerMatch() throws Exception {

        int numOfUsersAndTeams = 2;
        int numOfPlayersForMatch = 2 * numOfUsersAndTeams;

        List<Team> teams = teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        MatchPojo matchInfo = constructMatchPojo(GameEnum.COD_MWR, numOfPlayersForMatch, team1);
        MWRMatchPojo mwrMatchPojo = new MWRMatchPojo();
        mwrMatchPojo.setJuggernaut(true);
        mwrMatchPojo.setLethals(true);

        List<User> usersInCreatingTeam = new ArrayList<>(team1.getMembers());
        List<User> usersInAcceptingTeam = new ArrayList<>(team2.getMembers());
        User userCreatingMatch = userIWS.getUserByPk(team1.getTeamLeaderPk());
        User userAcceptingMatch = userIWS.getUserByPk(team2.getTeamLeaderPk());

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getMatches().isEmpty());
        }
        for (User user : usersInAcceptingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getMatches().isEmpty());
        }

        Match match = matchIWS.createMatch(matchInfo, mwrMatchPojo, team1, usersInCreatingTeam, team2, userCreatingMatch);

        //until teammates accept private wager match invite, they should still have same balance and not be associated to match
        List<User> usersInMatchWithoutCreator = new ArrayList<>(usersInCreatingTeam);
        usersInMatchWithoutCreator.remove(userCreatingMatch);
        for (User user : usersInMatchWithoutCreator) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }
        for (User user : usersInAcceptingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getMatches().isEmpty());
        }

        //after creating private wager match, match creator should be associated with match and be deducted the wager amount
        Assert.assertTrue(match.getUsersInMatch().contains(userCreatingMatch));
        Assert.assertTrue(match.getTeamsInMatch().contains(team1));
        BigDecimal balanceAfterCreatingOrAcceptingMatch = originalAmount.subtract(matchInfo.getWagerAmountPerMember());
        Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(userCreatingMatch.getBank().getTotalAmount()) == 0);
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT);

        //accept match invite for all team members besides leader since he already accepted
        for (User user : usersInMatchWithoutCreator) {
            MatchInvite matchInviteToAccept = matchInviteIWS.findMatchInviteForUserInMatch(match.getPk(), user.getPk());
            matchInviteIWS.acceptMatchInvite(matchInviteToAccept.getPk());

        }
        //after all teammates accept private wager match invite, they should be deducted wager amount and be associated to match.  Status of match should be Pending
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(match.getUsersInMatch().contains(user));
        }
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING);

        matchIWS.acceptToJoinPublicOrPrivateMatch(match, team2, usersInAcceptingTeam, userAcceptingMatch);

        //After team 2 leader accepts, associate him to the match, and deduct his balance
        Assert.assertTrue(match.getUsersInMatch().contains(userAcceptingMatch));
        Assert.assertTrue(match.getTeamsInMatch().contains(team2));
        Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(userAcceptingMatch.getBank().getTotalAmount()) == 0);
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_SECOND_ACCEPT);

        //Team 2 users should not be associated and deducted until they accept match invite
        List<User> usersInMatchWithoutAcceptor = new ArrayList<>(usersInAcceptingTeam);
        usersInMatchWithoutAcceptor.remove(userAcceptingMatch);
        for (User user : usersInMatchWithoutAcceptor) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }

        //accept match invite for members of team 2
        for (User user : usersInMatchWithoutAcceptor) {
            MatchInvite matchInviteToAccept = matchInviteIWS.findMatchInviteForUserInMatch(match.getPk(), user.getPk());
            matchInviteIWS.acceptMatchInvite(matchInviteToAccept.getPk());
        }

        //after all team 2 teammates accept private wager match invite, they should be deducted wager amount and be associated to match.  Status of match should be Pending
        for (User user : usersInAcceptingTeam) {
            Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(match.getUsersInMatch().contains(user));
        }
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.READY_TO_PLAY);

        return match;
    }

    public Match creatorTeam1DeclineDoublesPrivateWagerMatch() throws Exception {

        int numOfUsersAndTeams = 2;
        int numOfPlayersForMatch = 2 * numOfUsersAndTeams;

        List<Team> teams = teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        MatchPojo matchInfo = constructMatchPojo(GameEnum.COD_MWR, numOfPlayersForMatch, team1);
        MWRMatchPojo mwrMatchPojo = new MWRMatchPojo();
        mwrMatchPojo.setJuggernaut(true);
        mwrMatchPojo.setLethals(true);

        List<User> usersInCreatingTeam = new ArrayList<>(team1.getMembers());
        List<User> usersInAcceptingTeam = new ArrayList<>(team2.getMembers());
        User userCreatingMatch = userIWS.getUserByPk(team1.getTeamLeaderPk());
        //User userAcceptingMatch = userIWS.getUserByPk(team2.getTeamLeaderPk());

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getMatches().isEmpty());
        }

        Match match = matchIWS.createMatch(matchInfo, mwrMatchPojo, team1, usersInCreatingTeam, team2, userCreatingMatch);

        //until teammates accept private wager match invite, they should still have same balance and not be associated to match
        List<User> usersInMatchWithoutCreator = new ArrayList<>(usersInCreatingTeam);
        usersInMatchWithoutCreator.remove(userCreatingMatch);
        for (User user : usersInMatchWithoutCreator) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }
        //all team 2 users should not be associated and should still have original balance of $50
        for (User user : usersInAcceptingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }

        //after creating private wager match, match creator should be associated with match and be deducted the wager amount
        Assert.assertTrue(match.getUsersInMatch().contains(userCreatingMatch));
        Assert.assertTrue(match.getTeamsInMatch().contains(team1));
        BigDecimal balanceAfterCreatingOrAcceptingMatch = originalAmount.subtract(matchInfo.getWagerAmountPerMember());
        Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(userCreatingMatch.getBank().getTotalAmount()) == 0);
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT);

        //accept match invite for all members of team 1 except match creator and last member
        List<User> usersInMatchWithoutCreatorAndLastMember = new ArrayList<>(usersInMatchWithoutCreator);
        User lastMemberOnTeam1 = usersInMatchWithoutCreatorAndLastMember.get(usersInMatchWithoutCreatorAndLastMember.size() - 1);
        usersInMatchWithoutCreatorAndLastMember.remove(lastMemberOnTeam1);
        for (int i = 0; i < usersInMatchWithoutCreatorAndLastMember.size(); i++) {
            MatchInvite matchInviteToAccept = matchInviteIWS.findMatchInviteForUserInMatch(match.getPk(), usersInMatchWithoutCreatorAndLastMember.get(i).getPk());
            matchInviteIWS.acceptMatchInvite(matchInviteToAccept.getPk());
        }

        //deduct wager amount for all members accepting
        for (User user : usersInMatchWithoutCreatorAndLastMember) {
            Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(match.getUsersInMatch().contains(user));
        }
        //Assert last guy has not been deducted money and associated to match
        Assert.assertTrue(originalAmount.compareTo(lastMemberOnTeam1.getBank().getTotalAmount()) == 0);
        Assert.assertFalse(match.getUsersInMatch().contains(lastMemberOnTeam1));

        //decline match invite for last guy
        MatchInvite matchInviteToDecline = matchInviteIWS.findMatchInviteForUserInMatch(match.getPk(), lastMemberOnTeam1.getPk());
        matchInviteIWS.declineMatchInvite(matchInviteToDecline.getPk());

        //Everyone on team 1 should get their money back and match status should be back to cancelled
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }
        Assert.assertFalse(match.getTeamsInMatch().contains(team1));
        Assert.assertFalse(match.getTeamsInMatch().contains(team2));
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.CANCELLED);

        return match;
    }

    public Match creatorTeamAllAcceptAndAcceptorTeam1DeclineDoublesPrivateWagerMatch() throws Exception {

        int numOfUsersAndTeams = 2;
        int numOfPlayersForMatch = 2 * numOfUsersAndTeams;

        List<Team> teams = teamComponent.createAndAssociateDoublesTeams(numOfUsersAndTeams, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);
        Team team1 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team0", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);
        Team team2 = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize("team1", GameEnum.COD_MWR, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH, RegionEnum.NA, TeamStatusEnum.LIVE);

        MatchPojo matchInfo = constructMatchPojo(GameEnum.COD_MWR, numOfPlayersForMatch, team1);
        MWRMatchPojo mwrMatchPojo = new MWRMatchPojo();
        mwrMatchPojo.setJuggernaut(true);
        mwrMatchPojo.setLethals(true);

        List<User> usersInCreatingTeam = new ArrayList<>(team1.getMembers());
        List<User> usersInAcceptingTeam = new ArrayList<>(team2.getMembers());
        User userCreatingMatch = userIWS.getUserByPk(team1.getTeamLeaderPk());
        User userAcceptingMatch = userIWS.getUserByPk(team2.getTeamLeaderPk());

        //users should not be associated to match yet, and their balance should be the initial balance of $50
        BigDecimal originalAmount = new BigDecimal("50.00");
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getMatches().isEmpty());
        }
        for (User user : usersInAcceptingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getMatches().isEmpty());
        }

        Match match = matchIWS.createMatch(matchInfo, mwrMatchPojo, team1, usersInCreatingTeam, team2, userCreatingMatch);

        //until teammates accept private wager match invite, they should still have same balance and not be associated to match
        List<User> usersInMatchWithoutCreator = new ArrayList<>(usersInCreatingTeam);
        usersInMatchWithoutCreator.remove(userCreatingMatch);
        for (User user : usersInMatchWithoutCreator) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }
        for (User user : usersInAcceptingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(user.getMatches().isEmpty());
        }

        //after creating private wager match, match creator should be associated with match and be deducted the wager amount
        Assert.assertTrue(match.getUsersInMatch().contains(userCreatingMatch));
        Assert.assertTrue(match.getTeamsInMatch().contains(team1));
        BigDecimal balanceAfterCreatingOrAcceptingMatch = originalAmount.subtract(matchInfo.getWagerAmountPerMember());
        Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(userCreatingMatch.getBank().getTotalAmount()) == 0);
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT);

        //accept match invite for all team members besides leader since he already accepted
        for (User user : usersInMatchWithoutCreator) {
            MatchInvite matchInviteToAccept = matchInviteIWS.findMatchInviteForUserInMatch(match.getPk(), user.getPk());
            matchInviteIWS.acceptMatchInvite(matchInviteToAccept.getPk());

        }
        //after all teammates accept private wager match invite, they should be deducted wager amount and be associated to match.  Status of match should be Pending
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(match.getUsersInMatch().contains(user));
        }
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING);

        matchIWS.acceptToJoinPublicOrPrivateMatch(match, team2, usersInAcceptingTeam, userAcceptingMatch);

        //After team 2 leader accepts, associate him to the match, and deduct his balance
        Assert.assertTrue(match.getUsersInMatch().contains(userAcceptingMatch));
        Assert.assertTrue(match.getTeamsInMatch().contains(team2));
        Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(userAcceptingMatch.getBank().getTotalAmount()) == 0);
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_SECOND_ACCEPT);

        //Team 2 users should not be associated and deducted until they accept match invite
        List<User> usersInMatchWithoutAcceptor = new ArrayList<>(usersInAcceptingTeam);
        usersInMatchWithoutAcceptor.remove(userAcceptingMatch);
        for (User user : usersInMatchWithoutAcceptor) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }

        //accept match invite for all members of team 2 except match acceptor and last member
        List<User> usersInMatchWithoutAcceptorAndLastMember = new ArrayList<>(usersInMatchWithoutAcceptor);
        User lastMemberOnTeam2 = usersInMatchWithoutAcceptorAndLastMember.get(usersInMatchWithoutAcceptorAndLastMember.size() - 1);
        usersInMatchWithoutAcceptorAndLastMember.remove(lastMemberOnTeam2);
        for (int i = 0; i < usersInMatchWithoutAcceptorAndLastMember.size(); i++) {
            MatchInvite matchInviteToAccept = matchInviteIWS.findMatchInviteForUserInMatch(match.getPk(), usersInMatchWithoutAcceptorAndLastMember.get(i).getPk());
            matchInviteIWS.acceptMatchInvite(matchInviteToAccept.getPk());
        }

        //deduct wager amount for all members accepting
        for (User user : usersInMatchWithoutAcceptorAndLastMember) {
            Assert.assertTrue(balanceAfterCreatingOrAcceptingMatch.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertTrue(match.getUsersInMatch().contains(user));
        }
        //Assert last guy has not been deducted money and associated to match
        Assert.assertTrue(originalAmount.compareTo(lastMemberOnTeam2.getBank().getTotalAmount()) == 0);
        Assert.assertFalse(match.getUsersInMatch().contains(lastMemberOnTeam2));

        //decline match invite for last guy
        MatchInvite matchInviteToDecline = matchInviteIWS.findMatchInviteForUserInMatch(match.getPk(), lastMemberOnTeam2.getPk());
        matchInviteIWS.declineMatchInvite(matchInviteToDecline.getPk());

        //Everyone on team 1 should get their money back and match status should be back to pending
        for (User user : usersInCreatingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }
        //Everyone on team 2 should get their money back and match status should be back to pending
        for (User user : usersInAcceptingTeam) {
            Assert.assertTrue(originalAmount.compareTo(user.getBank().getTotalAmount()) == 0);
            Assert.assertFalse(match.getUsersInMatch().contains(user));
        }
        Assert.assertFalse(match.getTeamsInMatch().contains(team1));
        Assert.assertFalse(match.getTeamsInMatch().contains(team2));
        Assert.assertTrue(match.getMatchInfo().getMatchStatus() == MatchStatusEnum.CANCELLED);

        return match;
    }

    public MatchPojo constructMatchPojo(GameEnum gameEnum, int numOfPlayers, Team senderTeam) {
        MatchPojo matchInfo = new MatchPojo();
        matchInfo.setGameEnum(gameEnum);
        matchInfo.setWagerAmountPerMember(new BigDecimal(5.00));

        matchInfo.setNumOfPlayers(numOfPlayers);
        matchInfo.setNumOfPlayersNeedingToAccept(numOfPlayers);

        matchInfo.setTeamSizeEnum(senderTeam.getTeamPojo().getTeamSize());
        matchInfo.setPlatform(senderTeam.getTeamPojo().getPlatform());
        return matchInfo;
    }
}
