package com.ltlogic.db.repository;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.iw.IWMatch;
import com.ltlogic.db.entity.mwr.MWRMatch;
import com.ltlogic.db.entity.ww2.WW2Match;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class MatchRepository {

    private static final Logger LOG = LoggerFactory.getLogger(MatchRepository.class);

    private final static long UPCOMING_MATCH_NOTIFICATION_TIME_IN_MINUTES = 10;

    private final static int ITEMS_PER_PAGE = 15;

    @PersistenceContext
    private EntityManager entityManager;

    public void persistMatch(Match c) {
        entityManager.persist(c);
    }

    public Match findMatchByPk(long pk) {
        return entityManager.find(Match.class, pk);
    }

    public MWRMatch getMWRMatchByMatchPk(long matchPk) {
        TypedQuery<MWRMatch> query = entityManager.createNamedQuery("MWRMatch.getMWRMatchByMatchPk", MWRMatch.class);
        query.setParameter("matchPk", matchPk);

        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public IWMatch getIWMatchByMatchPk(long matchPk) {
        TypedQuery<IWMatch> query = entityManager.createNamedQuery("IWMatch.getIWMatchByMatchPk", IWMatch.class);
        query.setParameter("matchPk", matchPk);

        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public WW2Match getWW2MatchByMatchPk(long matchPk) {
        TypedQuery<WW2Match> query = entityManager.createNamedQuery("WW2Match.getWW2MatchByMatchPk", WW2Match.class);
        query.setParameter("matchPk", matchPk);

        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public List<Match> getAllMatchesPublic(int pageNumber) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.getAllMatchesPublic", Match.class);
        query.setParameter("isMatchPublic", true);
        query.setParameter("matchStatus", MatchStatusEnum.PENDING);
        pageNumber = pageNumber * ITEMS_PER_PAGE;

        return query.setFirstResult(pageNumber).setMaxResults(ITEMS_PER_PAGE).getResultList();
    }

    public Match findMatchByMatchId(int matchId) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.getMatchByMatchId", Match.class);
        query.setParameter("matchId", matchId);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    //Match.findMatchesByStatusInTournament
    public List<Match> findMatchesByStatusInTournament(MatchStatusEnum matchStatus, long tournamentPk) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.findMatchesByStatusInTournament", Match.class);
        query.setParameter("matchStatus", matchStatus);
        query.setParameter("tournamentPk", tournamentPk);
        return query.getResultList();
    }

    public List<Match> findMatchesByUserPk(long userPk) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.getMatchesByUserPk", Match.class);
        query.setParameter("userPk", userPk);
        return query.getResultList();
    }

    public List<Match> findMatchesByUserPkForPaginate(long userPk, int pageNumber) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.getMatchesByUserPk", Match.class);
        query.setParameter("userPk", userPk);
        pageNumber = pageNumber * ITEMS_PER_PAGE;

        return query.setFirstResult(pageNumber).setMaxResults(ITEMS_PER_PAGE).getResultList();
    }

    public List<Match> findMatchesByUserPkAndMatchStatus(long userPk, MatchStatusEnum matchStatusEnum, int pageNumber) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.findMatchesByUserPkAndMatchStatus", Match.class);
        query.setParameter("userPk", userPk);
        query.setParameter("matchStatusEnum", matchStatusEnum);
        pageNumber = pageNumber * ITEMS_PER_PAGE;
        return query.setFirstResult(pageNumber).setMaxResults(ITEMS_PER_PAGE).getResultList();
    }

    public List<Match> findMatchesByTeamPk(long teamPk) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.getMatchesByTeamPk", Match.class);
        query.setParameter("teamPk", teamPk);
        return query.getResultList();
    }
    
    public List<Match> findMatchesByTeamPkInActiveDisputedAndEndedStatus(long teamPk) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.findMatchesByTeamPkInActiveDisputedAndEndedStatus", Match.class);
        query.setParameter("teamPk", teamPk);
        return query.getResultList();
    }

    public List<Match> findMatchesByTeamPkForPaginate(long teamPk, int pageNumber) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.getMatchesByTeamPk", Match.class);
        query.setParameter("teamPk", teamPk);
        pageNumber = pageNumber * ITEMS_PER_PAGE;
        return query.setFirstResult(pageNumber).setMaxResults(ITEMS_PER_PAGE).getResultList();
    }

    public List<Match> getMatchesByTeamPkAndMatchNotKilledStatus(long teamPk) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.getMatchesByTeamPkAndMatchPendingAndWaitingAndActiveStatus", Match.class);
        query.setParameter("teamPk", teamPk);
        query.setParameter("pendingStatus", MatchStatusEnum.PENDING);
        query.setParameter("readyToPlayStatus", MatchStatusEnum.READY_TO_PLAY);
        query.setParameter("activeStatus", MatchStatusEnum.ACTIVE);
        query.setParameter("firstAcceptStatus", MatchStatusEnum.WAITING_ON_FIRST_ACCEPT);
        query.setParameter("secondAcceptStatus", MatchStatusEnum.WAITING_ON_SECOND_ACCEPT);
        return query.getResultList();
    }

    public List<Match> getMatchesByUserPkAndMatchNotKilledStatus(long userPk) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.getMatchesByUserPkAndMatchPendingAndWaitingAndActiveStatus", Match.class);
        query.setParameter("userPk", userPk);
        query.setParameter("pendingStatus", MatchStatusEnum.PENDING);
        query.setParameter("readyToPlayStatus", MatchStatusEnum.READY_TO_PLAY);
        query.setParameter("activeStatus", MatchStatusEnum.ACTIVE);
        query.setParameter("firstAcceptStatus", MatchStatusEnum.WAITING_ON_FIRST_ACCEPT);
        query.setParameter("secondAcceptStatus", MatchStatusEnum.WAITING_ON_SECOND_ACCEPT);
        return query.getResultList();
    }

    public List<Match> getXpMatchesByTeamPkAndActiveOrEndedOrDisputedStatus(long teamPk) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.getXpMatchesByTeamPkAndActiveOrEndedOrDisputedStatus", Match.class);
        query.setParameter("teamPk", teamPk);
        query.setMaxResults(2);
        return query.getResultList();
    }

    public List<Match> findAllMatchesToStart() {
        TypedQuery<Match> query = entityManager.createNamedQuery(Match.QUERY_GET_ALL_MATCHES_TO_START, Match.class);
        query.setParameter(Match.PARAM_MATCH_STATUS_TO_START, MatchStatusEnum.READY_TO_PLAY);
        query.setParameter(Match.PARAM_CURRENT_TIME, DateTimeUtil.getDefaultLocalDateTimeNow());
        List<Match> matchesToStart = query.getResultList();
        if (matchesToStart == null) {
            return new ArrayList<>();
        }
        return matchesToStart;
    }

    public List<Match> findAllMatchesToKill() {
        TypedQuery<Match> query = entityManager.createNamedQuery(Match.QUERY_GET_ALL_MATCHES_TO_KILL, Match.class);
        List<MatchStatusEnum> matchesToKillStatus = Arrays.asList(MatchStatusEnum.WAITING_ON_FIRST_ACCEPT, MatchStatusEnum.PENDING, MatchStatusEnum.WAITING_ON_SECOND_ACCEPT);
        query.setParameter(Match.PARAM_LIST_OF_MATCH_STATUSES_TO_KILL, matchesToKillStatus);
        query.setParameter(Match.PARAM_CURRENT_TIME, DateTimeUtil.getDefaultLocalDateTimeNow());
        List<Match> matchesToKill = query.getResultList();
        if (matchesToKill == null) {
            return new ArrayList<>();
        }
        return matchesToKill;
    }

    public List<Match> findAllUpcomingMatchesToNotify() {
        TypedQuery<Match> query = entityManager.createNamedQuery(Match.QUERY_GET_ALL_UPCOMING_MATCHES_TO_NOTIFY, Match.class);
        query.setParameter(Match.PARAM_MATCH_STATUS_TO_START, MatchStatusEnum.READY_TO_PLAY);
        query.setParameter(Match.PARAM_CURRENT_TIME_PLUS_NOTIFICATION_TIME, DateTimeUtil.getDefaultLocalDateTimeNow().plusMinutes(UPCOMING_MATCH_NOTIFICATION_TIME_IN_MINUTES));
        List<Match> upcomingMatchesToNotify = query.getResultList();
        if (upcomingMatchesToNotify == null) {
            return new ArrayList<>();
        }
        return upcomingMatchesToNotify;
    }

    public List<Match> findAllPublicWagerMatchesToKill() {
        TypedQuery<Match> query = entityManager.createNamedQuery(Match.QUERY_GET_ALL_PUBLIC_WAGER_MATCHES_TO_KILL, Match.class);
        query.setParameter("currentTimeMinus15Minutes", DateTimeUtil.getDefaultLocalDateTimeNow().minusMinutes(15));
        List<Match> matchesToKill = query.getResultList();
        if (matchesToKill == null) {
            return new ArrayList<>();
        }
        return matchesToKill;
    }
    
    public List<Match> findAllActiveXpAndWagerMatchesToCancel() {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.findAllActiveXpAndWagerMatchesToCancel", Match.class);
        query.setParameter("currentTimeMinus3Hours", DateTimeUtil.getDefaultLocalDateTimeNow().minusHours(3));
        return query.getResultList();
    }
    
    public List<Match> findAllActiveXpAndWagerMatchesToEnd() {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.findAllActiveXpAndWagerMatchesToEnd", Match.class);
        query.setParameter("currentTimeMinus3Hours", DateTimeUtil.getDefaultLocalDateTimeNow().minusHours(3));
        return query.getResultList();
    }

    public Match findUserMatchByMatchPKAndUsername(Long matchPk, String username) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.findUserMatchByMatchPKAndUsername", Match.class);
        query.setParameter("matchPk", matchPk);
        query.setParameter("username", username);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public List<Match> getMatchesByGame(GameEnum gameEnum, int pageNumber) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.getMatchesByGame", Match.class);
        query.setParameter("isMatchPublic", true);
        query.setParameter("matchStatus", MatchStatusEnum.PENDING);
        query.setParameter("gameEnum", gameEnum);
        pageNumber = pageNumber * ITEMS_PER_PAGE;

        return query.setFirstResult(pageNumber).setMaxResults(ITEMS_PER_PAGE).getResultList();
    }

    public List<Match> getMatchesByGameAndPlatform(GameEnum gameEnum, PlatformEnum platformEnum) {

        String sqlQuery = "SELECT m FROM Match m WHERE m.matchInfo.isMatchPublic = :isMatchPublic AND m.matchInfo.matchStatus = :matchStatus ";
        if (gameEnum != null) {
            sqlQuery += "AND m.matchInfo.gameEnum = :gameEnum";
        }
        if (platformEnum != null) {
            sqlQuery += " AND m.matchInfo.platform = :platform";
        }

        sqlQuery += " order by m.matchInfo.scheduledMatchTime asc";

        TypedQuery<Match> query = entityManager.createQuery(sqlQuery, Match.class);

        query.setParameter("isMatchPublic", true);
        query.setParameter("matchStatus", MatchStatusEnum.PENDING);
        if (gameEnum != null) {
            query.setParameter("gameEnum", gameEnum);
        }
        if (platformEnum != null) {
            query.setParameter("platform", platformEnum);
        }
        return query.getResultList();
    }

    public List<Match> getMatchesByPlatform(PlatformEnum platformEnum, int pageNumber) {
        TypedQuery<Match> query = entityManager.createNamedQuery("Match.getMatchesByPlatform", Match.class);
        query.setParameter("isMatchPublic", true);
        query.setParameter("matchStatus", MatchStatusEnum.PENDING);
        query.setParameter("platformEnum", platformEnum);
        pageNumber = pageNumber * ITEMS_PER_PAGE;

        return query.setFirstResult(pageNumber).setMaxResults(ITEMS_PER_PAGE).getResultList();
    }

}
