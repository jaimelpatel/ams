/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.entity;

import com.ltlogic.constants.DisputeStatus;
import com.ltlogic.db.superentity.SuperEntity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.envers.NotAudited;

/**
 *
 * @author Bishistha
 */
@NamedQueries({
    @NamedQuery(
            name = "Dispute.getDisputeByMatchAndUserPk",
            query = "SELECT d FROM Dispute d WHERE d.match.pk = :matchPk AND d.user.pk = :userPk"
    ),
    @NamedQuery(
            name = "Dispute.getDisputeByDisputeId",
            query = "SELECT d FROM Dispute d WHERE d.disputeId = :disputeId"
    ),
    @NamedQuery(
            name = "Dispute.getAllDisputesForAUser",
            query = "SELECT d FROM Dispute d WHERE d.user.pk = :userPk ORDER BY d.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "Dispute.getAllMatchDisputesByMatchPk",
            query = "Select d from Dispute d where d.match.pk = :matchPk ORDER BY d.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "Dispute.getDisputeByMatchAndTeam",
            query = "Select d from Dispute d where d.match.pk = :matchPk AND d.team.pk = :teamPk ORDER BY d.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "Dispute.getAllOpenMatchDisputesByMatchType",
            query = "Select d from Dispute d where d.disputeStatus = 'OPEN' AND d.match.matchInfo.matchType = :matchType ORDER BY d.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "Dispute.getAllOpenMatchDisputesByUserPk",
            query = "SELECT d FROM Dispute d WHERE d.user.pk = :userPk AND d.disputeStatus = 'OPEN' ORDER BY d.rowCreatedTimestamp DESC"
    )
    
})
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(
            columnNames = {"match_pk", "team_pk"},
            name = "unique_constraint_match_pk_team_pk"
    )
})
public class Dispute extends SuperEntity {
//each team can have 1 dispute for 1 match
//user keeps uploading media for the same dispute
    //

    @Enumerated(EnumType.STRING)
    private DisputeStatus disputeStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dispute")
    private Set<CloudMedia> cloudMedia = new HashSet<CloudMedia>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dispute")
    private Set<Conversation> conversation = new HashSet<Conversation>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_pk", referencedColumnName = "pk")
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", referencedColumnName = "pk")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_pk", referencedColumnName = "pk")
    private Team team;

    //
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dispute")
    private Set<Notification> notification = new HashSet<>(0);

    @ElementCollection
    @CollectionTable(name = "list_of_links_for_disputes",
            joinColumns = @JoinColumn(name = "dispute_pk", referencedColumnName = "pk"))
    private List<String> links = new ArrayList<>();

    private String reasonForDispute;

    private int disputeId;

    public Set<Notification> getNotification() {
        return notification;
    }

    public void setNotification(Set<Notification> notification) {
        this.notification = notification;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getReasonForDispute() {
        return reasonForDispute;
    }

    public void setReasonForDispute(String reasonForDispute) {
        this.reasonForDispute = reasonForDispute;
    }

    public Set<CloudMedia> getCloudMedia() {
        return cloudMedia;
    }

    public void setCloudMedia(Set<CloudMedia> cloudMedia) {
        this.cloudMedia = cloudMedia;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getDisputeId() {
        return disputeId;
    }

    public void setDisputeId(int disputeId) {
        this.disputeId = disputeId;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public Set<Conversation> getConversation() {
        return conversation;
    }

    public void setConversation(Set<Conversation> conversation) {
        this.conversation = conversation;
    }

    public DisputeStatus getDisputeStatus() {
        return disputeStatus;
    }

    public void setDisputeStatus(DisputeStatus disputeStatus) {
        this.disputeStatus = disputeStatus;
    }

}
