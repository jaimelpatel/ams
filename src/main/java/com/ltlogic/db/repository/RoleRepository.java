/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.constants.RoleEnum;
import com.ltlogic.db.entity.Role;
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
 * @author Jaimel
 */
@Repository
public class RoleRepository{
    
    private static final Logger LOG = LoggerFactory.getLogger(RoleRepository.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public void persistRole(Role c) {
        entityManager.persist(c);
    }
    
    public Role findByPk(long pk) {
        return entityManager.find(Role.class, pk);
    }
    
    public Role findRoleByEnum(RoleEnum roleEnum){
        TypedQuery<Role> query = entityManager.createNamedQuery("Role.getRoleByEnum", Role.class);
        query.setParameter("roleEnum", roleEnum);
        try{
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
}
