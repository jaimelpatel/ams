package com.ltlogic.fe.models.form;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.User;
import com.ltlogic.pojo.TeamPojo;
import java.util.ArrayList;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author raymond
 */
public class TeamForm {

    private String username;
    @NotEmpty(message = "Team Name is required.")
    @Pattern(regexp = "^[A-Za-z0-9\\s_]*$", message="Team Name may only contain letters, digits, underscores and spaces.")
    private String teamName;
    @NotNull(message = "Console is required.")
    private Integer platform;
    @NotNull(message = "Team Type is required.")
    private Integer teamType;
    @NotNull(message = "Game is required.")
    private Integer game;
    @NotNull(message = "Team Size is required.")
    private Integer teamSize;
    @Pattern(regexp = "^[A-Za-z0-9_]*$", message="Username #1 may only contain letters, digits and underscores.")
    private String userInvite1;
    @Pattern(regexp = "^[A-Za-z0-9_]*$", message="Username #2 may only contain letters, digits and underscores.")
    private String userInvite2;
    @Pattern(regexp = "^[A-Za-z0-9_]*$", message="Username #3 may only contain letters, digits and underscores.")
    private String userInvite3;
    @Pattern(regexp = "^[A-Za-z0-9_]*$", message="Username #4 may only contain letters, digits and underscores.")
    private String userInvite4;
    @Pattern(regexp = "^[A-Za-z0-9_]*$", message="Username #5 may only contain letters, digits and underscores.")
    private String userInvite5;    
    
    
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the teamName
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * @param teamName the teamName to set
     */
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

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
     * @return the teamType
     */
    public Integer getTeamType() {
        return teamType;
    }

    /**
     * @param teamType the teamType to set
     */
    public void setTeamType(Integer teamType) {
        this.teamType = teamType;
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
     * @return the teamSize
     */
    public Integer getTeamSize() {
        return teamSize;
    }

    /**
     * @param teamSize the teamSize to set
     */
    public void setTeamSize(Integer teamSize) {
        this.teamSize = teamSize;
    }

    public String getUserInvite1() {
        return userInvite1;
    }

    public void setUserInvite1(String userInvite1) {
        this.userInvite1 = userInvite1;
    }

    public String getUserInvite2() {
        return userInvite2;
    }

    public void setUserInvite2(String userInvite2) {
        this.userInvite2 = userInvite2;
    }

    public String getUserInvite3() {
        return userInvite3;
    }

    public void setUserInvite3(String userInvite3) {
        this.userInvite3 = userInvite3;
    }

    public String getUserInvite4() {
        return userInvite4;
    }

    public void setUserInvite4(String userInvite4) {
        this.userInvite4 = userInvite4;
    }

    public String getUserInvite5() {
        return userInvite5;
    }

    public void setUserInvite5(String userInvite5) {
        this.userInvite5 = userInvite5;
    }
    
    
    
    public static TeamPojo toTeamPojo(TeamForm form, User user) {
        TeamPojo teamPojo = null;
        ArrayList<String> listOfUsersToInviteToTeam  = new ArrayList<>();
        if(form != null) {
            teamPojo = new TeamPojo();
            if(user != null) {
                teamPojo.setUsername(user.getUsername());
                teamPojo.setRegion(user.getUserInfo().getRegion());
            }
            
            teamPojo.setTeamName(form.getTeamName().trim());
            if(form.getGame() != null)
                teamPojo.setGame(GameEnum.getGameEnumById(form.getGame()));
            if(form.getPlatform() != null)
                teamPojo.setPlatform(PlatformEnum.getPlatformEnumById(form.getPlatform()));
            if(form.getTeamSize() != null)
                teamPojo.setTeamSize(TeamSizeEnum.getTeamSizeEnumById(form.getTeamSize()));
            if(form.getTeamType() != null)
                teamPojo.setTeamType(TeamTypeEnum.getTeamTypeEnumById(form.getTeamType()));
            if(form.getUserInvite1() != null && !form.getUserInvite1().isEmpty()){
                listOfUsersToInviteToTeam.add(form.getUserInvite1());
            }
            if(form.getUserInvite2() != null && !form.getUserInvite2().isEmpty()){
                listOfUsersToInviteToTeam.add(form.getUserInvite2());
            }
            if(form.getUserInvite3() != null && !form.getUserInvite3().isEmpty()){
                listOfUsersToInviteToTeam.add(form.getUserInvite3());
            }
            if(form.getUserInvite4() != null && !form.getUserInvite4().isEmpty()){
                listOfUsersToInviteToTeam.add(form.getUserInvite4());
            }
            teamPojo.setUsersToInviteToTeam(listOfUsersToInviteToTeam);
        }
        
        return teamPojo;
    }
}
