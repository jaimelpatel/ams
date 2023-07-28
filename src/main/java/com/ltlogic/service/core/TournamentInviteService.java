/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.constants.NotificationTypeEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TournamentStatusEnum;
import com.ltlogic.db.entity.MatchInvite;
import com.ltlogic.db.entity.Notification;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentInvite;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.NotificationRepository;
import com.ltlogic.db.repository.TournamentInviteRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.iws.TeamIWS;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author Hoang
 */
@Service
@Transactional
public class TournamentInviteService {

    @Autowired
    TournamentInviteRepository tournamentInviteRepo;

    @Autowired
    NotificationRepository notificationRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    BankService bankService;

    @Autowired
    TournamentService tournamentService;

    @Autowired
    TournamentTeamService tournamentTeamService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    TeamIWS teamIWS;

    private static final Logger log = LoggerFactory.getLogger(TournamentInviteService.class);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setTournamentStatusToActive(Tournament t) {
        t.getTournamentInfo().setTournamentStatus(TournamentStatusEnum.ACTIVE);
    }

    public void persistTournamentInvite(TournamentInvite t) {
        tournamentInviteRepo.persistInvite(t);
    }

    public TournamentInvite findInviteByPk(long pk) {
        return tournamentInviteRepo.findByPk(pk);
    }

    public List<TournamentInvite> findAllTournamentInvitesByStatusForUser(Long userPk, InvitesEnum inviteStatus) {
        return tournamentInviteRepo.findAllTournamentInvitesByStatusForUser(userPk, inviteStatus);
    }

    public List<TournamentInvite> getTournamentInvitesByUsername(Long userPk) {
        return tournamentInviteRepo.findAllTournamentInvitesByUserPk(userPk);
    }

    public void setTournamentInviteStatus(TournamentInvite ti, InvitesEnum i) {
        ti.setInviteEnum(i);
        ti.setSentAt();
    }

    public TournamentInvite createNewTournamentInvite(Tournament tournament, User user, TournamentTeam tournamentTeam, User userJoinTournament) {
        TournamentInvite tournamentInvite = new TournamentInvite();
        tournamentInvite.setInviteEnum(InvitesEnum.PENDING);
        associateUserToTournamentInvite(user, tournamentInvite);
        associateTournamentTeamToTournamentInvite(tournamentTeam, tournamentInvite);
        associateTournamentToTournamentInvite(tournament, tournamentInvite);
        tournamentInvite.setSentAt();
        persistTournamentInvite(tournamentInvite);
        if (user.getPk() != userJoinTournament.getPk()) {
            receivedTournamentInviteNotification(tournamentInvite, user);
        }
        return tournamentInvite;
    }

    private void receivedTournamentInviteNotification(TournamentInvite tournamentInvite, User userRecieving) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.TOURNAMENT_INVITE);
        notification.setNotificationMessage("You have received a " + tournamentInvite.getTournament().getTournamentInfo().getGameEnum().getGameEnumDesc() + " tournament invite.");
        notification.setUser(userRecieving);
        userRecieving.getNotification().add(notification);
        notification.setTournamentInvite(tournamentInvite);
        tournamentInvite.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    public List<TournamentInvite> createNewTournamentInvitesForUsers(Tournament tournament, List<User> userList, TournamentTeam tournamentTeam) {
        List<TournamentInvite> inviteList = new ArrayList<>();
        for (User user : userList) {
            TournamentInvite tournamentInvite = new TournamentInvite();
            tournamentInvite.setInviteEnum(InvitesEnum.PENDING);
            associateUserToTournamentInvite(user, tournamentInvite);
            associateTournamentTeamToTournamentInvite(tournamentTeam, tournamentInvite);
            associateTournamentToTournamentInvite(tournament, tournamentInvite);
            tournamentInvite.setSentAt();
            persistTournamentInvite(tournamentInvite);
            inviteList.add(tournamentInvite);
        }
        return inviteList;
    }

    public void acceptTournamentInvite(long tournamentInvitePk) throws Exception {
        TournamentInvite tournamentInvite = tournamentInviteRepo.findByPk(tournamentInvitePk);
        Team team = tournamentInvite.getTournamentTeam().getTeam();
        TournamentTeam tournamentTeam = tournamentInvite.getTournamentTeam();
        Tournament tournament = tournamentInvite.getTournament();
        User user = tournamentInvite.getUser();

        if (tournamentTeam.isTeamCancelled() || tournament.getTournamentInfo().getTournamentStatus() != TournamentStatusEnum.PENDING) {
            tournamentInvite.setInviteEnum(InvitesEnum.DECLINED);
            tournamentInvite.setDeclinedAt();
            throw new Exception("This tournament invite has expired.");
        }

        if (team.getMembers().contains(user)) {
            tournamentTeam.getPksOfTeamMembersPlaying().add(user.getPk());
            tournamentInvite.setInviteEnum(InvitesEnum.ACCEPTED);
            tournamentInvite.setAcceptedAt();
            tournamentService.associateTournamentToUser(tournament, user);
            bankService.tournamentBuyIn(user, tournament);
            int numOfPlayersNeedingToAccept = Math.subtractExact(tournamentTeam.getNumOfPlayersNeedingToAccept(), 1);
            tournamentTeam.setNumOfPlayersNeedingToAccept(numOfPlayersNeedingToAccept);
            if (numOfPlayersNeedingToAccept == 0) {
                tournamentTeam.setAllPlayersAccepted(true);
            } else if (numOfPlayersNeedingToAccept < 0) {
                int matchSize = tournamentInvite.getTournament().getTournamentInfo().getMatchSize().getMatchSizeEnumId() + 1;
                throw new RuntimeException("This team already has " + matchSize + " members total that have accepted their tournament invites to play in this tournament.");
            }
            tournamentTeam.getPksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam().add(user.getPk());
            setAcceptTournamentInviteNotification(team, user, tournament);
        } else {
            if (team.getTeamPojo().getTeamSize() == TeamSizeEnum.SINGLES) {
                if (team.getMembers().size() == 1) {
                    throw new RuntimeException("This team already has the max number of members (1).");

                }
            }

            if (team.getTeamPojo().getTeamSize() == TeamSizeEnum.DOUBLES) {
                if (team.getMembers().size() == 6) {
                    throw new RuntimeException("This team already has the max number of members (6).");

                }
            }

            if (team.getTeamPojo().getTeamSize() == TeamSizeEnum.TEAM) {
                if (team.getMembers().size() == 10) {
                    throw new RuntimeException("This team already has the max number of members (10).");

                }
            }

            List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(user.getUsername(), team.getTeamPojo().getGame(), team.getTeamPojo().getPlatform(), team.getTeamPojo().getTeamSize(), team.getTeamPojo().getTeamType(), user.getUserInfo().getRegion());
            if (teams.size() == 3) {
                throw new RuntimeException("You are already a member of 3 cashout teams in the same ladder (" + team.getTeamPojo().getGame().getGameEnumDesc() + " - " + team.getTeamPojo().getPlatform().getPlatformEnumDesc() + " - " + team.getTeamPojo().getTeamSize().getTeamSizeEnumDesc() + " - " + team.getTeamPojo().getRegion().getRegionEnumDesc() + "). Max 3 teams per ladder for Cashout teams.  Max 2 teams per ladder for XP teams. ");

            }

            team.getMembers().add(user);
            user.getTeams().add(team);

            tournamentTeam.getPksOfTeamMembersPlaying().add(user.getPk());
            tournamentInvite.setInviteEnum(InvitesEnum.ACCEPTED);
            tournamentInvite.setAcceptedAt();
            tournamentService.associateTournamentToUser(tournament, user);
            bankService.tournamentBuyIn(user, tournament);
            int numOfPlayersNeedingToAccept = Math.subtractExact(tournamentTeam.getNumOfPlayersNeedingToAccept(), 1);
            tournamentTeam.setNumOfPlayersNeedingToAccept(numOfPlayersNeedingToAccept);
            if (numOfPlayersNeedingToAccept == 0) {
                tournamentTeam.setAllPlayersAccepted(true);
            } else if (numOfPlayersNeedingToAccept < 0) {
                int matchSize = tournamentInvite.getTournament().getTournamentInfo().getMatchSize().getMatchSizeEnumId() + 1;
                throw new RuntimeException("This team already has " + matchSize + " members total that have accepted their tournament invites to play in this tournament.");
            }
            tournamentTeam.getPksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam().add(user.getPk());
            setAcceptTournamentInviteNotification(team, user, tournament);
        }
    }

    private void setAcceptTournamentInviteNotification(Team team, User user, Tournament tournament) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.JOINED_TOURNAMENT);
        notification.setNotificationMessage("You have joined a tournament with team '" + team.getTeamPojo().getTeamName() + "'.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setTournament(tournament);
        tournament.getNotification().add(notification);
        notification.setTeam(team);
        team.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    public void declineTournamentInvite(long tournamentInvitePk) throws Exception {
        TournamentInvite tournamentInvite = tournamentInviteRepo.findByPk(tournamentInvitePk);
        TournamentTeam tournamentTeam = tournamentInvite.getTournamentTeam();
        Tournament tournament = tournamentInvite.getTournament();
        Team team = tournamentInvite.getTournamentTeam().getTeam();
        User user = tournamentInvite.getUser();

        if (tournamentTeam.isTeamCancelled() || tournament.getTournamentInfo().getTournamentStatus() != TournamentStatusEnum.PENDING) {
            tournamentInvite.setInviteEnum(InvitesEnum.DECLINED);
            tournamentInvite.setDeclinedAt();
            throw new Exception("This tournament invite has expired.");
        }

        tournamentTeam.setTeamCancelled(true);
        for (long userPk : tournamentTeam.getPksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam()) {
            User u = userRepo.findByPk(userPk);
            bankService.tournamentCancelled(u, tournament);
            setTournamentInviteDeclineNotification(userPk, team, tournament);
            tournamentService.dissociateUserFromTournament(tournament, u);
        }
        for (long userPk : tournamentTeam.getPksOfTeamMembersPlaying()) {
            TournamentInvite tournamentInv = tournamentInviteRepo.findTournamentInviteByTournamentTeamPkAndUserPk(tournamentTeam.getPk(), userPk);
            tournamentInv.setInviteEnum(InvitesEnum.DECLINED);
            tournamentInv.setDeclinedAt();
        }
        tournamentTeamService.disassociateTournamentTeamFromTournament(tournamentTeam, tournament);

        int currentTeamCount = tournament.getTournamentTeams().size();
        tournament.getTournamentInfo().setTeamCount(currentTeamCount);
    }

    public void declineTournamentInvite2(long tournamentInvitePk) throws Exception {
        TournamentInvite tournamentInvite = tournamentInviteRepo.findByPk(tournamentInvitePk);
        TournamentTeam tournamentTeam = tournamentInvite.getTournamentTeam();
        Tournament tournament = tournamentInvite.getTournament();
        Team team = tournamentInvite.getTournamentTeam().getTeam();
        User user = tournamentInvite.getUser();

        if (tournamentTeam.isTeamCancelled() || tournament.getTournamentInfo().getTournamentStatus() != TournamentStatusEnum.PENDING) {
            tournamentInvite.setInviteEnum(InvitesEnum.DECLINED);
            tournamentInvite.setDeclinedAt();
            throw new Exception("This tournament invite has expired.");
        }

        tournamentInvite.setInviteEnum(InvitesEnum.DECLINED);
        tournamentInvite.setDeclinedAt();
        bankService.tournamentCancelled(user, tournament);
        tournamentService.dissociateUserFromTournament(tournament, user);
        tournamentTeam.getPksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam().remove(user.getPk());
        tournamentTeam.getPksOfTeamMembersPlaying().remove(user.getPk());
        int numberOfPlayersNeededToAccept = tournamentTeam.getNumOfPlayersNeedingToAccept() + 1;
        tournamentTeam.setNumOfPlayersNeedingToAccept(numberOfPlayersNeededToAccept);
        tournamentTeam.setIsEligible(false);
        tournamentTeam.setAllPlayersAccepted(false);
        for (long userPk : tournamentTeam.getPksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam()) {
            User u = userRepo.findByPk(userPk);
            setTeamMemberHasLeftTournament(u, team, tournament);
        }
    }
    
    public void declineTournamentInvite3(long tournamentInvitePk) throws Exception {
        TournamentInvite tournamentInvite = tournamentInviteRepo.findByPk(tournamentInvitePk);
        TournamentTeam tournamentTeam = tournamentInvite.getTournamentTeam();
        Tournament tournament = tournamentInvite.getTournament();

        if (tournamentTeam.isTeamCancelled() || tournament.getTournamentInfo().getTournamentStatus() != TournamentStatusEnum.PENDING) {
            tournamentInvite.setInviteEnum(InvitesEnum.DECLINED);
            tournamentInvite.setDeclinedAt();
            throw new Exception("This tournament invite has expired.");
        }

        tournamentInvite.setInviteEnum(InvitesEnum.DECLINED);
        tournamentInvite.setDeclinedAt();
//        bankService.tournamentCancelled(user, tournament);
//        tournamentService.dissociateUserFromTournament(tournament, user);
//        tournamentTeam.getPksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam().remove(user.getPk());
//        tournamentTeam.getPksOfTeamMembersPlaying().remove(user.getPk());
//        int numberOfPlayersNeededToAccept = tournamentTeam.getNumOfPlayersNeedingToAccept() + 1;
//        tournamentTeam.setNumOfPlayersNeedingToAccept(numberOfPlayersNeededToAccept);
//        tournamentTeam.setIsEligible(false);
//        tournamentTeam.setAllPlayersAccepted(false);
//        for (long userPk : tournamentTeam.getPksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam()) {
//            User u = userRepo.findByPk(userPk);
//            setTeamMemberHasLeftTournament(u, team, tournament);
//        }
    }

    private void setTournamentInviteDeclineNotification(Long userPk, Team team, Tournament tournament) {
        User user = userRepo.findByPk(userPk);
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.TEAM_LEFT_TOURNAMENT);
        notification.setNotificationMessage("The leader of your cashout team '" + team.getTeamPojo().getTeamName() + "' has left the tournament. Therefore your team has been removed from the tournament.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setTournament(tournament);
        tournament.getNotification().add(notification);
        notification.setTeam(team);
        team.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    private void setTeamMemberHasLeftTournament(User user, Team team, Tournament tournament) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.TEAM_LEFT_TOURNAMENT);
        notification.setNotificationMessage("User '" + user.getUsername() + "' on your cashout team '" + team.getTeamPojo().getTeamName() + "' has left the tournament. Therefore your team is ineligible for play until you get a replacement.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setTournament(tournament);
        tournament.getNotification().add(notification);
        notification.setTeam(team);
        team.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    public void inviteUserToTournament(long userPk, long teamPk, long tournamentPk) {

    }

    public void associateUserToTournamentInvite(User user, TournamentInvite tournamentInvite) {
        user.getTournamentInvites().add(tournamentInvite);
        tournamentInvite.setUser(user);
    }

    public void associateTournamentTeamToTournamentInvite(TournamentTeam tournamentTeam, TournamentInvite tournamentInvite) {
        tournamentTeam.getTournamentInvites().add(tournamentInvite);
        tournamentInvite.setTournamentTeam(tournamentTeam);
    }

    public void associateTournamentToTournamentInvite(Tournament tournament, TournamentInvite tournamentInvite) {
        tournament.getTournamentInvites().add(tournamentInvite);
        tournamentInvite.setTournament(tournament);
    }

    public List<TournamentInvite> findAllTournamentInvitesByUserPk(long pk) {
        return tournamentInviteRepo.findAllTournamentInvitesByUserPk(pk);
    }

    public List<TournamentInvite> finAllTournamentInvitesByTournamentTeamPkNotCancelled(long pk) {
        return tournamentInviteRepo.findAllTournamentInvitesByTournamentTeamPkNotCancelled(pk);
    }

    public List<TournamentInvite> findAllTournamentInvitesByTournamentPk(long pk) {
        return tournamentInviteRepo.findAllTournamentInvitesByTournamentPk(pk);
    }

    public List<TournamentInvite> findAllTournamentInvitesByStatusForUser(long userPk, InvitesEnum invitesEnum) {
        return tournamentInviteRepo.findAllTournamentInvitesByStatusForUser(userPk, invitesEnum);
    }

    //Pass in pending for inviteEnum to find relevant results.  should only return 1 pending invite, can pass in different enum for statistical results
    public TournamentInvite findTournamentInvitesForUserInTournamentTeamByStatus(long tournamentPk, long userPk, InvitesEnum inviteEnum) {
        return tournamentInviteRepo.findTournamentInviteForUserInTournamentTeamByStatus(tournamentPk, userPk, inviteEnum);
    }

    public TournamentInvite findTournamentInviteForUserInTournamentTeam(long tournamentTeamPk, long userPk) {
        return tournamentInviteRepo.findTournamentInviteByTournamentTeamPkAndUserPk(tournamentTeamPk, userPk);
    }

}
