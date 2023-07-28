/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.constants.TournamentStatusEnum;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentInvite;
import com.ltlogic.db.entity.TournamentTeam;
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
public class TournamentTeamRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void persistTournamentTeam(TournamentTeam t) {
        entityManager.persist(t);
    }

    public TournamentTeam findByPk(long pk) {
        return entityManager.find(TournamentTeam.class, pk);
    }

    public List<TournamentTeam> getAllTournamentTeamsByTeam(long pk) {
        TypedQuery<TournamentTeam> query = entityManager.createNamedQuery("TournamentTeam.getAllTournamentTeamsForTeam", TournamentTeam.class);
        query.setParameter("pk", pk);
        return query.getResultList();
    }
    
    public List<TournamentTeam> getAllEligibleTournamentTeamsForTournament(long pk) {
        TypedQuery<TournamentTeam> query = entityManager.createNamedQuery("TournamentTeam.getAllEligibleTournamentTeamsForTournament", TournamentTeam.class);
        query.setParameter("pk", pk);
        return query.getResultList();
    }
    
    public List<TournamentTeam> getAllTournamentTeamsForTournament(long pk) {
        TypedQuery<TournamentTeam> query = entityManager.createNamedQuery("TournamentTeam.getAllTournamentTeamsForTournament", TournamentTeam.class);
        query.setParameter("pk", pk);
        return query.getResultList();
    }
    
    public TournamentTeam getAllTournamentTeamsForTeamByTournamentPendingStatus(long teamPk) {
        TypedQuery<TournamentTeam> query = entityManager.createNamedQuery("TournamentTeam.getAllTournamentTeamsForTeamByTournamentPendingStatus", TournamentTeam.class);
        query.setParameter("teamPk", teamPk);
        query.setParameter("pendingStatus", TournamentStatusEnum.PENDING);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
    public TournamentTeam getAllTournamentTeamsForTeamByTournamentActiveStatus(long teamPk) {
        TypedQuery<TournamentTeam> query = entityManager.createNamedQuery("TournamentTeam.getAllTournamentTeamsForTeamByTournamentActiveStatus", TournamentTeam.class);
        query.setParameter("teamPk", teamPk);
        query.setParameter("activeStatus", TournamentStatusEnum.ACTIVE);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
    
    public TournamentTeam getTournamentTeamByNameAndTournament(String teamName, Tournament tournament){
        TypedQuery<TournamentTeam> query = entityManager.createNamedQuery("TournamentTeam.getTournamentTeamByNameAndTournament", TournamentTeam.class);
        query.setParameter("teamName", teamName);
        query.setParameter("tournament", tournament);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
    
//    public TournamentTeam getTournamentTeamByTournamentPkAndUserPk(long tournamentPk, long userPk){
//        TypedQuery<TournamentTeam> query = entityManager.createNamedQuery("TournamentTeam.findTournamentTeamByTournamentPkAndUserPk", TournamentTeam.class);
//        query.setParameter("tournamentPk", tournamentPk);
//        query.setParameter("userPk", userPk);
//        try {
//            return query.getSingleResult();
//        } catch (NoResultException | NonUniqueResultException ex) {
//            return null;
//        }
//    }
}
