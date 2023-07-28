/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.repository;

import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.PasswordResetToken;
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
public class PasswordResetTokenRepository {
    
    private static final Logger LOG = LoggerFactory.getLogger(PasswordResetTokenRepository.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public void saveToken(PasswordResetToken token){
        entityManager.persist(token);
    }
    
     public PasswordResetToken getTokenByPk(long pk){
        return entityManager.find(PasswordResetToken.class, pk);
    }
     
        public PasswordResetToken findByTokenString(String token){
        TypedQuery<PasswordResetToken> query = entityManager.createNamedQuery("PasswordResetToken.findByTokenString", PasswordResetToken.class);
        query.setParameter("token", token);
        try{
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
     
    public PasswordResetToken findByUser(User user){
        TypedQuery<PasswordResetToken> query = entityManager.createNamedQuery("PasswordResetToken.findByUser", PasswordResetToken.class);
        query.setParameter("user", user);
        try{
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        } 
    }
     
    
     public List<PasswordResetToken> findAllByExpiryDateLessThan(LocalDateTime now){
        TypedQuery<PasswordResetToken> query = entityManager.createNamedQuery("PasswordResetToken.findAllByExpiryDateLessThan", PasswordResetToken.class);
        query.setParameter("now", now);
        return query.getResultList();
        
    }
     
     public void delete(PasswordResetToken token){
        entityManager.remove(token);
    }
     
     public void deleteByExpiryDateLessThan(LocalDateTime now){
        
    }
    
}
