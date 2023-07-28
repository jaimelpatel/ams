/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity.ww2;

import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.rank.team.MWRTeamRankXP;
import com.ltlogic.db.entity.rank.team.WW2TeamRankXP;
import com.ltlogic.pojo.iw.IWTeamPojo;
import com.ltlogic.pojo.mwr.MWRTeamPojo;
import com.ltlogic.pojo.ww2.WW2TeamPojo;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 * 
 * @author jaimel
 */
@NamedQueries({
    @NamedQuery(
            name = "WW2Team.getWW2TeamByTeamPk",
            query = "SELECT t FROM WW2Team t WHERE t.pk = :teamPk"
    ),
    @NamedQuery(
            name = "WW2Team.getTop100WW2TeamsByXP",
            query = "SELECT t FROM WW2Team t JOIN t.rankXP r WHERE t.teamPojo.region = :region AND t.teamPojo.platform = :platform AND t.teamPojo.teamSize = :teamSize AND t.teamPojo.teamType = :teamType ORDER BY r.rank ASC, r.millis ASC"
    )   
})
@Entity
@PrimaryKeyJoinColumn(name = "team_pk", referencedColumnName = "pk")
//@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
//@AuditTable(value = "ww2_team_history", schema = "xms")
public class WW2Team extends Team {
    
//    
    @OneToOne(mappedBy = "ww2Team", fetch = FetchType.LAZY)
    private WW2TeamRankXP rankXP;
    
//    @OneToOne(mappedBy = "ww2Team", fetch = FetchType.LAZY)
//    private WW2TeamRankCash rankCash;
    
    @Embedded
    private WW2TeamPojo ww2TeamInfo;

//    public WW2TeamRankCash getRankCash() {
//        return rankCash;
//    }
//
//    public void setRankCash(WW2TeamRankCash rankCash) {
//        this.rankCash = rankCash;
//    }

    public WW2TeamRankXP getRankXP() {
        return rankXP;
    }

    public void setRankXP(WW2TeamRankXP rankXP) {
        this.rankXP = rankXP;
    }

    public WW2TeamPojo getWw2TeamInfo() {
        return ww2TeamInfo;
    }

    public void setWw2TeamInfo(WW2TeamPojo ww2TeamInfo) {
        this.ww2TeamInfo = ww2TeamInfo;
    }

}