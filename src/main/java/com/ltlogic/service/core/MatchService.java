/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.BestOfEnum;
import com.ltlogic.constants.CancellationRequestEnum;
import com.ltlogic.constants.DisputeStatus;
import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.GameModeEnum;
import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.constants.MatchSizeEnum;
import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.constants.NotificationTypeEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.constants.TournamentStatusEnum;
import com.ltlogic.db.entity.Dispute;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.MatchCancellationRequest;
import com.ltlogic.db.entity.mwr.MWRMatch;
import com.ltlogic.db.entity.MatchInvite;
import com.ltlogic.db.entity.Notification;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.iw.IWMatch;
import com.ltlogic.db.entity.iw.IWTeam;
import com.ltlogic.db.entity.mwr.MWRTeam;
import com.ltlogic.db.entity.ww2.WW2Match;
import com.ltlogic.db.entity.ww2.WW2Team;
import com.ltlogic.db.repository.MatchCancellationRepository;
import com.ltlogic.db.repository.MatchInviteRepository;
import com.ltlogic.db.repository.MatchRepository;
import com.ltlogic.db.repository.NotificationRepository;
import com.ltlogic.db.repository.TeamRepository;
import com.ltlogic.db.repository.TournamentTeamRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.pojo.mwr.MWRMatchPojo;
import com.ltlogic.pojo.MatchPojo;
import com.ltlogic.pojo.iw.IWMatchPojo;
import com.ltlogic.pojo.ww2.WW2MatchPojo;
import com.ltlogic.service.challonge.ChallongeMatchService;
import com.ltlogic.service.challonge.ChallongeTournamentService;
import com.ltlogic.service.common.CommonService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Hoang
 */
@Service
@Transactional
public class MatchService {

    @Autowired
    private MatchRepository matchRepo;
    @Autowired
    private MatchInviteRepository matchInviteRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private MatchCancellationRepository cancellationRepo;
    @Autowired
    private TeamRepository teamRepo;
    @Autowired
    private MatchInviteService matchInviteService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private NotificationRepository notificationRepo;
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    private DisputeService disputeService;
    @Autowired
    private BankService bankService;
    @Autowired
    private ChallongeMatchService challongeMatchService;
    @Autowired
    private ChallongeTournamentService challongeTournamentService;
    @Autowired
    private TournamentTeamRepository tournamentTeamRepo;

    private static final Logger LOG = LoggerFactory.getLogger(MatchService.class);

    private static final BigDecimal COMMISSION_PERCENTAGE = new BigDecimal(.8);

    public Match findMatchByPk(long matchPk) {
        return matchRepo.findMatchByPk(matchPk);
    }

    public MWRMatch getMWRMatchByMatchPk(long matchPk) {
        return matchRepo.getMWRMatchByMatchPk(matchPk);
    }

    public IWMatch getIWMatchByMatchPk(long matchPk) {
        return matchRepo.getIWMatchByMatchPk(matchPk);
    }

    public WW2Match getWW2MatchByMatchPk(long matchPk) {
        return matchRepo.getWW2MatchByMatchPk(matchPk);
    }

    public List<Match> getAllMatchesPublic(int pageNumber) {
        return matchRepo.getAllMatchesPublic(pageNumber);
    }

    public void persistMatch(Match match) {
        matchRepo.persistMatch(match);
    }

    public List<Match> findMatchesByStatusInTournament(MatchStatusEnum matchStatus, long tournamentPk) {
        return matchRepo.findMatchesByStatusInTournament(matchStatus, tournamentPk);
    }

    public Match createMatchFromGameType(GameEnum gameEnum, Object object, MatchPojo matchInfo) {

        if (gameEnum == GameEnum.COD_IW) {
            IWMatchPojo iwMatchPojo = null;
            if (object instanceof IWMatchPojo) {
                iwMatchPojo = (IWMatchPojo) object;
            }
            IWMatch iwMatch = new IWMatch();
            iwMatch.setMatchInfo(matchInfo);
            iwMatch.setIwMatchInfo(iwMatchPojo);
            return iwMatch;
        } else if (gameEnum == GameEnum.COD_MWR) {
            MWRMatchPojo mwrMatchPojo = null;
            if (object instanceof MWRMatchPojo) {
                mwrMatchPojo = (MWRMatchPojo) object;
            }
            MWRMatch mwrMatch = new MWRMatch();
            mwrMatch.setMatchInfo(matchInfo);
            mwrMatch.setMwrMatchInfo(mwrMatchPojo);
            return mwrMatch;
        } else if (gameEnum == GameEnum.COD_WW2) {
            WW2MatchPojo ww2MatchPojo = null;
            if (object instanceof WW2MatchPojo) {
                ww2MatchPojo = (WW2MatchPojo) object;
            }
            WW2Match ww2Match = new WW2Match();
            ww2Match.setMatchInfo(matchInfo);
            ww2Match.setWw2MatchInfo(ww2MatchPojo);
            return ww2Match;
        } else {
            Match match = new Match();
            match.setMatchInfo(matchInfo);
            return match;
        }
    }

    public Match createMatch(MatchPojo matchInfo, Object object, Team senderTeam, List<User> usersInMatchForCreatingTeam, Team receiverTeam, User userCreatingMatch) throws Exception {
        GameEnum gameEnum = matchInfo.getGameEnum();
        Match match = createMatchFromGameType(gameEnum, object, matchInfo);

        //raymond is putting the match creator into the first index of the array he's returning
        match.setPkOfUserThatCreatedMatch(userCreatingMatch.getPk());
        match.setPkOfTeamThatCreatedMatch(senderTeam.getPk());
        matchRepo.persistMatch(match);
        int numOfPlayers = Math.multiplyExact(usersInMatchForCreatingTeam.size(), 2);
        int playersNeedingToAccept = numOfPlayers;
        matchInfo.setNumOfPlayers(numOfPlayers);
        matchInfo.setNumOfPlayersNeedingToAccept(playersNeedingToAccept);
        match.setHaveAllPlayersAccepted(false);
        boolean doesIdExist = true;
        while (doesIdExist) {
            int matchId = commonService.generateRandomId();
            Match m = matchRepo.findMatchByMatchId(matchId);
            if (m == null) {
                match.setMatchId(matchId);
                doesIdExist = false;
            }
        }

        matchInfo.setTeamSizeEnum(senderTeam.getTeamPojo().getTeamSize());
        matchInfo.setGameEnum(senderTeam.getTeamPojo().getGame());
        matchInfo.setPlatform(senderTeam.getTeamPojo().getPlatform());

        //tournaments will call a similar but different method to create matches
        convertTeamValuesToMatchValues(senderTeam, matchInfo, usersInMatchForCreatingTeam.size());

        //free matches are always public and no match invites required
        if (senderTeam.getTeamPojo().getTeamType() == TeamTypeEnum.XP) {
            matchInfo.setTeamTypeEnum(TeamTypeEnum.XP);
            matchInfo.setMatchType(MatchTypeEnum.XP);
            matchInfo.setMatchStatus(MatchStatusEnum.PENDING);
            matchInfo.setIsMatchPublic(true);
            for (User user : usersInMatchForCreatingTeam) {
                match.getPksOfCreatorTeamMembersPlaying().add(user.getPk());
                associateUserToMatch(user, match);
            }
            setXpMatchCreationNotification(matchInfo, userCreatingMatch, match);
        }

        Long creatorMatchInvitePk = 0L;
        //cash match
        if (senderTeam.getTeamPojo().getTeamType() == TeamTypeEnum.CASH) {
            BigDecimal totalPotAmount = matchInfo.getMatchWagerAmountPerMember().multiply(new BigDecimal(numOfPlayers));
            matchInfo.setPotAmount(totalPotAmount);
            matchInfo.setTeamTypeEnum(TeamTypeEnum.CASH);
            matchInfo.setMatchType(MatchTypeEnum.WAGER);
            matchInfo.setMatchStatus(MatchStatusEnum.WAITING_ON_FIRST_ACCEPT);

            //private cash
            if (receiverTeam != null) {
                matchInfo.setIsMatchPublic(false);
                //matchInviteService.inviteOpposingTeamLeaderToMatch(match, receiverTeam);
                //associateTeamToMatch(receiverTeam, match);
                match.setPkOfTeamThatAcceptedMatch(receiverTeam.getPk());
                setPrivateCashMatchCreationNotification(matchInfo, userCreatingMatch, match);
                //public cash
            } else {
                setPublicCashMatchCreationNotification(matchInfo, userCreatingMatch, match);
                matchInfo.setIsMatchPublic(true);
            }
            for (User user : usersInMatchForCreatingTeam) {
                match.getPksOfCreatorTeamMembersPlaying().add(user.getPk());
                matchInviteService.createNewMatchInvite(match, senderTeam, user, userCreatingMatch);
            }
            //automatically accept the match invite for the person creating match
            MatchInvite matchInviteForCreator = matchInviteService.findMatchInviteForUserInMatch(match.getPk(), userCreatingMatch.getPk());
            matchInviteService.acceptMatchInvite(matchInviteForCreator.getPk());

            //eventPublisher.publishEvent(new OnMatchCreationCompleteEvent(creatorMatchInvitePk));
        }

        associateTeamToMatch(senderTeam, match);
        return match;
    }

//for team leader to accept public and private matches (private is from inviteOpposingTeamLeaderToMatch in MatchInviteService)
    public void acceptToJoinPublicOrPrivateMatch(Match match, Team teamAccepting, List<User> userInMatchForAcceptingTeam, User userAcceptingMatch) throws Exception {
        associateTeamToMatch(teamAccepting, match);
        match.setTimeMatchWasAccepted();
        match.setPkOfTeamThatAcceptedMatch(teamAccepting.getPk());
        match.setPkOfUserThatAcceptedMatch(userAcceptingMatch.getPk());
        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.XP) {
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.READY_TO_PLAY);
            selectHostAndMapsForMatch(match, 0, 0, 0);
            for (User user : userInMatchForAcceptingTeam) {
                match.getPksOfAcceptorTeamMembersPlaying().add(user.getPk());
                associateUserToMatch(user, match);
                setAcceptPublicXpMatchNotification(match, user);
                match.setHaveAllPlayersAccepted(true);
            }
        } else if (match.getMatchInfo().getMatchType() == MatchTypeEnum.WAGER) {
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.WAITING_ON_SECOND_ACCEPT);
            for (User user : userInMatchForAcceptingTeam) {
                matchInviteService.createNewMatchInvite(match, teamAccepting, user, userAcceptingMatch);
                match.getPksOfAcceptorTeamMembersPlaying().add(user.getPk());
            }
            MatchInvite matchInviteForAcceptor = matchInviteService.findMatchInviteForUserInMatch(match.getPk(), userAcceptingMatch.getPk());
            matchInviteService.acceptMatchInvite(matchInviteForAcceptor.getPk());
            setAcceptPublicWagerMatchNotification(match, userAcceptingMatch);
        }
    }

    private void setAcceptPublicWagerMatchNotification(Match match, User user) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.JOINED_WAGER);
        notification.setNotificationMessage("You have accepted to join a " + match.getMatchInfo().getGameEnum().getGameEnumDesc() + " " + match.getMatchInfo().getMatchType().getMatchTypeEnumDesc() + " match. Your team members playing in the match have 15 minutes or until the scheduled match time if shorter to accept their match invites.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setMatch(match);
        match.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    private void setPublicCashMatchCreationNotification(MatchPojo matchInfo, User userWhoCreatedMatch, Match match) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.CREATE_WAGER_MATCH);
        notification.setNotificationMessage("You have created a public " + matchInfo.getGameEnum().getGameEnumDesc() + " " + matchInfo.getMatchType().getMatchTypeEnumDesc() + " match. The match will be posted in the matchfinder once all members on your team playing in the match have accepted.");
        notification.setUser(userWhoCreatedMatch);
        userWhoCreatedMatch.getNotification().add(notification);
        notification.setMatch(match);
        match.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    private void setPrivateCashMatchCreationNotification(MatchPojo matchInfo, User userWhoCreatedMatch, Match match) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.CREATE_WAGER_MATCH);
        notification.setNotificationMessage("You have created a private " + matchInfo.getGameEnum().getGameEnumDesc() + " " + matchInfo.getMatchType().getMatchTypeEnumDesc() + " match. A match invite will be sent to the leader of the team you are challenging once all members on your team playing in the match have accepted.");
        notification.setUser(userWhoCreatedMatch);
        userWhoCreatedMatch.getNotification().add(notification);
        notification.setMatch(match);
        match.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    private void setXpMatchCreationNotification(MatchPojo matchInfo, User userWhoCreatedMatch, Match match) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.CREATE_XP_MATCH);
        notification.setNotificationMessage("You have posted a new " + matchInfo.getGameEnum().getGameEnumDesc() + " " + matchInfo.getMatchType().getMatchTypeEnumDesc() + " match on the matchfinder.");
        notification.setUser(userWhoCreatedMatch);
        userWhoCreatedMatch.getNotification().add(notification);
        notification.setMatch(match);
        match.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    private void setAcceptPublicXpMatchNotification(Match match, User user) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.JOINED_XP);
        notification.setNotificationMessage("You have joined a " + match.getMatchInfo().getGameEnum().getGameEnumDesc() + " " + match.getMatchInfo().getMatchType().getMatchTypeEnumDesc() + " match.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setMatch(match);
        match.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    /**
     * @TODO need to create some kind of notification for the users in both
     * team, or at least to the team leader.
     */
    public void startMatches() {
        List<Match> matchesToStart = matchRepo.findAllMatchesToStart();
        if (matchesToStart.size() > 0) {
            for (Match m : matchesToStart) {
                m.getMatchInfo().setMatchStatus(MatchStatusEnum.ACTIVE);
                m.getMatchInfo().setMatchStartTime();
                for (User user : m.getUsersInMatch()) {
                    setMatchedStartedNotification(m, user);
                }
            }
        }
    }

    private void setMatchedStartedNotification(Match m, User user) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.MATCH_STARTED);
        notification.setNotificationMessage("A " + m.getMatchInfo().getGameEnum().getGameEnumDesc() + " " + m.getMatchInfo().getMatchType().getMatchTypeEnumDesc() + " match you are in has started.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setMatch(m);
        m.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    /**
     * @TODO need to create some kind of notification for the users in both
     * team, or at least to the team leader.
     */
    public void killMatches() {
        List<Match> matchesToStart = matchRepo.findAllMatchesToKill();
        if (matchesToStart.size() > 0) {
            for (Match m : matchesToStart) {
                m.getMatchInfo().setMatchStatus(MatchStatusEnum.EXPIRED);
                List<User> usersInMatch = userRepo.getUsersByMatchPk(m.getPk());
                for (User user : usersInMatch) {
                    dissociateUserFromMatch(user, m);
                    setMatchExpiredNotification(m, user);
                    if (m.getMatchInfo().getMatchType() == MatchTypeEnum.WAGER) {
                        MatchInvite matchInvite = matchInviteRepo.findMatchInviteForUserInMatch(m.getPk(), user.getPk());
                        if (matchInvite != null) {
                            if (matchInvite.getInviteEnum() == InvitesEnum.ACCEPTED) {
                                bankService.wagerMatchCancelled(user, m);
                            }
                        }
                        matchInvite.setInviteEnum(InvitesEnum.DECLINED);
                        matchInvite.setDeclinedAt();
                    }
                }
            }
        }
    }

    public void killPublicWagerMatches() {
        List<Match> upcomingMatchesToKill = matchRepo.findAllPublicWagerMatchesToKill();
        if (upcomingMatchesToKill.size() > 0) {
            for (Match m : upcomingMatchesToKill) {
                m.getMatchInfo().setMatchStatus(MatchStatusEnum.PENDING);
                long acceptorTeamPk = m.getPkOfTeamThatAcceptedMatch();
                Team acceptorTeam = teamRepo.findTeamByPk(acceptorTeamPk);
                if (acceptorTeam != null) {
                    List<Long> acceptorTeamMembersToRefund = m.getPksOfAcceptorTeamMembersPlaying();
                    if (acceptorTeamMembersToRefund != null && !acceptorTeamMembersToRefund.isEmpty()) {
                        for (long userPk : acceptorTeamMembersToRefund) {
                            MatchInvite matchInvite = matchInviteRepo.findMatchInviteForUserInMatch(m.getPk(), userPk);
                            if (matchInvite != null) {
                                if (matchInvite.getInviteEnum() == InvitesEnum.ACCEPTED) {
                                    User user = userRepo.findByPk(userPk);
                                    bankService.wagerMatchCancelled(user, m);
                                }
                            }
                        }
                    }
                    dissociateTeamFromMatch(acceptorTeam, m);
                }
            }
        }
    }

    private void setMatchExpiredNotification(Match m, User user) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.EXPIRED_MATCH);
        notification.setNotificationMessage("The " + m.getMatchInfo().getGameEnum().getGameEnumDesc() + " " + m.getMatchInfo().getMatchType().getMatchTypeEnumDesc() + " match you are in has expired as no team and its members accepted in time.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setMatch(m);
        m.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    /**
     * @TODO need to create some kind of notification for the users in both
     * team, or at least to the team leader.
     */
    public void notifyUpcomingMatches() {
        List<Match> upcomingMatchesToNotify = matchRepo.findAllUpcomingMatchesToNotify();
        if (upcomingMatchesToNotify.size() > 0) {
            for (Match m : upcomingMatchesToNotify) {
                for (User user : m.getUsersInMatch()) {
                    Notification n = notificationRepo.getNotificationByUserPkAndMatchPkAndType(user.getPk(), m.getPk(), NotificationTypeEnum.UPCOMING_MATCH);
                    LocalDateTime dateTime = convertStringToLocalDateTime(m);
                    if (n == null && DateTimeUtil.getDefaultLocalDateTimeNow().plusMinutes(2).isBefore(dateTime)) {
                        setUpcomingMatchNotfication(m, user);
                    }
                }
            }
        }
    }

    public void cancelMatches() {
        List<Match> matchesToCancel = matchRepo.findAllActiveXpAndWagerMatchesToCancel();
        for (Match m : matchesToCancel) {
            cancelMatchByAdmin(m);
        }
    }

    public void endMatches() {
        List<Match> matchesToCancel = matchRepo.findAllActiveXpAndWagerMatchesToEnd();
        for (Match m : matchesToCancel) {
            if (m.getPkOfUserThatReportedMatchScoreCreated() == 0) {
                User user = userRepo.findByPk(m.getPkOfUserThatCreatedMatch());
                Team team = teamRepo.findTeamByPk(m.getPkOfTeamThatCreatedMatch());
                reportMatchScore(user, m, team, false, false);
            } else {
                User user = userRepo.findByPk(m.getPkOfUserThatAcceptedMatch());
                Team team = teamRepo.findTeamByPk(m.getPkOfTeamThatAcceptedMatch());
                reportMatchScore(user, m, team, false, false);
            }
        }
    }

    private LocalDateTime convertStringToLocalDateTime(Match m) {
        String str = m.getMatchInfo().getScheduledMatchTime(null);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-d-yyyy h:mm a");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        return dateTime;
    }

    private void setUpcomingMatchNotfication(Match m, User user) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.UPCOMING_MATCH);
        notification.setNotificationMessage("You have a " + m.getMatchInfo().getGameEnum().getGameEnumDesc() + " " + m.getMatchInfo().getMatchType().getMatchTypeEnumDesc() + " match starting in less than 10 minutes.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setMatch(m);
        m.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    /**
     * Does not gets the correct list of matches to start
     *
     * @deprecated use
     * {@link com.ltlogic.service.core.MatchService#startMatches() startMatches}
     * instead.
     */
    @Deprecated
    public void startMatch(long matchPk) {
        Match match = matchRepo.findMatchByPk(matchPk);
        //This check may be unncessary if we kill the scheduler job at the right time
        if (match.isHaveAllPlayersAccepted() && match.getMatchInfo().getMatchStatus() != MatchStatusEnum.CANCELLED) {
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.ACTIVE);
            match.getMatchInfo().setMatchStartTime();
        }
    }

    public void editMWRMatch(MatchPojo matchInfo, MWRMatchPojo mwrMatchInfo, long matchPk) {
        MWRMatch mwrMatch = (MWRMatch) matchRepo.findMatchByPk(matchPk);
        mwrMatch.setMwrMatchInfo(mwrMatchInfo);
        mwrMatch.setMatchInfo(matchInfo);
    }

    public void editIWMatch(MatchPojo matchInfo, IWMatchPojo iwMatchInfo, long matchPk) {
        IWMatch iwMatch = (IWMatch) matchRepo.findMatchByPk(matchPk);
        iwMatch.setIwMatchInfo(iwMatchInfo);
        iwMatch.setMatchInfo(matchInfo);
    }

    public void editWW2Match(MatchPojo matchInfo, WW2MatchPojo ww2MatchInfo, long matchPk) {
        WW2Match ww2Match = (WW2Match) matchRepo.findMatchByPk(matchPk);
        ww2Match.setWw2MatchInfo(ww2MatchInfo);
        ww2Match.setMatchInfo(matchInfo);
    }

    public MatchCancellationRequest requestCancellation(long matchPk, String usernameOfUserRequesting) throws Exception {
        Match match = matchRepo.findMatchByPk(matchPk);
        User user = userRepo.findByUsername(usernameOfUserRequesting);
        if (match.getCancellationRequest() != null && match.getCancellationRequest().getCancellationRequestEnum() == CancellationRequestEnum.PENDING) {
            throw new Exception("This match already has a cancellation request pending.");
        }

        User userRequesting = userRepo.findByUsername(usernameOfUserRequesting);
        MatchCancellationRequest request = new MatchCancellationRequest();
        request.setCancellationRequestEnum(CancellationRequestEnum.PENDING);
        request.setMatch(match);
        match.setCancellationRequest(request);
        request.setCancellationRequester(userRequesting);
        userRequesting.getCancellationRequester().add(request);
        Team matchCreator = teamRepo.findTeamByPk(match.getPkOfTeamThatCreatedMatch());
        if (matchCreator.getMembers().contains(user)) {
            User acceptingUser = userRepo.findByPk(match.getPkOfUserThatAcceptedMatch());
            request.setCancellationRequestee(acceptingUser);
            acceptingUser.getCancellationRequestee().add(request);
        } else {
            User creatingUser = userRepo.findByPk(match.getPkOfUserThatCreatedMatch());
            request.setCancellationRequestee(creatingUser);
            creatingUser.getCancellationRequestee().add(request);
        }

        request.setSentAt();
        cancellationRepo.persistRequest(request);
        return request;
    }

    public void acceptMatchCancellationRequest(long matchPk, String usernameOfUserAccepting) {
        Match match = matchRepo.findMatchByPk(matchPk);
        MatchCancellationRequest request = match.getCancellationRequest();
        if (request.getCancellationRequestEnum() != CancellationRequestEnum.PENDING) {
            return;
        }
        if (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.READY_TO_PLAY && request.getCancellationRequestEnum() == CancellationRequestEnum.PENDING) {
            request.setCancellationRequestEnum(CancellationRequestEnum.ACCEPTED);
            request.setAcceptedAt();
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.CANCELLED);
            List<User> usersInMatch = userRepo.getUsersByMatchPk(match.getPk());
            for (User user : usersInMatch) {
                dissociateUserFromMatch(user, match);
                setMatchCancellationNotification(user, match);
                if (match.getMatchInfo().getMatchType() == MatchTypeEnum.WAGER) {
                    MatchInvite matchInvite = matchInviteRepo.findMatchInviteForUserInMatch(match.getPk(), user.getPk());
                    if (matchInvite != null) {
                        if (matchInvite.getInviteEnum() == InvitesEnum.ACCEPTED) {
                            bankService.wagerMatchCancelled(user, match);
                        }
                    }
                }
            }
        }
    }

    public void declineMatchCancellationRequest(long matchPk, String usernameOfUserDeclining) {
        Match match = matchRepo.findMatchByPk(matchPk);
        MatchCancellationRequest request = match.getCancellationRequest();
        if (request.getCancellationRequestEnum() != CancellationRequestEnum.PENDING) {
            return;
        }
        request.setCancellationRequestEnum(CancellationRequestEnum.DECLINED);
        request.setDeclinedAt();
    }

    private void setMatchCancellationNotification(User user, Match match) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.CANCELLED_MATCH);
        notification.setNotificationMessage("The match has been cancelled as both teams have agreed to it.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setMatch(match);
        match.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    public void associateTeamToMatch(Team t, Match m) {
        t.getMatches().add(m);
        m.getTeamsInMatch().add(t);
    }

    public void associateUserToMatch(User u, Match m) {
        u.getMatches().add(m);
        m.getUsersInMatch().add(u);
    }

    public void dissociateTeamFromMatch(Team t, Match m) {
        t.getMatches().remove(m);
        m.getTeamsInMatch().remove(t);
    }

    public void dissociateUserFromMatch(User u, Match m) {
        u.getMatches().remove(m);
        m.getUsersInMatch().remove(u);
    }

    public List<Match> findMatchesByUserPk(long userPk) {
        return matchRepo.findMatchesByUserPk(userPk);
    }

    public List<Match> findMatchesByUserPkForPaginate(long userPk, int pageNumber) {
        return matchRepo.findMatchesByUserPkForPaginate(userPk, pageNumber);
    }

    public void convertTeamValuesToMatchValues(Team senderTeam, MatchPojo matchInfo, int numOfUsersPerSide) {
        if (senderTeam.getTeamPojo().getTeamType() == TeamTypeEnum.XP) {
            matchInfo.setMatchType(MatchTypeEnum.XP);
        } else {
            matchInfo.setMatchType(MatchTypeEnum.WAGER);
        }

        switch (numOfUsersPerSide) {
            case 1:
                matchInfo.setMatchSizeEnum(MatchSizeEnum.SINGLES);
                break;
            case 2:
                matchInfo.setMatchSizeEnum(MatchSizeEnum.DOUBLES);
                break;
            case 3:
                matchInfo.setMatchSizeEnum(MatchSizeEnum.THREES);
                break;
            case 4:
                matchInfo.setMatchSizeEnum(MatchSizeEnum.FOURS);
                break;
            default:
                break;
        }
    }

    public Match findUserMatchByMatchPKAndUsername(Long matchPk, String username) {
        return matchRepo.findUserMatchByMatchPKAndUsername(matchPk, username);
    }

    public void cancelMatchByAdmin(Match match) {
        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.XP) {
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.CANCELLED);
            List<User> usersInMatch = userRepo.getUsersByMatchPk(match.getPk());
            for (User user : usersInMatch) {
                dissociateUserFromMatch(user, match);
                setMatchCancelledByAdminNotification(match, user);
            }

        } else if (match.getMatchInfo().getMatchType() == MatchTypeEnum.WAGER) {
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.CANCELLED);
            List<User> usersInMatch = userRepo.getUsersByMatchPk(match.getPk());
            for (User user : usersInMatch) {
                bankService.wagerMatchCancelled(user, match);
                dissociateUserFromMatch(user, match);
                setMatchCancelledByAdminNotification(match, user);
            }
        }

        if (match.getDisputes().size() > 0) {
            for (Dispute d : match.getDisputes()) {
                d.setDisputeStatus(DisputeStatus.RESOLVED);
            }
        }
    }

    public void sendAdminNotificationToAllUsersInMatch(Match match, String adminName) {
        for (User user : match.getUsersInMatch()) {
            Notification notification = new Notification();
            notification.setNotificationType(NotificationTypeEnum.CANCELLED_MATCH);
            notification.setNotificationMessage("NLG Admin '" + adminName + "' has been assigned to your match to help resolve any disputes or issues you may be having.");
            notification.setUser(user);
            user.getNotification().add(notification);
            notification.setMatch(match);
            match.getNotification().add(notification);
            notificationRepo.persistNotification(notification);
        }
    }

    private void setMatchCancelledByAdminNotification(Match match, User user) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.CANCELLED_MATCH);
        notification.setNotificationMessage("You have a match that has been cancelled by an admin.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setMatch(match);
        match.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    public void reportMatchScore(User user, Match match, Team team, boolean didWin, boolean isAdmin) {
        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.XP) {
            reportXpMatchScore(match, team, user, didWin);
        } else if (match.getMatchInfo().getMatchType() == MatchTypeEnum.WAGER) {
            reportWagerMatchScore(match, team, user, didWin);
        } else if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
            reportTournamentMatchScore(match, team, user, didWin);
        }
        // the only way to resolve a dispute by admins, is by declaring someone LOST the match.
        // above logics will automatically handle everything else; so just resolve by declaring that one guy lost
        if (!didWin && isAdmin) {
            disputeService.resolveDispute(match, team);
        }

        //setTeamDisputePercentage(team);
    }

    private void setTeamDisputePercentage(Team team) {
        int totalMatchCount = matchRepo.findMatchesByTeamPkInActiveDisputedAndEndedStatus(team.getPk()).size();
        int totalDisputedMatchCount = team.getDisputedMatches();
        if (totalDisputedMatchCount == 0) {
            team.setDisputePercentage(new BigDecimal(0).setScale(0, RoundingMode.HALF_UP));
        } else {
            BigDecimal disputeDecimal = new BigDecimal(totalDisputedMatchCount).divide(new BigDecimal(totalMatchCount)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal disputePercentage = disputeDecimal.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
            team.setDisputePercentage(disputePercentage);
        }
    }

    private void reportXpMatchScore(Match match, Team team, User user, boolean didWin) {
        if (match.getPkOfTeamThatCreatedMatch() == team.getPk()) {
            creatorTeamReportedXpMatchScore(match, user, didWin, team);
        } else if (match.getPkOfTeamThatAcceptedMatch() == team.getPk()) {
            acceptorTeamReportedXpMatchScore(match, user, didWin, team);
        }
    }

    private void reportWagerMatchScore(Match match, Team team, User user, boolean didWin) {
        if (match.getPkOfTeamThatCreatedMatch() == team.getPk()) {
            creatorTeamReportedWagerMatchScore(match, user, didWin, team);
        } else if (match.getPkOfTeamThatAcceptedMatch() == team.getPk()) {
            acceptorTeamReportedWagerMatchScore(match, user, didWin, team);
        }
    }

    private void reportTournamentMatchScore(Match match, Team team, User user, boolean didWin) {
        if (match.getPkOfTeamThatCreatedMatch() == team.getPk()) {
            creatorTeamReportedTournamentMatchScore(match, user, didWin, team);
        } else if (match.getPkOfTeamThatAcceptedMatch() == team.getPk()) {
            acceptorTeamReportedTournamentMatchScore(match, user, didWin, team);
        }
    }

    private void acceptorTeamReportedTournamentMatchScore(Match match, User user, boolean didWin, Team team) {
        match.setPkOfUserThatReportedMatchScoreAccepted(user.getPk());
        match.setReportedScoreOfTeamAccepted(didWin);
        if (didWin == false) {
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.ENDED);
            match.setPkOfTeamLostMatch(team.getPk());
            match.setPkOfTeamWonMatch(match.getPkOfTeamThatCreatedMatch());
            Tournament tournament = match.getTournament();
            tournament.getListOfTeamsRemainingInTournament().remove(team.getPk());

            if (tournament.getTournamentInfo().getMaxRounds() == match.getMatchResponse().getMatchPojo().getRound()) {
                tournament.getTournamentInfo().setTournamentStatus(TournamentStatusEnum.ENDED);
                Team teamThatCreatedMatchWon = teamRepo.findTeamByPk(match.getPkOfTeamThatCreatedMatch());
                TournamentTeam tournamentTeam = tournamentTeamRepo.getTournamentTeamByNameAndTournament(teamThatCreatedMatchWon.getTeamPojo().getTeamName(), match.getTournament());
                challongeMatchService.reportMatchWinner(match.getTournament(), match, tournamentTeam, true);
                challongeTournamentService.finalizeChallongeTournament(tournament);
                //give money to winners
                BigDecimal potAmountWithCut = match.getTournament().getTournamentInfo().getPotAmount().multiply(COMMISSION_PERCENTAGE);
                int sizeOfTeam = match.getMatchInfo().getNumOfPlayers() / 2;
                BigDecimal amountWonForUser = potAmountWithCut.divide(new BigDecimal(sizeOfTeam));
                Team creatorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatCreatedMatch());
                int matchesWon = creatorTeam.getMatchesWon();
                int newMatchesWon = matchesWon + 1;
                creatorTeam.setMatchesWon(newMatchesWon);
                int goldTrophies = creatorTeam.getGoldTrophyCount();
                int newGoldTrophies = goldTrophies + 1;
                creatorTeam.setGoldTrophyCount(newGoldTrophies);
                int amountOfXpWin = commonService.generateRandomXpWinForUser();
                for (long userPk : match.getPksOfCreatorTeamMembersPlaying()) {
                    User u = userRepo.findByPk(userPk);
                    bankService.tournamentWin(u, match.getTournament(), amountWonForUser);
                    int totalUserXp = u.getRankXP().getTotalXp();
                    int newTotalUserXp = totalUserXp + amountOfXpWin;
                    u.getRankXP().setTotalXp(newTotalUserXp);
                    int goldTrophiesUser = u.getGoldTrophyCount();
                    int newGoldTrophiesUser = goldTrophiesUser + 1;
                    u.setGoldTrophyCount(newGoldTrophiesUser);
                }
                Team acceptorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatAcceptedMatch());
                int matchesLost = acceptorTeam.getMatchesLost();
                int newMatchesLost = matchesLost + 1;
                acceptorTeam.setMatchesLost(newMatchesLost);
                int silverTrophies = acceptorTeam.getSilverTrophyCount();
                int newSilverTrophies = silverTrophies + 1;
                acceptorTeam.setSilverTrophyCount(newSilverTrophies);
                int amountOfXpLoss = commonService.generateRandomXpLossForUser();
                for (long userPk : match.getPksOfAcceptorTeamMembersPlaying()) {
                    User u = userRepo.findByPk(userPk);
                    int totalUserXp = u.getRankXP().getTotalXp();
                    int newTotalUserXp = totalUserXp - amountOfXpLoss;
                    if (newTotalUserXp <= 0) {
                        u.getRankXP().setTotalXp(0);
                    } else {
                        u.getRankXP().setTotalXp(newTotalUserXp);
                    }
                    int silverTrophiesUser = u.getSilverTrophyCount();
                    int newSilverTrophiesUser = silverTrophiesUser + 1;
                    u.setSilverTrophyCount(newSilverTrophiesUser);
                }

                if (match.getMatchInfo().getGameEnum() == GameEnum.COD_MWR) {
                    assignNewMwrTeamXpTotals(creatorTeam, acceptorTeam);
                    assignNewMwrTeamCashTotals(creatorTeam, potAmountWithCut);
                } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_WW2) {
                    assignNewWW2TeamXpTotals(creatorTeam, acceptorTeam);
                    assignNewWw2TeamCashTotals(creatorTeam, potAmountWithCut);
                } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_IW) {
                    assignNewIWTeamXpTotals(creatorTeam, acceptorTeam);
                    assignNewIwTeamCashTotals(creatorTeam, potAmountWithCut);
                }
            } else {
                Team creatorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatCreatedMatch());
                int matchesWon = creatorTeam.getMatchesWon();
                int newMatchesWon = matchesWon + 1;
                creatorTeam.setMatchesWon(newMatchesWon);
                int amountOfXpWin = commonService.generateRandomXpWinForUser();
                for (long userPk : match.getPksOfCreatorTeamMembersPlaying()) {
                    User u = userRepo.findByPk(userPk);
                    int totalUserXp = u.getRankXP().getTotalXp();
                    int newTotalUserXp = totalUserXp + amountOfXpWin;
                    u.getRankXP().setTotalXp(newTotalUserXp);
                }
                Team acceptorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatAcceptedMatch());
                int matchesLost = acceptorTeam.getMatchesLost();
                int newMatchesLost = matchesLost + 1;
                acceptorTeam.setMatchesLost(newMatchesLost);
                int semiFinalsRound = tournament.getTournamentInfo().getMaxRounds() - 1;
                if (semiFinalsRound == match.getMatchResponse().getMatchPojo().getRound()) {
                    //Team acceptorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatAcceptedMatch());
                    int bronzeTrophies = acceptorTeam.getBronzeTrophyCount();
                    int newBronzeTrophies = bronzeTrophies + 1;
                    acceptorTeam.setBronzeTrophyCount(newBronzeTrophies);
                }
                for (long userPk : match.getPksOfAcceptorTeamMembersPlaying()) {
                    User u = userRepo.findByPk(userPk);
                    int totalUserXp = u.getRankXP().getTotalXp();
                    int amountOfXpLoss = commonService.generateRandomXpLossForUser();
                    int newTotalUserXp = totalUserXp - amountOfXpLoss;
                    if (newTotalUserXp <= 0) {
                        u.getRankXP().setTotalXp(0);
                    } else {
                        u.getRankXP().setTotalXp(newTotalUserXp);
                    }
                    if (semiFinalsRound == match.getMatchResponse().getMatchPojo().getRound()) {
                        int bronzeTrophies = u.getBronzeTrophyCount();
                        int newBronzeTrophies = bronzeTrophies + 1;
                        u.setBronzeTrophyCount(newBronzeTrophies);
                    }
                }
                if (match.getMatchInfo().getGameEnum() == GameEnum.COD_MWR) {
                    assignNewMwrTeamXpTotals(creatorTeam, acceptorTeam);
                } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_WW2) {
                    assignNewWW2TeamXpTotals(creatorTeam, acceptorTeam);
                } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_IW) {
                    assignNewIWTeamXpTotals(creatorTeam, acceptorTeam);
                }

                Team teamThatCreatedMatchWon = teamRepo.findTeamByPk(match.getPkOfTeamThatCreatedMatch());
                TournamentTeam tournamentTeam = tournamentTeamRepo.getTournamentTeamByNameAndTournament(teamThatCreatedMatchWon.getTeamPojo().getTeamName(), match.getTournament());
                challongeMatchService.reportMatchWinner(match.getTournament(), match, tournamentTeam, true);
            }
        } else {
            if (match.getReportedScoreOfTeamCreated() == null) {
                //waiting on team that created to report to confirm win...
            } else if (match.getReportedScoreOfTeamCreated() == didWin) {
                //create dispute because both teams saying they won...
                match.getMatchInfo().setMatchStatus(MatchStatusEnum.DISPUTED);
                Dispute firstDispute = disputeService.createDispute(user.getPk(), match.getPk(), team.getPk(), "The match outcome has been disputed because both teams have claimed to win the match.", null, true);
                setDisputedMatchOutcomeNotification(user, match, firstDispute);
                Dispute secondDispute = disputeService.createDispute(match.getPkOfUserThatReportedMatchScoreCreated(), match.getPk(), match.getPkOfTeamThatCreatedMatch(), "The match outcome has been disputed because both teams have claimed to win the match.", null, true);
                User secondUser = userRepo.findByPk(match.getPkOfUserThatReportedMatchScoreCreated());
                setDisputedMatchOutcomeNotification(secondUser, match, secondDispute);
            }
        }
    }

    private void assignNewMwrTeamCashTotals(Team creatorTeam, BigDecimal potAmountWithCut) {
        MWRTeam mwrCreatorTeam = teamRepo.getMWRTeamByTeamPk(creatorTeam.getPk());
        BigDecimal totalCashEarned = mwrCreatorTeam.getRankXP().getTotalCashEarned();
        BigDecimal newTotalCashEarned = totalCashEarned.add(potAmountWithCut).setScale(2, RoundingMode.HALF_UP);
        mwrCreatorTeam.getRankXP().setTotalCashEarned(newTotalCashEarned);
    }

    private void assignNewWw2TeamCashTotals(Team creatorTeam, BigDecimal potAmountWithCut) {
        WW2Team mwrCreatorTeam = teamRepo.getWW2TeamByTeamPk(creatorTeam.getPk());
        BigDecimal totalCashEarned = mwrCreatorTeam.getRankXP().getTotalCashEarned();
        BigDecimal newTotalCashEarned = totalCashEarned.add(potAmountWithCut).setScale(2, RoundingMode.HALF_UP);
        mwrCreatorTeam.getRankXP().setTotalCashEarned(newTotalCashEarned);
    }

    private void assignNewIwTeamCashTotals(Team creatorTeam, BigDecimal potAmountWithCut) {
        IWTeam iwCreatorTeam = teamRepo.getIWTeamByTeamPk(creatorTeam.getPk());
        BigDecimal totalCashEarned = iwCreatorTeam.getRankXP().getTotalCashEarned();
        BigDecimal newTotalCashEarned = totalCashEarned.add(potAmountWithCut).setScale(2, RoundingMode.HALF_UP);
        iwCreatorTeam.getRankXP().setTotalCashEarned(newTotalCashEarned);
    }

    private void assignNewWW2TeamXpTotals(Team creatorTeam, Team acceptorTeam) {
        WW2Team ww2CreatorTeam = teamRepo.getWW2TeamByTeamPk(creatorTeam.getPk());
        WW2Team ww2AcceptorTeam = teamRepo.getWW2TeamByTeamPk(acceptorTeam.getPk());
        if (Math.abs(ww2CreatorTeam.getRankXP().getRank() - ww2AcceptorTeam.getRankXP().getRank()) <= 50) {
            int amountOfXpToGive = commonService.generateRandomXpWinForCloseTeam();
            int amountOfXpToTake = commonService.generateRandomXpLossForCloseTeam();
            int totalCreatorTeamXp = ww2CreatorTeam.getRankXP().getTotalTeamXp();
            int totalAcceptorTeamXp = ww2AcceptorTeam.getRankXP().getTotalTeamXp();
            int newTotalCreatorTeamXp = totalCreatorTeamXp + amountOfXpToGive;
            if (ww2CreatorTeam.isXpGainOn()) {
                ww2CreatorTeam.getRankXP().setTotalTeamXp(newTotalCreatorTeamXp);
            }
            int newTotalAcceptorTeamXp = totalAcceptorTeamXp - amountOfXpToTake;
            if (newTotalAcceptorTeamXp <= 0) {
                if (ww2AcceptorTeam.isXpGainOn()) {
                    ww2AcceptorTeam.getRankXP().setTotalTeamXp(0);
                }
            } else {
                if (ww2AcceptorTeam.isXpGainOn()) {
                    ww2AcceptorTeam.getRankXP().setTotalTeamXp(newTotalAcceptorTeamXp);
                }
            }
        } else if (ww2CreatorTeam.getRankXP().getRank() > ww2AcceptorTeam.getRankXP().getRank()) {
            int amountOfXpToGive = commonService.generateRandomXpWinForExpectedWin();
            int amountOfXpToTake = commonService.generateRandomXpLossForExpectedWin();
            int totalCreatorTeamXp = ww2CreatorTeam.getRankXP().getTotalTeamXp();
            int totalAcceptorTeamXp = ww2AcceptorTeam.getRankXP().getTotalTeamXp();
            int newTotalCreatorTeamXp = totalCreatorTeamXp + amountOfXpToGive;
            if (ww2CreatorTeam.isXpGainOn()) {
                ww2CreatorTeam.getRankXP().setTotalTeamXp(newTotalCreatorTeamXp);
            }
            int newTotalAcceptorTeamXp = totalAcceptorTeamXp - amountOfXpToTake;
            if (newTotalAcceptorTeamXp <= 0) {
                if (ww2AcceptorTeam.isXpGainOn()) {
                    ww2AcceptorTeam.getRankXP().setTotalTeamXp(0);
                }
            } else {
                if (ww2AcceptorTeam.isXpGainOn()) {
                    ww2AcceptorTeam.getRankXP().setTotalTeamXp(newTotalAcceptorTeamXp);
                }
            }
        } else {
            int amountOfXpToGive = commonService.generateRandomXpWinForUpsetWin();
            int amountOfXpToTake = commonService.generateRandomXpLossForUpsetWin();
            int totalCreatorTeamXp = ww2CreatorTeam.getRankXP().getTotalTeamXp();
            int totalAcceptorTeamXp = ww2AcceptorTeam.getRankXP().getTotalTeamXp();
            int newTotalCreatorTeamXp = totalCreatorTeamXp + amountOfXpToGive;
            ww2CreatorTeam.getRankXP().setTotalTeamXp(newTotalCreatorTeamXp);
            int newTotalAcceptorTeamXp = totalAcceptorTeamXp - amountOfXpToTake;
            if (newTotalAcceptorTeamXp <= 0) {
                ww2AcceptorTeam.getRankXP().setTotalTeamXp(0);
            } else {
                ww2AcceptorTeam.getRankXP().setTotalTeamXp(newTotalAcceptorTeamXp);
            }
        }
    }

    private void assignNewIWTeamXpTotals(Team creatorTeam, Team acceptorTeam) {
        IWTeam iwCreatorTeam = teamRepo.getIWTeamByTeamPk(creatorTeam.getPk());
        IWTeam iwAcceptorTeam = teamRepo.getIWTeamByTeamPk(acceptorTeam.getPk());
        if (Math.abs(iwCreatorTeam.getRankXP().getRank() - iwAcceptorTeam.getRankXP().getRank()) <= 50) {
            int amountOfXpToGive = commonService.generateRandomXpWinForCloseTeam();
            int amountOfXpToTake = commonService.generateRandomXpLossForCloseTeam();
            int totalCreatorTeamXp = iwCreatorTeam.getRankXP().getTotalTeamXp();
            int totalAcceptorTeamXp = iwAcceptorTeam.getRankXP().getTotalTeamXp();
            int newTotalCreatorTeamXp = totalCreatorTeamXp + amountOfXpToGive;
            iwCreatorTeam.getRankXP().setTotalTeamXp(newTotalCreatorTeamXp);
            int newTotalAcceptorTeamXp = totalAcceptorTeamXp - amountOfXpToTake;
            if (newTotalAcceptorTeamXp <= 0) {
                iwAcceptorTeam.getRankXP().setTotalTeamXp(0);
            } else {
                iwAcceptorTeam.getRankXP().setTotalTeamXp(newTotalAcceptorTeamXp);
            }
        } else if (iwCreatorTeam.getRankXP().getRank() > iwAcceptorTeam.getRankXP().getRank()) {
            int amountOfXpToGive = commonService.generateRandomXpWinForExpectedWin();
            int amountOfXpToTake = commonService.generateRandomXpLossForExpectedWin();
            int totalCreatorTeamXp = iwCreatorTeam.getRankXP().getTotalTeamXp();
            int totalAcceptorTeamXp = iwAcceptorTeam.getRankXP().getTotalTeamXp();
            int newTotalCreatorTeamXp = totalCreatorTeamXp + amountOfXpToGive;
            iwCreatorTeam.getRankXP().setTotalTeamXp(newTotalCreatorTeamXp);
            int newTotalAcceptorTeamXp = totalAcceptorTeamXp - amountOfXpToTake;
            if (newTotalAcceptorTeamXp <= 0) {
                iwAcceptorTeam.getRankXP().setTotalTeamXp(0);
            } else {
                iwAcceptorTeam.getRankXP().setTotalTeamXp(newTotalAcceptorTeamXp);
            }
        } else {
            int amountOfXpToGive = commonService.generateRandomXpWinForUpsetWin();
            int amountOfXpToTake = commonService.generateRandomXpLossForUpsetWin();
            int totalCreatorTeamXp = iwCreatorTeam.getRankXP().getTotalTeamXp();
            int totalAcceptorTeamXp = iwAcceptorTeam.getRankXP().getTotalTeamXp();
            int newTotalCreatorTeamXp = totalCreatorTeamXp + amountOfXpToGive;
            iwCreatorTeam.getRankXP().setTotalTeamXp(newTotalCreatorTeamXp);
            int newTotalAcceptorTeamXp = totalAcceptorTeamXp - amountOfXpToTake;
            if (newTotalAcceptorTeamXp <= 0) {
                iwAcceptorTeam.getRankXP().setTotalTeamXp(0);
            } else {
                iwAcceptorTeam.getRankXP().setTotalTeamXp(newTotalAcceptorTeamXp);
            }
        }
    }

    private void assignNewMwrTeamXpTotals(Team creatorTeam, Team acceptorTeam) {
        MWRTeam mwrCreatorTeam = teamRepo.getMWRTeamByTeamPk(creatorTeam.getPk());
        MWRTeam mwrAcceptorTeam = teamRepo.getMWRTeamByTeamPk(acceptorTeam.getPk());
        if (Math.abs(mwrCreatorTeam.getRankXP().getRank() - mwrAcceptorTeam.getRankXP().getRank()) <= 50) {
            int amountOfXpToGive = commonService.generateRandomXpWinForCloseTeam();
            int amountOfXpToTake = commonService.generateRandomXpLossForCloseTeam();
            int totalCreatorTeamXp = mwrCreatorTeam.getRankXP().getTotalTeamXp();
            int totalAcceptorTeamXp = mwrAcceptorTeam.getRankXP().getTotalTeamXp();
            int newTotalCreatorTeamXp = totalCreatorTeamXp + amountOfXpToGive;
            mwrCreatorTeam.getRankXP().setTotalTeamXp(newTotalCreatorTeamXp);
            int newTotalAcceptorTeamXp = totalAcceptorTeamXp - amountOfXpToTake;
            if (newTotalAcceptorTeamXp <= 0) {
                mwrAcceptorTeam.getRankXP().setTotalTeamXp(0);
            } else {
                mwrAcceptorTeam.getRankXP().setTotalTeamXp(newTotalAcceptorTeamXp);
            }
        } else if (mwrCreatorTeam.getRankXP().getRank() > mwrAcceptorTeam.getRankXP().getRank()) {
            int amountOfXpToGive = commonService.generateRandomXpWinForExpectedWin();
            int amountOfXpToTake = commonService.generateRandomXpLossForExpectedWin();
            int totalCreatorTeamXp = mwrCreatorTeam.getRankXP().getTotalTeamXp();
            int totalAcceptorTeamXp = mwrAcceptorTeam.getRankXP().getTotalTeamXp();
            int newTotalCreatorTeamXp = totalCreatorTeamXp + amountOfXpToGive;
            mwrCreatorTeam.getRankXP().setTotalTeamXp(newTotalCreatorTeamXp);
            int newTotalAcceptorTeamXp = totalAcceptorTeamXp - amountOfXpToTake;
            if (newTotalAcceptorTeamXp <= 0) {
                mwrAcceptorTeam.getRankXP().setTotalTeamXp(0);
            } else {
                mwrAcceptorTeam.getRankXP().setTotalTeamXp(newTotalAcceptorTeamXp);
            }
        } else {
            int amountOfXpToGive = commonService.generateRandomXpWinForUpsetWin();
            int amountOfXpToTake = commonService.generateRandomXpLossForUpsetWin();
            int totalCreatorTeamXp = mwrCreatorTeam.getRankXP().getTotalTeamXp();
            int totalAcceptorTeamXp = mwrAcceptorTeam.getRankXP().getTotalTeamXp();
            int newTotalCreatorTeamXp = totalCreatorTeamXp + amountOfXpToGive;
            mwrCreatorTeam.getRankXP().setTotalTeamXp(newTotalCreatorTeamXp);
            int newTotalAcceptorTeamXp = totalAcceptorTeamXp - amountOfXpToTake;
            if (newTotalAcceptorTeamXp <= 0) {
                mwrAcceptorTeam.getRankXP().setTotalTeamXp(0);
            } else {
                mwrAcceptorTeam.getRankXP().setTotalTeamXp(newTotalAcceptorTeamXp);
            }
        }
    }

    private void creatorTeamReportedTournamentMatchScore(Match match, User user, boolean didWin, Team team) {
        match.setPkOfUserThatReportedMatchScoreCreated(user.getPk());
        match.setReportedScoreOfTeamCreated(didWin);
        if (didWin == false) {
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.ENDED);
            match.setPkOfTeamLostMatch(team.getPk());
            match.setPkOfTeamWonMatch(match.getPkOfTeamThatAcceptedMatch());
            Tournament tournament = match.getTournament();
            tournament.getListOfTeamsRemainingInTournament().remove(team.getPk());

            //move winning team into next round unless was finals match
            if (tournament.getTournamentInfo().getMaxRounds() == match.getMatchResponse().getMatchPojo().getRound()) {
                tournament.getTournamentInfo().setTournamentStatus(TournamentStatusEnum.ENDED);
                Team teamThatAcceptedMatchWon = teamRepo.findTeamByPk(match.getPkOfTeamThatAcceptedMatch());
                TournamentTeam tournamentTeam = tournamentTeamRepo.getTournamentTeamByNameAndTournament(teamThatAcceptedMatchWon.getTeamPojo().getTeamName(), match.getTournament());
                challongeMatchService.reportMatchWinner(match.getTournament(), match, tournamentTeam, false);
                challongeTournamentService.finalizeChallongeTournament(tournament);

                //give money to winners
                BigDecimal potAmountWithCut = match.getTournament().getTournamentInfo().getPotAmount().multiply(COMMISSION_PERCENTAGE);
                int sizeOfTeam = match.getMatchInfo().getNumOfPlayers() / 2;
                BigDecimal amountWonForUser = potAmountWithCut.divide(new BigDecimal(sizeOfTeam));
                Team acceptorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatAcceptedMatch());
                int matchesWon = acceptorTeam.getMatchesWon();
                int newMatchesWon = matchesWon + 1;
                acceptorTeam.setMatchesWon(newMatchesWon);
                int goldTrophies = acceptorTeam.getGoldTrophyCount();
                int newGoldTrophies = goldTrophies + 1;
                acceptorTeam.setGoldTrophyCount(newGoldTrophies);
                for (long userPk : match.getPksOfAcceptorTeamMembersPlaying()) {
                    User u = userRepo.findByPk(userPk);
                    bankService.tournamentWin(u, match.getTournament(), amountWonForUser);
                    int totalUserXp = u.getRankXP().getTotalXp();
                    int amountOfXpWin = commonService.generateRandomXpWinForUser();
                    int newTotalUserXp = totalUserXp + amountOfXpWin;
                    u.getRankXP().setTotalXp(newTotalUserXp);
                    int goldTrophiesUser = u.getGoldTrophyCount();
                    int newGoldTrophiesUser = goldTrophiesUser + 1;
                    u.setGoldTrophyCount(newGoldTrophiesUser);
                }
                Team creatorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatCreatedMatch());
                int matchesLost = creatorTeam.getMatchesLost();
                int newMatchesLost = matchesLost + 1;
                creatorTeam.setMatchesLost(newMatchesLost);
                int silverTrophies = creatorTeam.getSilverTrophyCount();
                int newSilverTrophies = silverTrophies + 1;
                creatorTeam.setSilverTrophyCount(newSilverTrophies);
                for (long userPk : match.getPksOfCreatorTeamMembersPlaying()) {
                    User u = userRepo.findByPk(userPk);
                    int totalUserXp = u.getRankXP().getTotalXp();
                    int amountOfXpLoss = commonService.generateRandomXpLossForUser();
                    int newTotalUserXp = totalUserXp - amountOfXpLoss;
                    if (newTotalUserXp <= 0) {
                        u.getRankXP().setTotalXp(0);
                    } else {
                        u.getRankXP().setTotalXp(newTotalUserXp);
                    }
                    int silverTrophiesUser = u.getSilverTrophyCount();
                    int newSilverTrophiesUser = silverTrophiesUser + 1;
                    u.setSilverTrophyCount(newSilverTrophiesUser);
                }
                if (match.getMatchInfo().getGameEnum() == GameEnum.COD_MWR) {
                    assignNewMwrTeamXpTotals(acceptorTeam, creatorTeam);
                    assignNewMwrTeamCashTotals(acceptorTeam, potAmountWithCut);
                } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_WW2) {
                    assignNewWW2TeamXpTotals(acceptorTeam, creatorTeam);
                    assignNewWw2TeamCashTotals(acceptorTeam, potAmountWithCut);
                } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_IW) {
                    assignNewIWTeamXpTotals(acceptorTeam, creatorTeam);
                    assignNewIwTeamCashTotals(acceptorTeam, potAmountWithCut);
                }
            } else {
                Team acceptorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatAcceptedMatch());
                int matchesWon = acceptorTeam.getMatchesWon();
                int newMatchesWon = matchesWon + 1;
                acceptorTeam.setMatchesWon(newMatchesWon);
                int amountOfXpWin = commonService.generateRandomXpWinForUser();
                for (long userPk : match.getPksOfAcceptorTeamMembersPlaying()) {
                    User u = userRepo.findByPk(userPk);
                    int totalUserXp = u.getRankXP().getTotalXp();
                    int newTotalUserXp = totalUserXp + amountOfXpWin;
                    u.getRankXP().setTotalXp(newTotalUserXp);
                }
                Team creatorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatCreatedMatch());
                int matchesLost = creatorTeam.getMatchesLost();
                int newMatchesLost = matchesLost + 1;
                creatorTeam.setMatchesLost(newMatchesLost);
                int amountOfXpLoss = commonService.generateRandomXpLossForUser();
                int semiFinalsRound = tournament.getTournamentInfo().getMaxRounds() - 1;
                if (semiFinalsRound == match.getMatchResponse().getMatchPojo().getRound()) {
                    //Team creatorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatCreatedMatch());
                    int bronzeTrophies = creatorTeam.getBronzeTrophyCount();
                    int newBronzeTrophies = bronzeTrophies + 1;
                    creatorTeam.setBronzeTrophyCount(newBronzeTrophies);
                }
                for (long userPk : match.getPksOfCreatorTeamMembersPlaying()) {
                    User u = userRepo.findByPk(userPk);
                    int totalUserXp = u.getRankXP().getTotalXp();
                    int newTotalUserXp = totalUserXp - amountOfXpLoss;
                    if (newTotalUserXp <= 0) {
                        u.getRankXP().setTotalXp(0);
                    } else {
                        u.getRankXP().setTotalXp(newTotalUserXp);
                    }
                    if (semiFinalsRound == match.getMatchResponse().getMatchPojo().getRound()) {
                        int bronzeTrophies = u.getBronzeTrophyCount();
                        int newBronzeTrophies = bronzeTrophies + 1;
                        u.setBronzeTrophyCount(newBronzeTrophies);
                    }
                }
                if (match.getMatchInfo().getGameEnum() == GameEnum.COD_MWR) {
                    assignNewMwrTeamXpTotals(acceptorTeam, creatorTeam);
                } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_WW2) {
                    assignNewWW2TeamXpTotals(acceptorTeam, creatorTeam);
                } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_IW) {
                    assignNewIWTeamXpTotals(acceptorTeam, creatorTeam);
                }
                Team teamThatAcceptedMatchWon = teamRepo.findTeamByPk(match.getPkOfTeamThatAcceptedMatch());
                TournamentTeam tournamentTeam = tournamentTeamRepo.getTournamentTeamByNameAndTournament(teamThatAcceptedMatchWon.getTeamPojo().getTeamName(), match.getTournament());
                challongeMatchService.reportMatchWinner(match.getTournament(), match, tournamentTeam, false);
            }
        } else {
            if (match.getReportedScoreOfTeamAccepted() == null) {
                //waiting on team that accepted to report to confirm win...
            } else if (match.getReportedScoreOfTeamAccepted() == didWin) {
                //create dispute because both teams saying they won
                match.getMatchInfo().setMatchStatus(MatchStatusEnum.DISPUTED);
                Dispute firstDispute = disputeService.createDispute(user.getPk(), match.getPk(), team.getPk(), "The match outcome has been disputed because both teams have claimed to win the match.", null, true);
                setDisputedMatchOutcomeNotification(user, match, firstDispute);
                Dispute secondDispute = disputeService.createDispute(match.getPkOfUserThatReportedMatchScoreAccepted(), match.getPk(), match.getPkOfTeamThatAcceptedMatch(), "The match outcome has been disputed because both teams have claimed to win the match.", null, true);
                User secondUser = userRepo.findByPk(match.getPkOfUserThatReportedMatchScoreAccepted());
                setDisputedMatchOutcomeNotification(secondUser, match, secondDispute);
            }
        }
    }

    private void acceptorTeamReportedWagerMatchScore(Match match, User user, boolean didWin, Team team) {
        match.setPkOfUserThatReportedMatchScoreAccepted(user.getPk());
        match.setReportedScoreOfTeamAccepted(didWin);
        if (didWin == false) {
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.ENDED);
            match.setPkOfTeamLostMatch(team.getPk());
            match.setPkOfTeamWonMatch(match.getPkOfTeamThatCreatedMatch());

            BigDecimal potAmountWithCut = match.getMatchInfo().getPotAmount().multiply(COMMISSION_PERCENTAGE);
            int sizeOfTeam = match.getMatchInfo().getNumOfPlayers() / 2;
            BigDecimal amountWonForUser = potAmountWithCut.divide(new BigDecimal(sizeOfTeam));
            Team creatorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatCreatedMatch());
            int matchesWon = creatorTeam.getMatchesWon();
            int newMatchesWon = matchesWon + 1;
            creatorTeam.setMatchesWon(newMatchesWon);
            int amountOfXpWin = commonService.generateRandomXpWinForUser();
            for (long userPk : match.getPksOfCreatorTeamMembersPlaying()) {
                User u = userRepo.findByPk(userPk);
                bankService.wagerMatchWin(u, match, amountWonForUser);
                int totalUserXp = u.getRankXP().getTotalXp();
                int newTotalUserXp = totalUserXp + amountOfXpWin;
                u.getRankXP().setTotalXp(newTotalUserXp);
            }
            Team acceptorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatAcceptedMatch());
            int matchesLost = acceptorTeam.getMatchesLost();
            int newMatchesLost = matchesLost + 1;
            acceptorTeam.setMatchesLost(newMatchesLost);
            int amountOfXpLoss = commonService.generateRandomXpLossForUser();
            for (long userPk : match.getPksOfAcceptorTeamMembersPlaying()) {
                User u = userRepo.findByPk(userPk);
                int totalUserXp = u.getRankXP().getTotalXp();
                int newTotalUserXp = totalUserXp - amountOfXpLoss;
                if (newTotalUserXp <= 0) {
                    u.getRankXP().setTotalXp(0);
                } else {
                    u.getRankXP().setTotalXp(newTotalUserXp);
                }
            }
            if (match.getMatchInfo().getGameEnum() == GameEnum.COD_MWR) {
                assignNewMwrTeamXpTotals(creatorTeam, acceptorTeam);
                assignNewMwrTeamCashTotals(creatorTeam, potAmountWithCut);
            } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_WW2) {
                assignNewWW2TeamXpTotals(creatorTeam, acceptorTeam);
                assignNewWw2TeamCashTotals(creatorTeam, potAmountWithCut);
            } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_IW) {
                assignNewIWTeamXpTotals(creatorTeam, acceptorTeam);
                assignNewIwTeamCashTotals(creatorTeam, potAmountWithCut);
            }
        } else {
            if (match.getReportedScoreOfTeamCreated() == null) {
                //waiting on team that accepted to report to confirm win...
            } else if (match.getReportedScoreOfTeamCreated() == didWin) {
                //create dispute because both teams saying they won...
                match.getMatchInfo().setMatchStatus(MatchStatusEnum.DISPUTED);
                Dispute firstDispute = disputeService.createDispute(user.getPk(), match.getPk(), team.getPk(), "The match outcome has been disputed because both teams have claimed to win the match.", null, true);
                setDisputedMatchOutcomeNotification(user, match, firstDispute);
                Dispute secondDispute = disputeService.createDispute(match.getPkOfUserThatReportedMatchScoreCreated(), match.getPk(), match.getPkOfTeamThatCreatedMatch(), "The match outcome has been disputed because both teams have claimed to win the match.", null, true);
                User secondUser = userRepo.findByPk(match.getPkOfUserThatReportedMatchScoreCreated());
                setDisputedMatchOutcomeNotification(secondUser, match, secondDispute);
            }
        }
    }

    private void creatorTeamReportedWagerMatchScore(Match match, User user, boolean didWin, Team team) {
        match.setPkOfUserThatReportedMatchScoreCreated(user.getPk());
        match.setReportedScoreOfTeamCreated(didWin);
        if (didWin == false) {
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.ENDED);
            match.setPkOfTeamLostMatch(team.getPk());
            match.setPkOfTeamWonMatch(match.getPkOfTeamThatAcceptedMatch());
            //grant cash to winning team
            BigDecimal potAmountWithCut = match.getMatchInfo().getPotAmount().multiply(COMMISSION_PERCENTAGE);
            int sizeOfTeam = match.getMatchInfo().getNumOfPlayers() / 2;
            BigDecimal amountWonForUser = potAmountWithCut.divide(new BigDecimal(sizeOfTeam));
            Team acceptorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatAcceptedMatch());
            int matchesWon = acceptorTeam.getMatchesWon();
            int newMatchesWon = matchesWon + 1;
            acceptorTeam.setMatchesWon(newMatchesWon);
            int amountOfXpWin = commonService.generateRandomXpWinForUser();
            for (long userPk : match.getPksOfAcceptorTeamMembersPlaying()) {
                User u = userRepo.findByPk(userPk);
                bankService.wagerMatchWin(u, match, amountWonForUser);
                int totalUserXp = u.getRankXP().getTotalXp();
                int newTotalUserXp = totalUserXp + amountOfXpWin;
                u.getRankXP().setTotalXp(newTotalUserXp);
            }
            Team creatorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatCreatedMatch());
            int matchesLost = creatorTeam.getMatchesLost();
            int newMatchesLost = matchesLost + 1;
            creatorTeam.setMatchesLost(newMatchesLost);
            int amountOfXpLoss = commonService.generateRandomXpLossForUser();
            for (long userPk : match.getPksOfCreatorTeamMembersPlaying()) {
                User u = userRepo.findByPk(userPk);
                int totalUserXp = u.getRankXP().getTotalXp();
                int newTotalUserXp = totalUserXp - amountOfXpLoss;
                if (newTotalUserXp <= 0) {
                    u.getRankXP().setTotalXp(0);
                } else {
                    u.getRankXP().setTotalXp(newTotalUserXp);
                }
            }
            if (match.getMatchInfo().getGameEnum() == GameEnum.COD_MWR) {
                assignNewMwrTeamXpTotals(acceptorTeam, creatorTeam);
                assignNewMwrTeamCashTotals(acceptorTeam, potAmountWithCut);
            } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_WW2) {
                assignNewWW2TeamXpTotals(acceptorTeam, creatorTeam);
                assignNewWw2TeamCashTotals(acceptorTeam, potAmountWithCut);
            } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_IW) {
                assignNewIWTeamXpTotals(acceptorTeam, creatorTeam);
                assignNewIwTeamCashTotals(acceptorTeam, potAmountWithCut);
            }

        } else {
            if (match.getReportedScoreOfTeamAccepted() == null) {
                //waiting on team that accepted to report to confirm win...
            } else if (match.getReportedScoreOfTeamAccepted() == didWin) {
                //create dispute because both teams saying they won
                match.getMatchInfo().setMatchStatus(MatchStatusEnum.DISPUTED);
                Dispute firstDispute = disputeService.createDispute(user.getPk(), match.getPk(), team.getPk(), "The match outcome has been disputed because both teams have claimed to win the match.", null, true);
                setDisputedMatchOutcomeNotification(user, match, firstDispute);
                Dispute secondDispute = disputeService.createDispute(match.getPkOfUserThatReportedMatchScoreAccepted(), match.getPk(), match.getPkOfTeamThatAcceptedMatch(), "The match outcome has been disputed because both teams have claimed to win the match.", null, true);
                User secondUser = userRepo.findByPk(match.getPkOfUserThatReportedMatchScoreAccepted());
                setDisputedMatchOutcomeNotification(secondUser, match, secondDispute);
            }
        }
    }

    private void acceptorTeamReportedXpMatchScore(Match match, User user, boolean didWin, Team team) {
        match.setPkOfUserThatReportedMatchScoreAccepted(user.getPk());
        match.setReportedScoreOfTeamAccepted(didWin);
        if (didWin == false) {
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.ENDED);
            match.setPkOfTeamLostMatch(team.getPk());
            match.setPkOfTeamWonMatch(match.getPkOfTeamThatCreatedMatch());
            Team creatorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatCreatedMatch());
            int matchesWon = creatorTeam.getMatchesWon();
            int newMatchesWon = matchesWon + 1;
            creatorTeam.setMatchesWon(newMatchesWon);
            int amountOfXpWin = commonService.generateRandomXpWinForUser();
            for (long userPk : match.getPksOfCreatorTeamMembersPlaying()) {
                User u = userRepo.findByPk(userPk);
                int totalUserXp = u.getRankXP().getTotalXp();
                int newTotalUserXp = totalUserXp + amountOfXpWin;
                u.getRankXP().setTotalXp(newTotalUserXp);
            }
            Team acceptorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatAcceptedMatch());
            int matchesLost = acceptorTeam.getMatchesLost();
            int newMatchesLost = matchesLost + 1;
            acceptorTeam.setMatchesLost(newMatchesLost);
            int amountOfXpLoss = commonService.generateRandomXpLossForUser();
            for (long userPk : match.getPksOfAcceptorTeamMembersPlaying()) {
                User u = userRepo.findByPk(userPk);
                int totalUserXp = u.getRankXP().getTotalXp();
                int newTotalUserXp = totalUserXp - amountOfXpLoss;
                if (newTotalUserXp <= 0) {
                    u.getRankXP().setTotalXp(0);
                } else {
                    u.getRankXP().setTotalXp(newTotalUserXp);
                }
            }
            if (match.getMatchInfo().getGameEnum() == GameEnum.COD_MWR) {
                assignNewMwrTeamXpTotals(creatorTeam, acceptorTeam);
            } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_WW2) {
                assignNewWW2TeamXpTotals(creatorTeam, acceptorTeam);
            } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_IW) {
                assignNewIWTeamXpTotals(creatorTeam, acceptorTeam);
            }
            //set notifications for users that won and lost the match
        } else {
            if (match.getReportedScoreOfTeamCreated() == null) {
                //waiting on team that accepted to report to confirm win...
            } else if (match.getReportedScoreOfTeamCreated() == didWin) {
                //create dispute because both teams saying they won...
                match.getMatchInfo().setMatchStatus(MatchStatusEnum.DISPUTED);
                Dispute firstDispute = disputeService.createDispute(user.getPk(), match.getPk(), team.getPk(), "The match outcome has been disputed because both teams have claimed to win the match.", null, true);
                setDisputedMatchOutcomeNotification(user, match, firstDispute);
                Dispute secondDispute = disputeService.createDispute(match.getPkOfUserThatReportedMatchScoreCreated(), match.getPk(), match.getPkOfTeamThatCreatedMatch(), "The match outcome has been disputed because both teams have claimed to win the match.", null, true);
                User secondUser = userRepo.findByPk(match.getPkOfUserThatReportedMatchScoreCreated());
                setDisputedMatchOutcomeNotification(secondUser, match, secondDispute);
            }
        }
    }

    private void creatorTeamReportedXpMatchScore(Match match, User user, boolean didWin, Team team) {
        match.setPkOfUserThatReportedMatchScoreCreated(user.getPk());
        match.setReportedScoreOfTeamCreated(didWin);
        if (didWin == false) {
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.ENDED);
            match.setPkOfTeamLostMatch(team.getPk());
            match.setPkOfTeamWonMatch(match.getPkOfTeamThatAcceptedMatch());
            //grant xp to winning team
            Team acceptorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatAcceptedMatch());
            int matchesWon = acceptorTeam.getMatchesWon();
            int newMatchesWon = matchesWon + 1;
            acceptorTeam.setMatchesWon(newMatchesWon);
            int amountOfXpWin = commonService.generateRandomXpWinForUser();
            for (long userPk : match.getPksOfAcceptorTeamMembersPlaying()) {
                User u = userRepo.findByPk(userPk);
                int totalUserXp = u.getRankXP().getTotalXp();
                int newTotalUserXp = totalUserXp + amountOfXpWin;
                u.getRankXP().setTotalXp(newTotalUserXp);
            }
            Team creatorTeam = teamRepo.findTeamByPk(match.getPkOfTeamThatCreatedMatch());
            int matchesLost = creatorTeam.getMatchesLost();
            int newMatchesLost = matchesLost + 1;
            creatorTeam.setMatchesLost(newMatchesLost);
            int amountOfXpLoss = commonService.generateRandomXpLossForUser();
            for (long userPk : match.getPksOfCreatorTeamMembersPlaying()) {
                User u = userRepo.findByPk(userPk);
                int totalUserXp = u.getRankXP().getTotalXp();
                int newTotalUserXp = totalUserXp - amountOfXpLoss;
                if (newTotalUserXp <= 0) {
                    u.getRankXP().setTotalXp(0);
                } else {
                    u.getRankXP().setTotalXp(newTotalUserXp);
                }
            }
            if (match.getMatchInfo().getGameEnum() == GameEnum.COD_MWR) {
                assignNewMwrTeamXpTotals(acceptorTeam, creatorTeam);
            } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_WW2) {
                assignNewWW2TeamXpTotals(acceptorTeam, creatorTeam);
            } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_IW) {
                assignNewIWTeamXpTotals(acceptorTeam, creatorTeam);
            }
            //set notifications for users that won and lost the match
        } else {
            if (match.getReportedScoreOfTeamAccepted() == null) {
                //waiting on team that accepted to report to confirm win...
            } else if (match.getReportedScoreOfTeamAccepted() == didWin) {
                //create dispute because both teams saying they won
                match.getMatchInfo().setMatchStatus(MatchStatusEnum.DISPUTED);
                Dispute firstDispute = disputeService.createDispute(user.getPk(), match.getPk(), team.getPk(), "The match outcome has been disputed because both teams have claimed to win the match.", null, true);
                setDisputedMatchOutcomeNotification(user, match, firstDispute);
                Dispute secondDispute = disputeService.createDispute(match.getPkOfUserThatReportedMatchScoreAccepted(), match.getPk(), match.getPkOfTeamThatAcceptedMatch(), "The match outcome has been disputed because both teams have claimed to win the match.", null, true);
                User secondUser = userRepo.findByPk(match.getPkOfUserThatReportedMatchScoreAccepted());
                setDisputedMatchOutcomeNotification(secondUser, match, secondDispute);
            }
        }
    }

    private void setDisputedMatchOutcomeNotification(User user, Match match, Dispute dispute) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.DISPUTED_MATCH_OUTCOME);
        notification.setNotificationMessage("You have a match whose outcome was disputed. A dispute ticket has been automatically generated. Please view and submit proof.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setMatch(match);
        match.getNotification().add(notification);
        notification.setDispute(dispute);
        dispute.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    public void selectHostAndMapsForMatch(Match match, int roundNumber, int creatorSeed, int acceptorSeed) {
        if (match.getMatchInfo().getGameEnum() == GameEnum.COD_MWR) {
            setHostAndMapsForMwr(match, roundNumber, creatorSeed, acceptorSeed);
        } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_IW) {
            setHostAndMapsForIw(match, roundNumber, creatorSeed, acceptorSeed);
        } else if (match.getMatchInfo().getGameEnum() == GameEnum.COD_WW2) {
            setHostAndMapsForWw2(match, roundNumber, creatorSeed, acceptorSeed);
        }
    }

    private void setHostAndMapsForWw2(Match match, int roundNumber, int creatorSeed, int acceptorSeed) {
        WW2Match ww2Match = getWW2MatchByMatchPk(match.getPk());
        WW2Team teamCreator = teamRepo.findWW2TeamByTeamPk(match.getPkOfTeamThatCreatedMatch());
        WW2Team teamAcceptor = teamRepo.findWW2TeamByTeamPk(match.getPkOfTeamThatAcceptedMatch());
        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.XP) {
            if (ww2Match.getWw2MatchInfo().getGameModeEnum() == GameModeEnum.SearchAndDestroy) {
                if (teamCreator.getRankXP().getRank() > teamAcceptor.getRankXP().getRank()) {
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                } else {
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                }
                if (match.getMatchInfo().getMatchSizeEnum() == MatchSizeEnum.SINGLES || match.getMatchInfo().getMatchSizeEnum() == MatchSizeEnum.DOUBLES) {
                    ww2Match.getWw2MatchInfo().setMapsToPlayInMatchInOrder(ww2Match.getWw2MatchInfo().getMapNamesFor1sAnd2s());
                    Collections.shuffle(ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder());
                } else {
                    ww2Match.getWw2MatchInfo().setMapsToPlayInMatchInOrder(ww2Match.getWw2MatchInfo().getMapNamesFor3sAnd4s());
                    Collections.shuffle(ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder());
                }
            } else {
                if (teamCreator.getRankXP().getRank() > teamAcceptor.getRankXP().getRank()) {
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                } else {
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                }
                List<String> maps = ww2Match.getWw2MatchInfo().getVariantMaps();
                List<String> modes = ww2Match.getWw2MatchInfo().getVariantModes();
                Collections.shuffle(maps);
                Collections.shuffle(modes);
                for(int i = 0 ; i < 3 ; i++){
                    String mapAndMode = maps.get(i) + " - " + modes.get(i);
                    ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder().add(mapAndMode);
                }
            }
        } else if (match.getMatchInfo().getMatchType() == MatchTypeEnum.WAGER) {
            if (ww2Match.getWw2MatchInfo().getGameModeEnum() == GameModeEnum.SearchAndDestroy) {
                if (teamCreator.getRankXP().getRank() > teamAcceptor.getRankXP().getRank()) {
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                } else {
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                }
                if (match.getMatchInfo().getMatchSizeEnum() == MatchSizeEnum.SINGLES || match.getMatchInfo().getMatchSizeEnum() == MatchSizeEnum.DOUBLES) {
                    ww2Match.getWw2MatchInfo().setMapsToPlayInMatchInOrder(ww2Match.getWw2MatchInfo().getMapNamesFor1sAnd2s());
                    Collections.shuffle(ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder());
                } else {
                    ww2Match.getWw2MatchInfo().setMapsToPlayInMatchInOrder(ww2Match.getWw2MatchInfo().getMapNamesFor3sAnd4s());
                    Collections.shuffle(ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder());
                }
            } else {
                if (teamCreator.getRankXP().getRank() > teamAcceptor.getRankXP().getRank()) {
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                } else {
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                }
                List<String> maps = ww2Match.getWw2MatchInfo().getVariantMaps();
                List<String> modes = ww2Match.getWw2MatchInfo().getVariantModes();
                Collections.shuffle(maps);
                Collections.shuffle(modes);
                for(int i = 0 ; i < 3 ; i++){
                    String mapAndMode = maps.get(i) + " - " + modes.get(i);
                    ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder().add(mapAndMode);
                }
            }
        } else {
            Tournament tournament = match.getTournament();
//            if (tournament.getTournamentInfo().getMaxRounds() == match.getMatchResponse().getMatchPojo().getRound()) {
//                match.getMatchInfo().setBestOfEnum(tournament.getTournamentInfo().getBestOfEnumFinals());
//            }
            if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_1 || match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_3) {
                if (creatorSeed < acceptorSeed) {
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                } else {
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                    ww2Match.getWw2MatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                }
            }

            if (creatorSeed < acceptorSeed) {
                ww2Match.getWw2MatchInfo().getBestOf5HostNames().add(teamAcceptor.getTeamPojo().getTeamName());
                ww2Match.getWw2MatchInfo().getBestOf5HostNames().add(teamAcceptor.getTeamPojo().getTeamName());
                ww2Match.getWw2MatchInfo().getBestOf5HostNames().add(teamCreator.getTeamPojo().getTeamName());
                ww2Match.getWw2MatchInfo().getBestOf5HostNames().add(teamCreator.getTeamPojo().getTeamName());

            } else {
                ww2Match.getWw2MatchInfo().getBestOf5HostNames().add(teamCreator.getTeamPojo().getTeamName());
                ww2Match.getWw2MatchInfo().getBestOf5HostNames().add(teamCreator.getTeamPojo().getTeamName());
                ww2Match.getWw2MatchInfo().getBestOf5HostNames().add(teamAcceptor.getTeamPojo().getTeamName());
                ww2Match.getWw2MatchInfo().getBestOf5HostNames().add(teamAcceptor.getTeamPojo().getTeamName());
            }

            if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_1) {
                ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder().add(tournament.getTournamentMaps().getBestOf1MapsToPlay().get(roundNumber));
            } else if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_3) {
                ArrayList<String> mapNames = tournament.getTournamentMaps().getBestOf3MapsToPlay().get(roundNumber);
                for (String mapName : mapNames) {
                    ww2Match.getWw2MatchInfo().getMapsToPlayInMatchInOrder().add(mapName);
                }
            }
            //for best of 5s
            ArrayList<String> mapNames = tournament.getTournamentMaps().getVariantMaps();//doesnt matter if s and d or variant, just needed all maps that are played since as of now they are the same
            //Collections.shuffle(mapNames);
            for (String mapName : mapNames) {
                ww2Match.getWw2MatchInfo().getBestOf5MapsToPlay().add(mapName);
            }

            ArrayList<String> modeNames = tournament.getTournamentMaps().getVariantModesBestOf5();//doesnt matter if s and d or variant, just needed all maps that are played since as of now they are the same
            for (String modeName : modeNames) {
                ww2Match.getWw2MatchInfo().getBestOf5ModesToPlay().add(modeName);
            }

        }
    }

    private void setHostAndMapsForIw(Match match, int roundNumber, int creatorSeed, int acceptorSeed) {
        IWMatch iwMatch = getIWMatchByMatchPk(match.getPk());
        IWTeam teamCreator = teamRepo.findIWTeamByTeamPk(match.getPkOfTeamThatCreatedMatch());
        IWTeam teamAcceptor = teamRepo.findIWTeamByTeamPk(match.getPkOfTeamThatAcceptedMatch());
        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.XP) {
            if (teamCreator.getRankXP().getRank() > teamAcceptor.getRankXP().getRank()) {
                iwMatch.getIwMatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                iwMatch.getIwMatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                iwMatch.getIwMatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
            } else {
                iwMatch.getIwMatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                iwMatch.getIwMatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                iwMatch.getIwMatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
            }
            if (match.getMatchInfo().getMatchSizeEnum() == MatchSizeEnum.SINGLES || match.getMatchInfo().getMatchSizeEnum() == MatchSizeEnum.DOUBLES) {
                iwMatch.getIwMatchInfo().setMapsToPlayInMatchInOrder(iwMatch.getIwMatchInfo().getMapNamesFor1sAnd2s());
                Collections.shuffle(iwMatch.getIwMatchInfo().getMapsToPlayInMatchInOrder());
            } else {
                iwMatch.getIwMatchInfo().setMapsToPlayInMatchInOrder(iwMatch.getIwMatchInfo().getMapNamesFor3sAnd4s());
                Collections.shuffle(iwMatch.getIwMatchInfo().getMapsToPlayInMatchInOrder());
            }
        } else if (match.getMatchInfo().getMatchType() == MatchTypeEnum.WAGER) {
            if (teamCreator.getRankXP().getRank() > teamAcceptor.getRankXP().getRank()) {
                iwMatch.getIwMatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                iwMatch.getIwMatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
            } else {
                iwMatch.getIwMatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                iwMatch.getIwMatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
            }
            if (match.getMatchInfo().getMatchSizeEnum() == MatchSizeEnum.SINGLES || match.getMatchInfo().getMatchSizeEnum() == MatchSizeEnum.DOUBLES) {
                iwMatch.getIwMatchInfo().setMapsToPlayInMatchInOrder(iwMatch.getIwMatchInfo().getMapNamesFor1sAnd2s());
                Collections.shuffle(iwMatch.getIwMatchInfo().getMapsToPlayInMatchInOrder());
            } else {
                iwMatch.getIwMatchInfo().setMapsToPlayInMatchInOrder(iwMatch.getIwMatchInfo().getMapNamesFor3sAnd4s());
                Collections.shuffle(iwMatch.getIwMatchInfo().getMapsToPlayInMatchInOrder());
            }
        } else {
            if (creatorSeed < acceptorSeed) {
                iwMatch.getIwMatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                iwMatch.getIwMatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
            } else {
                iwMatch.getIwMatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                iwMatch.getIwMatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
            }

            Tournament tournament = match.getTournament();
            if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_1) {
                iwMatch.getIwMatchInfo().getMapsToPlayInMatchInOrder().add(tournament.getTournamentMaps().getBestOf1MapsToPlay().get(roundNumber));
            } else if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_3) {
                ArrayList<String> mapNames = tournament.getTournamentMaps().getBestOf3MapsToPlay().get(roundNumber);
                for (String mapName : mapNames) {
                    iwMatch.getIwMatchInfo().getMapsToPlayInMatchInOrder().add(mapName);
                }
            }
        }
    }

    private void setHostAndMapsForMwr(Match match, int roundNumber, int creatorSeed, int acceptorSeed) {
        MWRMatch mwrMatch = getMWRMatchByMatchPk(match.getPk());
        MWRTeam teamCreator = teamRepo.findMWRTeamByTeamPk(match.getPkOfTeamThatCreatedMatch());
        MWRTeam teamAcceptor = teamRepo.findMWRTeamByTeamPk(match.getPkOfTeamThatAcceptedMatch());
        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.XP) {
            if (teamCreator.getRankXP().getRank() > teamAcceptor.getRankXP().getRank()) {
                mwrMatch.getMwrMatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                mwrMatch.getMwrMatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                mwrMatch.getMwrMatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
            } else {
                mwrMatch.getMwrMatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                mwrMatch.getMwrMatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                mwrMatch.getMwrMatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
            }
            if (match.getMatchInfo().getMatchSizeEnum() == MatchSizeEnum.SINGLES || match.getMatchInfo().getMatchSizeEnum() == MatchSizeEnum.DOUBLES) {
                mwrMatch.getMwrMatchInfo().setMapsToPlayInMatchInOrder(mwrMatch.getMwrMatchInfo().getMapNamesFor1sAnd2s());
                Collections.shuffle(mwrMatch.getMwrMatchInfo().getMapsToPlayInMatchInOrder());
            } else {
                mwrMatch.getMwrMatchInfo().setMapsToPlayInMatchInOrder(mwrMatch.getMwrMatchInfo().getMapNamesFor3sAnd4s());
                Collections.shuffle(mwrMatch.getMwrMatchInfo().getMapsToPlayInMatchInOrder());
            }
        } else if (match.getMatchInfo().getMatchType() == MatchTypeEnum.WAGER) {
            if (teamCreator.getRankXP().getRank() > teamAcceptor.getRankXP().getRank()) {
                mwrMatch.getMwrMatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                mwrMatch.getMwrMatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
            } else {
                mwrMatch.getMwrMatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                mwrMatch.getMwrMatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
            }
            if (match.getMatchInfo().getMatchSizeEnum() == MatchSizeEnum.SINGLES || match.getMatchInfo().getMatchSizeEnum() == MatchSizeEnum.DOUBLES) {
                mwrMatch.getMwrMatchInfo().setMapsToPlayInMatchInOrder(mwrMatch.getMwrMatchInfo().getMapNamesFor1sAnd2s());
                Collections.shuffle(mwrMatch.getMwrMatchInfo().getMapsToPlayInMatchInOrder());
            } else {
                mwrMatch.getMwrMatchInfo().setMapsToPlayInMatchInOrder(mwrMatch.getMwrMatchInfo().getMapNamesFor3sAnd4s());
                Collections.shuffle(mwrMatch.getMwrMatchInfo().getMapsToPlayInMatchInOrder());
            }
        } else {
            if (creatorSeed < acceptorSeed) {
                mwrMatch.getMwrMatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
                mwrMatch.getMwrMatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
            } else {
                mwrMatch.getMwrMatchInfo().getHostNamesInOrder().add(teamCreator.getTeamPojo().getTeamName());
                mwrMatch.getMwrMatchInfo().getHostNamesInOrder().add(teamAcceptor.getTeamPojo().getTeamName());
            }

            Tournament tournament = match.getTournament();
            if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_1) {
                mwrMatch.getMwrMatchInfo().getMapsToPlayInMatchInOrder().add(tournament.getTournamentMaps().getBestOf1MapsToPlay().get(roundNumber));
            } else if (match.getMatchInfo().getBestOfEnum() == BestOfEnum.BEST_OF_3) {
                ArrayList<String> mapNames = tournament.getTournamentMaps().getBestOf3MapsToPlay().get(roundNumber);
                for (String mapName : mapNames) {
                    mwrMatch.getMwrMatchInfo().getMapsToPlayInMatchInOrder().add(mapName);
                }
            }
        }
    }

    public void cancelMatch(long matchPk) {
        Match match = matchRepo.findMatchByPk(matchPk);
        if (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING) {
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.CANCELLED);
            List<User> usersInMatch = userRepo.getUsersByMatchPk(match.getPk());
            if (match.getMatchInfo().getMatchType() == MatchTypeEnum.WAGER) {
                for (User user : usersInMatch) {
                    MatchInvite matchInvite = matchInviteService.findMatchInviteForUserInMatch(matchPk, user.getPk());
                    if (matchInvite.getInviteEnum() == InvitesEnum.ACCEPTED) {
                        bankService.wagerMatchCancelled(user, match);
                        dissociateUserFromMatch(user, match);
                        setMatchCreatorCancelsMatch(match, user);
                    }
                    matchInvite.setInviteEnum(InvitesEnum.DECLINED);
                    matchInvite.setDeclinedAt();
                }
            } else {
                for (User user : usersInMatch) {
                    dissociateUserFromMatch(user, match);
                    setMatchCreatorCancelsMatch(match, user);
                }
            }
        }
    }

    private void setMatchCreatorCancelsMatch(Match match, User user) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.CANCELLED_MATCH);
        notification.setNotificationMessage("A match you are in has been cancelled.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setMatch(match);
        match.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    public Match findMatchById(int matchId) {
        return matchRepo.findMatchByMatchId(matchId);
    }

    public List<Match> getMatchesByGame(GameEnum gameEnum, int pageNumber) {
        return matchRepo.getMatchesByGame(gameEnum, pageNumber);
    }

    public List<Match> getMatchesByGameAndPlatform(GameEnum gameEnum, PlatformEnum platformEnum) {
        return matchRepo.getMatchesByGameAndPlatform(gameEnum, platformEnum);
    }

    public List<Match> getMatchesByPlatform(PlatformEnum platformEnum, int pageNumber) {
        return matchRepo.getMatchesByPlatform(platformEnum, pageNumber);
    }

    public List<Match> findMatchesByUserPkAndMatchStatus(long userPk, MatchStatusEnum matchStatusEnum, int pageNumber) {
        return matchRepo.findMatchesByUserPkAndMatchStatus(userPk, matchStatusEnum, pageNumber);
    }

    public List<Match> getMatchesByUserPkAndMatchNotKilledStatus(long userPk) {
        return matchRepo.getMatchesByUserPkAndMatchNotKilledStatus(userPk);
    }
}
