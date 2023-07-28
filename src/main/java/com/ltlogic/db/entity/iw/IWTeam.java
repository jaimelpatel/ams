/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity.iw;

import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.rank.team.IWTeamRankXP;
import com.ltlogic.db.entity.rank.user.UserRankXP;
import com.ltlogic.pojo.iw.IWTeamPojo;
import com.ltlogic.pojo.mwr.MWRTeamPojo;
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
            name = "IWTeam.getIWTeamByTeamPk",
            query = "SELECT t FROM IWTeam t WHERE t.pk = :teamPk"
    ),
    @NamedQuery(
            name = "IWTeam.getTop100IWTeamsByXP",
            query = "SELECT t FROM IWTeam t JOIN t.rankXP r WHERE t.teamPojo.region = :region AND t.teamPojo.platform = :platform AND t.teamPojo.teamSize = :teamSize ORDER BY r.rank ASC, r.millis ASC"
    )   
})
@Entity
@PrimaryKeyJoinColumn(name = "team_pk", referencedColumnName = "pk")
//@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
//@AuditTable(value = "iw_team_history", schema = "xms")
public class IWTeam extends Team {
    
    @Embedded
    private IWTeamPojo iwTeamInfo;
    
    @OneToOne(mappedBy = "iwTeam", fetch = FetchType.LAZY)
    private IWTeamRankXP rankXP;

    public IWTeamRankXP getRankXP() {
        return rankXP;
    }

    public void setRankXP(IWTeamRankXP rankXP) {
        this.rankXP = rankXP;
    }

    public IWTeamPojo getIwTeamInfo() {
        return iwTeamInfo;
    }

    public void setIwTeamInfo(IWTeamPojo iwTeamInfo) {
        this.iwTeamInfo = iwTeamInfo;
    }
    
}