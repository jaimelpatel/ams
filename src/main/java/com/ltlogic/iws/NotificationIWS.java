/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;

import com.ltlogic.db.entity.Notification;
import com.ltlogic.service.core.NotificationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jaimel
 */
@Service
public class NotificationIWS {

    @Autowired
    private NotificationService notificationService;

    public List<Notification> getAllNotifictionsForUserPaginate(long userPk, int pageNumber) {
        return notificationService.getAllNotificationsForUserPaginate(userPk, pageNumber);
    }
    
    public Integer getTotalNoficationsByUserPk(long userPk) {
        return notificationService.getTotalNoficationsByUserPk(userPk);
    }

    public Long getNotificationCountByUserPk(long userPk) {
        return notificationService.getNotificationCountByUserPk(userPk);
    }

    public List<Notification> getAllNotifictionsForUser(long userPk) {
        return notificationService.getAllNotificationsForUser(userPk);
    }
}
