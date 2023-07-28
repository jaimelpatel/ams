/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.db.entity.Transaction;
import com.ltlogic.db.entity.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jaimel
 */
@Repository
public class TransactionRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private final static int ITEMS_PER_PAGE = 15;
    
    
    public void persistTransaction(Transaction transaction) {
        entityManager.persist(transaction);
    }

    public Transaction findTransactionByTransactionId(int transactionId) {
        TypedQuery<Transaction> query = entityManager.createNamedQuery("Transaction.getTransactionByTransactionId", Transaction.class);
        query.setParameter("transactionId", transactionId);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
    
   public List<Transaction> getTransactionsForUser(long userPk, int pageNumber) {
        TypedQuery<Transaction> query = entityManager.createNamedQuery("Transaction.getTransactionsForUser", Transaction.class);
        query.setParameter("userPk", userPk);
        pageNumber = pageNumber * ITEMS_PER_PAGE;

        return query.setFirstResult(pageNumber).setMaxResults(ITEMS_PER_PAGE).getResultList();
    }
   
    public Integer getTotalTransactionsForUser(long userPk) {
        TypedQuery<Transaction> query = entityManager.createNamedQuery("Transaction.getTransactionsForUser", Transaction.class);
        query.setParameter("userPk", userPk);
        return query.getResultList().size();
    }
   

//    public Transaction findTransactionByTransactionIdTest(int transactionId) {
//        Session session = entityManager.unwrap(Session.class);
//        Transaction t = session.bySimpleNaturalId(Transaction.class).load(transactionId);
//        return t;
//    }
//
//    public Transaction findTransactionByTransactionIdTest2(int transactionId) {
//        Session session = entityManager.unwrap(Session.class);
//
//        Criteria crit = session.createCriteria(Transaction.class);
//        crit.add(Restrictions.naturalId().set("taxId", "209384092")
//        ).setCacheable(true)
//                .uniqueResult();
//
//        entityManager.getTransaction().begin();
//
//        Transaction t = session.byNaturalId(Transaction.class).using("transactionId", transactionId).load();
//        return t;
//    }
    
}
