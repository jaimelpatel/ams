package com.ltlogic.db.repository;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamStatusEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.CloudMedia;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.TeamPermissions;
import com.ltlogic.db.entity.iw.IWTeam;
import com.ltlogic.db.entity.mwr.MWRTeam;
import com.ltlogic.db.entity.ww2.WW2Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class TeamRepository {

    private static final Logger LOG = LoggerFactory.getLogger(TeamRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    public void persistTeam(Team c) {
        entityManager.persist(c);
    }

    public void persistTeamPermissions(TeamPermissions tp) {
        entityManager.persist(tp);
    }

    public Team findTeamByPk(long pk) {
        return entityManager.find(Team.class, pk);
    }

    public MWRTeam findMWRTeamByTeamPk(long teamPk) {
        TypedQuery<MWRTeam> query = entityManager.createNamedQuery("MWRTeam.getMWRTeamByTeamPk", MWRTeam.class);
        query.setParameter("teamPk", teamPk);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public IWTeam findIWTeamByTeamPk(long teamPk) {
        TypedQuery<IWTeam> query = entityManager.createNamedQuery("IWTeam.getIWTeamByTeamPk", IWTeam.class);
        query.setParameter("teamPk", teamPk);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public WW2Team findWW2TeamByTeamPk(long teamPk) {
        TypedQuery<WW2Team> query = entityManager.createNamedQuery("WW2Team.getWW2TeamByTeamPk", WW2Team.class);
        query.setParameter("teamPk", teamPk);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public Team findTeamByName(String name) {
        TypedQuery<Team> query = entityManager.createNamedQuery("Team.getTeamByName", Team.class);
        query.setParameter("name", name);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public Team findTeamByTeamId(int teamId) {
        TypedQuery<Team> query = entityManager.createNamedQuery("Team.getTeamByTeamId", Team.class);
        query.setParameter("teamId", teamId);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public List<Team> findTeamsByUsername(String username) {
        TypedQuery<Team> query = entityManager.createNamedQuery("Team.getTeamsByUsername", Team.class);
        query.setParameter("username", username);
        return query.getResultList();
    }

    public List<Team> findTeamsByUsernameAndTeamType(String username, TeamTypeEnum tte) {
        TypedQuery<Team> query = entityManager.createNamedQuery("Team.getTeamsByUsernameAndTeamType", Team.class);
        query.setParameter("username", username);
        query.setParameter("teamType", tte);
        return query.getResultList();
    }

    public Team findTeamByNameAndGameAndPlatformAndTypeAndSizeAndStatus(String teamName, GameEnum gameEnum, PlatformEnum platformEnum, TeamSizeEnum teamSizeEnum, TeamTypeEnum teamTypeEnum, RegionEnum regionEnum, TeamStatusEnum teamStatusEnum) {
        TypedQuery<Team> query = entityManager.createNamedQuery("Team.getTeamByNameAndGameAndPlatformAndTypeAndSizeAndStatus", Team.class);
        query.setParameter("teamName", teamName);
        query.setParameter("gameEnum", gameEnum);
        query.setParameter("platformEnum", platformEnum);
        query.setParameter("teamSizeEnum", teamSizeEnum);
        query.setParameter("teamTypeEnum", teamTypeEnum);
        query.setParameter("regionEnum", regionEnum);
        query.setParameter("teamStatusEnum", teamStatusEnum);

        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public Long findCountOfTeamsByRegionAndGameAndPlatformAndTypeAndSize(RegionEnum regionEnum, GameEnum gameEnum, PlatformEnum platformEnum, TeamSizeEnum teamSizeEnum, TeamTypeEnum teamTypeEnum) {
        Query query = entityManager.createQuery("SELECT count(*) FROM Team t WHERE t.teamPojo.region ='" + regionEnum + "' AND t.teamPojo.game ='" + gameEnum + "' AND t.teamPojo.platform ='" + platformEnum + "' AND t.teamPojo.teamSize ='" + teamSizeEnum + "' AND t.teamPojo.teamType ='" + teamTypeEnum + "'");
        Long count = (Long) query.getSingleResult();
        return count;

    }

    public List<Team> findTeamByUsernameAndGameAndPlatformAndTypeAndSize(String username, GameEnum gameEnum, PlatformEnum platformEnum, TeamSizeEnum teamSizeEnum, TeamTypeEnum teamTypeEnum, RegionEnum regionEnum) {
        TypedQuery<Team> query = entityManager.createNamedQuery("Team.getTeamByUsernameAndGameAndPlatformAndTypeAndSize", Team.class);
        query.setParameter("username", username);
        query.setParameter("gameEnum", gameEnum);
        query.setParameter("platformEnum", platformEnum);
        query.setParameter("teamSizeEnum", teamSizeEnum);
        query.setParameter("teamTypeEnum", teamTypeEnum);
        query.setParameter("regionEnum", regionEnum);
        query.setParameter("teamStatusEnum", TeamStatusEnum.LIVE);
        return query.getResultList();
    }

    public TeamPermissions findTeamPermissionsByUserPkAndTeamPk(long userPk, long teamPk) {
        TypedQuery<TeamPermissions> query = entityManager.createNamedQuery("TeamPermissions.getTeamPermissionsByUserPkAndTeamPk", TeamPermissions.class);
        query.setParameter("userPk", userPk);
        query.setParameter("teamPk", teamPk);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public List<Team> findTeamsByMatchPk(long matchPk) {
        TypedQuery<Team> query = entityManager.createNamedQuery("Team.getTeamsByMatchPk", Team.class);
        query.setParameter("matchPk", matchPk);
        return query.getResultList();
    }

    public MWRTeam getMWRTeamByTeamPk(long teamPk) {
        TypedQuery<MWRTeam> query = entityManager.createNamedQuery("MWRTeam.getMWRTeamByTeamPk", MWRTeam.class);
        query.setParameter("teamPk", teamPk);
        return query.getSingleResult();

    }

    public IWTeam getIWTeamByTeamPk(long teamPk) {
        TypedQuery<IWTeam> query = entityManager.createNamedQuery("IWTeam.getIWTeamByTeamPk", IWTeam.class);
        query.setParameter("teamPk", teamPk);
        return query.getSingleResult();

    }

    public WW2Team getWW2TeamByTeamPk(long teamPk) {
        TypedQuery<WW2Team> query = entityManager.createNamedQuery("WW2Team.getWW2TeamByTeamPk", WW2Team.class);
        query.setParameter("teamPk", teamPk);
        return query.getSingleResult();

    }

    public List<MWRTeam> getTop100MWRTeamsByXP(RegionEnum region, PlatformEnum platform, TeamSizeEnum teamSize, TeamTypeEnum teamType) {
        TypedQuery<MWRTeam> query = entityManager.createNamedQuery("MWRTeam.getTop100MWRTeamsByXP", MWRTeam.class);
        query.setParameter("region", region);
        query.setParameter("platform", platform);
        query.setParameter("teamSize", teamSize);
        query.setParameter("teamType", teamType);
        query.setMaxResults(100);
        return query.getResultList();
    }

    public List<IWTeam> getTop100IWTeamsByXP(RegionEnum region, PlatformEnum platform, TeamSizeEnum teamSize) {
        TypedQuery<IWTeam> query = entityManager.createNamedQuery("IWTeam.getTop100IWTeamsByXP", IWTeam.class);
        query.setParameter("region", region);
        query.setParameter("platform", platform);
        query.setParameter("teamSize", teamSize);
        query.setMaxResults(100);
        return query.getResultList();
    }

    public List<WW2Team> getTop100WW2TeamsByXP(RegionEnum region, PlatformEnum platform, TeamSizeEnum teamSize, TeamTypeEnum teamType) {
        TypedQuery<WW2Team> query = entityManager.createNamedQuery("WW2Team.getTop100WW2TeamsByXP", WW2Team.class);
        query.setParameter("region", region);
        query.setParameter("platform", platform);
        query.setParameter("teamSize", teamSize);
        query.setParameter("teamType", teamType);
        query.setMaxResults(100);
        return query.getResultList();
    }

}
