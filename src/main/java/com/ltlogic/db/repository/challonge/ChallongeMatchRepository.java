/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository.challonge;

import com.ltlogic.db.entity.challonge.MatchResponse;
import com.ltlogic.db.entity.challonge.TournamentResponse;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Bishistha
 */
@Repository
public class ChallongeMatchRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public void persistChallongeMatch(MatchResponse matchResponse){
        entityManager.persist(matchResponse);
    }
    
    public void updateChallongeMatch(MatchResponse matchResponse){
        entityManager.merge(matchResponse);
    }
    
    public MatchResponse getMatchResponseById(long id){
        TypedQuery<MatchResponse> query = entityManager.createNamedQuery("MatchResponse.getMatchResponseById", MatchResponse.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
    
    public MatchResponse getNextMatchByCurrentMatchId(long matchResponseId){
        TypedQuery<MatchResponse> query = entityManager.createNamedQuery("MatchResponse.getNextMatchByCurrentMatchId", MatchResponse.class);
        query.setParameter("matchResponseId", "%" + matchResponseId + "%");
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
}
