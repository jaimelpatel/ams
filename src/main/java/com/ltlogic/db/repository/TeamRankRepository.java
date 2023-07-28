/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.rank.team.IWTeamRankXP;
import com.ltlogic.db.entity.rank.team.MWRTeamRankXP;
import com.ltlogic.db.entity.rank.team.WW2TeamRankXP;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Jaimel
 */
@Repository
public class TeamRankRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public void persistIWTeamRankXP(IWTeamRankXP iwTeamRankXP) {
        entityManager.persist(iwTeamRankXP);
    }
    
    public void persistMWRTeamRankXP(MWRTeamRankXP mwrTeamRankXP) {
        entityManager.persist(mwrTeamRankXP);
    }
        
    public void persistWW2TeamRankXP(WW2TeamRankXP ww2TeamRankXP) {
        entityManager.persist(ww2TeamRankXP);
    }
    
    public int updateMWRTeamRanks(RegionEnum region, PlatformEnum platform, TeamSizeEnum teamSize){
        Query q = entityManager.createNativeQuery("update xms.mwrteam_rankxp as r"
                + " set rank = new_rank"
                + " from ("
                + " select"
                + " mwr_team_pk,"
                + " row_number() over (ORDER BY r2.total_team_xp DESC,  r2.millis ASC) as new_rank"
                + " from xms.mwrteam_rankxp as r2 JOIN xms.mwrteam as mwrt ON r2.mwr_team_pk = mwrt.team_pk JOIN xms.team t ON mwrt.team_pk = t.pk"
                + " where t.platform='" + platform + "' AND t.team_size='" + teamSize + "' AND t.region='" + region
                + "' ) as s WHERE r.mwr_team_pk = s.mwr_team_pk");

        return q.executeUpdate();
    }
    
    public int updateIWTeamRanks(RegionEnum region, PlatformEnum platform, TeamSizeEnum teamSize){
        Query q = entityManager.createNativeQuery("update xms.iwteam_rankxp as r"
                + " set rank = new_rank"
                + " from ("
                + " select"
                + " iw_team_pk,"
                + " row_number() over (ORDER BY r2.total_team_xp DESC,  r2.millis ASC) as new_rank"
                + " from xms.iwteam_rankxp as r2 JOIN xms.iwteam as iwt ON r2.iw_team_pk = iwt.team_pk JOIN xms.team t ON iwt.team_pk = t.pk"
                + " where t.platform='" + platform + "' AND t.team_size='" + teamSize + "' AND t.region='" + region
                + "' ) as s WHERE r.iw_team_pk = s.iw_team_pk");

        return q.executeUpdate();
    }
        
    public int updateWW2TeamRanks(RegionEnum region, PlatformEnum platform, TeamSizeEnum teamSize, TeamTypeEnum teamType){
        Query q = entityManager.createNativeQuery("update xms.ww2team_rankxp as r"
                + " set rank = new_rank"
                + " from ("
                + " select"
                + " ww2team_pk,"
                + " row_number() over (ORDER BY r2.total_team_xp DESC,  r2.millis ASC) as new_rank"
                + " from xms.ww2team_rankxp as r2 JOIN xms.ww2team as ww2t ON r2.ww2team_pk = ww2t.team_pk JOIN xms.team t ON ww2t.team_pk = t.pk"
                + " where t.platform='" + platform + "' AND t.team_size='" + teamSize + "' AND t.region='" + region + "' AND t.team_type='" + teamType 
                + "' ) as s WHERE r.ww2team_pk = s.ww2team_pk");

        return q.executeUpdate();
    }
    
//    public int updateWW2CashTeamRanks(RegionEnum region, PlatformEnum platform, TeamSizeEnum teamSize){
//        Query q = entityManager.createNativeQuery("update xms.ww2team_rankxp as r"
//                + " set rank = new_rank"
//                + " from ("
//                + " select"
//                + " ww2team_pk,"
//                + " row_number() over (ORDER BY r2.total_team_xp DESC,  r2.millis ASC) as new_rank"
//                + " from xms.ww2team_rankxp as r2 JOIN xms.ww2team as ww2t ON r2.ww2team_pk = ww2t.team_pk JOIN xms.team t ON ww2t.team_pk = t.pk"
//                + " where t.platform='" + platform + "' AND t.team_size='" + teamSize + "' AND t.region='" + region
//                + "' ) as s WHERE r.ww2team_pk = s.ww2team_pk");
//
//        return q.executeUpdate();
//    }
}
