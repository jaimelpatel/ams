package com.ltlogic.iws;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.MatchCancellationRequest;
import com.ltlogic.db.entity.MatchInvite;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.iw.IWMatch;
import com.ltlogic.db.entity.mwr.MWRMatch;
import com.ltlogic.db.entity.ww2.WW2Match;
import com.ltlogic.pojo.MatchPojo;
import com.ltlogic.pojo.mwr.MWRMatchPojo;
import com.ltlogic.service.core.MatchService;
import com.ltlogic.service.core.TeamService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author raymond
 */
@Service
public class MatchIWS {

    @Autowired
    private MatchService matchService;

    public Match findMatchByPk(long matchPk) {
        return matchService.findMatchByPk(matchPk);
    }

    public List<Match> getAllMatchesPublic(int pageNumber) {
        return matchService.getAllMatchesPublic(pageNumber);
    }

    public Match createMatch(MatchPojo matchInfo, Object object, Team senderTeam, List<User> userInMatchForCreatingTeam, Team receiverTeam, User userCreatingMatch) throws Exception {
        return matchService.createMatch(matchInfo, object, senderTeam, userInMatchForCreatingTeam, receiverTeam, userCreatingMatch);
    }

    public void editMWRMatch(MatchPojo matchInfo, MWRMatchPojo mwrMatchInfo, long matchPk) {
        matchService.editMWRMatch(matchInfo, mwrMatchInfo, matchPk);
    }

    public void acceptToJoinPublicOrPrivateMatch(Match match, Team teamAccepting, List<User> userInMatchForAcceptingTeam, User userAcceptingMatch) throws Exception {
        matchService.acceptToJoinPublicOrPrivateMatch(match, teamAccepting, userInMatchForAcceptingTeam, userAcceptingMatch);
    }

    public void requestCancellation(long matchPk, String userRequesting) throws Exception {
        matchService.requestCancellation(matchPk, userRequesting);
    }

    public void acceptCancellationRequest(long matchPk, String userAccepting) {
        matchService.acceptMatchCancellationRequest(matchPk, userAccepting);
    }

    public void declineCancellationRequest(long matchPk, String userDeclining) {
        matchService.declineMatchCancellationRequest(matchPk, userDeclining);
    }

    public List<Match> findMatchesByUserPk(long userPk) {
        return matchService.findMatchesByUserPk(userPk);
    }

    public List<Match> findMatchesByUserPkForPaginate(long userPk, int pageNumber) {
        return matchService.findMatchesByUserPkForPaginate(userPk, pageNumber);
    }

    public Match findUserMatchByMatchPKAndUsername(Long matchPk, String username) {
        return matchService.findUserMatchByMatchPKAndUsername(matchPk, username);
    }

    public void reportMatchScore(User user, Match match, Team team, boolean didWin, boolean isAdmin) {
        matchService.reportMatchScore(user, match, team, didWin, isAdmin);
    }

    public MWRMatch getMWRMatchByMatchPk(long matchPk) {
        return matchService.getMWRMatchByMatchPk(matchPk);
    }

    public IWMatch getIWMatchByMatchPk(long matchPk) {
        return matchService.getIWMatchByMatchPk(matchPk);
    }

    public WW2Match getWW2MatchByMatchPk(long matchPk) {
        return matchService.getWW2MatchByMatchPk(matchPk);
    }

    public void cancelMatch(long matchPk) {
        matchService.cancelMatch(matchPk);
    }

    public List<Match> getMatchesByGame(GameEnum gameEnum, int pageNumber) {
        return matchService.getMatchesByGame(gameEnum, pageNumber);
    }

    public List<Match> getMatchesByGameAndPlatform(GameEnum gameEnum, PlatformEnum platformEnum) {
        return matchService.getMatchesByGameAndPlatform(gameEnum, platformEnum);
    }

    public List<Match> getMatchesByPlatform(PlatformEnum platformEnum, int pageNumber) {
        return matchService.getMatchesByPlatform(platformEnum, pageNumber);
    }

    public List<Match> findMatchesByUserPkAndMatchStatus(long userPk, MatchStatusEnum matchStatusEnum, int pageNumber) {
        return matchService.findMatchesByUserPkAndMatchStatus(userPk, matchStatusEnum, pageNumber);
    }

    public List<Match> getMatchesByUserPkAndMatchNotKilledStatus(long userPk) {
        return matchService.getMatchesByUserPkAndMatchNotKilledStatus(userPk);
    }

}
