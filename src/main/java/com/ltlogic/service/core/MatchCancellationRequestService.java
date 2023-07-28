/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.ltlogic.db.entity.MatchCancellationRequest;
import com.ltlogic.db.repository.MatchCancellationRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author raymond
 */
@Service
public class MatchCancellationRequestService {
    @Autowired
    MatchCancellationRepository repo;
    
    public MatchCancellationRequest getMatchCancellationRequestByMatchPk(long pk){
        return repo.getMatchCancellationRequestByMatchPk(pk);
    }
    
    public List<MatchCancellationRequest> getPendingMatchCancellationRequestForAUser(Long userPk) {
        return repo.getPendingMatchCancellationRequestForAUser(userPk);
    }
}
