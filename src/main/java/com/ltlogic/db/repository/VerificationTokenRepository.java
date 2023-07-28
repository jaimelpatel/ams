/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.repository;

import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.VerificationToken;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author Hoang
 */
@Repository
public class VerificationTokenRepository {
    
    private static final Logger LOG = LoggerFactory.getLogger(VerificationTokenRepository.class);

    @PersistenceContext
    private EntityManager entityManager;
    
    public void saveToken(VerificationToken token){
        entityManager.persist(token);
    }
    
    public VerificationToken getTokenByPk(long pk){
        return entityManager.find(VerificationToken.class, pk);
    }
    
    
    public VerificationToken findByTokenString(String token){
        TypedQuery<VerificationToken> query = entityManager.createNamedQuery("VerificationToken.findByTokenString", VerificationToken.class);
        query.setParameter("token", token);
        try{
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
    
    public VerificationToken findByUser(User user){
        TypedQuery<VerificationToken> query = entityManager.createNamedQuery("VerificationToken.findByUser", VerificationToken.class);
        query.setParameter("user", user);
        try{
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        } 
    }
    
    public List<VerificationToken> findAllByExpiryDateLessThan(LocalDateTime now){
        TypedQuery<VerificationToken> query = entityManager.createNamedQuery("VerificationToken.findAllByExpiryDateLessThan", VerificationToken.class);
        query.setParameter("now", now);
        return query.getResultList();
    }
    
    public void delete(VerificationToken token){
        entityManager.remove(token);
    }
    
    public void deleteByExpiryDateLessThan(LocalDateTime now){
        
    }
}
