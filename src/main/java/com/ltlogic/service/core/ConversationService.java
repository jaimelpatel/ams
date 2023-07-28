/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.ltlogic.db.entity.Conversation;
import com.ltlogic.db.repository.ConversationRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author raymond
 */
@Service
@Transactional
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;
    
    
    public List<Conversation> getAllConversationForTeamAndDispute(long teamPk, long disputePk) {
        return conversationRepository.getAllConversationForTeamAndDispute(teamPk, disputePk);
    }
}
