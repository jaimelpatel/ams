/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.models.form;

import com.ltlogic.constants.BestOfEnum;
import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.GameModeEnum;
import com.ltlogic.constants.MatchSizeEnum;
import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TournamentFormatEnum;
import com.ltlogic.constants.TournamentStatusEnum;
import com.ltlogic.pojo.TournamentPojo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import java.time.format.DateTimeFormatter;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Pattern;

/**
 *
 * @author raymond
 */
public class TournamentForm {
    
    @NotNull(message="Tournament Status field is required.")
    private Integer tournamentStatus;
//    @NotNull(message="Tournament Bracket field is required.")
//    private Integer tournamentFormat;
    private BigDecimal amountAwardedToFirstPlace;
    private BigDecimal amountAwardedToSecondPlace;
    private BigDecimal amountAwardedToThirdPlace;
    @NotNull(message="Wager Amount Per Member field is required.")
    private BigDecimal wagerAmountPerMember;
    @NotNull(message="Pot Amount field is required.")
    private BigDecimal potAmount;
    private BigDecimal potAmountWithoutDonations;
    @NotEmpty(message="Schedule Tournament Time field is required.")
    @Pattern(regexp = "^(1[0-2]|0[1-9])\\/(3[01]|[12][0-9]|0[1-9])\\/[0-9]{4} [0-9][0-9]:[0-9][0-9] (AM|PM)$", 
            message="Scheduled Match Time must be in the following format mm/dd/yyyy hh:mm AM.")
    private String scheduledTournamentTime;
//    @NotEmpty(message="Tournament Start Time field is required")
//    @Pattern(regexp = "^(1[0-2]|0[1-9])\\/(3[01]|[12][0-9]|0[1-9])\\/[0-9]{4} [0-9][0-9]:[0-9][0-9] (AM|PM)$", 
//            message="Scheduled Match Time must be in the following format mm/dd/yyyy hh:mm AM.")
//    private String tournamentStartTime;
//    @NotEmpty(message="Tournament End Time field is required")
//    @Pattern(regexp = "^(1[0-2]|0[1-9])\\/(3[01]|[12][0-9]|0[1-9])\\/[0-9]{4} [0-9][0-9]:[0-9][0-9] (AM|PM)$", 
//            message="Scheduled Match Time must be in the following format mm/dd/yyyy hh:mm AM.")
//    private String tournamentEndTime;
    @NotNull(message="Tournament Best of field is required.")
    private Integer bestOf;
    @NotNull(message="Tournament Best of Finals field is required.")
    private Integer bestOfFinals;
//    @NotNull(message = "Match Type field is required.")
//    private Integer matchType;  //always Tournament
    @NotNull(message = "Platform field is required.")
    private Integer platform;
    @NotNull(message = "Game field is required.")
    private Integer game;
//    @NotNull(message = "Team Type field is required.")
//    private Integer teamType;   //not really needed it
    @NotNull(message = "Match Size field is required.")
    private Integer matchSize;
    @NotNull(message = "Region field is required.")
    private Integer region;
    @NotNull(message = "Max Team Count is required.")
    private Integer maxTeamCount;
    @NotNull(message = "Game Mode is required.")
    private Integer gameModeEnum;
    
    private String title;

    public Integer getBestOfFinals() {
        return bestOfFinals;
    }

    public void setBestOfFinals(Integer bestOfFinals) {
        this.bestOfFinals = bestOfFinals;
    }

    public Integer getGameModeEnum() {
        return gameModeEnum;
    }

    public void setGameModeEnum(Integer gameModeEnum) {
        this.gameModeEnum = gameModeEnum;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getMaxTeamCount() {
        return maxTeamCount;
    }

    public void setMaxTeamCount(Integer maxTeamCount) {
        this.maxTeamCount = maxTeamCount;
    }

    public Integer getBestOf() {
        return bestOf;
    }

    public void setBestOf(Integer bestOf) {
        this.bestOf = bestOf;
    }
    
    /**
     * @return the tournamentStatus
     */
    public Integer getTournamentStatus() {
        return tournamentStatus;
    }

    /**
     * @param tournamentStatus the tournamentStatus to set
     */
    public void setTournamentStatus(Integer tournamentStatus) {
        this.tournamentStatus = tournamentStatus;
    }

//    /**
//     * @return the tournamentFormat
//     */
//    public Integer getTournamentFormat() {
//        return tournamentFormat;
//    }
//
//    /**
//     * @param tournamentFormat the tournamentFormat to set
//     */
//    public void setTournamentFormat(Integer tournamentFormat) {
//        this.tournamentFormat = tournamentFormat;
//    }

    /**
     * @return the amountAwardedToFirstPlace
     */
    public BigDecimal getAmountAwardedToFirstPlace() {
        return amountAwardedToFirstPlace;
    }

    /**
     * @param amountAwardedToFirstPlace the amountAwardedToFirstPlace to set
     */
    public void setAmountAwardedToFirstPlace(BigDecimal amountAwardedToFirstPlace) {
        this.amountAwardedToFirstPlace = amountAwardedToFirstPlace;
    }

    /**
     * @return the amountAwardedToSecondPlace
     */
    public BigDecimal getAmountAwardedToSecondPlace() {
        return amountAwardedToSecondPlace;
    }

    /**
     * @param amountAwardedToSecondPlace the amountAwardedToSecondPlace to set
     */
    public void setAmountAwardedToSecondPlace(BigDecimal amountAwardedToSecondPlace) {
        this.amountAwardedToSecondPlace = amountAwardedToSecondPlace;
    }

    /**
     * @return the amountAwardedToThirdPlace
     */
    public BigDecimal getAmountAwardedToThirdPlace() {
        return amountAwardedToThirdPlace;
    }

    /**
     * @param amountAwardedToThirdPlace the amountAwardedToThirdPlace to set
     */
    public void setAmountAwardedToThirdPlace(BigDecimal amountAwardedToThirdPlace) {
        this.amountAwardedToThirdPlace = amountAwardedToThirdPlace;
    }

    /**
     * @return the wagerAmountPerMember
     */
    public BigDecimal getWagerAmountPerMember() {
        return wagerAmountPerMember;
    }

    /**
     * @param wagerAmountPerMember the wagerAmountPerMember to set
     */
    public void setWagerAmountPerMember(BigDecimal wagerAmountPerMember) {
        this.wagerAmountPerMember = wagerAmountPerMember;
    }

    /**
     * @return the potAmount
     */
    public BigDecimal getPotAmount() {
        return potAmount;
    }

    /**
     * @param potAmount the potAmount to set
     */
    public void setPotAmount(BigDecimal potAmount) {
        this.potAmount = potAmount;
    }

    /**
     * @return the potAmountWithoutDonations
     */
    public BigDecimal getPotAmountWithoutDonations() {
        return potAmountWithoutDonations;
    }

    /**
     * @param potAmountWithoutDonations the potAmountWithoutDonations to set
     */
    public void setPotAmountWithoutDonations(BigDecimal potAmountWithoutDonations) {
        this.potAmountWithoutDonations = potAmountWithoutDonations;
    }

    /**
     * @return the scheduledTournamentTime
     */
    public String getScheduledTournamentTime() {
        return scheduledTournamentTime;
    }

    /**
     * @param scheduledTournamentTime the scheduledTournamentTime to set
     */
    public void setScheduledTournamentTime(String scheduledTournamentTime) {
        this.scheduledTournamentTime = scheduledTournamentTime;
    }

    /**
     * @return the tournamentStartTime
     */
//    public String getTournamentStartTime() {
//        return tournamentStartTime;
//    }
//
//    /**
//     * @param tournamentStartTime the tournamentStartTime to set
//     */
//    public void setTournamentStartTime(String tournamentStartTime) {
//        this.tournamentStartTime = tournamentStartTime;
//    }
//
//    /**
//     * @return the tournamentEndTime
//     */
//    public String getTournamentEndTime() {
//        return tournamentEndTime;
//    }
//
//    /**
//     * @param tournamentEndTime the tournamentEndTime to set
//     */
//    public void setTournamentEndTime(String tournamentEndTime) {
//        this.tournamentEndTime = tournamentEndTime;
//    }
//    
    
    /**
     * @return the matchType
     */
//    public Integer getMatchType() {
//        return matchType;
//    }
//
//    /**
//     * @param matchType the matchType to set
//     */
//    public void setMatchType(Integer matchType) {
//        this.matchType = matchType;
//    }

    /**
     * @return the platform
     */
    public Integer getPlatform() {
        return platform;
    }

    /**
     * @param platform the platform to set
     */
    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    /**
     * @return the game
     */
    public Integer getGame() {
        return game;
    }

    /**
     * @param game the game to set
     */
    public void setGame(Integer game) {
        this.game = game;
    }

    /**
     * @return the teamType
     */
//    public Integer getTeamType() {
//        return teamType;
//    }
//
//    /**
//     * @param teamType the teamType to set
//     */
//    public void setTeamType(Integer teamType) {
//        this.teamType = teamType;
//    }

    /**
     * @return the teamSizeEnum
     */
    public Integer getMatchSize() {
        return matchSize;
    }

    /**
     * @param teamSizeEnum the teamSizeEnum to set
     */
    public void setMatchSize(Integer matchSize) {
        this.matchSize = matchSize;
    }

    /**
     * @return the region
     */
    public Integer getRegion() {
        return region;
    }

    /**
     * @param region the region to set
     */
    public void setRegion(Integer region) {
        this.region = region;
    }
    
    
    
    public static TournamentPojo toTournamentPojo(TournamentForm form) {
        TournamentPojo pojo = new TournamentPojo();
        
        if(form != null) {
            if(form.getMaxTeamCount() != null){
                pojo.setMaxTeamCount(form.getMaxTeamCount());
            }
            if(form.getTitle() == null || form.getTitle().isEmpty()){
                pojo.setTitle("");
            }else{
                pojo.setTitle(form.getTitle());
            }
            
            pojo.setTournamentStatus(TournamentStatusEnum.getTournamentStatusEnumById(form.getTournamentStatus()));
//            pojo.setTournamentFormat(TournamentFormatEnum.getTournamentFormatEnumById(form.getTournamentFormat()));
            pojo.setTournamentFormat(TournamentFormatEnum.SINGLEELIMINATION);
            //Parse Date String to LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
            pojo.setScheduledTournamentTime(LocalDateTime.parse(form.getScheduledTournamentTime(), formatter));
//            pojo.setTournamentStartTime(LocalDateTime.parse(form.getTournamentStartTime(), formatter));
//            pojo.setTournamentEndTime(LocalDateTime.parse(form.getTournamentEndTime(), formatter));
            
            pojo.setWagerAmountPerMember(form.getWagerAmountPerMember());
            pojo.setTournamentStatus(TournamentStatusEnum.getTournamentStatusEnumById(form.getTournamentStatus()));
            pojo.setPotAmount(form.getPotAmount());
            pojo.setAmountAwardedToFirstPlace(form.getAmountAwardedToFirstPlace());
            pojo.setAmountAwardedToSecondPlace(form.getAmountAwardedToSecondPlace());
            pojo.setAmountAwardedToThirdPlace(form.getAmountAwardedToThirdPlace());
            pojo.setGameEnum(GameEnum.getGameEnumById(form.getGame()));
            pojo.setPlatform(PlatformEnum.getPlatformEnumById(form.getPlatform()));
            pojo.setRegion(RegionEnum.getRegionEnumById(form.getRegion()));
            pojo.setMatchSize(MatchSizeEnum.getMatchSizeEnumById(form.getMatchSize()));
            pojo.setBestOfEnum(BestOfEnum.getBestOfEnumById(form.getBestOf()));
            pojo.setBestOfEnumFinals(BestOfEnum.getBestOfEnumById(form.getBestOfFinals()));
            
            pojo.setGameModeEnum(GameModeEnum.getGameModeEnumById(form.getGameModeEnum()));
            
            if(form.getMatchSize() != null) {
                switch (form.getMatchSize()) {
                    case 0:
                        pojo.setTeamSizeEnum(TeamSizeEnum.SINGLES);
                        pojo.setMatchSize(MatchSizeEnum.SINGLES);
                        break;
                    case 1:
                        pojo.setTeamSizeEnum(TeamSizeEnum.DOUBLES);
                        pojo.setMatchSize(MatchSizeEnum.DOUBLES);
                        break;
                    case 2:
                        pojo.setTeamSizeEnum(TeamSizeEnum.TEAM);
                        pojo.setMatchSize(MatchSizeEnum.THREES);
                        break;
                    case 3:
                        pojo.setTeamSizeEnum(TeamSizeEnum.TEAM);
                        pojo.setMatchSize(MatchSizeEnum.FOURS);
                        break;
                    default:
                        break;
                }
            }
        }
        
        return pojo;
    }
}