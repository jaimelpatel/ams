/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository.challonge;

import com.ltlogic.db.entity.Bank;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.challonge.TournamentResponse;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jaimel
 */
@Repository
public class ChallongeTournamentRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void persistChallongeTournament(TournamentResponse t) {
        entityManager.persist(t);
    }
    
    public void updateChallongeTournament(TournamentResponse t){
        entityManager.merge(t);
    }
    
    public TournamentResponse getTournamentResponseById(long id){
        TypedQuery<TournamentResponse> query = entityManager.createNamedQuery("TournamentResponse.getTournamentResponseByChallongeTournamentId", TournamentResponse.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
}
