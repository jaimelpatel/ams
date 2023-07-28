package com.ltlogic.db.entity;

import com.ltlogic.DateTimeUtil;
import static com.ltlogic.db.entity.Match.PARAM_CURRENT_TIME;
import static com.ltlogic.db.entity.Match.PARAM_CURRENT_TIME_PLUS_NOTIFICATION_TIME;
import static com.ltlogic.db.entity.Match.PARAM_LIST_OF_MATCH_STATUSES_TO_KILL;
import static com.ltlogic.db.entity.Match.PARAM_MATCH_STATUS_TO_START;
import static com.ltlogic.db.entity.Match.QUERY_GET_ALL_MATCHES_TO_KILL;
import static com.ltlogic.db.entity.Match.QUERY_GET_ALL_MATCHES_TO_START;
import static com.ltlogic.db.entity.Match.QUERY_GET_ALL_PUBLIC_WAGER_MATCHES_TO_KILL;
import static com.ltlogic.db.entity.Match.QUERY_GET_ALL_UPCOMING_MATCHES_TO_NOTIFY;
import com.ltlogic.db.entity.challonge.MatchResponse;
import com.ltlogic.db.entity.challonge.ParticipantResponse;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.ltlogic.db.superentity.SuperEntity;
import com.ltlogic.pojo.MatchPojo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Enumerated;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

@NamedQueries({
    @NamedQuery(
            name = "Match.getMatchByMatchId",
            query = "SELECT m FROM Match m WHERE m.matchId = :matchId"
    ),
    @NamedQuery(
            name = "Match.findAllActiveXpAndWagerMatchesToCancel",
            query = "SELECT m FROM Match m WHERE m.matchInfo.matchStatus = 'ACTIVE' AND (m.matchInfo.matchType = 'XP' OR m.matchInfo.matchType = 'WAGER') AND :currentTimeMinus3Hours >= m.matchInfo.scheduledMatchTime AND m.pkOfUserThatReportedMatchScoreAccepted = '0' AND m.pkOfUserThatReportedMatchScoreCreated = '0'"
    ),
    @NamedQuery(
            name = "Match.findAllActiveXpAndWagerMatchesToEnd",
            query = "SELECT m FROM Match m WHERE m.matchInfo.matchStatus = 'ACTIVE' AND (m.matchInfo.matchType = 'XP' OR m.matchInfo.matchType = 'WAGER') AND :currentTimeMinus3Hours >= m.matchInfo.scheduledMatchTime AND ((m.pkOfUserThatReportedMatchScoreAccepted = '0' AND m.pkOfUserThatReportedMatchScoreCreated != '0') OR (m.pkOfUserThatReportedMatchScoreAccepted != '0' AND m.pkOfUserThatReportedMatchScoreCreated = '0'))"
    ),
    @NamedQuery(
            name = "Match.getMatchesByUserPk",
            query = "SELECT m FROM Match m JOIN m.usersInMatch u WHERE u.pk= :userPk ORDER BY m.rowCreatedTimestamp DESC"
    ),
        @NamedQuery(
            name = "Match.findMatchesByUserPkAndMatchStatus",
            query = "SELECT m FROM Match m JOIN m.usersInMatch u WHERE u.pk= :userPk AND m.matchInfo.matchStatus = :matchStatusEnum ORDER BY CASE WHEN m.matchInfo.scheduledMatchTime IS NULL THEN 1 ELSE 0 END desc"
    ),   
    @NamedQuery(
            name = "Match.getMatchesByTeamPk",
            query = "SELECT m FROM Match m JOIN m.teamsInMatch t WHERE t.pk= :teamPk ORDER BY m.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "Match.findMatchesByTeamPkInActiveDisputedAndEndedStatus",
            query = "SELECT m FROM Match m JOIN m.teamsInMatch t WHERE t.pk= :teamPk  AND (m.matchInfo.matchStatus = 'ACTIVE' OR m.matchInfo.matchStatus = 'DISPUTED' OR m.matchInfo.matchStatus = 'ENDED') ORDER BY m.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "Match.getMatchesByTeamPkAndMatchPendingAndWaitingAndActiveStatus",
            query = "SELECT m FROM Match m JOIN m.teamsInMatch t WHERE t.pk= :teamPk AND (m.matchInfo.matchStatus = :pendingStatus OR m.matchInfo.matchStatus = :readyToPlayStatus OR m.matchInfo.matchStatus = :activeStatus OR m.matchInfo.matchStatus = :firstAcceptStatus OR m.matchInfo.matchStatus = :secondAcceptStatus) ORDER BY m.rowCreatedTimestamp DESC"
    ),
        @NamedQuery(
            name = "Match.getMatchesByUserPkAndMatchPendingAndWaitingAndActiveStatus",
            query = "SELECT m FROM Match m JOIN m.usersInMatch u WHERE u.pk= :userPk AND (m.matchInfo.matchStatus = :pendingStatus OR m.matchInfo.matchStatus = :readyToPlayStatus OR m.matchInfo.matchStatus = :activeStatus OR m.matchInfo.matchStatus = :firstAcceptStatus OR m.matchInfo.matchStatus = :secondAcceptStatus) ORDER BY m.matchInfo.scheduledMatchTime DESC"
    ),
    @NamedQuery(
            name = "Match.getAllMatchesPublic",
            query = "SELECT m FROM Match m where m.matchInfo.isMatchPublic = :isMatchPublic and m.matchInfo.matchStatus = :matchStatus order by m.matchInfo.scheduledMatchTime desc"
    ),
    @NamedQuery(
            name = QUERY_GET_ALL_MATCHES_TO_START,
            query = "SELECT m FROM Match m where m.haveAllPlayersAccepted = true AND m.matchInfo.matchStatus = :" + PARAM_MATCH_STATUS_TO_START + " AND m.matchInfo.scheduledMatchTime <= :" + PARAM_CURRENT_TIME + " ORDER BY m.matchInfo.scheduledMatchTime desc"
    ),
    @NamedQuery(
            name = QUERY_GET_ALL_MATCHES_TO_KILL,
            query = "SELECT m FROM Match m where m.matchInfo.matchType != 'TOURNAMENT' AND m.matchInfo.matchStatus IN :" + PARAM_LIST_OF_MATCH_STATUSES_TO_KILL + " AND m.matchInfo.scheduledMatchTime <= :" + PARAM_CURRENT_TIME + " ORDER BY m.matchInfo.scheduledMatchTime desc"
    ),
    @NamedQuery( // @TODO:check the notification here for notification type  = upcoming match
            name = QUERY_GET_ALL_UPCOMING_MATCHES_TO_NOTIFY,
            query = "SELECT m FROM Match m where m.haveAllPlayersAccepted = true AND m.matchInfo.matchStatus = :" + PARAM_MATCH_STATUS_TO_START + " AND m.matchInfo.scheduledMatchTime <= :" + PARAM_CURRENT_TIME_PLUS_NOTIFICATION_TIME + " ORDER BY m.matchInfo.scheduledMatchTime desc"
    ),
    @NamedQuery( // @TODO:check the notification here for notification type  = upcoming match
            name = QUERY_GET_ALL_PUBLIC_WAGER_MATCHES_TO_KILL,
            query = "SELECT m FROM Match m where m.matchInfo.matchStatus = 'WAITING_ON_SECOND_ACCEPT' AND :currentTimeMinus15Minutes >= m.timeMatchWasAccepted"
    ),
    @NamedQuery(
            name = "Match.findUserMatchByMatchPKAndUsername",
            query = "Select m from Match m JOIN m.usersInMatch u where m.pk = :matchPk AND u.username = :username"
    ),
    @NamedQuery(
            name = "Match.findMatchesByStatusInTournament",
            query = "Select m from Match m where m.matchInfo.matchStatus = :matchStatus AND m.tournament.pk = :tournamentPk"
    ),
    @NamedQuery(
            name = "Match.getXpMatchesByTeamPkAndActiveOrEndedOrDisputedStatus",
            query = "SELECT m FROM Match m JOIN m.teamsInMatch t WHERE t.pk= :teamPk AND m.matchInfo.matchType = 'XP' AND (m.matchInfo.matchStatus = 'ENDED' OR m.matchInfo.matchStatus = 'DISPUTED' OR m.matchInfo.matchStatus = 'ACTIVE') ORDER BY m.rowCreatedTimestamp DESC"
    ),
    @NamedQuery(
            name = "Match.getMatchesByGame",
            query = "SELECT m FROM Match m where m.matchInfo.isMatchPublic = :isMatchPublic and m.matchInfo.matchStatus = :matchStatus and m.matchInfo.gameEnum = :gameEnum order by m.matchInfo.scheduledMatchTime desc"
    ),
    @NamedQuery(
            name = "Match.getMatchesByGameAndPlatform",
            query = "SELECT m FROM Match m where m.matchInfo.isMatchPublic = :isMatchPublic and m.matchInfo.matchStatus = :matchStatus and m.matchInfo.gameEnum = :gameEnum and m.matchInfo.platform = :platformEnum order by m.matchInfo.scheduledMatchTime desc"
    ),
    @NamedQuery(
            name = "Match.getMatchesByPlatform",
            query = "SELECT m FROM Match m where m.matchInfo.isMatchPublic = :isMatchPublic and m.matchInfo.matchStatus = :matchStatus and m.matchInfo.platform = :platformEnum order by m.matchInfo.scheduledMatchTime desc"
    )

})

@Entity
//@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
//@AuditTable(value = "match_history", schema = "xms")
@Inheritance(strategy = InheritanceType.JOINED)
public class Match extends SuperEntity {

    public final static String QUERY_GET_ALL_MATCHES_TO_START = "Match.getAllMatchesToStart";
    public final static String QUERY_GET_ALL_MATCHES_TO_KILL = "Match.getAllMatchesToKill";
    public final static String QUERY_GET_ALL_UPCOMING_MATCHES_TO_NOTIFY = "Match.getAllUpcomingMatchesToNotify";
    public final static String QUERY_GET_ALL_PUBLIC_WAGER_MATCHES_TO_KILL = "Match.getAllPublicWagerMatchesToKill";

    public final static String PARAM_MATCH_STATUS_TO_START = "matchStatusToStart";
    public final static String PARAM_LIST_OF_MATCH_STATUSES_TO_KILL = "listOfMatchStatusesToKill";
    public final static String PARAM_CURRENT_TIME = "currentTime";
    public final static String PARAM_CURRENT_TIME_PLUS_NOTIFICATION_TIME = "currentTimePlusTenMin";

    @Embedded
    private MatchPojo matchInfo = new MatchPojo();

    private int matchId;

    @ElementCollection
    @CollectionTable(name = "list_Of_Match_Donation_Transactions", joinColumns = @JoinColumn(name = "match_pk"))
    @Column(name = "donation_Transactions")

    private List<Transaction> matchDonationTransactions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "list_Of_Pks_Of_Creator_Team_Members_Playing", joinColumns = @JoinColumn(name = "match_pk"))
    @Column(name = "pks_Of_Creator_Team_Members_Playing")

    private List<Long> pksOfCreatorTeamMembersPlaying = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "list_Of_Pks_Of_Acceptor_Team_Members_Playing", joinColumns = @JoinColumn(name = "match_pk"))
    @Column(name = "pks_Of_Acceptor_Team_Members_Playing")

    private List<Long> pksOfAcceptorTeamMembersPlaying = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "list_Of_Pks_Of_Players_Who_Accepted_Match_Invite", joinColumns = @JoinColumn(name = "match_pk"))
    @Column(name = "pks_Of_Players_Who_Accepted_Match_Invite")

    private List<Long> pksOfPlayersWhoHaveAcceptedMatchInvite = new ArrayList<>();

    private long pkOfUserThatCreatedMatch;
    private long pkOfUserThatAcceptedMatch;
    private long pkOfTeamThatCreatedMatch;
    private long pkOfTeamThatAcceptedMatch;
    private long pkOfTeamWonMatch;
    private long pkOfTeamLostMatch;
    private LocalDateTime timeMatchWasAccepted;
    private boolean isDisputed;
    private boolean haveAllPlayersAccepted;
    private Boolean reportedScoreOfTeamCreated;//true = win, false = loss, null = not reported
    private Boolean reportedScoreOfTeamAccepted;//true = win, false = loss, null = not reported
    private long pkOfUserThatReportedMatchScoreCreated;
    private long pkOfUserThatReportedMatchScoreAccepted;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "team_and_match", joinColumns = {
        @JoinColumn(name = "matchPk")}, inverseJoinColumns = {
        @JoinColumn(name = "teamPk")})
    private Set<Team> teamsInMatch = new HashSet<>(0);

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_and_match", joinColumns = {
        @JoinColumn(name = "matchPk")}, inverseJoinColumns = {
        @JoinColumn(name = "userPk")})
    private Set<User> usersInMatch = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "match")
    private Set<MatchInvite> matchInvites = new HashSet<>(0);

    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_pk", referencedColumnName = "pk")
    private Tournament tournament;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "match")
    private Set<Dispute> disputes = new HashSet<>(0);

    @OneToOne(mappedBy = "match")
    private MatchCancellationRequest cancellationRequest;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "match")
    private Set<Transaction> transactions = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "match")
    private Set<Notification> notification = new HashSet<>(0);

    @OneToOne(mappedBy = "ourMatch", fetch = FetchType.LAZY)
    private MatchResponse matchResponse;

    public void setTimeMatchWasAccepted() {
        this.timeMatchWasAccepted = DateTimeUtil.getDefaultLocalDateTimeNow();
    }

    public MatchPojo getMatchInfo() {
        return matchInfo;
    }

    public void setMatchInfo(MatchPojo matchInfo) {
        this.matchInfo = matchInfo;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public List<Transaction> getMatchDonationTransactions() {
        return matchDonationTransactions;
    }

    public void setMatchDonationTransactions(List<Transaction> matchDonationTransactions) {
        this.matchDonationTransactions = matchDonationTransactions;
    }

    public List<Long> getPksOfCreatorTeamMembersPlaying() {
        return pksOfCreatorTeamMembersPlaying;
    }

    public void setPksOfCreatorTeamMembersPlaying(List<Long> pksOfCreatorTeamMembersPlaying) {
        this.pksOfCreatorTeamMembersPlaying = pksOfCreatorTeamMembersPlaying;
    }

    public List<Long> getPksOfAcceptorTeamMembersPlaying() {
        return pksOfAcceptorTeamMembersPlaying;
    }

    public void setPksOfAcceptorTeamMembersPlaying(List<Long> pksOfAcceptorTeamMembersPlaying) {
        this.pksOfAcceptorTeamMembersPlaying = pksOfAcceptorTeamMembersPlaying;
    }

    public List<Long> getPksOfPlayersWhoHaveAcceptedMatchInvite() {
        return pksOfPlayersWhoHaveAcceptedMatchInvite;
    }

    public void setPksOfPlayersWhoHaveAcceptedMatchInvite(List<Long> pksOfPlayersWhoHaveAcceptedMatchInvite) {
        this.pksOfPlayersWhoHaveAcceptedMatchInvite = pksOfPlayersWhoHaveAcceptedMatchInvite;
    }

    public long getPkOfUserThatCreatedMatch() {
        return pkOfUserThatCreatedMatch;
    }

    public void setPkOfUserThatCreatedMatch(long pkOfUserThatCreatedMatch) {
        this.pkOfUserThatCreatedMatch = pkOfUserThatCreatedMatch;
    }

    public long getPkOfUserThatAcceptedMatch() {
        return pkOfUserThatAcceptedMatch;
    }

    public void setPkOfUserThatAcceptedMatch(long pkOfUserThatAcceptedMatch) {
        this.pkOfUserThatAcceptedMatch = pkOfUserThatAcceptedMatch;
    }

    public long getPkOfTeamThatCreatedMatch() {
        return pkOfTeamThatCreatedMatch;
    }

    public void setPkOfTeamThatCreatedMatch(long pkOfTeamThatCreatedMatch) {
        this.pkOfTeamThatCreatedMatch = pkOfTeamThatCreatedMatch;
    }

    public long getPkOfTeamThatAcceptedMatch() {
        return pkOfTeamThatAcceptedMatch;
    }

    public void setPkOfTeamThatAcceptedMatch(long pkOfTeamThatAcceptedMatch) {
        this.pkOfTeamThatAcceptedMatch = pkOfTeamThatAcceptedMatch;
    }

    public long getPkOfTeamWonMatch() {
        return pkOfTeamWonMatch;
    }

    public void setPkOfTeamWonMatch(long pkOfTeamWonMatch) {
        this.pkOfTeamWonMatch = pkOfTeamWonMatch;
    }

    public long getPkOfTeamLostMatch() {
        return pkOfTeamLostMatch;
    }

    public void setPkOfTeamLostMatch(long pkOfTeamLostMatch) {
        this.pkOfTeamLostMatch = pkOfTeamLostMatch;
    }

    public LocalDateTime getTimeMatchWasAccepted() {
        return timeMatchWasAccepted;
    }

    public void setTimeMatchWasAccepted(LocalDateTime timeMatchWasAccepted) {
        this.timeMatchWasAccepted = timeMatchWasAccepted;
    }

    public boolean isIsDisputed() {
        return isDisputed;
    }

    public void setIsDisputed(boolean isDisputed) {
        this.isDisputed = isDisputed;
    }

    public boolean isHaveAllPlayersAccepted() {
        return haveAllPlayersAccepted;
    }

    public void setHaveAllPlayersAccepted(boolean haveAllPlayersAccepted) {
        this.haveAllPlayersAccepted = haveAllPlayersAccepted;
    }

    public Boolean getReportedScoreOfTeamCreated() {
        return reportedScoreOfTeamCreated;
    }

    public void setReportedScoreOfTeamCreated(Boolean reportedScoreOfTeamCreated) {
        this.reportedScoreOfTeamCreated = reportedScoreOfTeamCreated;
    }

    public Boolean getReportedScoreOfTeamAccepted() {
        return reportedScoreOfTeamAccepted;
    }

    public void setReportedScoreOfTeamAccepted(Boolean reportedScoreOfTeamAccepted) {
        this.reportedScoreOfTeamAccepted = reportedScoreOfTeamAccepted;
    }

    public long getPkOfUserThatReportedMatchScoreCreated() {
        return pkOfUserThatReportedMatchScoreCreated;
    }

    public void setPkOfUserThatReportedMatchScoreCreated(long pkOfUserThatReportedMatchScoreCreated) {
        this.pkOfUserThatReportedMatchScoreCreated = pkOfUserThatReportedMatchScoreCreated;
    }

    public long getPkOfUserThatReportedMatchScoreAccepted() {
        return pkOfUserThatReportedMatchScoreAccepted;
    }

    public void setPkOfUserThatReportedMatchScoreAccepted(long pkOfUserThatReportedMatchScoreAccepted) {
        this.pkOfUserThatReportedMatchScoreAccepted = pkOfUserThatReportedMatchScoreAccepted;
    }

    public Set<Team> getTeamsInMatch() {
        return teamsInMatch;
    }

    public void setTeamsInMatch(Set<Team> teamsInMatch) {
        this.teamsInMatch = teamsInMatch;
    }

    public Set<User> getUsersInMatch() {
        return usersInMatch;
    }

    public void setUsersInMatch(Set<User> usersInMatch) {
        this.usersInMatch = usersInMatch;
    }

    public Set<MatchInvite> getMatchInvites() {
        return matchInvites;
    }

    public void setMatchInvites(Set<MatchInvite> matchInvites) {
        this.matchInvites = matchInvites;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Set<Dispute> getDisputes() {
        return disputes;
    }

    public void setDisputes(Set<Dispute> disputes) {
        this.disputes = disputes;
    }

    public MatchCancellationRequest getCancellationRequest() {
        return cancellationRequest;
    }

    public void setCancellationRequest(MatchCancellationRequest cancellationRequest) {
        this.cancellationRequest = cancellationRequest;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Set<Notification> getNotification() {
        return notification;
    }

    public void setNotification(Set<Notification> notification) {
        this.notification = notification;
    }

    public MatchResponse getMatchResponse() {
        return matchResponse;
    }

    public void setMatchResponse(MatchResponse matchResponse) {
        this.matchResponse = matchResponse;
    }

    

}
