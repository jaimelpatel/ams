/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.constants.PrivilegeEnum;
import com.ltlogic.db.entity.Privilege;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Hoang
 */
@Repository
public class PrivilegeRepository {
    
    private static final Logger LOG = LoggerFactory.getLogger(PrivilegeRepository.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public void persistPrivilege(Privilege c) {
        entityManager.persist(c);
    }
    
    public Privilege findByPk(long pk) {
        return entityManager.find(Privilege.class, pk);
    }
    
    public Privilege findPrivilegeByEnum(PrivilegeEnum privEnum){
        TypedQuery<Privilege> query = entityManager.createNamedQuery("Privilege.getPrivilegeByEnum", Privilege.class);
        query.setParameter("privilegeEnum", privEnum);
        try{
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
}
