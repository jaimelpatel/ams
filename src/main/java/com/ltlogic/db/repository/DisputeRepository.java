/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.db.entity.Dispute;
import java.util.List;
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
public class DisputeRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    private final static int ITEMS_PER_PAGE = 15;

    public void persistDispute(Dispute d) {
        entityManager.persist(d);
    }

    public Dispute findDisputeByPk(long pk) {
        return entityManager.find(Dispute.class, pk);
    }
    
    public List<Dispute> getAllMatchDisputesByMatchPk(long matchPk) {
        TypedQuery<Dispute> query = entityManager.createNamedQuery("Dispute.getAllMatchDisputesByMatchPk", Dispute.class);
        query.setParameter("matchPk", matchPk);

        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            return null;
        }

    }
    
    public List<Dispute> getAllOpenMatchDisputesByMatchType(MatchTypeEnum matchType) {
        TypedQuery<Dispute> query = entityManager.createNamedQuery("Dispute.getAllOpenMatchDisputesByMatchType", Dispute.class);
        query.setParameter("matchType", matchType);
        return query.getResultList();
    }
 
    public Dispute findDisputeByDisputeId(int disputeId) {
        TypedQuery<Dispute> query = entityManager.createNamedQuery("Dispute.getDisputeByDisputeId", Dispute.class);
        query.setParameter("disputeId", disputeId);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public List<Dispute> findAllDisputesForAUserPk(long userPk) {
        TypedQuery<Dispute> query = entityManager.createNamedQuery("Dispute.getAllDisputesForAUser", Dispute.class);
        query.setParameter("userPk", userPk);
        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public Dispute findDisputeByUserAndMatchPk(long userPk, long matchPk) {
        TypedQuery<Dispute> query = entityManager.createNamedQuery("Dispute.getDisputeByMatchAndUserPk", Dispute.class);
        query.setParameter("userPk", userPk);
        query.setParameter("matchPk", matchPk);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
    
    public Dispute findDisputeForMatchAndTeam(long matchPk, long teamPk){
        TypedQuery<Dispute> query = entityManager.createNamedQuery("Dispute.getDisputeByMatchAndTeam", Dispute.class);
        query.setParameter("teamPk", teamPk);
        query.setParameter("matchPk", matchPk);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
    
    public int getTotalOpenMatchDisputesByUserPk(long userPk) {
        TypedQuery<Dispute> query = entityManager.createNamedQuery("Dispute.getAllOpenMatchDisputesByUserPk", Dispute.class);
        query.setParameter("userPk", userPk);
        
        return query.getResultList().size();
    }
    
    public List<Dispute> getAllOpenMatchDisputesByUserPkAndPageNumber(long userPk, int pageNumber) {
        TypedQuery<Dispute> query = entityManager.createNamedQuery("Dispute.getAllOpenMatchDisputesByUserPk", Dispute.class);
        query.setParameter("userPk", userPk);
        pageNumber = pageNumber * ITEMS_PER_PAGE;
        
        return query.setFirstResult(pageNumber).setMaxResults(ITEMS_PER_PAGE).getResultList();
    }

}
