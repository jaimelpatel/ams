/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.applicationevent.event;

import com.ltlogic.db.entity.User;
import java.util.Locale;
import org.springframework.context.ApplicationEvent;

/**
 * 
 * @author Hoang
 */
public class OnRegistrationCompleteEvent extends ApplicationEvent{
    
    private final String appUrl;
    private final Locale locale;
    private final User user;

     public OnRegistrationCompleteEvent(User user, Locale locale, String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public User getUser() {
        return user;
    }
     
}
