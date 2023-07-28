/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.entity.rank.team;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.db.entity.mwr.MWRTeam;
import com.ltlogic.db.superentity.SuperEntity;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.hibernate.envers.NotAudited;

/**
 *
 * @author Jaimel
 */
@Entity
public class MWRTeamRankXP extends SuperEntity{
    
    private int rank;    
    private int totalTeamXp;
    private BigDecimal totalCashEarned = new BigDecimal(0.00);
    
    private long millis = DateTimeUtil.getDefaultZonedDateTimeNow().toInstant().toEpochMilli();
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "mwrTeamPk")
    private MWRTeam mwrTeam;

    public BigDecimal getTotalCashEarned() {
        return totalCashEarned;
    }

    public void setTotalCashEarned(BigDecimal totalCashEarned) {
        this.totalCashEarned = totalCashEarned;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getTotalTeamXp() {
        return totalTeamXp;
    }

    public void setTotalTeamXp(int totalTeamXp) {
        this.totalTeamXp = totalTeamXp;
    }

    public MWRTeam getMwrTeam() {
        return mwrTeam;
    }

    public void setMwrTeam(MWRTeam mwrTeam) {
        this.mwrTeam = mwrTeam;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

}
