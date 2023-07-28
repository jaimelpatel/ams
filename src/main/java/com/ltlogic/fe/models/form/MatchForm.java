package com.ltlogic.fe.models.form;

import com.ltlogic.constants.BestOfEnum;
import com.ltlogic.constants.GameModeEnum;
import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.pojo.MatchPojo;
import com.ltlogic.pojo.iw.IWMatchPojo;
import com.ltlogic.pojo.mwr.MWRMatchPojo;
import com.ltlogic.pojo.ww2.WW2MatchPojo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.Pattern;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author raymond
 */
public class MatchForm {

    @NotNull(message = "Lethals field is required.")
    private boolean lethals;
    @NotNull(message = "Juggernaut field is required.")
    private boolean juggernaut;
    @NotNull(message = "Tacticals field is required.")
    private boolean tacticals;
    @NotNull(message = "Stopping Power field is required.")
    private boolean stoppingPower;

    private boolean scoreStreaks;
    private boolean overkill;

    private boolean radarOn;
    @Pattern(regexp = "^[A-Za-z0-9\\s]*$", message = "PSN ID can only be in letters, digits, and spaces.")
    private String mapName;
    @NotNull(message = "Game Type field is required.")
    private Integer gameMode;
    @NotNull(message = "Map Count field is required.")
    private Integer bestOfEnum;
    private BigDecimal matchPrizePool;

    @NotNull(message = "Game Start Time field is required.")
    @Pattern(regexp = "^(1[0-2]|0[1-9])\\/(3[01]|[12][0-9]|0[1-9])\\/[0-9]{4} [0-9][0-9]:[0-9][0-9] (AM|PM)$",
            message = "Scheduled Match Time must be in the following format mm/dd/yyyy hh:mm AM.")
    private String scheduledMatchTime;

    private Integer bonus;
    @Pattern(regexp = "^[A-Za-z0-9\\s]*$", message = "Opponent Team name can only be in letters, digits and spaces.")
    private String opponentTeamName;

    private Set<Long> usersInMatch = new HashSet<Long>(0);

    private Boolean matchPublic = false;

    private BigDecimal wagerAmount;

    /**
     * @return the lethals
     */
    public boolean isLethals() {
        return lethals;
    }

    /**
     * @param lethals the lethals to set
     */
    public void setLethals(boolean lethals) {
        this.lethals = lethals;
    }

    /**
     * @return the juggernaut
     */
    public boolean isJuggernaut() {
        return juggernaut;
    }

    /**
     * @param juggernaut the juggernaut to set
     */
    public void setJuggernaut(boolean juggernaut) {
        this.juggernaut = juggernaut;
    }

    /**
     * @return the tacticals
     */
    public boolean getTacticals() {
        return tacticals;
    }

    /**
     * @param tacticals the tacticals to set
     */
    public void setTacticals(boolean tacticals) {
        this.tacticals = tacticals;
    }

    /**
     * @return the stoppingPower
     */
    public boolean isStoppingPower() {
        return stoppingPower;
    }

    /**
     * @param stoppingPower the stoppingPower to set
     */
    public void setStoppingPower(boolean stoppingPower) {
        this.stoppingPower = stoppingPower;
    }

    public boolean isScoreStreaks() {
        return scoreStreaks;
    }

    public void setScoreStreaks(boolean scoreStreaks) {
        this.scoreStreaks = scoreStreaks;
    }

    public boolean isOverkill() {
        return overkill;
    }

    public void setOverkill(boolean overkill) {
        this.overkill = overkill;
    }

    /**
     * @return the radarOn
     */
    public boolean isRadarOn() {
        return radarOn;
    }

    /**
     * @param radarOn the radarOn to set
     */
    public void setRadarOn(boolean radarOn) {
        this.radarOn = radarOn;
    }

    /**
     * @return the mapName
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @param mapName the mapName to set
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * @return the gameModeEnum
     */
    public Integer getGameMode() {
        return gameMode;
    }

    public Integer getBestOfEnum() {
        return bestOfEnum;
    }

    public void setBestOfEnum(Integer bestOfEnum) {
        this.bestOfEnum = bestOfEnum;
    }

    /**
     * @return the matchPrizePool
     */
    public BigDecimal getMatchPrizePool() {
        return matchPrizePool;
    }

    /**
     * @param matchPrizePool the matchPrizePool to set
     */
    public void setMatchPrizePool(BigDecimal matchPrizePool) {
        this.matchPrizePool = matchPrizePool;
    }

    /**
     * @param gameModeEnum the gameModeEnum to set
     */
    public void setGameMode(Integer gameMode) {
        this.gameMode = gameMode;
    }

    public String getScheduledMatchTime() {
        return scheduledMatchTime;
    }

    public void setScheduledMatchTime(String scheduledMatchTime) {
        this.scheduledMatchTime = scheduledMatchTime;
    }

    /**
     * @return the bonus
     */
    public Integer getBonus() {
        return bonus;
    }

    /**
     * @param bonus the bonus to set
     */
    public void setBonus(Integer bonus) {
        this.bonus = bonus;
    }

    /**
     * @return the opponentTeamName
     */
    public String getOpponentTeamName() {
        return opponentTeamName;
    }

    /**
     * @param opponentTeamName the opponentTeamName to set
     */
    public void setOpponentTeamName(String opponentTeamName) {
        this.opponentTeamName = opponentTeamName;
    }

    /**
     * @return the usersInMatch
     */
    public Set<Long> getUsersInMatch() {
        return usersInMatch;
    }

    /**
     * @param usersInMatch the usersInMatch to set
     */
    public void setUsersInMatch(Set<Long> usersInMatch) {
        this.usersInMatch = usersInMatch;
    }

    /**
     * @return the isMatchPublic
     */
    public Boolean getMatchPublic() {
        return matchPublic;
    }

    /**
     * @param isMatchPublic the isMatchPublic to set
     */
    public void setMatchPublic(Boolean matchPublic) {
        this.matchPublic = matchPublic;
    }

    /**
     * @return the wagerAmount
     */
    public BigDecimal getWagerAmount() {
        return wagerAmount;
    }

    /**
     * @param wagerAmount the wagerAmount to set
     */
    public void setWagerAmount(BigDecimal wagerAmount) {
        this.wagerAmount = wagerAmount;
    }

    public static MWRMatchPojo toMWRMatchPojo(MatchForm form) {
        MWRMatchPojo pojo = new MWRMatchPojo();

        if (form != null) {
            pojo.setJuggernaut(form.isJuggernaut());
            pojo.setLethals(form.isLethals());
            pojo.setStoppingPower(form.isStoppingPower());
            pojo.setTacticals(form.getTacticals());
            pojo.setGameModeEnum(GameModeEnum.getGameModeEnumById(form.getGameMode()));
        }

        return pojo;
    }

    public static IWMatchPojo toIWMatchPojo(MatchForm form) {
        IWMatchPojo pojo = new IWMatchPojo();

        if (form != null) {
            pojo.setOverkill(form.isOverkill());
            pojo.setScoreStreaks(form.isScoreStreaks());
            pojo.setGameModeEnum(GameModeEnum.getGameModeEnumById(form.getGameMode()));
        }

        return pojo;
    }

    public static WW2MatchPojo toWW2MatchPojo(MatchForm form) {
        WW2MatchPojo pojo = new WW2MatchPojo();

        if (form != null) {
            pojo.setOverkill(form.isOverkill());
            pojo.setScoreStreaks(form.isScoreStreaks());
            pojo.setGameModeEnum(GameModeEnum.getGameModeEnumById(form.getGameMode()));
        }

        return pojo;
    }

    public static MatchPojo toMatchPojo(User u, MatchForm matchForm, Team senderTeam) {

        MatchPojo matchInfo = new MatchPojo();

        if (senderTeam != null && senderTeam.getTeamPojo() != null) {
            matchInfo.setGameModeEnumPojo(GameModeEnum.getGameModeEnumById(matchForm.getGameMode()));
            matchInfo.setBestOfEnum(BestOfEnum.getBestOfEnumById(matchForm.getBestOfEnum()));
            matchInfo.setGameEnum(senderTeam.getTeamPojo().getGame());
            matchInfo.setPlatform(senderTeam.getTeamPojo().getPlatform());
            matchInfo.setTeamSizeEnum(senderTeam.getTeamPojo().getTeamSize());
            matchInfo.setTeamTypeEnum(senderTeam.getTeamPojo().getTeamType());
            matchInfo.setWagerAmountPerMember(matchForm.getWagerAmount());
            if (senderTeam.getTeamPojo().getTeamType() == TeamTypeEnum.XP) {
                matchInfo.setIsMatchPublic(true);
                matchInfo.setMatchType(MatchTypeEnum.XP);
            } else if (senderTeam.getTeamPojo().getTeamType() == TeamTypeEnum.CASH) {
                if (matchForm.getMatchPublic() != null) {
                    if (matchForm.getMatchPublic() == true) {
                        matchInfo.setIsMatchPublic(true);
                    } else {
                        matchInfo.setIsMatchPublic(false);
                    }
                }
                matchInfo.setMatchType(MatchTypeEnum.WAGER);
            }

            BigDecimal wagerAmount = matchForm.getWagerAmount();
            if (wagerAmount != null) {
                matchForm.setWagerAmount(wagerAmount.setScale(2, RoundingMode.CEILING));
            }
            System.out.println("wager amount: " + wagerAmount);
            matchInfo.setMatchWagerAmountPerMember(matchForm.getWagerAmount());

            if (matchForm != null && matchForm.getScheduledMatchTime() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
                matchInfo.setScheduledMatchTime(LocalDateTime.parse(matchForm.getScheduledMatchTime(), formatter), u.getUserInfo().getTimeZone());
                System.out.println("Match Time: " + matchInfo.getScheduledMatchTime(TimeZoneEnum.UTC_MINUS_7_LA));
            }
            if (senderTeam.getTeamPojo().getTeamType() == TeamTypeEnum.CASH) {
                if (matchForm != null && matchForm.getMatchPrizePool() != null) {
                    matchInfo.setMatchWagerAmountPerMember(matchForm.getMatchPrizePool());
                }
            }
        }

        return matchInfo;
    }

}
