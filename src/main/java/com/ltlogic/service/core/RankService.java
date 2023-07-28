/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.iw.IWTeam;
import com.ltlogic.db.entity.mwr.MWRTeam;
import com.ltlogic.db.entity.ww2.WW2Team;
import com.ltlogic.db.repository.TeamRankRepository;
import com.ltlogic.db.repository.TeamRepository;
import com.ltlogic.db.repository.UserRankRepository;
import com.ltlogic.db.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jaimel
 */
@Service
@Transactional
public class RankService {
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private TeamRepository teamRepo;
    
    @Autowired
    private TeamRankRepository teamRankRepo;
    
    @Autowired
    private UserRankRepository userRankRepo;
            
    public List<User> getTop100UsersByXP(){
        List<User> listOfUsers = userRepo.getTop100UsersByXP();
        return listOfUsers;
    }
        
    public List<User> getTop100UsersByEarnings(){
        List<User> listOfUsers = userRepo.getTop100UsersByEarnings();
        return listOfUsers;
    }
  
    public List<MWRTeam> getTop100MWRTeamsByXP(RegionEnum region, PlatformEnum platform, TeamSizeEnum teamSize, TeamTypeEnum teamType){
        return teamRepo.getTop100MWRTeamsByXP(region, platform, teamSize, teamType);
    }
    
    public List<IWTeam> getTop100IWTeamsByXP(RegionEnum region, PlatformEnum platform, TeamSizeEnum teamSize){
        return teamRepo.getTop100IWTeamsByXP(region, platform, teamSize);
    }
        
    public List<WW2Team> getTop100WW2TeamsByXP(RegionEnum region, PlatformEnum platform, TeamSizeEnum teamSize, TeamTypeEnum teamType){
        return teamRepo.getTop100WW2TeamsByXP(region, platform, teamSize, teamType);
    }
    
    //need to schedule these methods below to run daily
    public void updateUserRankByXP(){
        userRankRepo.updateUserXpRankings();
    }
    
    public void updateUserRankByEarnings(){
        userRankRepo.updateUserEarningsRankings();
    }
    
    //need to add eu
    public void updateMWRTeamRankXP(){
        teamRankRepo.updateMWRTeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.SINGLES);
        teamRankRepo.updateMWRTeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.DOUBLES);
        teamRankRepo.updateMWRTeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.TEAM);
        teamRankRepo.updateMWRTeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.SINGLES);
        teamRankRepo.updateMWRTeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.DOUBLES);
        teamRankRepo.updateMWRTeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.TEAM);
    }
    
    public void updateIWTeamRankXP(){
        teamRankRepo.updateIWTeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.SINGLES);
        teamRankRepo.updateIWTeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.DOUBLES);
        teamRankRepo.updateIWTeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.TEAM);
        teamRankRepo.updateIWTeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.SINGLES);
        teamRankRepo.updateIWTeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.DOUBLES);
        teamRankRepo.updateIWTeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.TEAM);
    }
        
    public void updateWW2TeamRankXp(){
        //ps4 team ranks page 
        teamRankRepo.updateWW2TeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.SINGLES, TeamTypeEnum.XP);
        teamRankRepo.updateWW2TeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.XP);
        teamRankRepo.updateWW2TeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.TEAM, TeamTypeEnum.XP);
        teamRankRepo.updateWW2TeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.SINGLES, TeamTypeEnum.CASH);
        teamRankRepo.updateWW2TeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH);
        teamRankRepo.updateWW2TeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.TEAM, TeamTypeEnum.CASH);
        
        //xbox1 team ranks page
        teamRankRepo.updateWW2TeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.SINGLES, TeamTypeEnum.XP);
        teamRankRepo.updateWW2TeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.DOUBLES, TeamTypeEnum.XP);
        teamRankRepo.updateWW2TeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.TEAM, TeamTypeEnum.XP);
        teamRankRepo.updateWW2TeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.SINGLES, TeamTypeEnum.CASH);
        teamRankRepo.updateWW2TeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.DOUBLES, TeamTypeEnum.CASH);
        teamRankRepo.updateWW2TeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.TEAM, TeamTypeEnum.CASH);
    }
    
//    public void updateWW2CashTeamRank(){
//        teamRankRepo.updateWW2CashTeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.SINGLES);
//        teamRankRepo.updateWW2CashTeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.DOUBLES);
//        teamRankRepo.updateWW2CashTeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.TEAM);
//        teamRankRepo.updateWW2CashTeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.SINGLES);
//        teamRankRepo.updateWW2CashTeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.DOUBLES);
//        teamRankRepo.updateWW2CashTeamRanks(RegionEnum.NA, PlatformEnum.XBOXONE, TeamSizeEnum.TEAM);
//    }
}
