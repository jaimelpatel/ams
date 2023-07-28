package com.ltlogic.iws;

import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.TeamInvite;
import com.ltlogic.db.entity.User;
import com.ltlogic.service.core.TeamInviteService;
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
public class TeamInviteIWS {
    
    @Autowired
    TeamInviteService teamInviteService;
    
    
    public TeamInvite findInviteByPk(Long pk) {
        return teamInviteService.findInviteByPk(pk);
    }
    
    public TeamInvite inviteUserToTeam(String usernameOfSender, String usernameOfReceiver, Long teamPk) {
        return teamInviteService.inviteUserToTeam(usernameOfSender, usernameOfReceiver, teamPk);
    }
    
    public void acceptInvite(User userAccepting, Team teamToAddTo, TeamInvite inviteToUpdate) {
        teamInviteService.acceptInvite(userAccepting, teamToAddTo, inviteToUpdate);
    }
    
    public void declineInvite(User user, Team team, TeamInvite invite) {
        teamInviteService.declineInvite(user, team, invite);
    }
    
    public List<TeamInvite> getPendingInvitesByUserPk(long userPk, int pageNumber){
        return teamInviteService.getPendingInvitesByUserPk(userPk, pageNumber);
    }
    
    public Integer getTotalPendingInvitesByUserPk(long receiverPk) {
        return teamInviteService.getTotalPendingInvitesByUserPk(receiverPk);
    }
}
