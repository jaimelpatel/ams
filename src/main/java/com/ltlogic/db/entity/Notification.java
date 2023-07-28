/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.entity;

import com.ltlogic.constants.NotificationTypeEnum;
import com.ltlogic.db.superentity.SuperEntity;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@NamedQueries({
    @NamedQuery(
            name = "Notification.getNotificationsByUserPk",
            query = "SELECT n FROM Notification n WHERE n.user.pk = :userPk ORDER BY n.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "Notification.getNotificationCountByUserPk",
            query = "SELECT count(n) FROM Notification n WHERE n.user.pk = :userPk"
    ),
    @NamedQuery(
            name = "Notification.getNotificationByUserPkAndMatchPkAndType",
            query = "SELECT n FROM Notification n WHERE n.user.pk = :userPk AND n.match.pk= :matchPk AND n.notificationType= :notificationType"
    ),
    @NamedQuery(
            name = "Notification.getNotificationByUserPkAndTourmamentPkAndType",
            query = "SELECT n FROM Notification n WHERE n.user.pk = :userPk AND n.tournament.pk= :tournamentPk AND n.notificationType= :notificationType"
    )
})
/**
 *
 * @author Jaimel
 */
@Entity
public class Notification extends SuperEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", referencedColumnName = "pk")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_pk", referencedColumnName = "pk")
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispute_pk", referencedColumnName = "pk")
    private Dispute dispute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_pk", referencedColumnName = "pk")
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_pk", referencedColumnName = "pk")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_invite_pk", referencedColumnName = "pk")
    private TeamInvite teamInvite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_invite_pk", referencedColumnName = "pk")
    private MatchInvite matchInvite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_invite_pk", referencedColumnName = "pk")
    private TournamentInvite tournamentInvite;

    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum notificationType;
    private String notificationMessage;

    public TeamInvite getTeamInvite() {
        return teamInvite;
    }

    public void setTeamInvite(TeamInvite teamInvite) {
        this.teamInvite = teamInvite;
    }

    public MatchInvite getMatchInvite() {
        return matchInvite;
    }

    public void setMatchInvite(MatchInvite matchInvite) {
        this.matchInvite = matchInvite;
    }

    public TournamentInvite getTournamentInvite() {
        return tournamentInvite;
    }

    public void setTournamentInvite(TournamentInvite tournamentInvite) {
        this.tournamentInvite = tournamentInvite;
    }

    public Dispute getDispute() {
        return dispute;
    }

    public void setDispute(Dispute dispute) {
        this.dispute = dispute;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public NotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationTypeEnum notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

}
