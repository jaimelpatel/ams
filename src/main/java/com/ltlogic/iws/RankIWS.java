/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;

import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.iw.IWTeam;
import com.ltlogic.db.entity.mwr.MWRTeam;
import com.ltlogic.db.entity.ww2.WW2Team;
import com.ltlogic.service.core.RankService;
import com.ltlogic.service.core.TeamService;
import com.ltlogic.service.core.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Jaimel
 */
@Service
public class RankIWS {
    
    @Autowired
    private RankService rankService;
    
    public List<User> getTop100UsersByXP(){
        return rankService.getTop100UsersByXP();
    }
        
    public List<User> getTop100UsersByEarnings(){
        return rankService.getTop100UsersByEarnings();
    }
    
    public List<MWRTeam> getTop100MWRTeamsByXP(RegionEnum region, PlatformEnum platform, TeamSizeEnum teamSize, TeamTypeEnum teamType){
        return rankService.getTop100MWRTeamsByXP(region, platform, teamSize, teamType);
    }
    
    public List<IWTeam> getTop100IWTeamsByXP(RegionEnum region, PlatformEnum platform, TeamSizeEnum teamSize){
        return rankService.getTop100IWTeamsByXP(region, platform, teamSize);
    }
        
    public List<WW2Team> getTop100WW2TeamsByXP(RegionEnum region, PlatformEnum platform, TeamSizeEnum teamSize, TeamTypeEnum teamType){
        return rankService.getTop100WW2TeamsByXP(region, platform, teamSize, teamType);
    }
}
