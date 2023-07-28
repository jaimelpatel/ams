///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package com.ltlogic.core.test.common;
//
//import com.ltlogic.constants.GameEnum;
//import com.ltlogic.constants.PlatformEnum;
//import com.ltlogic.constants.TeamSizeEnum;
//import com.ltlogic.constants.TeamTypeEnum;
//import com.ltlogic.constants.TimeZoneEnum;
//import com.ltlogic.db.entity.Team;
//import com.ltlogic.db.entity.TeamInvite;
//import com.ltlogic.db.entity.User;
//import com.ltlogic.iws.TeamIWS;
//import com.ltlogic.iws.TeamInviteIWS;
//import com.ltlogic.iws.UserIWS;
//import com.ltlogic.pojo.RegistrationPojo;
//import com.ltlogic.pojo.TeamPojo;
//import java.util.ArrayList;
//import java.util.List;
//import javax.transaction.Transactional;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * 
// * @author Hoang
// */
//@Component
////@Service
//@Transactional
//public class TestSetupComponent {
//
//    @Autowired
//    UserIWS userIWS;
//    @Autowired
//    TeamIWS teamIWS;
//    @Autowired
//    TeamInviteIWS teamInviteIWS;
//    
//    
//    public ArrayList<User> createListOfUsers(){
//        
//         //Creating some users first
//        ArrayList<User> userList = new ArrayList<>();
//        ArrayList<RegistrationPojo> registrationPojoList = new ArrayList<>();
//        
//        RegistrationPojo bRegistration;
//
//        for (int i = 0; i < 20; i++) {
//            bRegistration = new RegistrationPojo();
//            bRegistration.setEmail("hnbusinesscontact" + i + "@gmail.com");
//            bRegistration.setPassword("password123");
//            bRegistration.setPasswordConfirm("password123");
//            bRegistration.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
//            bRegistration.setUsername("hoangn" + i);
//            registrationPojoList.add(bRegistration);
//        }
//        for (RegistrationPojo rp : registrationPojoList){
//            userList.add(userIWS.registerUser(rp));
//        }
//        return userList;
//    }
//    
//    public List<Team> createSinglesTeams(List<User> userList){
//        
//        User hoangn0 = userList.get(0);
//        User hoangn1 = userList.get(1);
//        User hoangn2 = userList.get(2);
//        User hoangn3 = userList.get(3);
//        User hoangn4 = userList.get(4);
//        User hoangn5 = userList.get(5);
//        User hoangn6 = userList.get(6);
//        
//        //PS4 CASH
//        TeamPojo teamPojo = new TeamPojo();
//        teamPojo.setGame(GameEnum.COD_MWR);
//        teamPojo.setPlatform(PlatformEnum.PS4);
//        teamPojo.setTeamName("Team0");
//        teamPojo.setTeamSize(TeamSizeEnum.SINGLES);
//        teamPojo.setTeamType(TeamTypeEnum.CASH);
//        teamPojo.setUsername(hoangn0.getUsername());
//        Team team0 = teamIWS.createTeam(teamPojo, hoangn0.getUsername());
//        
//        
//        TeamPojo teamPojo1 = new TeamPojo();
//        teamPojo1.setGame(GameEnum.COD_MWR);
//        teamPojo1.setPlatform(PlatformEnum.PS4);
//        teamPojo1.setTeamName("Team1");
//        teamPojo1.setTeamSize(TeamSizeEnum.SINGLES);
//        teamPojo1.setTeamType(TeamTypeEnum.CASH);
//        teamPojo1.setUsername(hoangn1.getUsername());
//        Team team1 = teamIWS.createTeam(teamPojo1, hoangn1.getUsername());
//        
//        
//        TeamPojo teamPojo2 = new TeamPojo();
//        teamPojo2.setGame(GameEnum.COD_IW);
//        teamPojo2.setPlatform(PlatformEnum.PS4);
//        teamPojo2.setTeamName("Team2");
//        teamPojo2.setTeamSize(TeamSizeEnum.SINGLES);
//        teamPojo2.setTeamType(TeamTypeEnum.CASH);
//        teamPojo2.setUsername(hoangn2.getUsername());
//        Team team2 = teamIWS.createTeam(teamPojo2, hoangn2.getUsername());
//        
//        TeamPojo teamPojo3 = new TeamPojo();
//        teamPojo3.setGame(GameEnum.COD_IW);
//        teamPojo3.setPlatform(PlatformEnum.PS4);
//        teamPojo3.setTeamName("Team3");
//        teamPojo3.setTeamSize(TeamSizeEnum.SINGLES);
//        teamPojo3.setTeamType(TeamTypeEnum.CASH);
//        teamPojo3.setUsername(hoangn3.getUsername());
//        Team team3 = teamIWS.createTeam(teamPojo3, hoangn3.getUsername());
//        
//        //XBOXONE CASH
//        TeamPojo teamPojo4 = new TeamPojo();
//        teamPojo4.setGame(GameEnum.COD_MWR);
//        teamPojo4.setPlatform(PlatformEnum.XBOXONE);
//        teamPojo4.setTeamName("Team4");
//        teamPojo4.setTeamSize(TeamSizeEnum.SINGLES);
//        teamPojo4.setTeamType(TeamTypeEnum.CASH);
//        teamPojo4.setUsername(hoangn4.getUsername());
//        Team team4 = teamIWS.createTeam(teamPojo4, hoangn4.getUsername());
//        
//        
//        TeamPojo teamPojo5 = new TeamPojo();
//        teamPojo5.setGame(GameEnum.COD_MWR);
//        teamPojo5.setPlatform(PlatformEnum.XBOXONE);
//        teamPojo5.setTeamName("Team5");
//        teamPojo5.setTeamSize(TeamSizeEnum.SINGLES);
//        teamPojo5.setTeamType(TeamTypeEnum.CASH);
//        teamPojo5.setUsername(hoangn1.getUsername());
//        Team team5 = teamIWS.createTeam(teamPojo5, hoangn1.getUsername());
//        
//        TeamPojo teamPojo5 = new TeamPojo();
//        teamPojo5.setGame(GameEnum.COD_IW);
//        teamPojo5.setPlatform(PlatformEnum.XBOXONE);
//        teamPojo5.setTeamName("Team5");
//        teamPojo5.setTeamSize(TeamSizeEnum.SINGLES);
//        teamPojo5.setTeamType(TeamTypeEnum.CASH);
//        teamPojo5.setUsername(hoangn4.getUsername());
//        Team team5 = teamIWS.createTeam(teamPojo4, hoangn4.getUsername());
//        
//        
//        TeamPojo teamPojo5 = new TeamPojo();
//        teamPojo5.setGame(GameEnum.COD_MWR);
//        teamPojo5.setPlatform(PlatformEnum.XBOXONE);
//        teamPojo5.setTeamName("Team5");
//        teamPojo5.setTeamSize(TeamSizeEnum.SINGLES);
//        teamPojo5.setTeamType(TeamTypeEnum.CASH);
//        teamPojo5.setUsername(hoangn1.getUsername());
//        Team team5 = teamIWS.createTeam(teamPojo5, hoangn1.getUsername());
//        
//        
//        return null;
//        
//        
//        
//    }
//    
//    public List<Team> inviteUsersToDoublesTeam(List<User> userList){
//        
//        List<Team> teamList = new ArrayList<>();
//        
//        User hoangn1 = userList.get(0);
//        User hoangn2 = userList.get(1);
//        User hoangn3 = userList.get(2);
//        User hoangn4 = userList.get(3);
//        
//        User hoangn5 = userList.get(4);
//        User hoangn6 = userList.get(5);
//
//        
//        TeamPojo teamPojo = new TeamPojo();
//        teamPojo.setGame(GameEnum.COD_MWR);
//        teamPojo.setPlatform(PlatformEnum.PS4);
//        teamPojo.setTeamName("Team1");
//        teamPojo.setTeamSize(TeamSizeEnum.DOUBLES);
//        teamPojo.setTeamType(TeamTypeEnum.CASH);
//        teamPojo.setUsername(hoangn1.getUsername());
//        
//        Team team1 = teamIWS.createTeam(teamPojo, hoangn1.getUsername());
//        TeamInvite teamInvite = teamInviteIWS.inviteUserToTeam(hoangn1.getUsername(), hoangn3.getUsername(), team1.getPk());
//        TeamInvite teamInvite3 = teamInviteIWS.inviteUserToTeam(hoangn1.getUsername(), hoangn5.getUsername(), team1.getPk());
//        
//        
//        teamInviteIWS.acceptInvite(hoangn3, team1, teamInvite);
//        teamInviteIWS.acceptInvite(hoangn5, team1, teamInvite3);
//        
//
//        TeamPojo teamPojo2 = new TeamPojo();
//        teamPojo2.setGame(GameEnum.COD_MWR);
//        teamPojo2.setPlatform(PlatformEnum.PS4);
//        teamPojo2.setTeamName("Team2");
//        teamPojo2.setTeamSize(TeamSizeEnum.DOUBLES);
//        teamPojo2.setTeamType(TeamTypeEnum.CASH);
//        teamPojo2.setUsername(hoangn2.getUsername());
//        
//        Team team2 = teamIWS.createTeam(teamPojo2, hoangn2.getUsername());
//        TeamInvite teamInvite2 = teamInviteIWS.inviteUserToTeam(hoangn2.getUsername(), hoangn4.getUsername(), team2.getPk());
//        TeamInvite teamInvite4 = teamInviteIWS.inviteUserToTeam(hoangn2.getUsername(), hoangn6.getUsername(), team2.getPk());
//        
//       
//        teamInviteIWS.acceptInvite(hoangn4, team2, teamInvite2);
//        teamInviteIWS.acceptInvite(hoangn6, team2, teamInvite4);
//        
//        teamList.add(team1);
//        teamList.add(team2);
//        return teamList;
//        
//    }
//    
//}
