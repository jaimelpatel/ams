/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.db.entity.MatchInvite;
import com.ltlogic.db.entity.Team;
import java.util.List;
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
public class MatchInviteRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void persistInvite(MatchInvite m) {
        entityManager.persist(m);
    }

    public MatchInvite findByPk(long pk) {
        return entityManager.find(MatchInvite.class, pk);
    }

    public List<MatchInvite> findAllMatchInvitesByUserPk(long pk) {
        TypedQuery<MatchInvite> query = entityManager.createNamedQuery("MatchInvite.getAllMatchInvitesForUser", MatchInvite.class);
        query.setParameter("pk", pk);
        return query.getResultList();
    }

    public List<MatchInvite> findAllMatchInvitesByTeamPk(long pk) {
        TypedQuery<MatchInvite> query = entityManager.createNamedQuery("MatchInvite.getAllMatchInvitesForTeam", MatchInvite.class);
        query.setParameter("pk", pk);
        return query.getResultList();
    }

    public List<MatchInvite> findAllMatchInvitesByMatchPk(long pk) {
        TypedQuery<MatchInvite> query = entityManager.createNamedQuery("MatchInvite.getAllMatchInvitesForMatch", MatchInvite.class);
        query.setParameter("pk", pk);
        return query.getResultList();
    }

    public List<MatchInvite> findAllMatchInvitesByStatusForUser(long pk, InvitesEnum inviteEnum) {
        TypedQuery<MatchInvite> query = entityManager.createNamedQuery("MatchInvite.getAllMatchInvitesByStatusForUser", MatchInvite.class);
        query.setParameter("pk", pk);
        query.setParameter("inviteEnum", inviteEnum);
        return query.getResultList();
    }
    
     public MatchInvite findMatchInviteForUserInMatch(long matchPk, long userPk) {
        TypedQuery<MatchInvite> query = entityManager.createNamedQuery("MatchInvite.getMatchInviteForUserInMatch", MatchInvite.class);
        query.setParameter("matchPk", matchPk);
        query.setParameter("userPk", userPk);
        List<MatchInvite> matchInvites =  query.getResultList();
        if(query.getResultList().size() > 0){
            return query.getResultList().get(0);
        }
        return null;

    }
    
}
