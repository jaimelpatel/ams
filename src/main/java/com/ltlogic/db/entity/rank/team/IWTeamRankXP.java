/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.entity.rank.team;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.db.entity.iw.IWTeam;
import com.ltlogic.db.superentity.SuperEntity;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 *
 * @author Jaimel
 */
@Entity
public class IWTeamRankXP extends SuperEntity{
    
    private int rank;    
    private BigDecimal totalCashEarned = new BigDecimal(0.00);
    private int totalTeamXp;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "iwTeamPk")
    private IWTeam iwTeam;
    
    private long millis = DateTimeUtil.getDefaultZonedDateTimeNow().toInstant().toEpochMilli();

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

    public IWTeam getIwTeam() {
        return iwTeam;
    }

    public void setIwTeam(IWTeam iwTeam) {
        this.iwTeam = iwTeam;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }
    
}
