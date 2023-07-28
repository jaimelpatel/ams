/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository.challonge;

import com.ltlogic.db.entity.challonge.ParticipantResponse;
import com.ltlogic.db.entity.challonge.TournamentResponse;
import javax.persistence.EntityManager;
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
public class ChallongeParticipantRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    public void persistChallongeParticipant(ParticipantResponse p){
        entityManager.persist(p);
    }
    
    public void updateChallongParticipant(ParticipantResponse p){
        entityManager.merge(p);
    }
    
    public ParticipantResponse getParticipantResponseById(long id){
        TypedQuery<ParticipantResponse> query = entityManager.createNamedQuery("ParticipantResponse.getParticipantResponseById", ParticipantResponse.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

//    public ParticipantResponse getParticipantResponseByTournamentAndParticipantName(long tournamentId, String participantName){
//        
//    }
    
    
}
