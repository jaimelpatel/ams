/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.entity;

import com.ltlogic.db.superentity.SuperEntity;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 *
 * @author jaimel
 */
@Entity
public class Bank extends SuperEntity{
    
    private BigDecimal totalAmount;
    
    private boolean hasWithdrawnToday = false;
    
    private BigDecimal totalTransferAmountForDay = new BigDecimal(0.00);
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "userPk")
    private User user;

    public BigDecimal getTotalTransferAmountForDay() {
        return totalTransferAmountForDay;
    }

    public void setTotalTransferAmountForDay(BigDecimal totalTransferAmountForDay) {
        this.totalTransferAmountForDay = totalTransferAmountForDay;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isHasWithdrawnToday() {
        return hasWithdrawnToday;
    }

    public void setHasWithdrawnToday(boolean hasWithdrawnToday) {
        this.hasWithdrawnToday = hasWithdrawnToday;
    }
    
}
