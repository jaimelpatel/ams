/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.pojo;

import com.ltlogic.constants.BestOfEnum;
import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.GameModeEnum;
import com.ltlogic.constants.MatchSizeEnum;
import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.db.superentity.SuperEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 *
 * @author Hoang
 */
@Embeddable
public class MatchPojo {

    @Enumerated(EnumType.STRING)
    private MatchStatusEnum matchStatus;

    private BigDecimal wagerAmountPerMember;

    private BigDecimal potAmount = new BigDecimal(0.00);

    private LocalDateTime scheduledMatchTime;

    private LocalDateTime matchEndTime;

    private LocalDateTime matchStartTime;

    @Enumerated(EnumType.STRING)
    private BestOfEnum bestOfEnum;

    private int numOfRounds;

    private int numOfPlayers;

    private int numOfPlayersNeedingToAccept;

    private boolean isMatchPublic;

    @Enumerated(EnumType.STRING)
    private MatchTypeEnum matchType;

    @Enumerated(EnumType.STRING)
    private PlatformEnum platform;

    @Enumerated(EnumType.STRING)
    private GameEnum gameEnum;

    @Enumerated(EnumType.STRING)
    private GameModeEnum gameModeEnumPojo;

    @Enumerated(EnumType.STRING)
    private TeamTypeEnum teamTypeEnum;

    @Enumerated(EnumType.STRING)
    private TeamSizeEnum teamSizeEnum;

    @Enumerated(EnumType.STRING)
    private MatchSizeEnum matchSizeEnum;

    public GameModeEnum getGameModeEnumPojo() {
        return gameModeEnumPojo;
    }

    public void setGameModeEnumPojo(GameModeEnum gameModeEnumPojo) {
        this.gameModeEnumPojo = gameModeEnumPojo;
    }

    public BestOfEnum getBestOfEnum() {
        return bestOfEnum;
    }

    public void setBestOfEnum(BestOfEnum bestOfEnum) {
        this.bestOfEnum = bestOfEnum;
    }

    public MatchSizeEnum getMatchSizeEnum() {
        return matchSizeEnum;
    }

    public void setMatchSizeEnum(MatchSizeEnum matchSizeEnum) {
        this.matchSizeEnum = matchSizeEnum;
    }

    public BigDecimal getPotAmount() {
        return potAmount;
    }

    public void setPotAmount(BigDecimal potAmount) {
        this.potAmount = potAmount;
    }

    public LocalDateTime getMatchEndTime() {
        return matchEndTime;
    }

    public void setMatchEndTime(LocalDateTime matchEndTime) {
        this.matchEndTime = matchEndTime;
    }

    public MatchStatusEnum getMatchStatus() {
        return matchStatus;
    }

    public LocalDateTime getMatchStartTime() {
        return matchStartTime;
    }

    public void setMatchStartTime() {
        this.matchStartTime = DateTimeUtil.getDefaultLocalDateTimeNow();
    }

    public void setMatchStatus(MatchStatusEnum matchStatus) {
        this.matchStatus = matchStatus;
    }

    public BigDecimal getMatchWagerAmountPerMember() {
        return wagerAmountPerMember;
    }

    public void setMatchWagerAmountPerMember(BigDecimal matchPrizePool) {
        this.wagerAmountPerMember = matchPrizePool;
    }

//    public LocalDateTime getScheduledMatchTime() {
//        return scheduledMatchTime;
//    }
    public String getScheduledMatchTime(TimeZoneEnum tze) {
        if (this.scheduledMatchTime == null) {
            return "TBD";
        }
        if (tze == null) {
            return DateTimeUtil.formatLocalDateTime(this.scheduledMatchTime);
        }
        LocalDateTime scheduledTime = DateTimeUtil.getUserLocalDateTime(this.scheduledMatchTime, tze);
        return DateTimeUtil.formatLocalDateTime(scheduledTime);
    }

    public void setScheduledMatchTime(LocalDateTime scheduledMatchTime, TimeZoneEnum tze) {
        this.scheduledMatchTime = DateTimeUtil.getDefaultLocalDateTime(scheduledMatchTime, tze);
        setMatchEndTime(this.scheduledMatchTime.plusHours(3L));
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

    public int getNumOfRounds() {
        return numOfRounds;
    }

    public void setNumOfRounds(int numOfRounds) {
        this.numOfRounds = numOfRounds;
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

    public BigDecimal getWagerAmountPerMember() {
        return wagerAmountPerMember;
    }

    public void setWagerAmountPerMember(BigDecimal wagerAmountPerMember) {
        this.wagerAmountPerMember = wagerAmountPerMember;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public void setNumOfPlayers(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }

    public int getNumOfPlayersNeedingToAccept() {
        return numOfPlayersNeedingToAccept;
    }

    public void setNumOfPlayersNeedingToAccept(int numOfPlayersNeedingToAccept) {
        this.numOfPlayersNeedingToAccept = numOfPlayersNeedingToAccept;
    }

    public boolean isIsMatchPublic() {
        return isMatchPublic;
    }

    public void setIsMatchPublic(boolean isMatchPublic) {
        this.isMatchPublic = isMatchPublic;
    }

}
