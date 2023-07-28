/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity.iw;

import com.ltlogic.db.entity.Match;
import com.ltlogic.pojo.iw.IWMatchPojo;
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
 * @author jaimel
 */
@NamedQueries({
    @NamedQuery(
            name = "IWMatch.getIWMatchByMatchPk",
            query = "SELECT m FROM IWMatch m WHERE m.pk = :matchPk"
    )    
})
@Entity
//@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
//@AuditTable(value = "iw_match_history", schema = "xms")
@PrimaryKeyJoinColumn(name = "match_pk", referencedColumnName = "pk")
public class IWMatch extends Match{

    @Embedded
    IWMatchPojo iwMatchInfo = new IWMatchPojo();

    public IWMatchPojo getIwMatchInfo() {
        return iwMatchInfo;
    }

    public void setIwMatchInfo(IWMatchPojo iwMatchInfo) {
        this.iwMatchInfo = iwMatchInfo;
    }
    
}
