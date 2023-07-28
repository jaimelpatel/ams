package com.ltlogic.db.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;

import com.ltlogic.db.superentity.SuperEntity;
import com.ltlogic.pojo.TeamPojo;
import java.math.BigDecimal;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@NamedQueries({
    @NamedQuery(
            name = "Team.getTeamByPk",
            query = "SELECT t FROM Team t WHERE t.pk = :pk"
    )
    ,
    @NamedQuery(
            name = "Team.getTeamByName",
            query = "SELECT t FROM Team t WHERE t.teamPojo.teamName = :name AND t.teamPojo.teamStatus = 'LIVE'"
    )
    ,
    @NamedQuery(
            name = "Team.getTeamByNameAndGameAndPlatformAndTypeAndSizeAndStatus",
            query = "SELECT t FROM Team t WHERE LOWER(t.teamPojo.teamName) = :teamName AND t.teamPojo.game = :gameEnum AND t.teamPojo.platform = :platformEnum AND t.teamPojo.teamSize = :teamSizeEnum AND t.teamPojo.teamType = :teamTypeEnum AND t.teamPojo.region = :regionEnum AND t.teamPojo.teamStatus = :teamStatusEnum"
    )
    ,
    @NamedQuery(
            name = "Team.getTeamByUsernameAndGameAndPlatformAndTypeAndSize",
            query = "SELECT t FROM Team t JOIN t.members u WHERE u.username= :username AND t.teamPojo.game = :gameEnum AND t.teamPojo.platform = :platformEnum AND t.teamPojo.teamSize = :teamSizeEnum AND t.teamPojo.teamType = :teamTypeEnum AND t.teamPojo.region = :regionEnum AND t.teamPojo.teamStatus = :teamStatusEnum"
    )
    ,
    @NamedQuery(
            name = "Team.getTeamByTeamId",
            query = "SELECT t FROM Team t WHERE t.teamId = :teamId"
    )
    ,
    @NamedQuery(
            name = "Team.getTeamsByUsername",
            query = "SELECT t FROM Team t JOIN t.members u WHERE u.username= :username AND t.teamPojo.teamStatus = 'LIVE' ORDER by t.rowCreatedTimestamp ASC"
    )
    ,
    @NamedQuery(
            name = "Team.getTeamsByUsernameAndTeamType",
            query = "SELECT t FROM Team t JOIN t.members u WHERE u.username= :username AND t.teamPojo.teamType= :teamType AND t.teamPojo.teamStatus = 'LIVE' ORDER by t.rowCreatedTimestamp ASC"
    )
    ,
    @NamedQuery(
            name = "Team.getTeamsByMatchPk",
            query = "SELECT t FROM Team t JOIN t.matches m WHERE m.pk = :matchPk ORDER BY CASE WHEN t.pk = m.pkOfTeamThatCreatedMatch THEN 1 ELSE 2 END"
    )
//ORDER BY CASE WHEN u.pk = t.teamLeaderPk THEN 1 ELSE 2 END, u.rowCreatedTimestamp
//    @NamedQuery(
//            name = "Team.getCountOfTeamsByRegionAndGameAndPlatformAndTypeAndSize",
//            query = "SELECT count(*) FROM Team t WHERE t.teamPojo.region = :regionEnum AND t.teamPojo.game = :gameEnum AND t.teamPojo.platform = :platformEnum AND t.teamPojo.teamSize = :teamSizeEnum AND t.teamPojo.teamType = :teamTypeEnum"
//    )

})

@Entity
//@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
//@AuditTable(value = "team_history", schema = "xms")
@Inheritance(strategy = InheritanceType.JOINED)
public class Team extends SuperEntity {

    @Embedded
    private TeamPojo teamPojo = new TeamPojo();

    private int teamId;

    private int matchesPlayed;
    
    private int disputedMatches;
    
    private BigDecimal disputePercentage = new BigDecimal(0.00);

    private int matchesWon;

    private int matchesLost;

    private long teamLeaderPk;

    private int minimumPlayers;

    private int maximumPlayers;

    private boolean isEligibleForPlay;

    private String displayUrl = "https://storage.googleapis.com/nlg-template-img/logo/NL1red.jpg";

    private int goldTrophyCount;
    private int silverTrophyCount;
    private int bronzeTrophyCount;
    private boolean xpGainOn = Boolean.TRUE;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "teams")
    private Set<User> members = new HashSet<>(0);

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "teamsInMatch")
    private Set<Match> matches = new HashSet<>(0);

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "teamsInTournament")
    private Set<Tournament> tournaments = new HashSet<>(0);

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "team")
    private Set<TeamInvite> teamInvites = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    private Set<MatchInvite> matchInvites = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    private Set<TournamentTeam> tournamentTeams = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    private Set<TeamPermissions> teamPermissions = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    private Set<Dispute> disputes = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    private Set<Notification> notification = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    private Set<Conversation> conversation = new HashSet<>(0);

    private long cloudMediaPkForProfilePic;

    public boolean isXpGainOn() {
        return xpGainOn;
    }

    public void setXpGainOn(boolean xpGainOn) {
        this.xpGainOn = xpGainOn;
    }

    public BigDecimal getDisputePercentage() {
        return disputePercentage;
    }

    public void setDisputePercentage(BigDecimal disputePercentage) {
        this.disputePercentage = disputePercentage;
    }

    public int getDisputedMatches() {
        return disputedMatches;
    }

    public void setDisputedMatches(int disputedMatches) {
        this.disputedMatches = disputedMatches;
    }

    public int getGoldTrophyCount() {
        return goldTrophyCount;
    }

    public void setGoldTrophyCount(int goldTrophyCount) {
        this.goldTrophyCount = goldTrophyCount;
    }

    public int getSilverTrophyCount() {
        return silverTrophyCount;
    }

    public void setSilverTrophyCount(int silverTrophyCount) {
        this.silverTrophyCount = silverTrophyCount;
    }

    public int getBronzeTrophyCount() {
        return bronzeTrophyCount;
    }

    public void setBronzeTrophyCount(int bronzeTrophyCount) {
        this.bronzeTrophyCount = bronzeTrophyCount;
    }

    public Set<Conversation> getConversation() {
        return conversation;
    }

    public void setConversation(Set<Conversation> conversation) {
        this.conversation = conversation;
    }

    public Set<Notification> getNotification() {
        return notification;
    }

    public void setNotification(Set<Notification> notification) {
        this.notification = notification;
    }

    public Set<Dispute> getDisputes() {
        return disputes;
    }

    public void setDisputes(Set<Dispute> disputes) {
        this.disputes = disputes;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public Set<Match> getMatches() {
        return matches;
    }

    public void setMatches(Set<Match> matches) {
        this.matches = matches;
    }

    public Set<TeamInvite> getTeamInvites() {
        return teamInvites;
    }

    public void setTeamInvites(Set<TeamInvite> teamInvites) {
        this.teamInvites = teamInvites;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public int getMatchesWon() {
        return matchesWon;
    }

    public void setMatchesWon(int matchesWon) {
        this.matchesWon = matchesWon;
    }

    public long getTeamLeaderPk() {
        return teamLeaderPk;
    }

    public void setTeamLeaderPk(long teamLeaderPk) {
        this.teamLeaderPk = teamLeaderPk;
    }

    public int getMinimumPlayers() {
        return minimumPlayers;
    }

    public void setMinimumPlayers(int minimumPlayers) {
        this.minimumPlayers = minimumPlayers;
    }

    public int getMaximumPlayers() {
        return maximumPlayers;
    }

    public void setMaximumPlayers(int maximumPlayers) {
        this.maximumPlayers = maximumPlayers;
    }

    public Set<TournamentTeam> getTournamentTeams() {
        return tournamentTeams;
    }

    public void setTournamentTeams(Set<TournamentTeam> tournamentTeams) {
        this.tournamentTeams = tournamentTeams;
    }

    public boolean isIsEligibleForPlay() {
        return isEligibleForPlay;
    }

    public void setIsEligibleForPlay(boolean isEligibleForPlay) {
        this.isEligibleForPlay = isEligibleForPlay;
    }

    public TeamPojo getTeamPojo() {
        return teamPojo;
    }

    public void setTeamPojo(TeamPojo teamPojo) {
        this.teamPojo = teamPojo;
    }

    public Set<TeamPermissions> getTeamPermissions() {
        return teamPermissions;
    }

    public void setTeamPermissions(Set<TeamPermissions> teamPermissions) {
        this.teamPermissions = teamPermissions;
    }

    public Set<MatchInvite> getMatchInvites() {
        return matchInvites;
    }

    public void setMatchInvites(Set<MatchInvite> matchInvites) {
        this.matchInvites = matchInvites;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public int getMatchesLost() {
        return matchesLost;
    }

    public void setMatchesLost(int matchesLost) {
        this.matchesLost = matchesLost;
    }

    public long getCloudMediaPkForProfilePic() {
        return cloudMediaPkForProfilePic;
    }

    public void setCloudMediaPkForProfilePic(long cloudMediaPkForProfilePic) {
        this.cloudMediaPkForProfilePic = cloudMediaPkForProfilePic;
    }

    public Set<Tournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(Set<Tournament> tournaments) {
        this.tournaments = tournaments;
    }

}
