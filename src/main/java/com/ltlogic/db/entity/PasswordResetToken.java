/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.db.superentity.SuperEntity;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

/**
 * 
 * @author Hoang
 */

@NamedQueries({
    @NamedQuery(
            name = "PasswordResetToken.getTokenByPk",
            query = "SELECT v FROM PasswordResetToken v WHERE v.pk = :pk"
    ),
    @NamedQuery(
            name = "PasswordResetToken.findByTokenString",
            query = "SELECT v FROM PasswordResetToken v WHERE v.token = :token"
    ),
    @NamedQuery(
            name = "PasswordResetToken.findByUser",
            query = "SELECT v FROM PasswordResetToken v WHERE v.user = :user"
    ),
    @NamedQuery(
            name = "PasswordResetToken.findAllByExpiryDateLessThan",
            query = "SELECT v FROM PasswordResetToken v WHERE v.expiryDateTime < :now"
    )
})

@Entity
public class PasswordResetToken extends SuperEntity {
    
    private static final int hoursTilExpiration = 24; 
    private String token;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "userPk")
    private User user;
    
    
    private LocalDateTime expiryDateTime;
    
    private LocalDateTime calculateExpiryDateTime(LocalDateTime sentOutDateTime) {
       LocalDateTime expirationDateTime = sentOutDateTime.plusHours(hoursTilExpiration);
       return expirationDateTime;
    }
    
    public PasswordResetToken(){
        
    }
    
    public PasswordResetToken(String token){
        this.token = token;
        this.expiryDateTime = calculateExpiryDateTime(DateTimeUtil.getDefaultLocalDateTimeNow());
    }
    
    public PasswordResetToken(User user, String token){
        this.token = token;
        this.user = user;
        this.expiryDateTime = calculateExpiryDateTime(DateTimeUtil.getDefaultLocalDateTimeNow());
    }
    
        public void updateToken(String token){
        this.token = token;
        this.expiryDateTime = calculateExpiryDateTime(DateTimeUtil.getDefaultLocalDateTimeNow());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDateTime() {
        return expiryDateTime;
    }

    public void setExpiryDateTime(LocalDateTime expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
    }
    
}
