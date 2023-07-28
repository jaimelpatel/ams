/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.db.entity.Product;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.ww2.WW2Team;
import java.util.List;
import javax.persistence.EntityManager;
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
public class ProductRepository {
    private static final Logger LOG = LoggerFactory.getLogger(ProductRepository.class);

    @PersistenceContext
    private EntityManager entityManager;
    
    public void persistProduct(Product p){
        entityManager.persist(p);
    }
    
    public List<Product> getAllProducts(){
        TypedQuery<Product> query = entityManager.createNamedQuery("Product.getAllProducts", Product.class);
        return query.getResultList();
    }
    
}
