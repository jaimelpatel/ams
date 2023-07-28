/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;

import com.ltlogic.db.entity.MatchCancellationRequest;
import com.ltlogic.service.core.MatchCancellationRequestService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author raymond
 */
@Service
public class MatchCancellationRequestIWS {
    @Autowired
            
    MatchCancellationRequestService matchCancellationRequest;
    
    public MatchCancellationRequest getMatchCancellationRequestByMatchPk(long pk){
        return matchCancellationRequest.getMatchCancellationRequestByMatchPk(pk);
    }
    
    public List<MatchCancellationRequest> getPendingMatchCancellationRequestForAUser(Long userPk) {
        return matchCancellationRequest.getPendingMatchCancellationRequestForAUser(userPk);
    }
}
