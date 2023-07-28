/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.constants.TournamentStatusEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentMaps;
import java.util.ArrayList;
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
public class TournamentRepository {

    private final static long UPCOMING_TOURNAMENT_NOTIFICATION_TIME_IN_MINUTES = 10;

    private final static int ITEMS_PER_PAGE = 15;

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void persistTournament(Tournament t) {
        entityManager.persist(t);
    }

    public void persistTournamentMaps(TournamentMaps t) {
        entityManager.persist(t);
    }

    public Tournament findByPk(long pk) {
        return entityManager.find(Tournament.class, pk);
    }

    public List<Tournament> getAllTournaments() {
        TypedQuery<Tournament> query = entityManager.createNamedQuery("Tournament.getAllTournaments", Tournament.class);
        return query.getResultList();
    }

    public List<Tournament> getAllTournamentsByStatus(TournamentStatusEnum tournamentStatus) {
        TypedQuery<Tournament> query = entityManager.createNamedQuery("Tournament.getAllTournamentsByStatus", Tournament.class);
        query.setParameter("tournamentStatus", tournamentStatus);
        if (tournamentStatus == TournamentStatusEnum.ENDED) {
            return query.setMaxResults(15).getResultList();
        }
        return query.getResultList();
    }

    public List<Tournament> getAllTournamentsByGameAndPlatform(GameEnum gameEnum, PlatformEnum platformEnum) {
        String sqlQuery = "SELECT t FROM Tournament t WHERE t.tournamentInfo.tournamentStatus = :tournamentStatus ";
        if (gameEnum != null) {
            sqlQuery += "AND t.tournamentInfo.gameEnum = :gameEnum";
        }
        if (platformEnum != null) {
            sqlQuery += " AND t.tournamentInfo.platform = :platform";
        }

        sqlQuery += " ORDER BY t.tournamentInfo.scheduledTournamentTime ASC";

        TypedQuery<Tournament> query = entityManager.createQuery(sqlQuery, Tournament.class);

        query.setParameter("tournamentStatus", TournamentStatusEnum.PENDING);
        if (gameEnum != null) {
            query.setParameter("gameEnum", gameEnum);
        }
        if (platformEnum != null) {
            query.setParameter("platform", platformEnum);
        }
        return query.getResultList();
    }

    public List<Tournament> getAllTournamentsByStatusAndPlatform(TournamentStatusEnum tournamentStatus, PlatformEnum platformEnum) {
        TypedQuery<Tournament> query = entityManager.createNamedQuery("Tournament.getAllTournamentsByStatusAndPlatform", Tournament.class);
        query.setParameter("tournamentStatus", tournamentStatus);
        query.setParameter("platformEnum", platformEnum);
        if (tournamentStatus == TournamentStatusEnum.ENDED) {
            return query.setMaxResults(15).getResultList();
        }
        return query.getResultList();
    }

    public Tournament findTournamentByTournamentId(int tournamentId) {
        TypedQuery<Tournament> query = entityManager.createNamedQuery("Tournament.getTournamentByTournamentId", Tournament.class);
        query.setParameter("tournamentId", tournamentId);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public List<Tournament> getAllTournamentsByGameAndPlatformAndTypeAndSize(GameEnum gameEnum, PlatformEnum platformEnum, TeamSizeEnum teamSizeEnum, TeamTypeEnum teamTypeEnum, TournamentStatusEnum tournamentStatus) {
        TypedQuery<Tournament> query = entityManager.createNamedQuery("Tournament.getAllTournamentByGameAndPlatformAndTypeAndSizeAndStatus", Tournament.class);
        query.setParameter("gameEnum", gameEnum);
        query.setParameter("platformEnum", platformEnum);
        query.setParameter("teamSizeEnum", teamSizeEnum);
        query.setParameter("teamTypeEnum", teamTypeEnum);
        query.setParameter("tournamentStatus", tournamentStatus);
        return query.getResultList();
    }

    public List<Tournament> getTournamentsByUserPk(long userPk) {
        TypedQuery<Tournament> query = entityManager.createNamedQuery("Tournament.getTournamentsByUserPk", Tournament.class);
        query.setParameter("userPk", userPk);
        return query.getResultList();
    }

    public List<Tournament> getTournamentsByTeamPkForPaginate(long teamPk, int pageNumber) {
        TypedQuery<Tournament> query = entityManager.createNamedQuery("Tournament.getTournamentsByTeamPk", Tournament.class);
        query.setParameter("teamPk", teamPk);
        pageNumber = pageNumber * ITEMS_PER_PAGE;

        return query.setFirstResult(pageNumber).setMaxResults(ITEMS_PER_PAGE).getResultList();
    }

    public Integer getTournamentsByTeamPk(long teamPk) {
        TypedQuery<Tournament> query = entityManager.createNamedQuery("Tournament.getTournamentsByTeamPk", Tournament.class);
        query.setParameter("teamPk", teamPk);

        return query.getResultList().size();
    }

    public List<Tournament> getTournamentsByUserPkForPaginate(long userPk, int pageNumber) {
        TypedQuery<Tournament> query = entityManager.createNamedQuery("Tournament.getTournamentsByUserPk", Tournament.class);
        query.setParameter("userPk", userPk);
        pageNumber = pageNumber * ITEMS_PER_PAGE;

        return query.setFirstResult(pageNumber).setMaxResults(ITEMS_PER_PAGE).getResultList();
    }

    public List<Tournament> getTournamentsByTeamPkAndStatus(long teamPk, TournamentStatusEnum status) {
        TypedQuery<Tournament> query = entityManager.createNamedQuery("Tournament.getTournamentsByTeamPkAndStatus", Tournament.class);
        query.setParameter("teamPk", teamPk);
        query.setParameter("status", teamPk);
        return query.getResultList();
    }

    public List<Tournament> getAllPendingAndActiveTournamentsForTeam(long teamPk) {
        TypedQuery<Tournament> query = entityManager.createNamedQuery("Tournament.getAllPendingAndActiveTournamentsForTeam", Tournament.class);
        query.setParameter("teamPk", teamPk);
        query.setParameter("activeStatus", TournamentStatusEnum.ACTIVE);
        query.setParameter("pendingStatus", TournamentStatusEnum.PENDING);
        return query.getResultList();
    }

    public List<Tournament> getAllPendingAndActiveTournamentsForUser(long userPk) {
        TypedQuery<Tournament> query = entityManager.createNamedQuery("Tournament.getAllPendingAndActiveTournamentsForUser", Tournament.class);
        query.setParameter("userPk", userPk);
        query.setParameter("activeStatus", TournamentStatusEnum.ACTIVE);
        query.setParameter("pendingStatus", TournamentStatusEnum.PENDING);
        return query.getResultList();
    }

    public Tournament findTournamentByTournamentIdAndUserPk(int tournamentId, long userPk) {
        TypedQuery<Tournament> query = entityManager.createNamedQuery("Tournament.getTournamentByTournamentIdAndUserPk", Tournament.class);
        query.setParameter("tournamentId", tournamentId);
        query.setParameter("userPk", userPk);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public List<Tournament> findAllTournamentsToStart() {
        TypedQuery<Tournament> query = entityManager.createNamedQuery(Tournament.QUERY_GET_ALL_TOURNAMENTS_TO_START, Tournament.class);
        query.setParameter(Tournament.PARAM_TOURNAMENT_STATUS_TO_START, TournamentStatusEnum.PENDING);
        query.setParameter(Tournament.PARAM_CURRENT_TIME, DateTimeUtil.getDefaultLocalDateTimeNow());
        List<Tournament> tournamentsToStart = query.getResultList();
        if (tournamentsToStart == null) {
            tournamentsToStart = new ArrayList<>();
        }
        return tournamentsToStart;
    }

    public List<Tournament> findAllUpcomingTournamentsToNotify() {
        TypedQuery<Tournament> query = entityManager.createNamedQuery(Tournament.QUERY_GET_ALL_UPCOMING_TOURNAMENTS_TO_NOTIFY, Tournament.class);
        query.setParameter(Tournament.PARAM_TOURNAMENT_STATUS_TO_START, TournamentStatusEnum.PENDING);
        query.setParameter(Tournament.PARAM_CURRENT_TIME_PLUS_NOTIFICATION_TIME, DateTimeUtil.getDefaultLocalDateTimeNow().plusMinutes(UPCOMING_TOURNAMENT_NOTIFICATION_TIME_IN_MINUTES));
        List<Tournament> tournamentsToStart = query.getResultList();
        if (tournamentsToStart == null) {
            tournamentsToStart = new ArrayList<>();
        }
        return tournamentsToStart;
    }
}
