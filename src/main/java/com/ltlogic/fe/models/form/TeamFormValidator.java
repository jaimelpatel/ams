/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.models.form;

import com.ltlogic.constants.TeamStatusEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.pojo.TeamPojo;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author raymond
 */
@Component
public class TeamFormValidator implements Validator {

    @Autowired
    private TeamIWS teamIWS;
    
    @Autowired
    private UserRepository userRepo;
    
    @Override
    public boolean supports(Class<?> aClass) {
        return TeamForm.class.equals(aClass);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        if(target != null && errors != null) {
            TeamPojo teamPojo = (TeamPojo) target;
            String lowerCaseTeamName = teamPojo.getTeamName().toLowerCase().trim();
            teamPojo.setTeamName(teamPojo.getTeamName().trim());
            User user = userRepo.findByUsername(teamPojo.getUsername());
            //valid team existed or not here
            Team t = teamIWS.findTeamByNameAndGameAndPlatformAndTypeAndSize(lowerCaseTeamName, teamPojo.getGame(), teamPojo.getPlatform(), teamPojo.getTeamSize(), teamPojo.getTeamType(), user.getUserInfo().getRegion(), TeamStatusEnum.LIVE);
            if (t != null) {
                errors.reject("team.uniqueness", "The team name " + "'" + teamPojo.getTeamName() + "'" + " is already in use in this ladder (" + teamPojo.getGame().getGameEnumDesc() + " - " + teamPojo.getTeamType().getTeamTypeEnumDesc() + " - " + teamPojo.getPlatform().getPlatformEnumDesc() + " - " + teamPojo.getTeamSize().getTeamSizeEnumDesc() + " - " + teamPojo.getRegion().getRegionEnumDesc() + ").");
            }
            
            if(teamPojo.getTeamType() == TeamTypeEnum.XP){
                List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(teamPojo.getUsername(), teamPojo.getGame(), teamPojo.getPlatform(), teamPojo.getTeamSize(), teamPojo.getTeamType(), user.getUserInfo().getRegion());                  
                if (teams.size() ==  2) {
                    errors.reject("team.uniqueness", "You are already a member of 2 XP teams in the same ladder (" + teamPojo.getGame().getGameEnumDesc() + " - " + teamPojo.getPlatform().getPlatformEnumDesc() + " - " + teamPojo.getTeamSize().getTeamSizeEnumDesc() + " - " + teamPojo.getRegion().getRegionEnumDesc() + "). Max 2 teams per ladder for XP teams. Max 3 teams per ladder for Cashout teams.");
                }
            }
            else{
                List<Team> teams = teamIWS.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(teamPojo.getUsername(), teamPojo.getGame(), teamPojo.getPlatform(), teamPojo.getTeamSize(), teamPojo.getTeamType(), user.getUserInfo().getRegion());                  
                if (teams.size() == 3) {
                    errors.reject("team.uniqueness", "You are already a member of 3 cashout teams in the same ladder (" + teamPojo.getGame().getGameEnumDesc() + " - " + teamPojo.getPlatform().getPlatformEnumDesc() + " - " + teamPojo.getTeamSize().getTeamSizeEnumDesc() + " - " + teamPojo.getRegion().getRegionEnumDesc() + "). Max 3 teams per ladder for Cashout teams.  Max 2 teams per ladder for XP teams. ");
                }
            }
            
            if (teamPojo.getTeamName().trim().length() == 0){
                errors.reject("team.uniqueness", "Your teamname cannot be empty.");
            }
        }
        
    }
}
