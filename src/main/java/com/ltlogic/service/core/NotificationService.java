/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.ltlogic.db.entity.Notification;
import com.ltlogic.db.repository.NotificationRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jaimel
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepo;

    public List<Notification> getAllNotificationsForUserPaginate(long userPk, int pageNumber) {
        return notificationRepo.getAllNotificationsForAUserPaginate(userPk, pageNumber);
    }
    
    public Integer getTotalNoficationsByUserPk(long userPk) {
        return notificationRepo.getTotalNoficationsByUserPk(userPk);
    }

    public Long getNotificationCountByUserPk(long userPk) {
        return notificationRepo.getNotificationCountByUserPk(userPk);
    }

    public List<Notification> getAllNotificationsForUser(long userPk) {
        return notificationRepo.getAllNotificationsForAUser(userPk);
    }
}
