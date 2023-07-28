/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.db.entity.MatchInvite;
import com.ltlogic.db.entity.TournamentInvite;
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
public class TournamentInviteRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void persistInvite(TournamentInvite t) {
        entityManager.persist(t);
    }

    public TournamentInvite findByPk(long pk) {
        return entityManager.find(TournamentInvite.class, pk);
    }

    public List<TournamentInvite> findAllTournamentInvitesByUserPk(long pk) {
        TypedQuery<TournamentInvite> query = entityManager.createNamedQuery("TournamentInvite.getAllTournamentInvitesForUser", TournamentInvite.class);
        query.setParameter("pk", pk);
        return query.getResultList();
    }

    public List<TournamentInvite> findAllTournamentInvitesByTournamentTeamPkNotCancelled(long pk) {
        TypedQuery<TournamentInvite> query = entityManager.createNamedQuery("TournamentInvite.getAllTournamentInvitesForTournamentTeamNotCancelled", TournamentInvite.class);
        query.setParameter("pk", pk);
        return query.getResultList();
    }

    public List<TournamentInvite> findAllTournamentInvitesByTournamentPk(long pk) {
        TypedQuery<TournamentInvite> query = entityManager.createNamedQuery("TournamentInvite.getAllTournamentInvitesForTournament", TournamentInvite.class);
        query.setParameter("pk", pk);
        return query.getResultList();
    }

    public List<TournamentInvite> findAllTournamentInvitesByTournamentPkAndUserPk(long tournamentPk, long userPk) {
        TypedQuery<TournamentInvite> query = entityManager.createNamedQuery("TournamentInvite.getAllTournamentInvitesByTournamentPkAndUserPk", TournamentInvite.class);
        query.setParameter("tournamentPk", tournamentPk);
        query.setParameter("userPk", userPk);
        return query.getResultList();
    }

    public TournamentInvite findTournamentInviteByTournamentTeamPkAndUserPk(long tournamentTeamPk, long userPk) {
        TypedQuery<TournamentInvite> query = entityManager.createNamedQuery("TournamentInvite.getTournamentInviteByTournamentTeamPkAndUserPk", TournamentInvite.class);
        query.setParameter("tournamentTeamPk", tournamentTeamPk);
        query.setParameter("userPk", userPk);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public List<TournamentInvite> findAllTournamentInvitesByStatusForUser(long pk, InvitesEnum inviteEnum) {
        TypedQuery<TournamentInvite> query = entityManager.createNamedQuery("TournamentInvite.getAllTournamentInvitesByStatusForUser", TournamentInvite.class);
        query.setParameter("pk", pk);
        query.setParameter("inviteEnum", inviteEnum);
        return query.getResultList();
    }

    public TournamentInvite findTournamentInviteForUserInTournamentTeamByStatus(long tournamentTeamPk, long userPk, InvitesEnum inviteEnum) {
        TypedQuery<TournamentInvite> query = entityManager.createNamedQuery("TournamentInvite.getTournamentInviteForUserInTournamentTeamByStatus", TournamentInvite.class);
        query.setParameter("tournamentTeamPk", tournamentTeamPk);
        query.setParameter("userPk", userPk);
        query.setParameter("inviteEnum", inviteEnum);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
}
