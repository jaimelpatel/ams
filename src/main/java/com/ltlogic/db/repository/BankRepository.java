/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.db.entity.Bank;
import com.ltlogic.db.entity.WorldBank;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Jaimel
 */
@Repository
public class BankRepository {

    private static final Logger LOG = LoggerFactory.getLogger(BankRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    public void persistBank(Bank c) {
        entityManager.persist(c);
    }

    public void persistWorldBank(WorldBank b) {
        entityManager.persist(b);
    }

    public WorldBank findLatestWorldBank() {
        TypedQuery<WorldBank> query = entityManager.createNamedQuery("WorldBank.getLatestWorldBank", WorldBank.class);
        query.setMaxResults(1);
        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return new WorldBank();
        }
    }

    public WorldBank getWorldBankForTransactionPk(long transactionPk) {
        TypedQuery<WorldBank> query = entityManager.createNamedQuery("WorldBank.getWorldBankForTransactionPk", WorldBank.class);
        query.setParameter("transactionPk", transactionPk);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
    
    public void resetBankForAllUsers(){
        Query query = entityManager.createNativeQuery("update xms.bank set has_withdrawn_today = false, total_transfer_amount_for_day = 0.00");
        int noOfEntitiesUpdated = query.executeUpdate();
        LOG.info("Total of " + noOfEntitiesUpdated + " bank entities were reset with hasWithdrawnToday = false and totalTransferAmountForDay = 0.00");
    }
    
}
