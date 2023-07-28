/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;

import com.ltlogic.db.entity.Conversation;
import com.ltlogic.service.core.ConversationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author raymond
 */
@Service
public class ConversationIWS {
    
    @Autowired
    ConversationService conversationService;
    
    public List<Conversation> getAllConversationForTeamAndDispute(long teamPk, long disputePk) {
        return conversationService.getAllConversationForTeamAndDispute(teamPk, disputePk);
    }
}
