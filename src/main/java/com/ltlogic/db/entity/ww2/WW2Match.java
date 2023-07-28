/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity.ww2;

import com.ltlogic.db.entity.Match;
import com.ltlogic.pojo.mwr.MWRMatchPojo;
import com.ltlogic.pojo.ww2.WW2MatchPojo;
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
 * @author jaimel
 */
@NamedQueries({
    @NamedQuery(
            name = "WW2Match.getWW2MatchByMatchPk",
            query = "SELECT m FROM WW2Match m WHERE m.pk = :matchPk"
    )    
})
@Entity
//@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
//@AuditTable(value = "ww2_match_history", schema = "xms")
@PrimaryKeyJoinColumn(name = "match_pk", referencedColumnName = "pk")
public class WW2Match extends Match{

    @Embedded
    WW2MatchPojo ww2MatchInfo = new WW2MatchPojo();

    public WW2MatchPojo getWw2MatchInfo() {
        return ww2MatchInfo;
    }

    public void setWw2MatchInfo(WW2MatchPojo ww2MatchInfo) {
        this.ww2MatchInfo = ww2MatchInfo;
    }
    
}
