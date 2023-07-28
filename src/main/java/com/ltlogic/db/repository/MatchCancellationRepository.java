/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.repository;

import com.ltlogic.db.entity.MatchCancellationRequest;
import com.ltlogic.db.entity.Tournament;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author Hoang
 */


@Repository
public class MatchCancellationRepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    public void persistRequest(MatchCancellationRequest r){
        entityManager.persist(r);
    }
    
    public MatchCancellationRequest getMatchCancellationRequestByMatchPk(long pk){
        TypedQuery<MatchCancellationRequest> query = entityManager.createNamedQuery("MatchCancellationRequest.getMatchCancellationRequestByMatchPk", MatchCancellationRequest.class);
        query.setParameter("pk", pk);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
    
    public List<MatchCancellationRequest> getPendingMatchCancellationRequestForAUser(long userPk){
        TypedQuery<MatchCancellationRequest> query = entityManager.createNamedQuery("MatchCancellationRequest.getPendingMatchCancellationRequestForAUser", MatchCancellationRequest.class);
        query.setParameter("userPk", userPk);
        
        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            return null;
        }
        
    }
}
