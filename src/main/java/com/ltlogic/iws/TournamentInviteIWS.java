/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;

import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.db.entity.TournamentInvite;
import com.ltlogic.service.core.TournamentInviteService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jaimel
 */
@Component
public class TournamentInviteIWS {
    
    @Autowired
    TournamentInviteService tournamentInviteService;

    public TournamentInvite findInviteByPk(long pk) {
        return tournamentInviteService.findInviteByPk(pk);
    }
    
    public List<TournamentInvite> findAllTournamentInvitesByStatusForUser(Long userPk, InvitesEnum inviteStatus) {
        return tournamentInviteService.findAllTournamentInvitesByStatusForUser(userPk, inviteStatus);
    }
    
    public List<TournamentInvite> getTournamentInvitesByUsername(Long userPk) {
        return tournamentInviteService.getTournamentInvitesByUsername(userPk);
    }
    
    public void acceptTournamentInvite(long tournamentInvitePk) throws Exception{
        tournamentInviteService.acceptTournamentInvite(tournamentInvitePk);
    }
    
    public void declineTournamentInvite(long tournamentInvitePk) throws Exception{
        tournamentInviteService.declineTournamentInvite3(tournamentInvitePk);
    }
    
    public TournamentInvite findTournamentInviteForUserInTournamentTeam(long tournamentTeamPk, long userPk){
       return tournamentInviteService.findTournamentInviteForUserInTournamentTeam(tournamentTeamPk, userPk);
    }
    
    public TournamentInvite findTournamentInviteForUserInTournamentTeamByInviteStatus(long tournamentTeamPk, long userPk, InvitesEnum inviteEnum){
       return tournamentInviteService.findTournamentInvitesForUserInTournamentTeamByStatus(tournamentTeamPk, userPk, inviteEnum);
    }
}
