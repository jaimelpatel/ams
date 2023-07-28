/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.pojo;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.BestOfEnum;
import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.GameModeEnum;
import com.ltlogic.constants.MatchSizeEnum;
import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.constants.TournamentFormatEnum;
import com.ltlogic.constants.TournamentStatusEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;

/**
 * 
 * @author Hoang
 */
@Embeddable
public class TournamentPojo {
    
    private String title; 
    
    private int maxTeamCount = 256;
    
    private int teamCount;

    private int maxRounds;
    
    @Enumerated(EnumType.STRING)
    private TournamentStatusEnum tournamentStatus;
    
    @Enumerated(EnumType.STRING)
    private TournamentFormatEnum tournamentFormat;
    
    private BigDecimal amountAwardedToFirstPlace;
    
    private BigDecimal amountAwardedToSecondPlace;
    
    private BigDecimal amountAwardedToThirdPlace;
    
    private BigDecimal wagerAmountPerMember;
    
    private BigDecimal potAmount;
    
    private BigDecimal potAmountWithoutDonations;
    
    private LocalDateTime scheduledTournamentTime;
    
    private LocalDateTime tournamentStartTime;
    
    private LocalDateTime tournamentEndTime;
    
    @Enumerated(EnumType.STRING)
    private BestOfEnum bestOfEnum;
    
    @Enumerated(EnumType.STRING)
    private BestOfEnum bestOfEnumFinals;
    
    //#########################################   For statistics later   #########################################
    
    private int numOfMatches;
    
    private int numOfTeamsSignedUp;
   
    private int numOfTeamsInTournament;
   
    private int numOfPlayersInTournament;
    
    private int numOfPlayers;
    
    private int numOfTeamsNeededToStart;
    
    private int numOfTeamsReady;
    
     private int numOfPlayersNeedingToAcceptForTournamentStart;
    
    private int numOfTeamsNeedingToAccept;
    
    private int numOfPlayersPerTeam;
    
    private int numOfPlayersNeedingToAccept;
    
    @Enumerated(EnumType.STRING)
    private MatchTypeEnum matchType;
    
    @Enumerated(EnumType.STRING)
    private MatchSizeEnum matchSize;

    @Enumerated(EnumType.STRING)
    private PlatformEnum platform;
    
    @Enumerated(EnumType.STRING)
    private GameEnum gameEnum;
    
    @Enumerated(EnumType.STRING)
    private TeamTypeEnum teamTypeEnum;
    
    @Enumerated(EnumType.STRING)
    private TeamSizeEnum teamSizeEnum;
    
    @Enumerated(EnumType.STRING)
    private RegionEnum region;
    
    @Enumerated(EnumType.STRING)
    private GameModeEnum gameModeEnum;

    public BestOfEnum getBestOfEnumFinals() {
        return bestOfEnumFinals;
    }

    public void setBestOfEnumFinals(BestOfEnum bestOfEnumFinals) {
        this.bestOfEnumFinals = bestOfEnumFinals;
    }

    public GameModeEnum getGameModeEnum() {
        return gameModeEnum;
    }

    public void setGameModeEnum(GameModeEnum gameModeEnum) {
        this.gameModeEnum = gameModeEnum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMaxTeamCount() {
        return maxTeamCount;
    }

    public void setMaxTeamCount(int maxTeamCount) {
        this.maxTeamCount = maxTeamCount;
    }

    public int getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }
    
    public BestOfEnum getBestOfEnum() {
        return bestOfEnum;
    }

    public void setBestOfEnum(BestOfEnum bestOfEnum) {
        this.bestOfEnum = bestOfEnum;
    }

    public MatchSizeEnum getMatchSize() {
        return matchSize;
    }

    public void setMatchSize(MatchSizeEnum matchSize) {
        this.matchSize = matchSize;
    }

    public RegionEnum getRegion() {
        return region;
    }

    public void setRegion(RegionEnum region) {
        this.region = region;
    }

    public TournamentStatusEnum getTournamentStatus() {
        return tournamentStatus;
    }

    public void setTournamentStatus(TournamentStatusEnum tournamentStatus) {
        this.tournamentStatus = tournamentStatus;
    }

    public TournamentFormatEnum getTournamentFormat() {
        return tournamentFormat;
    }

    public void setTournamentFormat(TournamentFormatEnum tournamentFormat) {
        this.tournamentFormat = tournamentFormat;
    }

    public BigDecimal getAmountAwardedToFirstPlace() {
        return amountAwardedToFirstPlace;
    }

    public void setAmountAwardedToFirstPlace(BigDecimal amountAwardedToFirstPlace) {
        this.amountAwardedToFirstPlace = amountAwardedToFirstPlace;
    }

    public BigDecimal getAmountAwardedToSecondPlace() {
        return amountAwardedToSecondPlace;
    }

    public void setAmountAwardedToSecondPlace(BigDecimal amountAwardedToSecondPlace) {
        this.amountAwardedToSecondPlace = amountAwardedToSecondPlace;
    }

    public BigDecimal getAmountAwardedToThirdPlace() {
        return amountAwardedToThirdPlace;
    }

    public void setAmountAwardedToThirdPlace(BigDecimal amountAwardedToThirdPlace) {
        this.amountAwardedToThirdPlace = amountAwardedToThirdPlace;
    }

    public BigDecimal getWagerAmountPerMember() {
        return wagerAmountPerMember;
    }

    public void setWagerAmountPerMember(BigDecimal wagerAmountPerMember) {
        this.wagerAmountPerMember = wagerAmountPerMember;
    }

    public BigDecimal getPotAmount() {
        return potAmount;
    }

    public void setPotAmount(BigDecimal potAmount) {
        this.potAmount = potAmount;
    }

    public BigDecimal getPotAmountWithoutDonations() {
        return potAmountWithoutDonations;
    }

    public void setPotAmountWithoutDonations(BigDecimal potAmountWithoutDonations) {
        this.potAmountWithoutDonations = potAmountWithoutDonations;
    }

    public LocalDateTime getScheduledTournamentTime() {
        return scheduledTournamentTime;
    }
    
        //pass this user.getUserInfo().getTimeZone() in param
    public String getScheduledTournamentTimeForFE(TimeZoneEnum tze) {
        if (tze == null) {
            return DateTimeUtil.formatLocalDateTime(this.scheduledTournamentTime);
        }
        LocalDateTime scheduledTime = DateTimeUtil.getUserLocalDateTime(this.scheduledTournamentTime, tze);
        return DateTimeUtil.formatLocalDateTime(scheduledTime);
    }

    public void setScheduledTournamentTime(LocalDateTime scheduledTournamentTime) {
        this.scheduledTournamentTime = scheduledTournamentTime;
    }

    public LocalDateTime getTournamentStartTime() {
        return tournamentStartTime;
    }

    public void setTournamentStartTime(LocalDateTime tournamentStartTime) {
        this.tournamentStartTime = tournamentStartTime;
    }

    public LocalDateTime getTournamentEndTime() {
        return tournamentEndTime;
    }

    public void setTournamentEndTime(LocalDateTime tournamentEndTime) {
        this.tournamentEndTime = tournamentEndTime;
    }

    public int getNumOfMatches() {
        return numOfMatches;
    }

    public void setNumOfMatches(int numOfMatches) {
        this.numOfMatches = numOfMatches;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public void setNumOfPlayers(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }

    public int getNumOfTeamsSignedUp() {
        return numOfTeamsSignedUp;
    }

    public void setNumOfTeamsSignedUp(int numOfTeamsSignedUp) {
        this.numOfTeamsSignedUp = numOfTeamsSignedUp;
    }

    public int getNumOfTeamsNeededToStart() {
        return numOfTeamsNeededToStart;
    }

    public void setNumOfTeamsNeededToStart(int numOfTeamsNeededToStart) {
        this.numOfTeamsNeededToStart = numOfTeamsNeededToStart;
    }

    public int getNumOfTeamsReady() {
        return numOfTeamsReady;
    }

    public void setNumOfTeamsReady(int numOfTeamsReady) {
        this.numOfTeamsReady = numOfTeamsReady;
    }

    public int getNumOfTeamsNeedingToAccept() {
        return numOfTeamsNeedingToAccept;
    }

    public void setNumOfTeamsNeedingToAccept(int numOfTeamsNeedingToAccept) {
        this.numOfTeamsNeedingToAccept = numOfTeamsNeedingToAccept;
    }

    public int getNumOfPlayersNeedingToAccept() {
        return numOfPlayersNeedingToAccept;
    }

    public void setNumOfPlayersNeedingToAccept(int numOfPlayersNeedingToAccept) {
        this.numOfPlayersNeedingToAccept = numOfPlayersNeedingToAccept;
    }

    public MatchTypeEnum getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchTypeEnum matchType) {
        this.matchType = matchType;
    }

    public PlatformEnum getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformEnum platform) {
        this.platform = platform;
    }

    public GameEnum getGameEnum() {
        return gameEnum;
    }

    public void setGameEnum(GameEnum gameEnum) {
        this.gameEnum = gameEnum;
    }

    public TeamTypeEnum getTeamTypeEnum() {
        return teamTypeEnum;
    }

    public void setTeamTypeEnum(TeamTypeEnum teamTypeEnum) {
        this.teamTypeEnum = teamTypeEnum;
    }

    public TeamSizeEnum getTeamSizeEnum() {
        return teamSizeEnum;
    }

    public void setTeamSizeEnum(TeamSizeEnum teamSizeEnum) {
        this.teamSizeEnum = teamSizeEnum;
    }

    public int getNumOfTeamsInTournament() {
        return numOfTeamsInTournament;
    }

    public void setNumOfTeamsInTournament(int numOfTeamsInTournament) {
        this.numOfTeamsInTournament = numOfTeamsInTournament;
    }

    public int getNumOfPlayersInTournament() {
        return numOfPlayersInTournament;
    }

    public void setNumOfPlayersInTournament(int numOfPlayersInTournament) {
        this.numOfPlayersInTournament = numOfPlayersInTournament;
    }

    public int getNumOfPlayersNeedingToAcceptForTournamentStart() {
        return numOfPlayersNeedingToAcceptForTournamentStart;
    }

    public void setNumOfPlayersNeedingToAcceptForTournamentStart(int numOfPlayersNeedingToAcceptForTournamentStart) {
        this.numOfPlayersNeedingToAcceptForTournamentStart = numOfPlayersNeedingToAcceptForTournamentStart;
    }

    public int getNumOfPlayersPerTeam() {
        return numOfPlayersPerTeam;
    }

    public void setNumOfPlayersPerTeam(int numOfPlayersPerTeam) {
        this.numOfPlayersPerTeam = numOfPlayersPerTeam;
    }
    
    
    
}
