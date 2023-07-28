/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.db.entity.MatchCancellationRequest;
import com.ltlogic.db.entity.MatchInvite;
import com.ltlogic.db.entity.Notification;
import com.ltlogic.db.entity.TeamInvite;
import com.ltlogic.db.entity.User;
import com.ltlogic.fe.helpers.Paginator;
import com.ltlogic.iws.MatchCancellationRequestIWS;
import com.ltlogic.iws.NotificationIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.web.exception.CustomErrorException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Jaimel
 */
@Controller
public class NotificationsController {
    
    @Autowired 
    NotificationIWS notificationIWS;
    
    @Autowired
    UserIWS userIWS;
    
    @Autowired
    MatchCancellationRequestIWS matchCancellationRequestIWS;
    
    @Autowired
    Paginator paginator;
    
    @RequestMapping(value = "{username}/notifications", method = RequestMethod.GET)
    public String viewInvites(@PathVariable String username, ModelMap model, Principal p,
            @RequestParam(value = "page", required = false) Integer page) {

        if(p == null || (username != null && username.equalsIgnoreCase(p.getName()) == false)) {
            throw new CustomErrorException();
        }
        
        Integer currentPage = 1;
        
        if(page != null) {
            currentPage = page;
        }
        
        User user = userIWS.getUserByUsername(username);
        List<Notification> notifications = notificationIWS.getAllNotifictionsForUserPaginate(user.getPk(), currentPage-1);
        Long notificationSize = notificationIWS.getNotificationCountByUserPk(user.getPk());
        
        
        userIWS.setUserNotificationNumber(user, notificationSize.intValue());

        List<MatchCancellationRequest> matchCancellationRequests =  matchCancellationRequestIWS.getPendingMatchCancellationRequestForAUser(user.getPk());
     
        //Integer totalNofications = notificationIWS.getTotalNoficationsByUserPk(user.getPk());
        
        paginator.setPaginationForNotifications(null, currentPage, notificationSize.intValue(), model);
        
        model.addAttribute("user", user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("matchCancellationRequests", matchCancellationRequests);

        return "notifications";
    }
    
    @RequestMapping(value="/notifications/number", method=RequestMethod.GET)
    public @ResponseBody int notificationNumber(ModelMap model, Principal p){
        if(p == null){
            //do nothing
        }
        else{
            User user = userIWS.getUserByUsername(p.getName());
            Long notificationSize = notificationIWS.getNotificationCountByUserPk(user.getPk());
            int newNotificationCount = notificationSize.intValue();
            int currentNotificationCount = user.getNumberOfNotifications();
            int notificationNumberToDisplay = newNotificationCount - currentNotificationCount;
            return notificationNumberToDisplay;
        }
        return 0;
    }
}
