package com.ltlogic.db.entity;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.db.entity.rank.user.UserRankXP;
import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.db.entity.rank.user.UserRankEarnings;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import com.ltlogic.db.superentity.SuperEntity;
import com.ltlogic.pojo.UserPojo;
import java.util.ArrayList;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import javax.persistence.Table;

@NamedQueries({
    @NamedQuery(
            name = "User.getAllUsers",
            query = "SELECT u FROM User u"
    ),
    @NamedQuery(
            name = "User.getUserByPk",
            query = "SELECT u FROM User u WHERE u.pk = :pk"
    ),
    @NamedQuery(
            name = "User.getAllUsersInList",
            query = "SELECT u FROM User u WHERE u.pk IN :userList"
    ),
    @NamedQuery(
            name = "User.getUsersByPks",
            query = "SELECT u FROM User u WHERE u.pk IN :userPks ORDER BY u.username ASC"
    ),
    @NamedQuery(
            name = "User.getUserByUsername",
            query = "SELECT u FROM User u WHERE u.username = :username"
    ),
//    @NamedQuery(
//            name = "User.getUserByUsernameLowercase",
//            query = "SELECT u FROM User u WHERE u.usernameLowercase = :username"
//    ),
    @NamedQuery(
            name = "User.getUserByUsernameLowercase",
            query = "SELECT u FROM User u WHERE LOWER(u.username) = :username"
    ),
    @NamedQuery(
            name = "User.getUserByEmail",
            query = "SELECT u FROM User u WHERE u.email = :email"
    ),
    @NamedQuery(
            name = "User.getUserByUsernameAndPassword",
            query = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password"
    ),
    @NamedQuery(
            name = "User.getUsersOnTeam",
            query = "SELECT u FROM User u JOIN u.teams t WHERE t.pk= :teamPk ORDER BY CASE WHEN u.pk = t.teamLeaderPk THEN 1 ELSE 2 END, u.rowCreatedTimestamp"
    ),
    @NamedQuery(
            name = "User.getUsersByMatchPk",
            query = "SELECT u FROM User u JOIN u.matches m WHERE m.pk= :matchPk ORDER BY u.rowCreatedTimestamp"
    ),
    @NamedQuery(
            name = "User.getUsersByMatchPkAndTeamPk",
            query = "SELECT u FROM User u JOIN u.matches m JOIN u.teams t WHERE m.pk= :matchPk AND t.pk = :teamPk ORDER BY CASE WHEN u.pk = t.teamLeaderPk THEN 1 ELSE 2 END, u.rowCreatedTimestamp"
    ),
    @NamedQuery(
            name = "User.getUsersOnTeamOrderByMatchAcceptor",
            query = "SELECT u FROM User u JOIN u.teams t WHERE t.pk= :teamPk ORDER BY CASE WHEN u.pk = :matchAcceptorPk THEN 1 ELSE 2 END, u.rowCreatedTimestamp"
    ),
    @NamedQuery(
            name = "User.getTop100UsersByXP",
            query = "SELECT u FROM User u JOIN u.rankXP r ORDER BY r.rank ASC"
    ),
    @NamedQuery(
            name = "User.getTop100UsersByEarnings",
            query = "SELECT u FROM User u JOIN u.rankEarnings r ORDER BY r.rank ASC"
    ),
    @NamedQuery(
            name = "User.getUsersBySearchedUsername",
            query = "SELECT u FROM User u WHERE u.username LIKE :username ORDER BY u.rowUpdatedTimestamp DESC"
    )
//    @NamedQuery(
//            name = "User.getCountOfUserTable",
//            query = "SELECT count(*) FROM User u"
//    )
})
/*
 from DomesticCat as cat
 where cat.name not in (
 select name.nickName from Name as name
 )
 */
@Entity
@Table(name = "UserBase")
//@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
//@AuditTable(value = "user_history", schema = "xms")
public class User extends SuperEntity {

    @Embedded
    UserPojo userInfo = new UserPojo();

    private String username;
    private String password;
    
    private String usernameLowercase;

    private String email;
    private boolean isVerified;
    private boolean isEligible;
    private int goldTrophyCount;
    private int silverTrophyCount;
    private int bronzeTrophyCount;
    private long totalEarnings;
    private ArrayList<Long> pinnedUsersPkList = new ArrayList<Long>();
    private ArrayList<Long> teamPkInvites = new ArrayList<Long>();
    private LocalDateTime lastLoggedIn;
    private String displayUrl = "https://storage.googleapis.com/xms-user-profile-pic-prod/team-member-1.jpg";
    private long cloudMediaPkForProfilePic;
    private int numberOfNotifications;

    private LocalDateTime testingJavaTimeinDb = DateTimeUtil.getDefaultLocalDateTimeNow();

    public User() {
        super();
        this.isVerified = false;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cancellationRequester")
    private Set<MatchCancellationRequest> cancellationRequester = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cancellationRequestee")
    private Set<MatchCancellationRequest> cancellationRequestee = new HashSet<>(0);

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_and_team", joinColumns = {
        @JoinColumn(name = "userPk")}, inverseJoinColumns = {
        @JoinColumn(name = "teamPk")})
    private Set<Team> teams = new HashSet<>(0);

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "usersInMatch")
    private Set<Match> matches = new HashSet<>(0);

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "usersInTournament")
    private Set<Tournament> tournaments = new HashSet<>(0);

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "userPk"), inverseJoinColumns = @JoinColumn(name = "rolePk"))
    private Set<Role> roles = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userSender")
    private Set<TeamInvite> teamInvitesSent = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<TournamentInvite> tournamentInvites = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userReceiver")
    private Set<TeamInvite> teamInvitesReceived = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<MatchInvite> matchInvites = new HashSet<>(0);

    @OneToOne(mappedBy = "user")
    private VerificationToken vToken;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<TeamPermissions> teamPermissions = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Notification> notification = new HashSet<>(0);

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Bank bank;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Transaction> transactions = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Dispute> disputes = new HashSet<>(0);

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserRankXP rankXP;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserRankEarnings rankEarnings;

    public String getUsernameLowercase() {
        return usernameLowercase;
    }

    public void setUsernameLowercase(String usernameLowercase) {
        this.usernameLowercase = usernameLowercase;
    }

    public int getNumberOfNotifications() {
        return numberOfNotifications;
    }

    public void setNumberOfNotifications(int numberOfNotifications) {
        this.numberOfNotifications = numberOfNotifications;
    }

    public Set<MatchCancellationRequest> getCancellationRequester() {
        return cancellationRequester;
    }

    public void setCancellationRequester(Set<MatchCancellationRequest> cancellationRequester) {
        this.cancellationRequester = cancellationRequester;
    }

    public Set<MatchCancellationRequest> getCancellationRequestee() {
        return cancellationRequestee;
    }

    public void setCancellationRequestee(Set<MatchCancellationRequest> cancellationRequestee) {
        this.cancellationRequestee = cancellationRequestee;
    }

    public Set<Notification> getNotification() {
        return notification;
    }

    public void setNotification(Set<Notification> notification) {
        this.notification = notification;
    }

    public UserRankXP getRankXP() {
        return rankXP;
    }

    public void setRankXP(UserRankXP rankXP) {
        this.rankXP = rankXP;
    }

    public UserRankEarnings getRankEarnings() {
        return rankEarnings;
    }

    public void setRankEarnings(UserRankEarnings rankEarnings) {
        this.rankEarnings = rankEarnings;
    }

    public Set<TournamentInvite> getTournamentInvites() {
        return tournamentInvites;
    }

    public void setTournamentInvites(Set<TournamentInvite> tournamentInvites) {
        this.tournamentInvites = tournamentInvites;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Set<Dispute> getDisputes() {
        return disputes;
    }

    public void setDisputes(Set<Dispute> disputes) {
        this.disputes = disputes;
    }

    public UserPojo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserPojo userInfo) {
        this.userInfo = userInfo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isIsVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public boolean isIsEligible() {
        return isEligible;
    }

    public void setIsEligible(boolean isEligible) {
        this.isEligible = isEligible;
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

    public long getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(long totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public ArrayList<Long> getPinnedUsersPkList() {
        return pinnedUsersPkList;
    }

    public void setPinnedUsersPkList(ArrayList<Long> pinnedUsersPkList) {
        this.pinnedUsersPkList = pinnedUsersPkList;
    }

    public ArrayList<Long> getTeamPkInvites() {
        return teamPkInvites;
    }

    public void setTeamPkInvites(ArrayList<Long> teamPkInvites) {
        this.teamPkInvites = teamPkInvites;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    public Set<Match> getMatches() {
        return matches;
    }

    public void setMatches(Set<Match> matches) {
        this.matches = matches;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<TeamInvite> getTeamInvitesSent() {
        return teamInvitesSent;
    }

    public void setTeamInvitesSent(Set<TeamInvite> teamInvitesSent) {
        this.teamInvitesSent = teamInvitesSent;
    }

    public Set<TeamInvite> getTeamInvitesReceived() {
        return teamInvitesReceived;
    }

    public void setTeamInvitesReceived(Set<TeamInvite> teamInvitesReceived) {
        this.teamInvitesReceived = teamInvitesReceived;
    }

    public VerificationToken getvToken() {
        return vToken;
    }

    public void setvToken(VerificationToken vToken) {
        this.vToken = vToken;
    }

    public Set<TeamPermissions> getTeamPermissions() {
        return teamPermissions;
    }

    public void setTeamPermissions(Set<TeamPermissions> teamPermissions) {
        this.teamPermissions = teamPermissions;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public LocalDateTime getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(LocalDateTime lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getTestingJavaTimeinDb(TimeZoneEnum tze) {
        LocalDateTime testingTime = DateTimeUtil.getUserLocalDateTime(testingJavaTimeinDb, tze);
        return DateTimeUtil.formatLocalDateTime(testingTime);
    }

    public void setTestingJavaTimeinDb(LocalDateTime testingJavaTimeinDb) {
        this.testingJavaTimeinDb = testingJavaTimeinDb;
    }

    public LocalDateTime getTestingJavaTimeinDb() {
        return testingJavaTimeinDb;
    }

    public Set<MatchInvite> getMatchInvites() {
        return matchInvites;
    }

    public void setMatchInvites(Set<MatchInvite> matchInvites) {
        this.matchInvites = matchInvites;
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
