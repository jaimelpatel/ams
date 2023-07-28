/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.constants.NotificationTypeEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Notification;
import java.util.List;
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
 * @author jaimel
 */
@Repository
public class NotificationRepository {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final static int ITEMS_PER_PAGE = 20;

    public void persistNotification(Notification c) {
        entityManager.persist(c);
    }
    
    public Integer getTotalNoficationsByUserPk(long userPk) {
        TypedQuery<Notification> query = entityManager.createNamedQuery("Notification.getNotificationsByUserPk", Notification.class);
        query.setParameter("userPk", userPk);

        return query.getResultList().size();
    }

    public List<Notification> getAllNotificationsForAUserPaginate(long userPk, int pageNumber) {
        TypedQuery<Notification> query = entityManager.createNamedQuery("Notification.getNotificationsByUserPk", Notification.class);
        query.setParameter("userPk", userPk);
        pageNumber = pageNumber * ITEMS_PER_PAGE;

        return query.setFirstResult(pageNumber).setMaxResults(ITEMS_PER_PAGE).getResultList();

    }
    
    public Long getNotificationCountByUserPk(long userPk) {
        Query query = entityManager.createNamedQuery("Notification.getNotificationCountByUserPk");
        query.setParameter("userPk", userPk);

        return (Long) query.getSingleResult();

    }

    public List<Notification> getAllNotificationsForAUser(long userPk) {
        TypedQuery<Notification> query = entityManager.createNamedQuery("Notification.getNotificationsByUserPk", Notification.class);
        query.setParameter("userPk", userPk);
        return query.getResultList();

    }

    public Notification getNotificationByUserPkAndMatchPkAndType(long userPk, long matchPk, NotificationTypeEnum notificationType) {
        TypedQuery<Notification> query = entityManager.createNamedQuery("Notification.getNotificationByUserPkAndMatchPkAndType", Notification.class);
        query.setParameter("userPk", userPk);
        query.setParameter("matchPk", matchPk);
        query.setParameter("notificationType", notificationType);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    public Notification getNotificationByUserPkAndTourmamentPkAndType(long userPk, long tournamentPk, NotificationTypeEnum notificationType) {
        TypedQuery<Notification> query = entityManager.createNamedQuery("Notification.getNotificationByUserPkAndTourmamentPkAndType", Notification.class);
        query.setParameter("userPk", userPk);
        query.setParameter("tournamentPk", tournamentPk);
        query.setParameter("notificationType", notificationType);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }
}
