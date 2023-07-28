/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.MatchRepository;
import com.ltlogic.db.repository.TournamentRepository;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.iws.TeamInviteIWS;
import com.ltlogic.iws.TournamentServiceIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.RegistrationPojo;
import com.ltlogic.pojo.TeamPojo;
import com.ltlogic.pojo.TournamentPojo;
import com.ltlogic.service.challonge.ChallongeMatchService;
import com.ltlogic.service.core.TournamentService;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Bishistha
 */
@RestController
@RequestMapping(value = "/rest/soda")
public class TestWS {

    private String temp;

    @Autowired
    UserIWS userIWS;
    @Autowired
    TeamIWS teamIWS;
    @Autowired
    TeamInviteIWS teamInviteIWS;
    @Autowired
    TournamentServiceIWS tournamentServiceIWS;
    @Autowired
    ChallongeMatchService challongeMatchService;
    @Autowired
    MatchRepository matchRepository;
    @Autowired
    TournamentRepository tournamentRepository;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/up", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public String respondAsReady() {
        return "Test service is ready!";
    }

    @RequestMapping(value = "/down", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public String respondAsDone() {
        String upString = respondAsReady();
        return upString + "\n up or down, its still running";
    }

    /**
     * Basic test of input parameters from URL
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/hi/{firstName}", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public String sayHello(@PathVariable("firstName") String name) {
        temp = name;

        return "Hello, " + name + "!";
    }

    @RequestMapping(value = "/hello/{lastName}", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public String sayHelloByFullName(@PathVariable("lastName") String name) {

        System.out.println("hey i know your first name! quiet amazing global variable doing its thing");
        System.out.println("----------#########################################################################----------");

        return "Hello, " + temp + " " + name + "!";
    }
    
    @RequestMapping(value = "/syncChallongeMatch/{tournamentId}/{matchId}", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public String syncChallongeMatch(@PathVariable("tournamentId") int tournamentId, @PathVariable("matchId") int matchId) {
        challongeMatchService.syncSingleMatch(matchRepository.findMatchByMatchId(matchId), tournamentRepository.findTournamentByTournamentId(tournamentId));
        return "Success";
    }
    
    
//    @RequestMapping(value = "/teamsjointournament", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
//    @ResponseBody
//    public String createDefaultTournamentAndHaveTournamentTeamsJoin() {
//        String response = "";
//        int numOfTeamsToCreate = 60;
//        List<Team> cashSinglesTeamsCreated = createTeams(numOfTeamsToCreate, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH, TeamSizeEnum.SINGLES);
//        List<Team> freeSinglesTeamsCreated = createTeams(numOfTeamsToCreate, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.XP, TeamSizeEnum.SINGLES);
//        List<Team> cashDoublesTeamsCreated = createTeams(numOfTeamsToCreate, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH, TeamSizeEnum.DOUBLES);
//        List<Team> freeDoublesTeamsCreated = createTeams(numOfTeamsToCreate, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.XP, TeamSizeEnum.DOUBLES);
//        List<Team> cashFoursTeamsCreated = createTeams(numOfTeamsToCreate, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH, TeamSizeEnum.TEAM);
//        List<Team> freeFoursTeamsCreated = createTeams(numOfTeamsToCreate, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.XP, TeamSizeEnum.TEAM);
//        
//        TournamentPojo tournamentPojo = new TournamentPojo();
//        
//        tournamentServiceIWS.createTournament(null);
//        
//        for (Team t : cashSinglesTeamsCreated) {
//            response = response.concat("Team leader pk: " + t.getTeamLeaderPk() + " Team leader name: " + t.getTeamPojo().getUsername() + System.lineSeparator());
//        }
//        return response;
//    }
    
    

    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    @ResponseBody
    public String createDefaultUsersAndTeams() {
        String response = "";
        int numOfTeamsToCreate = 10;
        //createListOfUsers(0, 40);
        List<User> usersCreatedList = createTeams(numOfTeamsToCreate, GameEnum.COD_WW2, PlatformEnum.PS4, TeamTypeEnum.CASH, TeamSizeEnum.SINGLES, false, null);
        createTeams(numOfTeamsToCreate, GameEnum.COD_WW2, PlatformEnum.PS4, TeamTypeEnum.XP, TeamSizeEnum.SINGLES, true, usersCreatedList);
        createTeams(numOfTeamsToCreate, GameEnum.COD_WW2, PlatformEnum.PS4, TeamTypeEnum.CASH, TeamSizeEnum.DOUBLES, true, usersCreatedList);
        createTeams(numOfTeamsToCreate, GameEnum.COD_WW2, PlatformEnum.PS4, TeamTypeEnum.XP, TeamSizeEnum.DOUBLES, true, usersCreatedList);
        createTeams(numOfTeamsToCreate, GameEnum.COD_WW2, PlatformEnum.PS4, TeamTypeEnum.CASH, TeamSizeEnum.TEAM, true, usersCreatedList);
        createTeams(numOfTeamsToCreate, GameEnum.COD_WW2, PlatformEnum.PS4, TeamTypeEnum.XP, TeamSizeEnum.TEAM, true, usersCreatedList);
        
        createTeams(numOfTeamsToCreate, GameEnum.COD_WW2, PlatformEnum.XBOXONE, TeamTypeEnum.CASH, TeamSizeEnum.SINGLES, true, usersCreatedList);
        createTeams(numOfTeamsToCreate, GameEnum.COD_WW2, PlatformEnum.XBOXONE, TeamTypeEnum.XP, TeamSizeEnum.SINGLES, true, usersCreatedList);
        createTeams(numOfTeamsToCreate, GameEnum.COD_WW2, PlatformEnum.XBOXONE, TeamTypeEnum.CASH, TeamSizeEnum.DOUBLES, true, usersCreatedList);
        createTeams(numOfTeamsToCreate, GameEnum.COD_WW2, PlatformEnum.XBOXONE, TeamTypeEnum.XP, TeamSizeEnum.DOUBLES, true, usersCreatedList);
        createTeams(numOfTeamsToCreate, GameEnum.COD_WW2, PlatformEnum.XBOXONE, TeamTypeEnum.CASH, TeamSizeEnum.TEAM, true, usersCreatedList);
        createTeams(numOfTeamsToCreate, GameEnum.COD_WW2, PlatformEnum.XBOXONE, TeamTypeEnum.XP, TeamSizeEnum.TEAM, true, usersCreatedList);
        
//        for (Team t : freeSinglesTeamsCreated) {
//            response = response.concat("Team leader pk: " + t.getTeamLeaderPk() + " Team leader name: " + t.getTeamPojo().getUsername() + System.lineSeparator());
//        }
        return response;
    }

    public ArrayList<User> createListOfUsers(int indexToStartAt, int numOfUsersToCreate) {
        ArrayList<User> userList = new ArrayList<>();
        ArrayList<RegistrationPojo> registrationPojoList = new ArrayList<>();
        RegistrationPojo bRegistration;
        int indexToStopAt = indexToStartAt + numOfUsersToCreate;
        for (int i = indexToStartAt; i < indexToStopAt; i++) {
            bRegistration = new RegistrationPojo();
            bRegistration.setEmail("hnbusinesscontact" + i + "@ddgfdegsdfsgdsggdgf.com");
            bRegistration.setPassword("pass");
            bRegistration.setPasswordConfirm("pass");
            bRegistration.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
            bRegistration.setUsername("user" + i);
            bRegistration.setRegionId("0");
            registrationPojoList.add(bRegistration);
        }
        for (RegistrationPojo rp : registrationPojoList) {
            User user = new User();
            user = userIWS.registerUser(rp);
            user.getBank().setTotalAmount(new BigDecimal(50.00));
            user.getUserInfo().setPlayStation4Name("ola"+user.getPk());
            user.getUserInfo().setXboxOneGamerTag("comostas"+user.getPk());
            userList.add(user);
        }
        return userList;
    }
    public List<User> createTeams(int numberOfTeamsToCreate, GameEnum gameEnum, PlatformEnum platform, TeamTypeEnum teamType, TeamSizeEnum teamSize, boolean areUsersCreated, List<User> userList) {
        List<User> list = null;
        if(!areUsersCreated){
            list = createListOfUsers(0, numberOfTeamsToCreate); 
        }else{
            list = userList;
        }
        List<Team> teamList = new ArrayList<>();
        TeamPojo teamPojo;
        for (int i = 0; i < numberOfTeamsToCreate; i++) {
            teamPojo = new TeamPojo();
            teamPojo.setGame(gameEnum);
            teamPojo.setPlatform(platform);
            teamPojo.setTeamName("team"+i);
            teamPojo.setTeamSize(teamSize);
            teamPojo.setTeamType(teamType);
            teamPojo.setRegion(RegionEnum.NA);
            teamPojo.setUsername(list.get(i).getUsername());
            teamList.add(teamIWS.createTeam(teamPojo, list.get(i).getUsername()));
        }
        return list;
    }
    
}
