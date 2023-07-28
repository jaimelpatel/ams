/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.pojo;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamStatusEnum;
import com.ltlogic.constants.TeamTypeEnum;
import java.util.ArrayList;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * 
 * @author Hoang
 */
@Embeddable
public class TeamPojo {
    
    private String username; //username of user creating team
    private String teamName;
    @Enumerated(EnumType.STRING)
    private PlatformEnum platform;
    @Enumerated(EnumType.STRING)
    private TeamTypeEnum teamType;
    @Enumerated(EnumType.STRING)
    private GameEnum game;
    @Enumerated(EnumType.STRING)
    private TeamSizeEnum teamSize;
    @Enumerated(EnumType.STRING)
    private RegionEnum region;
    private ArrayList<String> usersToInviteToTeam = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private TeamStatusEnum teamStatus = TeamStatusEnum.LIVE;

    public TeamStatusEnum getTeamStatus() {
        return teamStatus;
    }

    public void setTeamStatus(TeamStatusEnum teamStatus) {
        this.teamStatus = teamStatus;
    }

    public RegionEnum getRegion() {
        return region;
    }

    public void setRegion(RegionEnum region) {
        this.region = region;
    }
    
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PlatformEnum getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformEnum platform) {
        this.platform = platform;
    }

    public TeamTypeEnum getTeamType() {
        return teamType;
    }

    public void setTeamType(TeamTypeEnum teamType) {
        this.teamType = teamType;
    }

    public GameEnum getGame() {
        return game;
    }

    public void setGame(GameEnum game) {
        this.game = game;
    }

    public TeamSizeEnum getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(TeamSizeEnum teamSize) {
        this.teamSize = teamSize;
    }

    public ArrayList<String> getUsersToInviteToTeam() {
        return usersToInviteToTeam;
    }

    public void setUsersToInviteToTeam(ArrayList<String> usersToInviteToTeam) {
        this.usersToInviteToTeam = usersToInviteToTeam;
    }

}
