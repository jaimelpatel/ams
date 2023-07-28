/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.repository;

import com.ltlogic.db.entity.Conversation;
import com.ltlogic.db.entity.Dispute;
import com.ltlogic.db.entity.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Bishistha
 */
@Repository
public class ConversationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void persistConversation(Conversation c) {
        entityManager.persist(c);
    }

    public List<Conversation> getAllConversationForTeamAndDispute(long teamPk, long disputePk) {
        TypedQuery<Conversation> query = entityManager.createNamedQuery("Conversation.getAllConversationForTeamAndDispute", Conversation.class);
        query.setParameter("teamPk", teamPk);
        query.setParameter("disputePk", disputePk);
        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
    public void associateConversationAndTeam(Conversation c, Team t){
        c.setTeam(t);
        t.getConversation().add(c);
    }
    public void associateConversationAndDispute(Conversation c, Dispute d){
        c.setDispute(d);
        d.getConversation().add(c);
    }
    
}
