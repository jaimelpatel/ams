/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.constants.NotificationTypeEnum;
import com.ltlogic.db.entity.Notification;
import com.ltlogic.db.entity.TeamInvite;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.NotificationRepository;
import com.ltlogic.db.repository.TeamInviteRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jaimel
 */
@Service
@Transactional
public class TeamInviteService {

    @Autowired
    TeamInviteRepository teamInviteRepository;

    @Autowired
    UserService userService;

    @Autowired
    TeamService teamService;
    
    @Autowired 
    NotificationRepository notificationRepo;

    public void persistInvite(TeamInvite i) {
        teamInviteRepository.persistInvite(i);
    }

    public TeamInvite findInviteByPk(long pk) {
        return teamInviteRepository.findByPk(pk);
    }

    public TeamInvite inviteUserToTeam(String usernameOfSender, String usernameOfReceiver, long teamPk) {
        User sender = userService.findByUsername(usernameOfSender);
        User receiver = userService.findByUsernameLowercase(usernameOfReceiver);
        Team team = teamService.getTeamByPk(teamPk);
        TeamInvite i = new TeamInvite();
        associateTeamToTeamInvite(team, i);
        associateSenderUserToTeamInvite(sender, i);
        associateReceiverUserToTeamInvite(receiver, i);
        i.setInviteEnum(InvitesEnum.PENDING);
        persistInvite(i);
        receivedTeamInviteNotification(i, receiver);
        return i;
    }
    
    private void receivedTeamInviteNotification(TeamInvite teamInvite, User userRecieving) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.TEAM_INVITE);
        notification.setNotificationMessage("You have received a " + teamInvite.getTeam().getTeamPojo().getGame().getGameEnumDesc() + " " + teamInvite.getTeam().getTeamPojo().getTeamType().getTeamTypeEnumDesc() + " invite.");
        notification.setUser(userRecieving);
        userRecieving.getNotification().add(notification);
        notification.setTeamInvite(teamInvite);
        teamInvite.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    public void acceptInvite(User userAccepting, Team teamToAddTo, TeamInvite inviteToUpdate) {
        if (inviteToUpdate.getInviteEnum() == InvitesEnum.ACCEPTED || inviteToUpdate.getInviteEnum() == InvitesEnum.DECLINED) {
            return;
        }
        inviteToUpdate.setInviteEnum(InvitesEnum.ACCEPTED);
        inviteToUpdate.setAcceptedAt();
        teamService.associateUserToTeam(teamToAddTo, userAccepting);
        acceptTeamInviteNotification(teamToAddTo, userAccepting);
    }

    private void acceptTeamInviteNotification(Team teamToAddTo, User userAccepting) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.CREATE_TEAM);
        notification.setNotificationMessage("You have joined a " + teamToAddTo.getTeamPojo().getGame().getGameEnumDesc() +  " team named '" + teamToAddTo.getTeamPojo().getTeamName() + "'.");
        notification.setUser(userAccepting);
        userAccepting.getNotification().add(notification);
        notification.setTeam(teamToAddTo);
        teamToAddTo.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    public void declineInvite(User user, Team team, TeamInvite invite) {
        if (invite.getInviteEnum() == InvitesEnum.ACCEPTED || invite.getInviteEnum() == InvitesEnum.DECLINED) {
            return;
        }
        invite.setInviteEnum(InvitesEnum.DECLINED);
        invite.setDeclinedAt();
        //NO NEED TO DISSOCIATE USER FROM TEAM IF INVITATION IS TO BE DECLINED, SINCE IT WILL NEVER BE SET IN THE FIRST PLACE
        //teamService.dissociateUserFromTeam(team, user);
    }
    
    public List<TeamInvite> getPendingInvitesByUserPk(long userPk, int pageNumber){
        return teamInviteRepository.findPendingReceivedInvitesByUserPk(userPk, pageNumber);
    }
    
    public Integer getTotalPendingInvitesByUserPk(long receiverPk) {
        return teamInviteRepository.getTotalPendingInvitesByUserPk(receiverPk);
    }
    
    public void associateTeamToTeamInvite(Team team, TeamInvite teamInvite){
        team.getTeamInvites().add(teamInvite);
        teamInvite.setTeam(team);
    }
    
    public void associateSenderUserToTeamInvite(User senderUser, TeamInvite teamInvite){
        senderUser.getTeamInvitesSent().add(teamInvite);
        teamInvite.setUserSender(senderUser);
    }
    
    public void associateReceiverUserToTeamInvite(User receiverUser, TeamInvite teamInvite){
        receiverUser.getTeamInvitesReceived().add(teamInvite);
        teamInvite.setUserReceiver(receiverUser);
    }
}


/*
    WE JUST CHANGE THE STATUS TO 'ACCEPTED' OR 'DECLINED'. NO NEED TO REMOVE THE INVITE.

 public void removeInviteFromUsersReceivedList(User user, TeamInvite i) {
        Set<TeamInvite> newInviteList = new HashSet<TeamInvite>(0);
        user.getTeamInvitesReceived().remove(i);
        newInviteList = user.getTeamInvitesReceived();
        user.setTeamInvitesReceived(newInviteList);
    }
*/