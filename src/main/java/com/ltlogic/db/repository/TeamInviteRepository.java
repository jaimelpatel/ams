/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.db.entity.TeamInvite;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jaimel
 */
@Repository
public class TeamInviteRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final static int ITEMS_PER_PAGE = 10;

    public void persistInvite(TeamInvite c) {
        entityManager.persist(c);
    }

    public TeamInvite findByPk(long pk) {
        return entityManager.find(TeamInvite.class, pk);
    }

    public List<TeamInvite> findPendingReceivedInvitesByUserPk(long receiverPk, int pageNumber) {
        TypedQuery<TeamInvite> query = entityManager.createNamedQuery("TeamInvite.getPendingReceivedInvitesByUserPk", TeamInvite.class);
        query.setParameter("receiverPk", receiverPk);
        pageNumber = pageNumber * ITEMS_PER_PAGE;

        return query.setFirstResult(pageNumber).setMaxResults(ITEMS_PER_PAGE).getResultList();

    }
    
    public Integer getTotalPendingInvitesByUserPk(long receiverPk) {
        TypedQuery<TeamInvite> query = entityManager.createNamedQuery("TeamInvite.getPendingReceivedInvitesByUserPk", TeamInvite.class);
        query.setParameter("receiverPk", receiverPk);

        return query.getResultList().size();

    }

    public TeamInvite getPendingReceivedInvitesByUserPkAndTeamPk(long receiverPk, long teamPk) {
        TypedQuery<TeamInvite> query = entityManager.createNamedQuery("TeamInvite.getPendingReceivedInvitesByUserPkAndTeamPk", TeamInvite.class);
        query.setParameter("receiverPk", receiverPk);
        query.setParameter("teamPk", teamPk);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }

    }
}
