/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.entity;

import com.ltlogic.constants.TransactionStatusEnum;
import com.ltlogic.constants.TransactionTypeEnum;
import com.ltlogic.db.superentity.SuperEntity;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import org.hibernate.annotations.NaturalId;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.Length;

/**
 *
 * @author jaimel
 */
//SELECT * FROM Transaction t WHERE t.user= :user OR t.userPkTransferTo = :user.getPk ----- this is to get ALL (including money transferred to said user) the transaction of a given user
@NamedQueries({
        @NamedQuery(
            name = "Transaction.getTransactionByTransactionId",
            query = "SELECT t FROM Transaction t WHERE t.transactionId = :transactionId"
    ),
    @NamedQuery(
            name = "Transaction.getTransactionsForUser",
            query = "SELECT t FROM Transaction t WHERE t.user.pk = :userPk OR t.userPkTransferTo = :userPk ORDER BY t.rowCreatedTimestamp DESC"
    )
})

@Entity
//@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
//@AuditTable(value = "transaction_history", schema = "xms")
public class Transaction extends SuperEntity{
    
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;
    
    @Enumerated(EnumType.STRING)
    private TransactionStatusEnum transactionStatus;
    
    @NaturalId(mutable = false)
    private int transactionId;
    
    private BigDecimal transactionAmount;
    
    private long userPkTransferTo;
    
    private String transactionMessage;
    
    private String transactionTransferToMessage;
        
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", referencedColumnName = "pk")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_pk", referencedColumnName = "pk")
    private Match match;
        
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_pk", referencedColumnName = "pk")
    private Tournament tournament;
    
    @OneToOne(mappedBy = "transaction")
    private WorldBank worldBank;
    
    private BigDecimal commissionEarned;
    
    private BigDecimal commissionPercentage;
    
    private BigDecimal paypalTransactionFee;
    
    private String paypalTransactionId;

    public String getTransactionTransferToMessage() {
        return transactionTransferToMessage;
    }

    public void setTransactionTransferToMessage(String transactionTransferToMessage) {
        this.transactionTransferToMessage = transactionTransferToMessage;
    }

    public String getTransactionMessage() {
        return transactionMessage;
    }

    public void setTransactionMessage(String transactionMessage) {
        this.transactionMessage = transactionMessage;
    }
    
    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public TransactionStatusEnum getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatusEnum transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TransactionTypeEnum getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionTypeEnum transactionType) {
        this.transactionType = transactionType;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public long getUserPkTransferTo() {
        return userPkTransferTo;
    }

    public void setUserPkTransferTo(long userPkTransferTo) {
        this.userPkTransferTo = userPkTransferTo;
    }

    public WorldBank getWorldBank() {
        return worldBank;
    }

    public void setWorldBank(WorldBank worldBank) {
        this.worldBank = worldBank;
    }

    public BigDecimal getCommissionEarned() {
        return commissionEarned;
    }

    public void setCommissionEarned(BigDecimal commissionEarned) {
        this.commissionEarned = commissionEarned;
    }

    public BigDecimal getCommissionPercentage() {
        return commissionPercentage;
    }

    public void setCommissionPercentage(BigDecimal commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }

    public BigDecimal getPaypalTransactionFee() {
        return paypalTransactionFee;
    }

    public void setPaypalTransactionFee(BigDecimal paypalTransactionFee) {
        this.paypalTransactionFee = paypalTransactionFee;
    }

    public String getPaypalTransactionId() {
        return paypalTransactionId;
    }

    public void setPaypalTransactionId(String paypalTransactionId) {
        this.paypalTransactionId = paypalTransactionId;
    }
       
}
