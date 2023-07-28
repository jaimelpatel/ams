/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.db.entity.Privilege;
import com.ltlogic.db.entity.rank.user.UserRankEarnings;
import com.ltlogic.db.entity.rank.user.UserRankXP;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jaimel
 */
@Repository
@Transactional
public class UserRankRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public void persistUserRankXP(UserRankXP userRankXP) {
        entityManager.persist(userRankXP);
    }
    
    public void persistUserRankEarnings(UserRankEarnings userRankEarnings) {
        entityManager.persist(userRankEarnings);
    }
    
    public int updateUserXpRankings(){
        Query q = entityManager.createNativeQuery("update xms.user_rankxp as r"
                        + " set rank = new_rank"
                        + " from ("
                        + " select"
                        + " user_pk,"
                        + " row_number() over (ORDER BY r2.total_xp DESC,  r2.millis ASC) as new_rank"
                        + " from xms.user_rankxp as r2"
                        + " ) as s WHERE r.user_pk = s.user_pk");
    
        return q.executeUpdate();
    }
    
    public int updateUserEarningsRankings(){
        Query q = entityManager.createNativeQuery("update xms.user_rank_earnings as r"
                        + " set rank = new_rank"
                        + " from ("
                        + " select"
                        + " user_pk,"
                        + " row_number() over (ORDER BY r2.total_earnings DESC,  r2.millis ASC) as new_rank"
                        + " from xms.user_rank_earnings as r2"
                        + " ) as s WHERE r.user_pk = s.user_pk");
        
        return q.executeUpdate();
    }
    
}
