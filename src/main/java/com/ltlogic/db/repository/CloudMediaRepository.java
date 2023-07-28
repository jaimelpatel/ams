/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.constants.CloudMediaCategory;
import com.ltlogic.constants.CloudMediaType;
import com.ltlogic.db.entity.CloudMedia;
import com.ltlogic.db.entity.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Bishistha
 */
@Repository
public class CloudMediaRepository {
    
      @PersistenceContext
    private EntityManager entityManager;
    
    public void persistCloudMedia(CloudMedia c){
        entityManager.persist(c);
    }
    
    public CloudMedia findCloudMediaByPk(long pk){
        return entityManager.find(CloudMedia.class, pk);
    }
    
    public List<CloudMedia> getCloudMediasByUserPkTypeAndCategory(long userPk, CloudMediaType type, CloudMediaCategory category) throws Exception{
        TypedQuery<CloudMedia> query = entityManager.createNamedQuery("CloudMedia.getCloudMediasByUserPkTypeAndCategory", CloudMedia.class);
        query.setParameter("userPk", userPk);
        query.setParameter("type", type);
        query.setParameter("category",category);
        try {
            List<CloudMedia> cloudMediaList = query.getResultList();
            if(category.equals(CloudMediaCategory.USER_IMG)){
                if(cloudMediaList.size() > 1){
                    throw new Exception("# Multiple Profile Pics cannot exist for the user.");
                }
                return cloudMediaList;
            }
            return query.getResultList();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
}
