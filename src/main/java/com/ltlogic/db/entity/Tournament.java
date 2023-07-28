/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.entity;

import static com.ltlogic.db.entity.Match.PARAM_CURRENT_TIME;
import static com.ltlogic.db.entity.Tournament.PARAM_CURRENT_TIME_PLUS_NOTIFICATION_TIME;
import static com.ltlogic.db.entity.Tournament.PARAM_TOURNAMENT_STATUS_TO_START;
import static com.ltlogic.db.entity.Tournament.QUERY_GET_ALL_TOURNAMENTS_TO_START;
import static com.ltlogic.db.entity.Tournament.QUERY_GET_ALL_UPCOMING_TOURNAMENTS_TO_NOTIFY;
import com.ltlogic.db.entity.challonge.TournamentResponse;
import com.ltlogic.db.superentity.SuperEntity;
import com.ltlogic.pojo.TournamentPojo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.envers.NotAudited;

/**
 *
 * @author Hoang
 */
@NamedQueries({
    @NamedQuery(
            name = "Tournament.getAllTournaments",
            query = "SELECT t FROM Tournament t"
    ),
    //Needs testing !!!!
    @NamedQuery(
            name = "Tournament.getAllTournamentByGameAndPlatformAndTypeAndSizeAndStatus",
            query = "SELECT t FROM Tournament t WHERE t.tournamentInfo.gameEnum  = :gameEnum AND t.tournamentInfo.platform = :platformEnum "
            + "AND t.tournamentInfo.teamSizeEnum = :teamSizeEnum AND t.tournamentInfo.teamTypeEnum = :teamTypeEnum AND t.tournamentInfo.tournamentStatus = :tournamentStatus"
    ),
    @NamedQuery(
            name = "Tournament.getTournamentByTournamentId",
            query = "SELECT t FROM Tournament t WHERE t.tournamentId = :tournamentId"
    ),
    @NamedQuery(
            name = "Tournament.getAllTournamentsByStatus",
            query = "SELECT t FROM Tournament t WHERE t.tournamentInfo.tournamentStatus = :tournamentStatus ORDER BY t.tournamentInfo.scheduledTournamentTime ASC"
    ),
    @NamedQuery(
            name = "Tournament.getAllTournamentsByStatusAndPlatform",
            query = "SELECT t FROM Tournament t WHERE t.tournamentInfo.tournamentStatus = :tournamentStatus AND t.tournamentInfo.platform = :platformEnum ORDER BY t.tournamentInfo.scheduledTournamentTime ASC"
    ),
    @NamedQuery(
            name = "Tournament.getAllPendingAndActiveTournamentsForTeam",
            query = "SELECT t FROM Tournament t join t.teamsInTournament team WHERE team.pk = :teamPk AND (t.tournamentInfo.tournamentStatus = :activeStatus OR t.tournamentInfo.tournamentStatus = :pendingStatus)"
    ),
        @NamedQuery(
            name = "Tournament.getAllPendingAndActiveTournamentsForUser",
            query = "SELECT t FROM Tournament t join t.usersInTournament u WHERE u.pk = :userPk AND (t.tournamentInfo.tournamentStatus = :activeStatus OR t.tournamentInfo.tournamentStatus = :pendingStatus)"
    ),
    @NamedQuery(
            name = "Tournament.getTournamentsByUserPk",
            query = "SELECT t FROM Tournament t join t.usersInTournament t1 WHERE t1.pk = :userPk ORDER BY t.tournamentInfo.scheduledTournamentTime ASC"
    ),
        @NamedQuery(
            name = "Tournament.getTournamentsByTeamPk",
            query = "SELECT t FROM Tournament t join t.teamsInTournament t1 WHERE t1.pk = :teamPk ORDER BY t.tournamentInfo.scheduledTournamentTime ASC"
    ),
    @NamedQuery(
            name = QUERY_GET_ALL_TOURNAMENTS_TO_START,
            query = "SELECT t FROM Tournament t WHERE t.tournamentInfo.tournamentStatus = :" + PARAM_TOURNAMENT_STATUS_TO_START + " AND t.tournamentInfo.scheduledTournamentTime <= :" + PARAM_CURRENT_TIME
    ),
    @NamedQuery(
            name = QUERY_GET_ALL_UPCOMING_TOURNAMENTS_TO_NOTIFY,
            query = "SELECT t FROM Tournament t WHERE t.tournamentInfo.tournamentStatus = :" + PARAM_TOURNAMENT_STATUS_TO_START + " AND t.tournamentInfo.scheduledTournamentTime <= :" + PARAM_CURRENT_TIME_PLUS_NOTIFICATION_TIME
    ),
    @NamedQuery(
            name = "Tournament.getTournamentByTournamentIdAndUserPk",
            query = "SELECT t FROM Tournament t join t.usersInTournament t1 WHERE t.tournamentId = :tournamentId AND t1.pk = :userPk"
    )

})

@Entity
public class Tournament extends SuperEntity {

    public final static String QUERY_GET_ALL_TOURNAMENTS_TO_START = "Tournament.getAllTournamentsToStart";
    public final static String QUERY_GET_ALL_UPCOMING_TOURNAMENTS_TO_NOTIFY = "Tournament.getAllUpcomingTournamentsToNotify";

    public final static String PARAM_TOURNAMENT_STATUS_TO_START = "tournamentStatusToStart";
    public final static String PARAM_CURRENT_TIME = "currentTime";
    public final static String PARAM_CURRENT_TIME_PLUS_NOTIFICATION_TIME = "currentTimePlusTenMin";

    @Embedded
    TournamentPojo tournamentInfo = new TournamentPojo();

    private int tournamentId;

    //This is to keep track of the number of teams when tournament starts and when teams get eliminated
    @ElementCollection
    @CollectionTable(name = "list_Of_Teams_Remaining_In_Tournament", joinColumns = @JoinColumn(name = "tournament_pk"))
    @Column(name = "team_pk")
    private List<Long> listOfTeamsRemainingInTournament = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "list_Of_Tournament_Donation_Transactions", joinColumns = @JoinColumn(name = "tournament_pk"))
    @Column(name = "donation_Transactions")
    private List<Transaction> tournamentDonationTransactions = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tournament")
    private Set<TournamentInvite> tournamentInvites = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tournament")
    private Set<TournamentTeam> tournamentTeams = new HashSet<>(0);

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_and_tournament", joinColumns = {
        @JoinColumn(name = "tournamentPk")}, inverseJoinColumns = {
        @JoinColumn(name = "userPk")})
    private Set<User> usersInTournament = new HashSet<>(0);

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "team_and_tournament", joinColumns = {
        @JoinColumn(name = "tournamentPk")}, inverseJoinColumns = {
        @JoinColumn(name = "teamPk")})
    private Set<Team> teamsInTournament = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tournament")
    private Set<Match> matches = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tournament")
    private Set<Transaction> transactions = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tournament")
    private Set<Notification> notification = new HashSet<>(0);

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "tournamentResponsePk")
    private TournamentResponse tournamentResponse;

    @OneToOne(fetch = FetchType.LAZY)//cascade=CascadeType.ALL
    @JoinColumn(nullable = false, name = "tournamentMapsPk")
    private TournamentMaps tournamentMaps;

    public TournamentMaps getTournamentMaps() {
        return tournamentMaps;
    }

    public void setTournamentMaps(TournamentMaps tournamentMaps) {
        this.tournamentMaps = tournamentMaps;
    }

    public List<Long> getListOfTeamsRemainingInTournament() {
        return listOfTeamsRemainingInTournament;
    }

    public void setListOfTeamsRemainingInTournament(List<Long> listOfTeamsRemainingInTournament) {
        this.listOfTeamsRemainingInTournament = listOfTeamsRemainingInTournament;
    }

    public List<Transaction> getTournamentDonationTransactions() {
        return tournamentDonationTransactions;
    }

    public void setTournamentDonationTransactions(List<Transaction> tournamentDonationTransactions) {
        this.tournamentDonationTransactions = tournamentDonationTransactions;
    }

    public TournamentResponse getTournamentResponse() {
        return tournamentResponse;
    }

    public void setTournamentResponse(TournamentResponse tournamnentResponse) {
        this.tournamentResponse = tournamnentResponse;
    }

    public Set<Notification> getNotification() {
        return notification;
    }

    public void setNotification(Set<Notification> notification) {
        this.notification = notification;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public TournamentPojo getTournamentInfo() {
        return tournamentInfo;
    }

    public void setTournamentInfo(TournamentPojo tournamentInfo) {
        this.tournamentInfo = tournamentInfo;
    }

    public Set<TournamentInvite> getTournamentInvites() {
        return tournamentInvites;
    }

    public void setTournamentInvites(Set<TournamentInvite> tournamentInvites) {
        this.tournamentInvites = tournamentInvites;
    }

    public Set<TournamentTeam> getTournamentTeams() {
        return tournamentTeams;
    }

    public void setTournamentTeams(Set<TournamentTeam> tournamentTeams) {
        this.tournamentTeams = tournamentTeams;
    }

    public Set<Match> getMatches() {
        return matches;
    }

    public void setMatches(Set<Match> matches) {
        this.matches = matches;
    }

    public Set<User> getUsersInTournament() {
        return usersInTournament;
    }

    public void setUsersInTournament(Set<User> usersInTournament) {
        this.usersInTournament = usersInTournament;
    }

    public Set<Team> getTeamsInTournament() {
        return teamsInTournament;
    }

    public void setTeamsInTournament(Set<Team> teamsInTournament) {
        this.teamsInTournament = teamsInTournament;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

}
