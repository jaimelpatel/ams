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
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 *
 * @author Bishistha
 */
@NamedQueries({
    @NamedQuery(
            name = "WorldBank.getLatestWorldBank",
            query = "SELECT w FROM WorldBank w ORDER BY w.rowCreatedTimestamp DESC"
    ),
     @NamedQuery(
            name = "WorldBank.getWorldBankForTransactionPk",
            query = "SELECT w FROM WorldBank w WHERE w.transaction.pk = :transactionPk "
    )
})
@Entity
//@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
//@AuditTable(value = "world_bank_history", schema = "xms")
public class WorldBank extends SuperEntity {
    
    // this value is a cumulative of transactionAmount and is to be added/subtracted/neutral for each transaction
    private BigDecimal cumulativeTransactionAmount;
    
    //this is the total amount which also includes the amount business stakeholders put in
    private BigDecimal totalAmount;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, name = "transactionPk")
    private Transaction transaction;

    public BigDecimal getCumulativeTransactionAmount() {
        return cumulativeTransactionAmount;
    }

    public void setCumulativeTransactionAmount(BigDecimal cumulativeTransactionAmount) {
        this.cumulativeTransactionAmount = cumulativeTransactionAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
    
    
}
