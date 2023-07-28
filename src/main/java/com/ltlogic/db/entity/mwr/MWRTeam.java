/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity.mwr;

import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.rank.team.IWTeamRankXP;
import com.ltlogic.db.entity.rank.team.MWRTeamRankXP;
import com.ltlogic.pojo.mwr.MWRTeamPojo;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 * 
 * @author Hoang
 */

@NamedQueries({
    @NamedQuery(
            name = "MWRTeam.getMWRTeamByTeamPk",
            query = "SELECT t FROM MWRTeam t WHERE t.pk = :teamPk"
    ),
    @NamedQuery(
            name = "MWRTeam.getTop100MWRTeamsByXP",
            query = "SELECT t FROM MWRTeam t JOIN t.rankXP r WHERE t.teamPojo.region = :region AND t.teamPojo.platform = :platform AND t.teamPojo.teamSize = :teamSize AND t.teamPojo.teamType = :teamType ORDER BY r.rank ASC, r.millis ASC"
    )       
})
@Entity
@PrimaryKeyJoinColumn(name = "team_pk", referencedColumnName = "pk")
//@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
//@AuditTable(value = "mwr_team_history", schema = "xms")
//@Inheritance(strategy = InheritanceType.JOINED)
public class MWRTeam extends Team {
    
    @OneToOne(mappedBy = "mwrTeam", fetch = FetchType.LAZY)
    private MWRTeamRankXP rankXP;
    
    @Embedded
    private MWRTeamPojo mwrTeamInfo = new MWRTeamPojo();

    public MWRTeamRankXP getRankXP() {
        return rankXP;
    }

    public void setRankXP(MWRTeamRankXP rankXP) {
        this.rankXP = rankXP;
    }
    public MWRTeamPojo getMwrTeamInfo() {
        return mwrTeamInfo;
    }

    public void setMwrTeamInfo(MWRTeamPojo mwrTeamInfo) {
        this.mwrTeamInfo = mwrTeamInfo;
    }   
    
}
