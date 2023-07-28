/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.db.superentity.SuperEntity;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * 
 * @author Hoang
 */

@NamedQueries({
    @NamedQuery(
            name = "MatchInvite.getAllMatchInvites",
            query = "SELECT m FROM MatchInvite m"
    ),
    @NamedQuery(
            name = "MatchInvite.getAllMatchInvitesForUser",
            query = "SELECT m FROM MatchInvite m WHERE m.user.pk = :pk ORDER BY m.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "MatchInvite.getAllMatchInvitesByStatusForUser",
            query = "SELECT m FROM MatchInvite m WHERE m.user.pk = :pk AND m.inviteEnum = :inviteEnum AND (m.match.matchInfo.matchStatus = 'WAITING_ON_FIRST_ACCEPT' OR m.match.matchInfo.matchStatus = 'PENDING' OR m.match.matchInfo.matchStatus = 'WAITING_ON_SECOND_ACCEPT') ORDER BY m.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "MatchInvite.getAllMatchInvitesForMatch",
            query = "SELECT m FROM MatchInvite m WHERE m.match.pk = :pk"
    ),
    @NamedQuery(
            name = "MatchInvite.getMatchInviteForUserInMatch",
            query = "SELECT m FROM MatchInvite m WHERE m.match.pk = :matchPk AND m.user.pk = :userPk ORDER BY m.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "MatchInvite.getAllMatchInvitesForTeam",
            query = "SELECT m FROM MatchInvite m WHERE m.team.pk = :pk"
    )
})

@Entity
public class MatchInvite extends SuperEntity{
    
    @Enumerated(EnumType.STRING)
    private InvitesEnum inviteEnum;
    
    private LocalDateTime sentAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime declinedAt;
    private LocalDateTime expiredAt;
    
    private boolean isPrivateMatchInviteToLeader = false;
    
    private String privateMatchTeamSenderName;
    
    private long privateMatchTeamSenderPk;
    
    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", referencedColumnName = "pk")
    private User user;
    
    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_pk", referencedColumnName = "pk")
    private Match match;
    
    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_pk", referencedColumnName = "pk")
    private Team team;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "matchInvite")
    private Set<Notification> notification = new HashSet<>(0);

    public long getPrivateMatchTeamSenderPk() {
        return privateMatchTeamSenderPk;
    }

    public void setPrivateMatchTeamSenderPk(long privateMatchTeamSenderPk) {
        this.privateMatchTeamSenderPk = privateMatchTeamSenderPk;
    }

    public String getPrivateMatchTeamSenderName() {
        return privateMatchTeamSenderName;
    }

    public void setPrivateMatchTeamSenderName(String privateMatchTeamSenderName) {
        this.privateMatchTeamSenderName = privateMatchTeamSenderName;
    }
    
    public Set<Notification> getNotification() {
        return notification;
    }

    public void setNotification(Set<Notification> notification) {
        this.notification = notification;
    }

    public boolean isIsPrivateMatchInviteToLeader() {
        return isPrivateMatchInviteToLeader;
    }

    public void setIsPrivateMatchInviteToLeader(boolean isPrivateMatchInviteToLeader) {
        this.isPrivateMatchInviteToLeader = isPrivateMatchInviteToLeader;
    }
    
    public InvitesEnum getInviteEnum() {
        return inviteEnum;
    }

    public void setInviteEnum(InvitesEnum inviteEnum) {
        this.inviteEnum = inviteEnum;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt() {
        this.acceptedAt = DateTimeUtil.getDefaultLocalDateTimeNow();
    }

    public LocalDateTime getDeclinedAt() {
        return declinedAt;
    }

    public void setDeclinedAt() {
        this.declinedAt = DateTimeUtil.getDefaultLocalDateTimeNow();
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt() {
        this.expiredAt = DateTimeUtil.getDefaultLocalDateTimeNow();
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

//    public Team getTeamSender() {
//        return teamSender;
//    }
//
//    public void setTeamSender(Team teamSender) {
//        this.teamSender = teamSender;
//    }
//
//    public Team getTeamReceiver() {
//        return teamReceiver;
//    }
//
//    public void setTeamReceiver(Team teamReceiver) {
//        this.teamReceiver = teamReceiver;
//    }

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

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt() {
        this.sentAt = DateTimeUtil.getDefaultLocalDateTimeNow();
    }

}
