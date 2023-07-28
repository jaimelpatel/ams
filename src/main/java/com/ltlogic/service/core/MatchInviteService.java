/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.constants.MatchSizeEnum;
import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.NotificationTypeEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.MatchCancellationRequest;
import com.ltlogic.db.entity.MatchInvite;
import com.ltlogic.db.entity.Notification;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.TeamInvite;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.MatchCancellationRepository;
import com.ltlogic.db.repository.MatchInviteRepository;
import com.ltlogic.db.repository.MatchRepository;
import com.ltlogic.db.repository.NotificationRepository;
import com.ltlogic.db.repository.TeamRepository;
import com.ltlogic.db.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Bishistha
 */
@Service
@Transactional
public class MatchInviteService {

    @Autowired
    MatchInviteRepository matchInviteRepo;

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    UserRepository userRepo;

    @Autowired
    MatchService matchService;

    @Autowired
    MatchInviteService matchInviteService;

    @Autowired
    NotificationRepository notificationRepo;

    @Autowired
    MatchCancellationRepository matchCancellationRepo;

    @Autowired
    BankService bankService;

    public void persistInvite(MatchInvite m) {
        matchInviteRepo.persistInvite(m);
    }

    public MatchInvite findInvitebyPk(long pk) {
        return matchInviteRepo.findByPk(pk);
    }

    public MatchInvite createNewMatchInvite(Match match, Team senderTeam, User user, User userAcceptingOrCreatingMatch) {
        MatchInvite matchInvite = new MatchInvite();
        matchInvite.setInviteEnum(InvitesEnum.PENDING);
        associateUserToMatchInvite(user, matchInvite);
        associateTeamToMatchInvite(senderTeam, matchInvite);
        associateMatchToMatchInvite(match, matchInvite);
        matchInvite.setSentAt();
        persistInvite(matchInvite);
        if (user.getPk() != userAcceptingOrCreatingMatch.getPk()) {
            receivedMatchInviteNotification(matchInvite, user);
        }
        return matchInvite;
    }

    private void receivedMatchInviteNotification(MatchInvite matchInvite, User userRecieving) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.MATCH_INVITE);
        notification.setNotificationMessage("You have received a " + matchInvite.getTeam().getTeamPojo().getGame().getGameEnumDesc() + " " + matchInvite.getMatch().getMatchInfo().getMatchType().getMatchTypeEnumDesc() + " match invite.");
        notification.setUser(userRecieving);
        userRecieving.getNotification().add(notification);
        notification.setMatchInvite(matchInvite);
        matchInvite.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    public void createNewMatchInvitesForUsers(Match match, Team senderTeam, List<User> userList) {
        for (User user : userList) {
            MatchInvite matchInvite = new MatchInvite();
            matchInvite.setInviteEnum(InvitesEnum.PENDING);
            associateUserToMatchInvite(user, matchInvite);
            associateTeamToMatchInvite(senderTeam, matchInvite);
            associateMatchToMatchInvite(match, matchInvite);
            matchInvite.setSentAt();
            persistInvite(matchInvite);
        }
    }

    //show him match invite, and then accpt/decline, on accept call acceptPublicOrPrivateMatch in MatchService
    public MatchInvite inviteOpposingTeamLeaderToMatch(Match match, Team receiverTeam, Team creatorTeam) {
        MatchInvite matchInvite = new MatchInvite();
        matchInvite.setPrivateMatchTeamSenderName(creatorTeam.getTeamPojo().getTeamName());
        matchInvite.setPrivateMatchTeamSenderPk(creatorTeam.getPk());
        match.setPkOfTeamThatAcceptedMatch(receiverTeam.getPk());
        matchInvite.setInviteEnum(InvitesEnum.PENDING);
        matchInvite.setIsPrivateMatchInviteToLeader(true);
        User receiverTeamLeader = userRepo.findByPk(receiverTeam.getTeamLeaderPk());
        associateUserToMatchInvite(receiverTeamLeader, matchInvite);
        associateTeamToMatchInvite(receiverTeam, matchInvite);
        associateMatchToMatchInvite(match, matchInvite);
        matchInvite.setSentAt();
        persistInvite(matchInvite);
        receivedPrivateMatchInviteNotification(matchInvite, receiverTeamLeader);
        return matchInvite;
    }

    private void receivedPrivateMatchInviteNotification(MatchInvite matchInvite, User userRecieving) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.MATCH_INVITE);
        notification.setNotificationMessage("You have received a private " + matchInvite.getTeam().getTeamPojo().getGame().getGameEnumDesc() + " " + matchInvite.getMatch().getMatchInfo().getMatchType().getMatchTypeEnumDesc() + " match invite.");
        notification.setUser(userRecieving);
        userRecieving.getNotification().add(notification);
        notification.setMatchInvite(matchInvite);
        matchInvite.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    public void acceptMatchInvite(long matchInvitePk) throws Exception {
        MatchInvite matchInvite = matchInviteRepo.findByPk(matchInvitePk);
        if (matchInvite.getInviteEnum() != InvitesEnum.PENDING) {
            matchInvite.setInviteEnum(InvitesEnum.DECLINED);
            matchInvite.setDeclinedAt();
            throw new Exception("This match invite has expired.");
        }
        User user = matchInvite.getUser();
        Match match = matchInvite.getMatch();
        if (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_SECOND_ACCEPT) {
            matchInvite.setInviteEnum(InvitesEnum.ACCEPTED);
            matchInvite.setAcceptedAt();
            bankService.wagerMatchBuyIn(user, match);
            match.getPksOfPlayersWhoHaveAcceptedMatchInvite().add(user.getPk());
            matchService.associateUserToMatch(user, match);
            int numOfPlayersNeedingToAccept = Math.subtractExact(match.getMatchInfo().getNumOfPlayersNeedingToAccept(), 1);
            match.getMatchInfo().setNumOfPlayersNeedingToAccept(numOfPlayersNeedingToAccept);

            if (user.getPk() != match.getPkOfUserThatCreatedMatch() && user.getPk() != match.getPkOfUserThatAcceptedMatch()) {
                setAcceptMatchInviteNotification(match, user);
            }

            if (numOfPlayersNeedingToAccept == match.getMatchInfo().getNumOfPlayers() / 2) {
                match.getMatchInfo().setMatchStatus(MatchStatusEnum.PENDING);
                if (!match.getMatchInfo().isIsMatchPublic()) {
                    Team teamAcceptor = teamRepository.findTeamByPk(match.getPkOfTeamThatAcceptedMatch());
                    Team teamCreator = teamRepository.findTeamByPk(match.getPkOfTeamThatCreatedMatch());
                    if (teamAcceptor != null) {
                        inviteOpposingTeamLeaderToMatch(match, teamAcceptor, teamCreator);
                    }
                } else {
                    User matchCreator = userRepo.findByPk(match.getPkOfUserThatCreatedMatch());
                    setMatchPostedInMatchfinderNotification(match, matchCreator);
                }
            }
            if (numOfPlayersNeedingToAccept == 0) {
                match.getMatchInfo().setMatchStatus(MatchStatusEnum.READY_TO_PLAY);
                match.setHaveAllPlayersAccepted(true);
                matchService.selectHostAndMapsForMatch(match, 0, 0, 0);

            }
        } else {
            matchInvite.setInviteEnum(InvitesEnum.DECLINED);
            matchInvite.setDeclinedAt();
            throw new Exception("This match invite has expired.");

        }
    }

    private void setMatchPostedInMatchfinderNotification(Match match, User user) {
        if (match != null && user != null) {
            Notification notification = new Notification();
            notification.setNotificationType(NotificationTypeEnum.JOINED_WAGER);
            notification.setNotificationMessage("Your " + match.getMatchInfo().getGameEnum().getGameEnumDesc() + " " + match.getMatchInfo().getMatchType().getMatchTypeEnumDesc() + " match has been posted on the matchfinder.");
            notification.setUser(user);
            user.getNotification().add(notification);
            notification.setMatch(match);
            match.getNotification().add(notification);
            notificationRepo.persistNotification(notification);
        }
    }

    private void setAcceptMatchInviteNotification(Match match, User user) {
        if (match != null && user != null) {
            Notification notification = new Notification();
            notification.setNotificationType(NotificationTypeEnum.JOINED_WAGER);
            if (match.getMatchInfo() != null && match.getMatchInfo().getGameEnum() != null && match.getMatchInfo().getMatchType() != null) {
                notification.setNotificationMessage("You have joined a " + match.getMatchInfo().getGameEnum().getGameEnumDesc() + " " + match.getMatchInfo().getMatchType().getMatchTypeEnumDesc() + " match.");
            }
            notification.setUser(user);
            user.getNotification().add(notification);
            notification.setMatch(match);
            match.getNotification().add(notification);
            notificationRepo.persistNotification(notification);
        }
    }

    //this is for public creating team member declined or private cash match creating/accepting team declined, need different decline for public cash accepting team member decline
    public void declineMatchInvite(long matchInvitePk) throws Exception {
        MatchInvite matchInvite = matchInviteRepo.findByPk(matchInvitePk);
        if (matchInvite.getInviteEnum() != InvitesEnum.PENDING) {
            matchInvite.setInviteEnum(InvitesEnum.DECLINED);
            matchInvite.setDeclinedAt();
            throw new Exception("This match invite has expired.");
        }
        matchInvite.setInviteEnum(InvitesEnum.DECLINED);
        matchInvite.setDeclinedAt();
        Match match = matchInvite.getMatch();
        if (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_SECOND_ACCEPT) {
            //for corner case where match has expired or cancelled and a user declines after
            User user = matchInvite.getUser();
            Long creatorTeamPk = match.getPkOfTeamThatCreatedMatch();
            Team creatorTeam = teamRepository.findTeamByPk(creatorTeamPk);
            Long acceptorTeamPk = match.getPkOfTeamThatAcceptedMatch();
            Team acceptorTeam = null;
            if (acceptorTeamPk != null) {
                acceptorTeam = teamRepository.findTeamByPk(acceptorTeamPk);
            }
            //A member on the creating team declines public or private match
            if (match.getPksOfCreatorTeamMembersPlaying().contains(user.getPk())) {
                matchService.dissociateTeamFromMatch(creatorTeam, match);
                match.getMatchInfo().setMatchStatus(MatchStatusEnum.CANCELLED);
                //for everyone who's gotten an invite
                for (long userPk : match.getPksOfCreatorTeamMembersPlaying()) {
                    User u = userRepo.findByPk(userPk);
                    MatchInvite matchInv = matchInviteRepo.findMatchInviteForUserInMatch(match.getPk(), userPk);
                    if (matchInv.getInviteEnum() == InvitesEnum.ACCEPTED) {
                        bankService.wagerMatchCancelled(u, match);
                        matchService.dissociateUserFromMatch(u, match);
                    }
                    matchInv.setInviteEnum(InvitesEnum.DECLINED);
                    matchInv.setDeclinedAt();
                    setMatchCreatingTeamMemberDeclinesNotification(u, match, matchInvite.getTeam());
                }
            } //A member on the accepting team declines
            else {
                if (match.getMatchInfo().isIsMatchPublic()) {
                    match.getMatchInfo().setMatchStatus(MatchStatusEnum.PENDING);
                    matchService.dissociateTeamFromMatch(acceptorTeam, match);
                    //for everyone who's gotten an invite
                    for (long userPk : match.getPksOfAcceptorTeamMembersPlaying()) {
                        User u = userRepo.findByPk(userPk);
                        MatchInvite matchInv = matchInviteRepo.findMatchInviteForUserInMatch(match.getPk(), userPk);
                        if (matchInv.getInviteEnum() == InvitesEnum.ACCEPTED) {
                            bankService.wagerMatchCancelled(u, match);
                            matchService.dissociateUserFromMatch(u, match);
                        }
                        matchInv.setInviteEnum(InvitesEnum.DECLINED);
                        matchInv.setDeclinedAt();
                        setAcceptingMatchInviteDeclineNotification(match, matchInvite.getTeam(), u);
                    }
                    match.getMatchInfo().setNumOfPlayersNeedingToAccept(match.getMatchInfo().getNumOfPlayers() / 2);
                    match.setPkOfTeamThatAcceptedMatch(0);
                    match.setPkOfUserThatAcceptedMatch(0);
                    List<Long> newAcceptorListOfPks = new ArrayList<>();
                    match.setPksOfAcceptorTeamMembersPlaying(newAcceptorListOfPks);
                } else if (!match.getMatchInfo().isIsMatchPublic()) {
                    matchService.dissociateTeamFromMatch(acceptorTeam, match);
                    matchService.dissociateTeamFromMatch(creatorTeam, match);
                    match.getMatchInfo().setMatchStatus(MatchStatusEnum.CANCELLED);
                    //for everyone who accepted
                    for (long userPk : match.getPksOfPlayersWhoHaveAcceptedMatchInvite()) {
                        User u = userRepo.findByPk(userPk);
                        bankService.wagerMatchCancelled(u, match);
                        matchService.dissociateUserFromMatch(u, match);
                        MatchInvite matchInv = matchInviteRepo.findMatchInviteForUserInMatch(match.getPk(), userPk);
                        matchInv.setInviteEnum(InvitesEnum.DECLINED);
                        matchInv.setDeclinedAt();
                        setMatchCreatingTeamMemberDeclinesNotification(u, match, matchInvite.getTeam());
                    }
                }
            }
        } else {
            matchInvite.setInviteEnum(InvitesEnum.DECLINED);
            matchInvite.setDeclinedAt();
            throw new Exception("This match invite has expired.");

        }
    }

    public List<MatchCancellationRequest> getPendingMatchCancellationRequestForAUser(long userPk) {
        return matchCancellationRepo.getPendingMatchCancellationRequestForAUser(userPk);
    }

    private void setAcceptingMatchInviteDeclineNotification(Match match, Team team, User u) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.TEAM_LEFT_WAGER);
        notification.setNotificationMessage("A user on your " + match.getMatchInfo().getGameEnum().getGameEnumDesc() + " " + match.getMatchInfo().getMatchType().getMatchTypeEnumDesc() + " team '" + team.getTeamPojo().getTeamName() + "' has declined the match invite. Therefore your team has been removed from the match.");
        notification.setUser(u);
        u.getNotification().add(notification);
        notification.setMatch(match);
        match.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    private void setMatchCreatingTeamMemberDeclinesNotification(User u, Match match, Team team) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.CANCELLED_MATCH);
        notification.setNotificationMessage("A user on the " + match.getMatchInfo().getGameEnum().getGameEnumDesc() + " " + match.getMatchInfo().getMatchType().getMatchTypeEnumDesc() + " team '" + team.getTeamPojo().getTeamName() + "' has declined the match invite. Therefore the match has been cancelled.");
        notification.setUser(u);
        u.getNotification().add(notification);
        notification.setMatch(match);
        match.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

//   public void declineMatchInviteFromAcceptingTeamMember(long matchInvitePk){
//             
//   }
    public void associateUserToMatchInvite(User user, MatchInvite matchInvite) {
        user.getMatchInvites().add(matchInvite);
        matchInvite.setUser(user);
    }

    public void associateMatchToMatchInvite(Match match, MatchInvite matchInvite) {
        match.getMatchInvites().add(matchInvite);
        matchInvite.setMatch(match);
    }

    public void associateTeamToMatchInvite(Team team, MatchInvite matchInvite) {
        team.getMatchInvites().add(matchInvite);
        matchInvite.setTeam(team);
    }

    public List<MatchInvite> findAllMatchInvitesByUserPk(long pk) {
        return matchInviteRepo.findAllMatchInvitesByUserPk(pk);
    }

    public List<MatchInvite> findAllMatchInvitesByTeamPk(long pk) {
        return matchInviteRepo.findAllMatchInvitesByTeamPk(pk);

    }

    public List<MatchInvite> findAllMatchInvitesByMatchPk(long pk) {
        return matchInviteRepo.findAllMatchInvitesByMatchPk(pk);
    }

    public List<MatchInvite> findAllMatchInvitesByStatusForUser(long pk, InvitesEnum inviteEnum) {
        return matchInviteRepo.findAllMatchInvitesByStatusForUser(pk, inviteEnum);
    }

    public MatchInvite findMatchInviteForUserInMatch(long matchPk, long userPk) {
        return matchInviteRepo.findMatchInviteForUserInMatch(matchPk, userPk);
    }

}
