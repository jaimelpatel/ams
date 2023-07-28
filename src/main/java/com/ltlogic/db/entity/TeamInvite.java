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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.OneToMany;

/**
 *
 * @author Bishistha
 */
@NamedQueries({
    @NamedQuery(
            name = "TeamInvite.getPendingReceivedInvitesByUserPk",
            query = "SELECT i FROM TeamInvite i WHERE i.userReceiver.pk = :receiverPk AND i.inviteEnum = 'PENDING' AND i.team.teamPojo.teamStatus != 'DISBANDED' ORDER BY i.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "TeamInvite.getPendingReceivedInvitesByUserPkAndTeamPk",
            query = "SELECT i FROM TeamInvite i WHERE i.userReceiver.pk = :receiverPk AND i.team.pk = :teamPk AND i.inviteEnum = 'PENDING' AND i.team.teamPojo.teamStatus != 'DISBANDED'"
    )
})
@Entity
public class TeamInvite extends SuperEntity {

    @Enumerated(EnumType.STRING)
    private InvitesEnum inviteEnum;

    private LocalDateTime acceptedAt;
    private LocalDateTime declinedAt;
    private LocalDateTime expiredAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "teamInvite")
    private Set<Notification> notification = new HashSet<>(0);

    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_sender_pk", referencedColumnName = "pk")
    private User userSender;

    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_receiver_pk", referencedColumnName = "pk")
    private User userReceiver;

    @XmlTransient
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_pk", referencedColumnName = "pk")
    private Team team;

    public Set<Notification> getNotification() {
        return notification;
    }

    public void setNotification(Set<Notification> notification) {
        this.notification = notification;
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

    public User getUserSender() {
        return userSender;
    }

    public void setUserSender(User userSender) {
        this.userSender = userSender;
    }

    public User getUserReceiver() {
        return userReceiver;
    }

    public void setUserReceiver(User userReceiver) {
        this.userReceiver = userReceiver;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

}
