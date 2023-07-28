/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.entity.rank.user;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.superentity.SuperEntity;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


/**
 *
 * @author Jaimel
 */
@Entity
public class UserRankXP extends SuperEntity{
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "userPk")
    private User user;
    
    private int totalXp;
    private int rank;
    private long millis = DateTimeUtil.getDefaultZonedDateTimeNow().toInstant().toEpochMilli();
    
    public int getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(int totalXp) {
        this.totalXp = totalXp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }
    
}
