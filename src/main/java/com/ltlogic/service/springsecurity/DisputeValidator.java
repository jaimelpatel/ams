/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.springsecurity;

import com.ltlogic.constants.TeamPermissionsEnum;
import com.ltlogic.db.entity.Dispute;
import com.ltlogic.db.entity.TeamPermissions;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.DisputeRepository;
import com.ltlogic.db.repository.TeamRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.service.core.DisputeService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 *
 * @author Bishistha
 */
@Component
public class DisputeValidator {
    
    @Autowired
    private DisputeService disputeService;
    
    @Autowired
    private DisputeRepository disputeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    public void createDisputeForMatchValidation(long userPk, long teamPk, long matchPk, Errors errors){
        Dispute dispute = disputeRepository.findDisputeByUserAndMatchPk(userPk, matchPk);
        TeamPermissions tp = teamRepository.findTeamPermissionsByUserPkAndTeamPk(userPk, teamPk);
        
        if(!tp.isHasLeaderPermissions()){
            errors.reject("teampermission.unavailable", "User " + dispute.getUser().getUsername() + " does not have permission to report a match.");
        } else{
            if(dispute != null){
            errors.reject("dispute.uniqueness","There is already a dispute filed by user " + dispute.getUser().getUsername() + " from team: " + dispute.getTeam() +" for the match with Match ID #" + dispute.getMatch().getMatchId());
        }
        }
    }
}
