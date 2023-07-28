/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.CancellationRequestEnum;
import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.db.superentity.SuperEntity;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * 
 * @author Hoang
 */

@NamedQueries({
    @NamedQuery(
            name = "MatchCancellationRequest.getMatchCancellationRequestByMatchPk",
            query = "SELECT m FROM MatchCancellationRequest m WHERE m.match.pk = :pk"
    ),
    @NamedQuery(
            name = "MatchCancellationRequest.getPendingMatchCancellationRequestForAUser",
            query = "SELECT m FROM MatchCancellationRequest m WHERE m.cancellationRequestee.pk = :userPk and m.match.matchInfo.matchStatus = 'READY_TO_PLAY' and m.cancellationRequestEnum = 'PENDING' ORDER BY m.rowCreatedTimestamp DESC"
    )
})

@Entity
public class MatchCancellationRequest extends SuperEntity {
    
    private LocalDateTime sentAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime declinedAt;
    private LocalDateTime expiredAt;
    
    @Enumerated(EnumType.STRING)
    private CancellationRequestEnum cancellationRequestEnum;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancellationRequester_pk", referencedColumnName = "pk")
    private User cancellationRequester;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancellationRequestee_pk", referencedColumnName = "pk")
    private User cancellationRequestee;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "matchPk")
    private Match match;

    public String getSentAt(TimeZoneEnum tze) {
        LocalDateTime sentAtUserTime = DateTimeUtil.getUserLocalDateTime(sentAt, tze);
        return DateTimeUtil.formatLocalDateTime(sentAtUserTime);
    }

    public void setSentAt() {
        this.sentAt = DateTimeUtil.getDefaultLocalDateTimeNow();
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt() {
        this.acceptedAt = DateTimeUtil.getDefaultLocalDateTimeNow();
    }

    public LocalDateTime getDeclinedAt() {
        return declinedAt;
    }

    public void setDeclinedAt() {
        this.declinedAt = DateTimeUtil.getDefaultLocalDateTimeNow();
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt() {
        this.expiredAt = DateTimeUtil.getDefaultLocalDateTimeNow();
    }

    public User getCancellationRequester() {
        return cancellationRequester;
    }

    public void setCancellationRequester(User cancellationRequester) {
        this.cancellationRequester = cancellationRequester;
    }

    public CancellationRequestEnum getCancellationRequestEnum() {
        return cancellationRequestEnum;
    }

    public void setCancellationRequestEnum(CancellationRequestEnum cancellationRequestEnum) {
        this.cancellationRequestEnum = cancellationRequestEnum;
    }

    public User getCancellationRequestee() {
        return cancellationRequestee;
    }

    public void setCancellationRequestee(User cancellationRequestee) {
        this.cancellationRequestee = cancellationRequestee;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
    
}
