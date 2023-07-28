/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.applicationevent.event;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * @author Hoang
 */
public class OnMatchCreationCompleteEvent extends ApplicationEvent {
    
    private final Long matchInvitePk;
    
    public OnMatchCreationCompleteEvent(Long matchInvitePk){
        super(matchInvitePk);
        this.matchInvitePk = matchInvitePk;
    }

    public Long getMatchInvitePk() {
        return matchInvitePk;
    }
    
    

}
