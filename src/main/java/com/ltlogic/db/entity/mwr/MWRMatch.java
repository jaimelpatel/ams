/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity.mwr;

import com.ltlogic.db.entity.Match;
import com.ltlogic.pojo.mwr.MWRMatchPojo;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 * 
 * @author Hoang
 */
@NamedQueries({
    @NamedQuery(
            name = "MWRMatch.getMWRMatchByMatchPk",
            query = "SELECT m FROM MWRMatch m WHERE m.pk = :matchPk"
    )    
})
@Entity
//@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
//@AuditTable(value = "mwr_match_history", schema = "xms")
@PrimaryKeyJoinColumn(name = "match_pk", referencedColumnName = "pk")
public class MWRMatch extends Match{

    @Embedded
    MWRMatchPojo mwrMatchInfo = new MWRMatchPojo();

    public MWRMatchPojo getMwrMatchInfo() {
        return mwrMatchInfo;
    }

    public void setMwrMatchInfo(MWRMatchPojo mwrMatchInfo) {
        this.mwrMatchInfo = mwrMatchInfo;
    }
    
}
