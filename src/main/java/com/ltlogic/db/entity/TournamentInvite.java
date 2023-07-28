/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.db.superentity.SuperEntity;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * @author Hoang
 */

@NamedQueries({
    @NamedQuery(
            name = "TournamentInvite.getAllTournamentInvites",
            query = "SELECT t FROM TournamentInvite t"
    ),
    @NamedQuery(
            name = "TournamentInvite.getAllTournamentInvitesForUser",
            query = "SELECT t FROM TournamentInvite t WHERE t.user.pk = :pk ORDER BY t.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "TournamentInvite.getAllTournamentInvitesByStatusForUser",
            query = "SELECT t FROM TournamentInvite t WHERE t.user.pk = :pk AND t.inviteEnum = :inviteEnum ORDER BY t.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "TournamentInvite.getAllTournamentInvitesForTournament",
            query = "SELECT t FROM TournamentInvite t WHERE t.tournament.pk = :pk"
    ),
    @NamedQuery(
            name = "TournamentInvite.getAllTournamentInvitesByTournamentPkAndUserPk",
            query = "SELECT t FROM TournamentInvite t WHERE t.tournament.pk = :tournamentPk AND t.user.pk = :userPk"
    ),
    @NamedQuery(
            name = "TournamentInvite.getTournamentInviteForUserInTournamentTeamByStatus",
            query = "SELECT t FROM TournamentInvite t WHERE t.tournamentTeam.pk = :tournamentTeamPk AND t.user.pk = :userPk AND t.inviteEnum = :inviteEnum"
    ),
    @NamedQuery(
            name = "TournamentInvite.getAllTournamentInvitesForTournamentTeamNotCancelled",
            query = "SELECT t FROM TournamentInvite t WHERE t.tournamentTeam.pk = :pk AND t.tournamentTeam.teamCancelled IS false"
    ),
        @NamedQuery(
            name = "TournamentInvite.getTournamentInviteByTournamentTeamPkAndUserPk",
            query = "SELECT t FROM TournamentInvite t WHERE t.tournamentTeam.pk = :tournamentTeamPk AND t.user.pk = :userPk"
    )
})
@Entity
public class TournamentInvite extends SuperEntity {
    
    @Enumerated(EnumType.STRING)
    private InvitesEnum inviteEnum;
    
    private LocalDateTime sentAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime declinedAt;
    private LocalDateTime expiredAt;
    
    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", referencedColumnName = "pk")
    private User user;
    
    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_pk", referencedColumnName = "pk")
    private Tournament tournament;
    
    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_team_pk", referencedColumnName = "pk")
    private TournamentTeam tournamentTeam;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tournamentInvite")
    private Set<Notification> notification = new HashSet<>(0);

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

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt() {
        this.sentAt = DateTimeUtil.getDefaultLocalDateTimeNow();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public TournamentTeam getTournamentTeam() {
        return tournamentTeam;
    }

    public void setTournamentTeam(TournamentTeam tournamentTeam) {
        this.tournamentTeam = tournamentTeam;
    }
    
    
}
