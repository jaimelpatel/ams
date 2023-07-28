/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;

import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.MatchCancellationRequest;
import com.ltlogic.db.entity.MatchInvite;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.service.core.MatchInviteService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author raymond
 */
@Service
public class MatchInviteIWS {
    
    @Autowired
    MatchInviteService service;
    
    
    public MatchInvite findInvitebyPk(long pk) {
        return service.findInvitebyPk(pk);
    }

//    public MatchInvite createNewMatchInvite(Match match, Team senderTeam, User user) {
//        return service.createNewMatchInvite(match, senderTeam, user);
//    }
    
    public void createNewMatchInvitesForUsers(Match match, Team senderTeam, List<User> userList) {
        service.createNewMatchInvitesForUsers(match, senderTeam, userList);
    }
    
//    public MatchInvite inviteTeamToMatch(Match match, Team receiverTeam) {
//        return service.inviteOpposingTeamLeaderToMatch(match, receiverTeam);
//    }
    
    public void acceptMatchInvite(long matchInvitePk) throws Exception {
        service.acceptMatchInvite(matchInvitePk);
    }
    
    public void declineMatchInvite(long matchInvitePk) throws Exception{
        service.declineMatchInvite(matchInvitePk);
    }
    
    public List<MatchInvite> findAllMatchInvitesByUserPk(long pk) {
        return service.findAllMatchInvitesByUserPk(pk);
    }

    public List<MatchInvite> findAllMatchInvitesByTeamPk(long pk) {
        return service.findAllMatchInvitesByTeamPk(pk);

    }

    public List<MatchInvite> findAllMatchInvitesByMatchPk(long pk) {
        return service.findAllMatchInvitesByMatchPk(pk);
    }

    public List<MatchInvite> findAllMatchInvitesByStatusForUser(long pk, InvitesEnum inviteEnum) {
        return service.findAllMatchInvitesByStatusForUser(pk, inviteEnum);
    }
    
    public MatchInvite findMatchInviteForUserInMatch(long matchPk, long userPk){
        return service.findMatchInviteForUserInMatch(matchPk, userPk);
    }
    
    public List<MatchCancellationRequest> getPendingMatchCancellationRequestForAUser(long userPk){
        return service.getPendingMatchCancellationRequestForAUser(userPk);
    }
}
