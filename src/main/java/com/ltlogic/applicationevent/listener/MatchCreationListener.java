/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.applicationevent.listener;

import com.ltlogic.applicationevent.event.OnMatchCreationCompleteEvent;
import com.ltlogic.service.core.MatchInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Hoang
 */
@Component
public class MatchCreationListener implements ApplicationListener<OnMatchCreationCompleteEvent>{
    
    @Autowired
    MatchInviteService matchInviteService;

    @Override
    public void onApplicationEvent(OnMatchCreationCompleteEvent event) {
        //matchInviteService.acceptMatchInvite(event.getMatchInvitePk());
    }

}
