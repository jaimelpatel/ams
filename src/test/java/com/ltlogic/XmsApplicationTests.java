//package com.ltlogic;
//
//import com.ltlogic.constants.GameEnum;
//import com.ltlogic.constants.MatchStatusEnum;
//import com.ltlogic.constants.MatchTypeEnum;
//import com.ltlogic.constants.PlatformEnum;
//import com.ltlogic.constants.RegionEnum;
//import com.ltlogic.constants.TeamPermissionsEnum;
//import com.ltlogic.constants.TeamSizeEnum;
//import com.ltlogic.constants.TeamTypeEnum;
//import com.ltlogic.constants.TimeZoneEnum;
//import com.ltlogic.constants.TournamentStatusEnum;
//import com.ltlogic.db.entity.mwr.MWRMatch;
//import com.ltlogic.db.entity.Match;
//import com.ltlogic.db.entity.rank.user.UserRankXP;
//import com.ltlogic.db.entity.Team;
//import com.ltlogic.db.entity.TeamInvite;
//import com.ltlogic.db.entity.TeamPermissions;
//import com.ltlogic.db.entity.Tournament;
//import com.ltlogic.db.entity.TournamentTeam;
//import com.ltlogic.db.entity.User;
//import com.ltlogic.db.entity.mwr.MWRTeam;
//import com.ltlogic.db.repository.MatchRepository;
//import com.ltlogic.db.repository.TeamRankRepository;
//import com.ltlogic.db.repository.UserRankRepository;
//import com.ltlogic.db.repository.TeamRepository;
//import com.ltlogic.db.repository.TournamentRepository;
//import com.ltlogic.db.repository.UserRepository;
//import com.ltlogic.iws.TeamIWS;
//import com.ltlogic.iws.TeamInviteIWS;
//import com.ltlogic.iws.UserIWS;
//import com.ltlogic.pojo.MatchPojo;
//import com.ltlogic.pojo.RegistrationPojo;
//import com.ltlogic.pojo.TeamPojo;
//import com.ltlogic.pojo.UserPojo;
//import com.ltlogic.service.challonge.ChallongeMatchService;
//import com.ltlogic.service.challonge.ChallongeParticipantService;
//import com.ltlogic.service.challonge.ChallongeTournamentService;
//import com.ltlogic.service.core.TeamInviteService;
//import com.ltlogic.service.core.TeamService;
//import com.ltlogic.service.core.UserService;
//import com.ltlogic.service.springsecurity.UserValidator;
//import java.math.BigDecimal;
//import java.security.Principal;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//import java.util.Properties;
//import java.util.Set;
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//import javax.persistence.EntityManager;
//import javax.transaction.Transactional;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.TimeZone;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.stereotype.Service;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.annotation.DirtiesContext.ClassMode;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.context.transaction.TransactionConfiguration;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.Errors;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@Transactional
//@TransactionConfiguration(defaultRollback = false)
////@Ignore
////@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
//
///*
// Populate db before each test
//
// @Transactional(value=JpaConfiguration.TRANSACTION_MANAGER_NAME)
// @Sql(executionPhase=ExecutionPhase.BEFORE_TEST_METHOD,scripts="classpath:/test-sql/group2.sql")
// */
//public class XmsApplicationTests {
//
//    @Autowired
//    ChallongeTournamentService challongeTournamentService;
//    @Autowired
//    ChallongeParticipantService cps;
//
//    @Autowired
//    ChallongeMatchService challongeMatchService;
//    @Autowired
//    UserRepository userRepo;
//    @Autowired
//    UserRankRepository rankRepo;
//    @Autowired
//    TeamRepository teamRepo;
//    @Autowired
//    MatchRepository matchRepo;
//    @Autowired
//    UserValidator formValidator;
//    @Autowired
//    UserService userService;
//    @Autowired
//    TeamService teamService;
//    @Autowired
//    TeamInviteService teamInviteService;
//    @Autowired
//    UserIWS userIWS;
//    @Autowired
//    TeamIWS teamIWS;
//    @Autowired
//    TeamInviteIWS teamInviteIWS;
//    @Autowired
//    EntityManager em;
//    @Autowired
//    TeamRankRepository teamRankRepo;
//    @Autowired
//    TournamentRepository tournamentRepo;
//
//    private static final Logger LOG = LoggerFactory.getLogger(XmsApplicationTests.class);
//
//    @Ignore
//    @Test
//    public void loginTest() {
////        User u = new User();
////        u.setAge(0);
////        u.setUsername("admin1");
////        u.setPassword("admin1");
////        userRepo.persistUser(u);
////        Team t = new Team();
////        t.setTeamName("NEXT LEVEL");
////        teamRepo.persistTeam(t);
////        u.getTeams().add(t);
////        t.getMembers().add(u);
//    }
//
////    @Test
////    @Ignore
////    public void testTeamCreation(){
////        User u = new User();
////        UserPojo up = new UserPojo();
////        u.setUserInfo(up);
////        u.setUsername("jaimel");
////        u.getUserInfo().setFirstName("jaimel");
////        u.getUserInfo().setLastName("patel");
////        userRepo.persistUser(u);
////        
////                User u2 = new User();
////        UserPojo up2 = new UserPojo();
////        u2.setUserInfo(up2);
////        u2.setUsername("hoang");
////        u2.getUserInfo().setFirstName("hoang");
////        u2.getUserInfo().setLastName("patel");
////        userRepo.persistUser(u2);
////        
////        TeamPojo tp = new TeamPojo();
////        tp.setGame(GameEnum.COD_MWR);
////        tp.setTeamSize(TeamSizeEnum.DOUBLES);
////        tp.setPlatform(PlatformEnum.PS4);
////        tp.setTeamType(TeamTypeEnum.XP);
////        tp.setTeamName("TESTING11111111");
////        
////        TeamPojo tp1 = new TeamPojo();
////        tp1.setGame(GameEnum.COD_MWR);
////        tp1.setTeamSize(TeamSizeEnum.SINGLES);
////        tp1.setPlatform(PlatformEnum.PS4);
////        tp1.setTeamType(TeamTypeEnum.XP);
////        tp1.setTeamName("TESTING22222222");
////        
////        TeamPojo tp2 = new TeamPojo();
////        tp2.setGame(GameEnum.COD_MWR);
////        tp2.setTeamSize(TeamSizeEnum.TEAM);
////        tp2.setPlatform(PlatformEnum.PS4);
////        tp2.setTeamType(TeamTypeEnum.XP);
////        tp2.setTeamName("TESTING3333333");
////        
////        teamService.createTeam(tp, u.getUsername());
////        teamService.createTeam(tp1, u.getUsername());
////        teamService.createTeam(tp2, u.getUsername());
////        Team t = teamRepo.findTeamByPk(1);
////        System.out.println("////////////////////////TEAM NAME: " + t.getTeamPojo().getTeamName());
////        Team t1 = teamRepo.findTeamByPk(2);
////        System.out.println("////////////////////////TEAM NAME: " + t1.getTeamPojo().getTeamName());
////        Team t2 = teamRepo.findTeamByPk(3);
////        System.out.println("////////////////////////TEAM NAME: " + t2.getTeamPojo().getTeamName());
////        MWRTeam mt = teamRepo.findMWRTeamByTeamPk(2);
////        System.out.println("////////////////////////MWRTEAM NAME: " + mt.getTeamPojo().getTeamName());
////        
////        List<Team> teams = userRepo.getAllTeamsOfUserByTeamType("jaimel", TeamTypeEnum.XP);
////        for(Team tea : teams){
////            System.out.println("TEAMSSSSSSSSSSSSSSSSSSSSSS: " + tea.getPk());
////        }
////        
////        teamInviteService.inviteUserToTeam("jaimel", "hoang", 1);
////        
////        List<TeamInvite> lti = teamInviteService.getPendingInviteByUserPk(2);
////        
////        for(TeamInvite x: lti){
////            System.out.println("TEAM INVITES PK: " + x.getTeamInvites().getPk());
////            System.out.println("TEAM INVITES TEAM ID INVITED FROM: " + x.getTeamInvites().getTeamId());
////        }
////        
////    }
//    //@Rollback(true)
//    @Test
//    @Ignore
//    public void addUserTest() {
//        User u = new User();
//        u.setUsername("jaimel");
//        u.getUserInfo().setFirstName("jaimel");
//        u.getUserInfo().setLastName("patel");
//        userRepo.persistUser(u);
//
//        User v = new User();
//        v.setUsername("hoang");
//        v.getUserInfo().setFirstName("hoang");
//        v.getUserInfo().setLastName("nguyen");
//        userRepo.persistUser(v);
//
//        User x = new User();
//        x.setUsername("bishistha");
//        x.getUserInfo().setFirstName("bishistha");
//        x.getUserInfo().setLastName("shrestha");
//        userRepo.persistUser(x);
//
//        User a = new User();
//        a.setUsername("jose");
//        a.getUserInfo().setFirstName("jose");
//        a.getUserInfo().setLastName("sanchez");
//        userRepo.persistUser(a);
//
//        User b = new User();
//        b.setUsername("donald");
//        b.getUserInfo().setFirstName("donald");
//        b.getUserInfo().setLastName("trump");
//        userRepo.persistUser(b);
//
//        User c = new User();
//        c.setUsername("allahu");
//        c.getUserInfo().setFirstName("allahu");
//        c.getUserInfo().setLastName("akbar");
//        userRepo.persistUser(c);
//
//        Team aTeam = new MWRTeam();
//        aTeam.getTeamPojo().setTeamName("nxtlvl");
//        aTeam.getMembers().add(u);
//        aTeam.getMembers().add(v);
//        aTeam.getMembers().add(x);
//        teamRepo.persistTeam(aTeam);
//
//        Team bTeam = new MWRTeam();
//        bTeam.getTeamPojo().setTeamName("lowlvl");
//        bTeam.getMembers().add(a);
//        bTeam.getMembers().add(b);
//        bTeam.getMembers().add(c);
//        bTeam.getMembers().add(v);
//        teamRepo.persistTeam(bTeam);
//
//        a.getTeams().add(bTeam);
//        b.getTeams().add(bTeam);
//        c.getTeams().add(bTeam);
//
//        u.getTeams().add(aTeam);
//        v.getTeams().add(aTeam);
//        v.getTeams().add(bTeam);
//        x.getTeams().add(aTeam);
//
//        MWRMatch firstMatch = new MWRMatch();
//        firstMatch.getMatchInfo().setGameEnum(GameEnum.COD_MWR);
//        firstMatch.getMatchInfo().setMatchType(MatchTypeEnum.XP);
//        firstMatch.getMatchInfo().setPlatform(PlatformEnum.PS4);
//        firstMatch.getTeamsInMatch().add(aTeam);
//        firstMatch.getTeamsInMatch().add(bTeam);
//        firstMatch.getUsersInMatch().addAll(aTeam.getMembers());
//        firstMatch.getUsersInMatch().addAll(bTeam.getMembers());
////        firstMatch.setRowUpdatedTimestamp(DateTimeUtil.getDefaultLocalDateTimeNow());
//        matchRepo.persistMatch(firstMatch);
//
//        List<Team> hoangsTeams = teamService.getAllTeamsOfUser("hoang");
//        System.out.println(hoangsTeams.size());
//        for (Team t : hoangsTeams) {
//            System.out.println(t.getTeamPojo().getTeamName());
//        }
//
//    }
//
//    @Test
//    @Ignore
//    public void teamCreationValidationTest() {
////        User u = new User();
////        u.setUsername("jaimel");
////        u.setFirstName("jaimel");
////        u.setLastName("patel");
////        userRepo.persistUser(u);
////        
////        Team t = new Team();
////        t.setTeamName("Team");
////        t.setGameEnum(GameEnum.COD_MWR);
////        t.setPlatformEnum(PlatformEnum.PS4);
////        t.setTeamSizeEnum(TeamSizeEnum.DOUBLES);
////        t.setTeamTypeEnum(TeamTypeEnum.XP);
////        t.getMembers().add(u);
////        teamRepo.persistTeam(t);
////        
////        u.getTeams().add(t);
////        
////        TeamPojo tPojo = new TeamPojo();
////        tPojo.setTeamName("Team");
////        tPojo.setGame(GameEnum.COD_MWR);
////        tPojo.setPlatform(PlatformEnum.PS4);
////        tPojo.setTeamSize(TeamSizeEnum.DOUBLES);
////        tPojo.setTeamType(TeamTypeEnum.CASH);
////        tPojo.setUsername("jaimel");
////        
////        String message = formValidator.teamCreationValidation(tPojo, null);
////        
////        System.out.println("///////////////////////////////////////////////// Error Results Message: " + message);
//    }
//
//    @Test
//    @Ignore
//    public void sendEmailTest() {
//        long startTime = System.currentTimeMillis();
//        final String username = "johnsmithbusiness2017@gmail.com";
//        final String password = "SodaCan1337";
//
//        Properties props = new Properties();
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "587");
//
//        //for ssl
////        props.put("mail.smtp.socketFactory.port", "465");
////        props.put("mail.smtp.socketFactory.class",
////                 "javax.net.ssl.SSLSocketFactory");
//        Session session = Session.getInstance(props,
//                new javax.mail.Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username, password);
//            }
//        });
//
//        try {
//
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress("johnsmithbusiness2017@gmail.com"));
//            message.setRecipients(Message.RecipientType.TO,
//                    InternetAddress.parse("jaimelpatel@yahoo.com"));
//            message.setSubject("Testing Subject");
//            message.setText("Dear Mail Crawler,"
//                    + "\n\n No spam to my email, please!");
//
//            Transport.send(message);
//
//            System.out.println("Done");
//
//            long endTime = System.currentTimeMillis();
//
//            System.out.println("That took " + (endTime - startTime) / 1000.00 + " seconds");
//
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    @Ignore
//    public void testGetUsersByUserPksList() {
////        ArrayList<Long> list = new ArrayList<Long>();
////        list.add(new Long(2));
////        list.add(new Long(3));
////        list.add(new Long(1));
////        list.add(new Long(3));
////        List<User> finalList = userService.getListOfUserObjectsFromListOfUserPks(list);
////        System.out.println("LIST SIZE: " + finalList.size());
////        for(User u : finalList){          
////            System.out.println(".////////////////////////////////////////////////////////////USER NAME: " + u.getUsername());
////        }
//    }
//
//    @Test
//    @Ignore
//    public void testTeamPermissions() {
//        User u = new User();
//        UserPojo up = new UserPojo();
//        up.setFirstName("jaimel");
//        u.setUserInfo(up);
//        userRepo.persistUser(u);
//
//        TeamPojo tPojo = new TeamPojo();
//        tPojo.setTeamName("Team");
//
//        Team t = new Team();
//        t.setTeamPojo(tPojo);
//        t.getMembers().add(u);
//        teamRepo.persistTeam(t);
//        u.getTeams().add(t);
//
//        TeamPermissions tp = new TeamPermissions();
//        tp.setTeam(t);
//        tp.setUser(u);
//        teamRepo.persistTeamPermissions(tp);
//
////        tp.getTeamPermissions().add(TeamPermissionsEnum.KICK_PLAYERS);
////        tp.getTeamPermissions().add(TeamPermissionsEnum.EDIT_TEAM_INFO);
//    }
//
//    @Test
//    @Ignore
//    public void createMatchTest() {
//        User u = new User();
//        UserPojo up = new UserPojo();
//        up.setFirstName("jaimel");
//        u.setUserInfo(up);
//        userRepo.persistUser(u);
//
//        TeamPojo tPojo = new TeamPojo();
//        tPojo.setTeamName("Team");
//
//        Team t = new Team();
//        t.setTeamPojo(tPojo);
//        t.getMembers().add(u);
//        teamRepo.persistTeam(t);
//        u.getTeams().add(t);
//
//        MatchPojo matchPojo = new MatchPojo();
//        matchPojo.setMatchStatus(MatchStatusEnum.PENDING);
//        matchPojo.setMatchType(MatchTypeEnum.WAGER);
//        matchPojo.setPlatform(PlatformEnum.PS4);
//        MWRMatch m = new MWRMatch();
//        m.getMwrMatchInfo().setJuggernaut(true);
//        m.getMwrMatchInfo().setJuggernaut(true);
//        m.setMatchInfo(matchPojo);
//        m.getUsersInMatch().add(u);
//        m.getTeamsInMatch().add(t);
//        matchRepo.persistMatch(m);
//
//        MatchPojo matchPojo1 = new MatchPojo();
//        matchPojo1.setMatchStatus(MatchStatusEnum.PENDING);
//        matchPojo1.setMatchType(MatchTypeEnum.WAGER);
//        matchPojo1.setPlatform(PlatformEnum.PS4);
//        MWRMatch m2 = new MWRMatch();
//        m2.getMwrMatchInfo().setJuggernaut(true);
//        m2.getMwrMatchInfo().setJuggernaut(true);
//        m2.setMatchInfo(matchPojo1);
//        m2.getUsersInMatch().add(u);
//        m2.getTeamsInMatch().add(t);
//        matchRepo.persistMatch(m2);
//
//        u.getMatches().add(m);
//        t.getMatches().add(m);
//        u.getMatches().add(m2);
//        t.getMatches().add(m2);
//
//        List<Match> list = matchRepo.findMatchesByUserPk(1);
//        for (Match ma : list) {
//            System.out.println("MATCH PKS--------------------------------- " + ma.getPk());
//        }
//    }
//
//    public Team create4PMWRTeam() {
//
//        //Creating some users first
//        ArrayList<RegistrationPojo> registrationPojoList = new ArrayList<>();
//        RegistrationPojo bRegistration0;
//        RegistrationPojo bRegistration1;
//        RegistrationPojo bRegistration2;
//        RegistrationPojo bRegistration3;
//
//        bRegistration0 = new RegistrationPojo();
//        bRegistration0.setEmail("shouldbetheleader@yahoo.com");
//        bRegistration0.setPassword("p");
//        bRegistration0.setPasswordConfirm("p");
//        bRegistration0.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
//        bRegistration0.setUsername("iShouldBeTheLeader");
//        registrationPojoList.add(bRegistration0);
//
//        bRegistration1 = new RegistrationPojo();
//        bRegistration1.setEmail("jaimelpatel523454@yahoo.com");
//        bRegistration1.setPassword("p");
//        bRegistration1.setPasswordConfirm("p");
//        bRegistration1.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
//        bRegistration1.setUsername("jaimel");
//        registrationPojoList.add(bRegistration1);
//
//        bRegistration2 = new RegistrationPojo();
//        bRegistration2.setEmail("jaimelpatel123@yahoo.com");
//        bRegistration2.setPassword("p");
//        bRegistration2.setPasswordConfirm("p");
//        bRegistration2.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
//        bRegistration2.setUsername("jaimel12345");
//        registrationPojoList.add(bRegistration2);
//
//        bRegistration3 = new RegistrationPojo();
//        bRegistration3.setEmail("jaimelpatel12345@yahoo.com");
//        bRegistration3.setPassword("p");
//        bRegistration3.setPasswordConfirm("p");
//        bRegistration3.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
//        bRegistration3.setUsername("testuser");
//        registrationPojoList.add(bRegistration3);
//
//        User user0 = userIWS.registerUser(bRegistration0);
//        User user1 = userIWS.registerUser(bRegistration1);
//        User user2 = userIWS.registerUser(bRegistration2);
//        User user3 = userIWS.registerUser(bRegistration3);
//
//        TeamPojo teamPojo = new TeamPojo();
//        teamPojo.setGame(GameEnum.COD_MWR);
//        teamPojo.setPlatform(PlatformEnum.PS4);
//        teamPojo.setTeamName("Team1");
//        teamPojo.setTeamSize(TeamSizeEnum.TEAM);
//        teamPojo.setTeamType(TeamTypeEnum.CASH);
//        teamPojo.setUsername("jaimel");
//
////        TeamPojo teamPojo2 = new TeamPojo();
////        teamPojo2.setGame(GameEnum.COD_IW);
////        teamPojo2.setPlatform(PlatformEnum.PS4);
////        teamPojo2.setTeamName("Team2");
////        teamPojo2.setTeamSize(TeamSizeEnum.SINGLES);
////        teamPojo2.setTeamType(TeamTypeEnum.XP);
////        teamPojo2.setUsername("jaimel");
////        
////                TeamPojo teamPojo3 = new TeamPojo();
////        teamPojo3.setGame(GameEnum.COD_IW);
////        teamPojo3.setPlatform(PlatformEnum.XBOXONE);
////        teamPojo3.setTeamName("Team3");
////        teamPojo3.setTeamSize(TeamSizeEnum.SINGLES);
////        teamPojo3.setTeamType(TeamTypeEnum.XP);
////        teamPojo3.setUsername("jaimel");
//        Team team1 = teamIWS.createTeam(teamPojo, "iShouldBeTheLeader");
//        teamService.associateUserToTeam(team1, user1);
//        teamService.associateUserToTeam(team1, user2);
//        teamService.associateUserToTeam(team1, user3);
//
////        Team team2 = teamIWS.createTeam(teamPojo2, "jaimel");
////        Team team3 = teamIWS.createTeam(teamPojo3, "jaimel");
////        
////        List<Team> teams = teamRepo.findTeamsByUsername("jaimel");
////        for(Team t : teams){
////            System.out.println(t.getPk());
////        }
//        List<User> users = userRepo.getAllUsersOnTeam(team1.getPk());
//        for (User user : users) {
//            LOG.info("USER NAME: " + user.getUsername());
//        }
//        return team1;
//    }
//
//    @Test
//    @Ignore
//    public void createTeamTest() {
//        Team team = create4PMWRTeam();
//        LOG.info("################### Team:  " + team.getTeamId() + " : " + team.getTeamLeaderPk());
//        RegistrationPojo regPojo = new RegistrationPojo();
//        regPojo.setEmail("shouldbetheleader@yahoo.com");
//        regPojo.setPassword("p");
//        regPojo.setPasswordConfirm("p");
//        regPojo.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
//        regPojo.setUsername("iShouldBeInvited");
//        User userToInvite = userIWS.registerUser(regPojo);
//
//        User teamLeader = userIWS.getUserByPk(team.getTeamLeaderPk());
////        TeamInvite teamInvite = teamIWS.inviteUserToTeam(teamLeader.getUsername(), "iShouldBeInvited", team.getPk());
////        
////        
////        LOG.info("### Team Invite with pk {} sent to user with username {} is in status {}", teamInvite.getPk(), userToInvite.getUsername(), teamInvite.getInviteEnum());
////        LOG.info("### User is about to accept the invitation now.");
////        //TeamInvite teamInvite = teamInviteIWS.findInviteByPk(teamInvitePk);
////        teamInviteIWS.acceptInvite(userToInvite, team, teamInvite);
////        LOG.info("### Team Invite with pk {} sent to user with username {} is in status {}", teamInvite.getPk(), userToInvite.getUsername(), teamInvite.getInviteEnum());
////        //teamInvite.get
//    }
//
//    @Test
//    @Ignore
//    public void testTimeZoneConversion() {
//
//        String DATE_FORMAT = "M-d-yyyy hh:mm:ss a";
//
//        User u = new User();
//        u.setUsername("jaimel");
//        UserPojo up = new UserPojo();
//        up.setFirstName("jaimel");
//        u.setUserInfo(up);
//        u.setTestingJavaTimeinDb(DateTimeUtil.getDefaultLocalDateTimeNow());
//        userRepo.persistUser(u);
//
//        ZoneId defaultZone = TimeZone.getDefault().toZoneId();
//        System.out.println("####### time zone default: " + defaultZone.toString());
//        System.out.println("####### time zone default: " + defaultZone.getId());
//
////        System.out.println("####### TIME IN LA: " + u.getTestingJavaTimeinDb());
////        LocalDateTime nyTime = u.getTestingJavaTimeinDb(ZoneId.of("America/New_York"));
////        System.out.println("####### TIME IN NY: " + nyTime);
////        System.out.println("####### TIME IN AZ: " + u.getTestingJavaTimeinDb(ZoneId.of("America/Phoenix")));
////        System.out.println("####### TIME IN CO: " + u.getTestingJavaTimeinDb(ZoneId.of("America/Denver")));
//    }
//
//    @Test
//    @Ignore
//    public void testRankQuery() {
//
////        User u = new User();
////        u.setUsername("jaimel");
////        UserPojo up = new UserPojo();
////        up.setFirstName("jaimel");
////        u.setUserInfo(up);
////        userRepo.persistUser(u);
////        Rank rank = new Rank();
////        rank.setUser(u);
////        u.setRank(rank);
////        rank.setTotalXp(1000);
////        rank.setTotalEarnings(new BigDecimal(100));
////        rankRepo.persistRank(rank);
////
////        
////        User u1 = new User();
////        u1.setUsername("jaimel1");
////        UserPojo up1 = new UserPojo();
////        up1.setFirstName("jaimel1");
////        u1.setUserInfo(up1);
////        userRepo.persistUser(u1);
////        Rank rank1 = new Rank();
////        rank1.setUser(u1);
////        u1.setRank(rank1);
////        rank1.setTotalXp(2000);
////        rank1.setTotalEarnings(new BigDecimal(100));
////        rankRepo.persistRank(rank1);
////        
////        User u2 = new User();
////        u2.setUsername("jaimel2");
////        UserPojo up2 = new UserPojo();
////        up2.setFirstName("jaimel2");
////        u2.setUserInfo(up2);
////        userRepo.persistUser(u2);
////        Rank rank2 = new Rank();
////        rank2.setUser(u2);
////        u2.setRank(rank2);
////        rank2.setTotalXp(3000);
////        rank2.setTotalEarnings(new BigDecimal(100));
////        rankRepo.persistRank(rank2);
////        
//////        int i = rankRepo.updateXpRanks();
////        int i = rankRepo.updateEarningsRanks();
////        System.out.println("-------------ROWS CHANGED: " + i);
//    }
//
//    @Test
//    @Ignore
//    public void testMWRTeamRankQuery() {
//
//        ArrayList<Team> teamList = new ArrayList<>();
//
//        //Creating some users first
//        ArrayList<RegistrationPojo> registrationPojoList = new ArrayList<>();
//        ArrayList<User> userList = new ArrayList<>();
//        RegistrationPojo bRegistration;
//
//        for (int i = 1; i < 9; i++) {
//            bRegistration = new RegistrationPojo();
//            bRegistration.setEmail("hnbusinesscontact" + i + "@gmail.com");
//            bRegistration.setPassword("password123");
//            bRegistration.setPasswordConfirm("password123");
//            bRegistration.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
//            bRegistration.setRegionId("0");
//            bRegistration.setUsername("hoangn" + i);
//            registrationPojoList.add(bRegistration);
//        }
//
//        User user0 = userIWS.registerUser(registrationPojoList.get(0));
//        User user1 = userIWS.registerUser(registrationPojoList.get(1));
//        User user2 = userIWS.registerUser(registrationPojoList.get(2));
//        User user3 = userIWS.registerUser(registrationPojoList.get(3));
//        User user4 = userIWS.registerUser(registrationPojoList.get(4));
//
//        TeamPojo teamPojo = new TeamPojo();
//        teamPojo.setGame(GameEnum.COD_MWR);
//        teamPojo.setPlatform(PlatformEnum.PS4);
//        teamPojo.setTeamName("Team1");
//        teamPojo.setTeamSize(TeamSizeEnum.SINGLES);
//        teamPojo.setTeamType(TeamTypeEnum.XP);
//        teamPojo.setUsername("hoangn1");
//        teamPojo.setRegion(RegionEnum.NA);
//
//        TeamPojo teamPojo2 = new TeamPojo();
//        teamPojo2.setGame(GameEnum.COD_IW);
//        teamPojo2.setPlatform(PlatformEnum.PS4);
//        teamPojo2.setTeamName("Team2");
//        teamPojo2.setTeamSize(TeamSizeEnum.SINGLES);
//        teamPojo2.setTeamType(TeamTypeEnum.XP);
//        teamPojo2.setUsername("hoangn2");
//        teamPojo2.setRegion(RegionEnum.NA);
//
//        TeamPojo teamPojo3 = new TeamPojo();
//        teamPojo3.setGame(GameEnum.COD_MWR);
//        teamPojo3.setPlatform(PlatformEnum.PS4);
//        teamPojo3.setTeamName("Team1");
//        teamPojo3.setTeamSize(TeamSizeEnum.SINGLES);
//        teamPojo3.setTeamType(TeamTypeEnum.XP);
//        teamPojo3.setUsername("hoangn3");
//        teamPojo3.setRegion(RegionEnum.NA);
//
//        TeamPojo teamPojo4 = new TeamPojo();
//        teamPojo4.setGame(GameEnum.COD_MWR);
//        teamPojo4.setPlatform(PlatformEnum.PS4);
//        teamPojo4.setTeamName("Team1");
//        teamPojo4.setTeamSize(TeamSizeEnum.SINGLES);
//        teamPojo4.setTeamType(TeamTypeEnum.XP);
//        teamPojo4.setUsername("hoangn4");
//        teamPojo4.setRegion(RegionEnum.NA);
//
//        TeamPojo teamPojo5 = new TeamPojo();
//        teamPojo5.setGame(GameEnum.COD_MWR);
//        teamPojo5.setPlatform(PlatformEnum.PS4);
//        teamPojo5.setTeamName("Team1");
//        teamPojo5.setTeamSize(TeamSizeEnum.DOUBLES);
//        teamPojo5.setTeamType(TeamTypeEnum.XP);
//        teamPojo5.setUsername("hoangn5");
//        teamPojo5.setRegion(RegionEnum.NA);
//
////        Team team1 = teamIWS.createTeam(teamPojo, "hoangn1");
////        MWRTeam mwrTeam1 = teamRepo.getMWRTeamByTeamPk(team1.getPk());
////        mwrTeam1.getMwrRankXP().setTotalTeamXp(0);
////        
////        Team team2 = teamIWS.createTeam(teamPojo2, "hoangn2");
////        
////        Team team3 = teamIWS.createTeam(teamPojo3, "hoangn3");
////        MWRTeam mwrTeam2 = teamRepo.getMWRTeamByTeamPk(team3.getPk());
////        mwrTeam2.getMwrRankXP().setTotalTeamXp(500);
////        
////        Team team4 = teamIWS.createTeam(teamPojo4, "hoangn4");
////        MWRTeam mwrTeam3 = teamRepo.getMWRTeamByTeamPk(team4.getPk());
////        mwrTeam3.getMwrRankXP().setTotalTeamXp(1000);
////        
////        Team team5 = teamIWS.createTeam(teamPojo5, "hoangn5");
////        
////        em.flush();
////        int count = teamRankRepo.updateMWRTeamRanks(RegionEnum.NA, PlatformEnum.PS4, TeamSizeEnum.SINGLES);
////        System.out.println("--------------------------------------------------------COUNT: " + count);
//    }
//
////    public Tournament createT(){
////        Tournament t = new Tournament();
////        t.getTournamentInfo().setTournamentStatus(TournamentStatusEnum.PENDING);
////        tournamentRepo.persistTournament(t);
////        return t;
////    }
////    @Ignore
////    @Test
////    public long testCreateChallongeTournament(){
////        Tournament t = new Tournament();
////        t.getTournamentInfo().setTournamentStatus(TournamentStatusEnum.PENDING);
////        tournamentRepo.persistTournament(t);
////        long id = challongeTournamentService.createTournament(t);
////        return id;
////    }
////    
////    public void testAddBulkParticipants(long tournamentid){
////        List<TournamentTeam> teamList = new ArrayList<>();
////        for(int i = 0;i<6;i++){
////            TournamentTeam t = new TournamentTeam();
////            t.setName("TeamName"+i);
////            teamList.add(t);
////        }
////        
////        cps.bulkAddParticipantsToTournament(tournamentid, teamList);
////    }
////   
////    public void testGetAllChallongeTournament(){
////        challongeTournamentService.getAllTournaments();
////    }
////    
////    @Test
////    @Ignore
////    public void getChallongeMatches(){
////        challongeMatchService.getAndCreateMatches(3787002);
////    }
////    
////    @Ignore
////    @Test
////    public void doAllTheThings(){
////        long tournamentId = testCreateChallongeTournament();
////        testAddBulkParticipants(tournamentId);
////    }
//    @Test
//    @Ignore
//    public void testChallTournamentSync() {
//        challongeTournamentService.syncAllChallongeTournaments();
//    }
//
//    @Test
//    @Ignore
//    public void testTeamAndMatchDissociation() {
//
//        ArrayList<RegistrationPojo> registrationPojoList = new ArrayList<>();
//        RegistrationPojo bRegistration;
//
//        for (int i = 1; i < 2; i++) {
//            bRegistration = new RegistrationPojo();
//            bRegistration.setEmail("hnbusinesscontact" + i + "@gmail.com");
//            bRegistration.setPassword("password123");
//            bRegistration.setPasswordConfirm("password123");
//            bRegistration.setTimeZoneEnumId(TimeZoneEnum.UTC_MINUS_7_LA.getTimeZonesEnumId());
//            bRegistration.setRegionId("0");
//            bRegistration.setUsername("hoangn" + i);
//            registrationPojoList.add(bRegistration);
//        }
//
//        User user0 = userIWS.registerUser(registrationPojoList.get(0));
//
//        TeamPojo teamPojo = new TeamPojo();
//        teamPojo.setGame(GameEnum.COD_MWR);
//        teamPojo.setPlatform(PlatformEnum.PS4);
//        teamPojo.setTeamName("Team1");
//        teamPojo.setTeamSize(TeamSizeEnum.SINGLES);
//        teamPojo.setTeamType(TeamTypeEnum.XP);
//        teamPojo.setUsername("hoangn1");
//        teamPojo.setRegion(RegionEnum.NA);
//
//        Team team1 = teamIWS.createTeam(teamPojo, "hoangn1");
//
//    }
//
//    @Test
//    @Ignore
//    public void testGetMatches() {
//        int size = matchRepo.findMatchesByTeamPk(2).size();
////        for (Match m : list) {
//            System.out.println("MATCH LIST SIZE: " + size);
////        }
//    }
//}
