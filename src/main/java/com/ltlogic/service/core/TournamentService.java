/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.BestOfEnum;
import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.GameModeEnum;
import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.constants.MatchSizeEnum;
import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.constants.NotificationTypeEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.constants.TournamentStatusEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Notification;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentInvite;
import com.ltlogic.db.entity.TournamentMaps;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.challonge.TournamentResponse;
import com.ltlogic.db.entity.iw.IWMatch;
import com.ltlogic.db.entity.mwr.MWRMatch;
import com.ltlogic.db.entity.ww2.WW2Match;
import com.ltlogic.db.repository.MatchRepository;
import com.ltlogic.db.repository.NotificationRepository;
import com.ltlogic.db.repository.TournamentInviteRepository;
import com.ltlogic.db.repository.TournamentRepository;
import com.ltlogic.db.repository.TournamentTeamRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.db.repository.challonge.ChallongeTournamentRepository;
import com.ltlogic.fe.models.form.TeamFormValidator;
import com.ltlogic.fe.models.form.TournamentFormValidator;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.iws.TeamInviteIWS;
import com.ltlogic.iws.TournamentServiceIWS;
import com.ltlogic.iws.TournamentTeamIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.MatchPojo;
import com.ltlogic.pojo.TeamPojo;
import com.ltlogic.pojo.TournamentPojo;
import com.ltlogic.pojo.challonge.ChallongeTournamentPojo;
import com.ltlogic.pojo.iw.IWMatchPojo;
import com.ltlogic.pojo.mwr.MWRMatchPojo;
import com.ltlogic.pojo.ww2.WW2MatchPojo;
import com.ltlogic.service.challonge.ChallongeMatchService;
import com.ltlogic.service.challonge.ChallongeParticipantService;
import com.ltlogic.service.challonge.ChallongeTournamentService;
import com.ltlogic.service.common.CommonService;
import com.ltlogic.service.springsecurity.TeamValidator;
import com.ltlogic.service.springsecurity.UserValidator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

/**
 *
 * @author Hoang
 */
@Service
@Transactional
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepo;

    @Autowired
    private MatchRepository matchRepo;

    @Autowired
    private TournamentTeamRepository tournamentTeamRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private MatchService matchService;

    @Autowired
    private TournamentInviteService tournamentInviteService;

    @Autowired
    private TournamentInviteRepository tournamentInviteRepo;

    @Autowired
    private CommonService commonService;

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private BankService bankService;

    @Autowired
    private ChallongeTournamentService challongeTournamentService;

    @Autowired
    private ChallongeParticipantService challongeParticipantService;

    @Autowired
    private ChallongeMatchService challongeMatchService;

    @Autowired
    private ChallongeTournamentRepository challongeTournamentResponseRepo;
    
    @Autowired
    private TournamentServiceIWS tournamentIWS;

    @Autowired
    private TeamIWS teamIWS;

    @Autowired
    private UserIWS userIWS;

    @Autowired
    private TeamValidator teamValidator;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private TeamFormValidator teamCreationFormValidator;

    @Autowired
    private TeamInviteIWS teamInviteIWS;

    @Autowired
    private TournamentFormValidator tournamentFormValidator;

    @Autowired
    private TournamentTeamIWS tournamentTeamIWS;

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(TournamentService.class);

    public void persistTournament(Tournament t) {
        tournamentRepo.persistTournament(t);
    }

    public Tournament findByPk(long pk) {
        return tournamentRepo.findByPk(pk);
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepo.getAllTournaments();
    }

    public List<Tournament> getTournamentsByUserPk(long userPk) {
        return tournamentRepo.getTournamentsByUserPk(userPk);
    }

    public List<Tournament> getTournamentsByUserPkForPaginate(long userPk, int pageNumber) {
        return tournamentRepo.getTournamentsByUserPkForPaginate(userPk, pageNumber);
    }

    public Tournament findTournamentByTournamentIdAndUserPk(int tournamentId, Long userPk) {
        return tournamentRepo.findTournamentByTournamentIdAndUserPk(tournamentId, userPk);
    }

    public List<Tournament> getAllTournamentsByStatus(TournamentStatusEnum tournamentStatus) {
        return tournamentRepo.getAllTournamentsByStatus(tournamentStatus);
    }

    public List<Tournament> getAllTournamentsByStatusAndPlatform(TournamentStatusEnum tournamentStatus) {
        return tournamentRepo.getAllTournamentsByStatus(tournamentStatus);
    }

    public List<Tournament> getAllTournamentsByGameAndPlatform(GameEnum gameEnum, PlatformEnum platformEnum) {
        return tournamentRepo.getAllTournamentsByGameAndPlatform(gameEnum, platformEnum);
    }

    public Tournament createTournamentForTest(TournamentPojo tournamentPojo) {
        Tournament tournament = new Tournament();
        ChallongeTournamentPojo challongeTournamentPojo = new ChallongeTournamentPojo();
        TournamentResponse tournamentResponse = new TournamentResponse();
        tournamentResponse.setTournamentPojo(challongeTournamentPojo);
        challongeTournamentResponseRepo.persistChallongeTournament(tournamentResponse);
        tournament.setTournamentInfo(tournamentPojo);
        tournament.setTournamentResponse(tournamentResponse);

        TournamentMaps tournamentMaps = new TournamentMaps();
        associateTournamentToTournamentMaps(tournament, tournamentMaps);

        if (tournament.getTournamentInfo().getGameEnum() == GameEnum.COD_MWR) {
            ArrayList<String> mwrMapsOnesAndTwos = tournamentMaps.getMwrMapNamesFor1sAnd2s();
            ArrayList<String> mwrMapsThreesAndFours = tournamentMaps.getMwrMapNamesFor3sAnd4s();
            String lastMap = "";
            if (tournament.getTournamentInfo().getTeamSizeEnum() == TeamSizeEnum.SINGLES || tournament.getTournamentInfo().getTeamSizeEnum() == TeamSizeEnum.DOUBLES) {
                for (int i = 0; i < 3; i++) {
                    Collections.shuffle(mwrMapsOnesAndTwos);
                    while (lastMap.equals(mwrMapsOnesAndTwos.get(0))) {
                        Collections.shuffle(mwrMapsOnesAndTwos);
                    }
                    for (String mapName : mwrMapsOnesAndTwos) {
                        tournamentMaps.getBestOf1MapsToPlay().add(mapName);
                        lastMap = mapName;
                    }
                }
                for (int i = 0; i < 10; i++) {
                    ArrayList<String> bestOf3MapForMatch = new ArrayList<>();
                    Collections.shuffle(mwrMapsOnesAndTwos);
                    for (String mapName : mwrMapsOnesAndTwos) {
                        bestOf3MapForMatch.add(mapName);
                    }
                    tournamentMaps.getBestOf3MapsToPlay().add(bestOf3MapForMatch);
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    Collections.shuffle(mwrMapsThreesAndFours);
                    while (lastMap.equals(mwrMapsThreesAndFours.get(0))) {
                        Collections.shuffle(mwrMapsThreesAndFours);
                    }
                    for (String mapName : mwrMapsThreesAndFours) {
                        tournamentMaps.getBestOf1MapsToPlay().add(mapName);
                        lastMap = mapName;
                    }
                }
                for (int i = 0; i < 10; i++) {
                    ArrayList<String> bestOf3MapForMatch = new ArrayList<>();
                    Collections.shuffle(mwrMapsThreesAndFours);
                    for (String mapName : mwrMapsThreesAndFours) {
                        bestOf3MapForMatch.add(mapName);
                    }
                    tournamentMaps.getBestOf3MapsToPlay().add(bestOf3MapForMatch);
                }
            }
        } else if (tournament.getTournamentInfo().getGameEnum() == GameEnum.COD_WW2) {
            ArrayList<String> ww2MapsOnesAndTwos = tournamentMaps.getWw2MapNamesFor1sAnd2s();
            ArrayList<String> ww2MapsThreesAndFours = tournamentMaps.getWw2MapNamesFor3sAnd4s();
            String lastMap = "";
            if (tournament.getTournamentInfo().getTeamSizeEnum() == TeamSizeEnum.SINGLES || tournament.getTournamentInfo().getTeamSizeEnum() == TeamSizeEnum.DOUBLES) {
                for (int i = 0; i < 3; i++) {
                    Collections.shuffle(ww2MapsOnesAndTwos);
                    while (lastMap.equals(ww2MapsOnesAndTwos.get(0))) {
                        Collections.shuffle(ww2MapsOnesAndTwos);
                    }
                    for (String mapName : ww2MapsOnesAndTwos) {
                        tournamentMaps.getBestOf1MapsToPlay().add(mapName);
                        lastMap = mapName;
//                        
                    }
                }
                for (int i = 0; i < 10; i++) {
                    ArrayList<String> bestOf3MapForMatch = new ArrayList<>();
                    Collections.shuffle(ww2MapsOnesAndTwos);
                    for (String mapName : ww2MapsOnesAndTwos) {
                        bestOf3MapForMatch.add(mapName);
                    }
                    tournamentMaps.getBestOf3MapsToPlay().add(bestOf3MapForMatch);
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    Collections.shuffle(ww2MapsThreesAndFours);
                    while (lastMap.equals(ww2MapsThreesAndFours.get(0))) {
                        Collections.shuffle(ww2MapsThreesAndFours);
                    }
                    for (String mapName : ww2MapsThreesAndFours) {
                        tournamentMaps.getBestOf1MapsToPlay().add(mapName);
                        lastMap = mapName;
                    }
                }
                for (int i = 0; i < 10; i++) {
                    ArrayList<String> bestOf3MapForMatch = new ArrayList<>();
                    Collections.shuffle(ww2MapsThreesAndFours);
                    for (String mapName : ww2MapsThreesAndFours) {
                        bestOf3MapForMatch.add(mapName);
                    }
                    tournamentMaps.getBestOf3MapsToPlay().add(bestOf3MapForMatch);
                }
            }
        }

        boolean doesIdExist = true;
        while (doesIdExist) {
            int tournamentId = commonService.generateRandomId();
            Tournament t = tournamentRepo.findTournamentByTournamentId(tournamentId);
            if (t == null) {
                tournament.setTournamentId(tournamentId);
                doesIdExist = false;
            }
        }
        MatchSizeEnum matchSize = tournamentPojo.getMatchSize();

        if (null != matchSize) {
            switch (matchSize) {
                case SINGLES:
                    tournament.getTournamentInfo().setNumOfPlayersPerTeam(1);
                    break;
                case DOUBLES:
                    tournament.getTournamentInfo().setNumOfPlayersPerTeam(2);
                    break;
                case THREES:
                    tournament.getTournamentInfo().setNumOfPlayersPerTeam(3);
                    break;
                case FOURS:
                    tournament.getTournamentInfo().setNumOfPlayersPerTeam(4);
                    break;
                default:
                    break;
            }
        }
        tournamentRepo.persistTournamentMaps(tournamentMaps);
        tournamentRepo.persistTournament(tournament);

//        if (challongeTournamentService.createChallongeTournament(tournament)) {
//            tournamentRepo.persistTournamentMaps(tournamentMaps);
//            tournamentRepo.persistTournament(tournament);
//        }
        return tournament;
    }

    public Tournament createTournament(TournamentPojo tournamentPojo) {
        Tournament tournament = new Tournament();
        tournament.setTournamentInfo(tournamentPojo);
        tournamentPojo.setTeamTypeEnum(TeamTypeEnum.CASH);

        TournamentMaps tournamentMaps = new TournamentMaps();
        associateTournamentToTournamentMaps(tournament, tournamentMaps);

        if (tournament.getTournamentInfo().getGameEnum() == GameEnum.COD_MWR) {
            ArrayList<String> mwrMapsOnesAndTwos = tournamentMaps.getMwrMapNamesFor1sAnd2s();
            ArrayList<String> mwrMapsThreesAndFours = tournamentMaps.getMwrMapNamesFor3sAnd4s();

            String lastMap = "";
            if (tournament.getTournamentInfo().getTeamSizeEnum() == TeamSizeEnum.SINGLES || tournament.getTournamentInfo().getTeamSizeEnum() == TeamSizeEnum.DOUBLES) {
                for (int i = 0; i < 3; i++) {
                    Collections.shuffle(mwrMapsOnesAndTwos);
                    while (lastMap.equals(mwrMapsOnesAndTwos.get(0))) {
                        Collections.shuffle(mwrMapsOnesAndTwos);
                    }
                    for (String mapName : mwrMapsOnesAndTwos) {
                        tournamentMaps.getBestOf1MapsToPlay().add(mapName);
                        lastMap = mapName;
                    }
                }
                for (int i = 0; i < 10; i++) {
                    ArrayList<String> bestOf3MapForMatch = new ArrayList<>();
                    Collections.shuffle(mwrMapsOnesAndTwos);
                    for (String mapName : mwrMapsOnesAndTwos) {
                        bestOf3MapForMatch.add(mapName);
                    }
                    tournamentMaps.getBestOf3MapsToPlay().add(bestOf3MapForMatch);
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    Collections.shuffle(mwrMapsThreesAndFours);
                    while (lastMap.equals(mwrMapsThreesAndFours.get(0))) {
                        Collections.shuffle(mwrMapsThreesAndFours);
                    }
                    for (String mapName : mwrMapsThreesAndFours) {
                        tournamentMaps.getBestOf1MapsToPlay().add(mapName);
                        lastMap = mapName;
                    }
                }
                for (int i = 0; i < 10; i++) {
                    ArrayList<String> bestOf3MapForMatch = new ArrayList<>();
                    Collections.shuffle(mwrMapsThreesAndFours);
                    for (String mapName : mwrMapsThreesAndFours) {
                        bestOf3MapForMatch.add(mapName);
                    }
                    tournamentMaps.getBestOf3MapsToPlay().add(bestOf3MapForMatch);
                }
            }
        } else if (tournament.getTournamentInfo().getGameEnum() == GameEnum.COD_WW2) {
            if (tournament.getTournamentInfo().getGameModeEnum() == GameModeEnum.SearchAndDestroy) {
                ArrayList<String> ww2MapsOnesAndTwos = tournamentMaps.getWw2MapNamesFor1sAnd2s();
                ArrayList<String> ww2MapsThreesAndFours = tournamentMaps.getWw2MapNamesFor3sAnd4s();
                ww2MapsOnesAndTwos.remove("Gibraltar");
                ww2MapsThreesAndFours.remove("Gibraltar");
                for (String name : ww2MapsOnesAndTwos) {
                    System.out.println("MAP NAME 1s: " + name);
                }
                for (String name : ww2MapsThreesAndFours) {
                    System.out.println("MAP NAME 3s: " + name);
                }
                String lastMap = "";
                if (tournament.getTournamentInfo().getTeamSizeEnum() == TeamSizeEnum.SINGLES || tournament.getTournamentInfo().getTeamSizeEnum() == TeamSizeEnum.DOUBLES) {
                    for (int i = 0; i < 3; i++) {
                        Collections.shuffle(ww2MapsOnesAndTwos);
                        while (lastMap.equals(ww2MapsOnesAndTwos.get(0))) {
                            Collections.shuffle(ww2MapsOnesAndTwos);
                        }
                        for (String mapName : ww2MapsOnesAndTwos) {
                            tournamentMaps.getBestOf1MapsToPlay().add(mapName);
                            lastMap = mapName;
                        }
                    }
                    for (int i = 0; i < 10; i++) {
                        ArrayList<String> bestOf3MapForMatch = new ArrayList<>();
                        Collections.shuffle(ww2MapsOnesAndTwos);
                        for (String mapName : ww2MapsOnesAndTwos) {
                            bestOf3MapForMatch.add(mapName);
                        }
                        tournamentMaps.getBestOf3MapsToPlay().add(bestOf3MapForMatch);
                    }
                } else {
                    for (int i = 0; i < 3; i++) {
                        Collections.shuffle(ww2MapsThreesAndFours);
                        while (lastMap.equals(ww2MapsThreesAndFours.get(0))) {
                            Collections.shuffle(ww2MapsThreesAndFours);
                        }
                        for (String mapName : ww2MapsThreesAndFours) {
                            tournamentMaps.getBestOf1MapsToPlay().add(mapName);
                            lastMap = mapName;
                        }
                    }
                    for (int i = 0; i < 10; i++) {
                        ArrayList<String> bestOf3MapForMatch = new ArrayList<>();
                        Collections.shuffle(ww2MapsThreesAndFours);
                        for (String mapName : ww2MapsThreesAndFours) {
                            bestOf3MapForMatch.add(mapName);
                        }
                        tournamentMaps.getBestOf3MapsToPlay().add(bestOf3MapForMatch);
                    }
                }
            } else {
                ArrayList<String> variantMaps = tournamentMaps.getVariantMaps();
                ArrayList<String> variantModes = tournamentMaps.getVariantModes();
                ArrayList<String> mapsToPlay = new ArrayList<>();
                ArrayList<String> modesToPlay = new ArrayList<>();
                String lastMap = "";
                String lastMode = "";
                for (int i = 0; i < 2; i++) {
                    Collections.shuffle(variantMaps);
                    while (lastMap.equals(variantMaps.get(0))) {
                        Collections.shuffle(variantMaps);
                    }
                    for (String map : variantMaps) {
                        mapsToPlay.add(map);
                        lastMap = map;
                    }

                }
                for (int i = 0; i < 4; i++) {
                    Collections.shuffle(variantModes);
                    while (lastMode.equals(variantModes.get(0))) {
                        Collections.shuffle(variantModes);
                    }
                    for (String mode : variantModes) {
                        modesToPlay.add(mode);
                        lastMode = mode;
                    }

                }
                for (int j = 0; j < 10; j++) {
                    String mapAndMode = mapsToPlay.get(j) + " - " + modesToPlay.get(j);
                    tournamentMaps.getBestOf1MapsToPlay().add(mapAndMode);
                }

                for (int i = 0; i < 10; i++) {
                    ArrayList<String> bestOf3MapForMatch = new ArrayList<>();
                    Collections.shuffle(variantMaps);
                    Collections.shuffle(variantModes);
                    for (int j = 0; j < 3; j++) {
                        String mapAndMode = variantMaps.get(j) + " - " + variantModes.get(j);
                        bestOf3MapForMatch.add(mapAndMode);
                    }
                    tournamentMaps.getBestOf3MapsToPlay().add(bestOf3MapForMatch);
                }
            }
        }

        boolean doesIdExist = true;
        while (doesIdExist) {
            int tournamentId = commonService.generateRandomId();
            Tournament t = tournamentRepo.findTournamentByTournamentId(tournamentId);
            if (t == null) {
                tournament.setTournamentId(tournamentId);
                doesIdExist = false;
            }
        }
        MatchSizeEnum matchSize = tournamentPojo.getMatchSize();

        if (null != matchSize) {
            switch (matchSize) {
                case SINGLES:
                    tournament.getTournamentInfo().setNumOfPlayersPerTeam(1);
                    break;
                case DOUBLES:
                    tournament.getTournamentInfo().setNumOfPlayersPerTeam(2);
                    break;
                case THREES:
                    tournament.getTournamentInfo().setNumOfPlayersPerTeam(3);
                    break;
                case FOURS:
                    tournament.getTournamentInfo().setNumOfPlayersPerTeam(4);
                    break;
                default:
                    break;
            }
        }

        if (challongeTournamentService.createChallongeTournament(tournament)) {
            tournamentRepo.persistTournamentMaps(tournamentMaps);
            tournamentRepo.persistTournament(tournament);
        }
        return tournament;
    }

    private void associateTournamentToTournamentMaps(Tournament tournament, TournamentMaps tournamentMaps) {
        tournament.setTournamentMaps(tournamentMaps);
        tournamentMaps.setTournament(tournament);
    }

    public TournamentTeam joinTournament(Team team, Tournament tournament, List<User> usersPlayingInTournament, User userJoiningTournamentForTeam) throws Exception {
        if (tournamentTeamRepo.getTournamentTeamByNameAndTournament(team.getTeamPojo().getTeamName(), tournament) != null) {
            return null;
        }
        TournamentTeam tournamentTeam = new TournamentTeam();
        int numOfPlayers = tournament.getTournamentInfo().getMatchSize().getMatchSizeEnumId() + 1;
        tournamentTeam.setNumOfPlayersNeedingToAccept(numOfPlayers);
        System.out.println("NUM OF PLAYERS: " + numOfPlayers);
        tournamentTeam.setNumOfPlayers(numOfPlayers);
        tournamentTeam.setTeam(team);
        tournamentTeamRepo.persistTournamentTeam(tournamentTeam);
        tournamentTeam.getPksOfTeamMembersPlaying().add(userJoiningTournamentForTeam.getPk());

        for (int i = 0; i < usersPlayingInTournament.size(); i++) {
            User user = usersPlayingInTournament.get(i);
//            tournamentTeam.getPksOfTeamMembersPlaying().add(user.getPk());
            TournamentInvite invite = tournamentInviteService.createNewTournamentInvite(tournament, user, tournamentTeam, userJoiningTournamentForTeam);
            associateTournamentToTournamentInvite(tournament, invite);

            System.out.println("########## Tournament Invite User: " + user.getPk() + " Tournament: " + tournament.getPk() + " Tournament Team: " + tournamentTeam.getPk());
        }
        //auto accept tournmanent invite for the guy that clicks join
        System.out.println("################ TournamentTeam Pk: " + tournamentTeam.getPk());
        System.out.println("################ User Joining Tournament Pk: " + userJoiningTournamentForTeam.getPk());
        TournamentInvite tournamentInviteForCreator = tournamentInviteService.findTournamentInvitesForUserInTournamentTeamByStatus(tournamentTeam.getPk(), userJoiningTournamentForTeam.getPk(), InvitesEnum.PENDING);
        tournamentInviteService.acceptTournamentInvite(tournamentInviteForCreator.getPk());
        //setJoinedTournamentNotification(team, userJoiningTournamentForTeam, tournament);
        associateTournamentToTournamentTeam(tournament, tournamentTeam);

        int currentTeamCount = tournament.getTournamentTeams().size();
        tournament.getTournamentInfo().setTeamCount(currentTeamCount);
        return tournamentTeam;
    }

    private void setJoinedTournamentNotification(Team team, User user, Tournament tournament) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.JOINED_TOURNAMENT);
        notification.setNotificationMessage("You have joined a tournament with team '" + team.getTeamPojo().getTeamName() + "'.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setTournament(tournament);
        tournament.getNotification().add(notification);
        notification.setTeam(team);
        team.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    public void leaveTournament(User user, TournamentTeam tournamentTeam) throws Exception {
        Team team = tournamentTeam.getTeam();
        if (team.getTeamLeaderPk() == user.getPk()) {
            TournamentInvite tournamentInvite = tournamentInviteRepo.findTournamentInviteByTournamentTeamPkAndUserPk(tournamentTeam.getPk(), user.getPk());
            tournamentInviteService.declineTournamentInvite(tournamentInvite.getPk());
        } else {
            TournamentInvite tournamentInvite2 = tournamentInviteRepo.findTournamentInviteByTournamentTeamPkAndUserPk(tournamentTeam.getPk(), user.getPk());
            tournamentInviteService.declineTournamentInvite2(tournamentInvite2.getPk());
        }
    }

    public Match createTournamentMatch(Tournament tournament, TournamentTeam tournamentTeam1, TournamentTeam tournamentTeam2, int roundNumber) {

        GameEnum gameEnum = tournament.getTournamentInfo().getGameEnum();
        Match match = createMatchFromGameType(tournament);
        matchService.persistMatch(match);
        associateTournamentToMatch(tournament, match);
        match.getMatchInfo().setMatchType(MatchTypeEnum.TOURNAMENT);
        if (tournamentTeam1 != null) {
            Team team1 = tournamentTeam1.getTeam();
            matchService.associateTeamToMatch(team1, match);
            associateTournamentToTeam(tournament, team1);
            for (long userPk : tournamentTeam1.getPksOfTeamMembersPlaying()) {
                User user = userRepo.findByPk(userPk);
                matchService.associateUserToMatch(user, match);
                match.getPksOfCreatorTeamMembersPlaying().add(userPk);
                //associateTournamentToUser(tournament, user);
            }
            match.setPkOfTeamThatCreatedMatch(tournamentTeam1.getTeam().getPk());
        }
        if (tournamentTeam2 != null) {
            Team team2 = tournamentTeam2.getTeam();
            matchService.associateTeamToMatch(team2, match);
            associateTournamentToTeam(tournament, team2);
            for (long userPk : tournamentTeam2.getPksOfTeamMembersPlaying()) {
                User user = userRepo.findByPk(userPk);
                matchService.associateUserToMatch(user, match);
                match.getPksOfAcceptorTeamMembersPlaying().add(userPk);
                //associateTournamentToUser(tournament, user);
            }
            match.setPkOfTeamThatAcceptedMatch(tournamentTeam2.getTeam().getPk());
        }
        if (tournamentTeam1 != null && tournamentTeam2 != null) {
            match.setHaveAllPlayersAccepted(true);
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.READY_TO_PLAY);
            match.getMatchInfo().setScheduledMatchTime(DateTimeUtil.getDefaultLocalDateTimeNow(), DateTimeUtil.DEFAULT_TIME_ZONE_ENUM);
            matchService.selectHostAndMapsForMatch(match, roundNumber, tournamentTeam1.getParticipantResponse().getParticipant().getSeed(), tournamentTeam2.getParticipantResponse().getParticipant().getSeed());
        } else {
            match.getMatchInfo().setMatchStatus(MatchStatusEnum.PENDING);
        }
        return match;
    }

    public Match createMatchFromGameType(Tournament tournament) {
        boolean doesIdExist = true;
        int matchId = 0;
        while (doesIdExist) {
            matchId = commonService.generateRandomId();
            Match match = matchRepo.findMatchByMatchId(matchId);
            if (match == null) {
                doesIdExist = false;
            }
        }

        GameEnum gameEnum = tournament.getTournamentInfo().getGameEnum();
        MatchSizeEnum matchSize = tournament.getTournamentInfo().getMatchSize();
        MatchTypeEnum matchType = tournament.getTournamentInfo().getMatchType();
        PlatformEnum platform = tournament.getTournamentInfo().getPlatform();
        TeamTypeEnum teamType = tournament.getTournamentInfo().getTeamTypeEnum();
        TeamSizeEnum teamSize = tournament.getTournamentInfo().getTeamSizeEnum();
        BestOfEnum bestOf = tournament.getTournamentInfo().getBestOfEnum();
        GameModeEnum gameModeEnum = tournament.getTournamentInfo().getGameModeEnum();

        //Still have to set all the tournament values in here first before returning match object
        MatchPojo matchInfo = new MatchPojo();
        matchInfo.setGameEnum(gameEnum);
        matchInfo.setMatchType(matchType);
        matchInfo.setPlatform(platform);
        matchInfo.setTeamSizeEnum(teamSize);
        matchInfo.setTeamTypeEnum(teamType);
        matchInfo.setMatchSizeEnum(matchSize);
        matchInfo.setBestOfEnum(bestOf);
        matchInfo.setGameModeEnumPojo(gameModeEnum);

        if (null != matchSize) {
            switch (matchSize) {
                case SINGLES:
                    matchInfo.setNumOfPlayers(1 * 2);
                    break;
                case DOUBLES:
                    matchInfo.setNumOfPlayers(2 * 2);
                    break;
                case THREES:
                    matchInfo.setNumOfPlayers(3 * 2);
                    break;
                case FOURS:
                    matchInfo.setNumOfPlayers(4 * 2);
                    break;
                default:
                    break;
            }
        }

        if (gameEnum == GameEnum.COD_IW) {
            IWMatchPojo iwMatchPojo = new IWMatchPojo();
            IWMatch iwMatch = new IWMatch();
            iwMatch.setMatchInfo(matchInfo);
            iwMatch.setIwMatchInfo(iwMatchPojo);
            iwMatch.setMatchId(matchId);
            return iwMatch;
        } else if (gameEnum == GameEnum.COD_MWR) {
            MWRMatchPojo mwrMatchPojo = new MWRMatchPojo();
            MWRMatch mwrMatch = new MWRMatch();
            mwrMatchPojo.setJuggernaut(true);
            mwrMatchPojo.setStoppingPower(true);
            mwrMatchPojo.setLethals(true);
            mwrMatchPojo.setTacticals(true);
            mwrMatchPojo.setGameModeEnum(GameModeEnum.SearchAndDestroy);
            mwrMatch.setMatchInfo(matchInfo);
            mwrMatch.setMwrMatchInfo(mwrMatchPojo);
            mwrMatch.setMatchId(matchId);
            return mwrMatch;
        } else if (gameEnum == GameEnum.COD_WW2) {
            WW2MatchPojo ww2MatchPojo = new WW2MatchPojo();
            WW2Match ww2Match = new WW2Match();
            ww2MatchPojo.setScoreStreaks(true);
            ww2MatchPojo.setOverkill(true);
            ww2MatchPojo.setGameModeEnum(gameModeEnum);
            ww2Match.setMatchInfo(matchInfo);
            ww2Match.setWw2MatchInfo(ww2MatchPojo);
            ww2Match.setMatchId(matchId);
            return ww2Match;
        } else {
            Match match = new Match();
            match.setMatchInfo(matchInfo);
            match.setMatchId(matchId);
            return match;
        }
    }

    public void associateTournamentToTournamentInvite(Tournament tournament, TournamentInvite tournamentInvite) {
        tournament.getTournamentInvites().add(tournamentInvite);
        tournamentInvite.setTournament(tournament);
    }

    public void associateTournamentToTournamentTeam(Tournament tournament, TournamentTeam tournamentTeam) {
        tournament.getTournamentTeams().add(tournamentTeam);
        tournamentTeam.setTournament(tournament);
    }

    //  ### Do this only when tournament is starting ###  
    public void associateTournamentToUser(Tournament tournament, User user) {
        tournament.getUsersInTournament().add(user);
        user.getTournaments().add(tournament);
    }

    public void associateTournamentToTeam(Tournament tournament, Team team) {
        tournament.getTeamsInTournament().add(team);
        team.getTournaments().add(tournament);
    }

    public void associateTournamentToMatch(Tournament tournament, Match match) {
        tournament.getMatches().add(match);
        match.setTournament(tournament);
    }

    public void dissociateUserFromTournament(Tournament tournament, User user) {
        tournament.getUsersInTournament().remove(user);
        user.getTournaments().remove(tournament);
    }
    //  ### Do this only when tournament is starting ###  

    /**
     * @TODO need to create some kind of notification for the users in both
     * team, or at least to the team leader.
     */
    public void startTournaments() throws Exception {
        List<Tournament> tournamentsToStart = tournamentRepo.findAllTournamentsToStart();
//        log.info("No. of Tournaments to start: " + tournamentsToStart.size());
        if (!tournamentsToStart.isEmpty()) {
            for (Tournament t : tournamentsToStart) {
                List<TournamentTeam> touranmentTeamsInTournament = tournamentTeamRepo.getAllTournamentTeamsForTournament(t.getPk());
                for (TournamentTeam tournamentTeam : touranmentTeamsInTournament) {
                    if (!tournamentTeam.isAllPlayersAccepted() && !tournamentTeam.isTeamCancelled()) {
                        long userPk = tournamentTeam.getTeam().getTeamLeaderPk();
                        User user = userService.findByPk(userPk);
                        leaveTournament(user, tournamentTeam);
                        tournamentTeam.setTeamCancelled(true);
                    }
                }

                List<TournamentTeam> allReadyTournamentTeams = tournamentTeamRepo.getAllEligibleTournamentTeamsForTournament(t.getPk());
                int numberOfTeamsInTournament = allReadyTournamentTeams.size();
                t.getTournamentInfo().setNumOfTeamsInTournament(numberOfTeamsInTournament);
                int numberOfPlayersInTournament = numberOfTeamsInTournament * t.getTournamentInfo().getNumOfPlayersPerTeam();
                BigDecimal totalPotAmount = t.getTournamentInfo().getWagerAmountPerMember().multiply(new BigDecimal(numberOfPlayersInTournament)).setScale(2, RoundingMode.HALF_UP);
                t.getTournamentInfo().setPotAmount(totalPotAmount);
                for (TournamentTeam tournamentTeam : allReadyTournamentTeams) {
                    t.getListOfTeamsRemainingInTournament().add(tournamentTeam.getTeam().getPk());
                }
                if (allReadyTournamentTeams.size() > 1) {
                    //tournamentInviteService.setTournamentStatusToActive(t);
                    t.getTournamentInfo().setTournamentStatus(TournamentStatusEnum.ACTIVE);
                    t.getTournamentInfo().setTournamentStartTime(DateTimeUtil.getDefaultLocalDateTimeNow());
                    log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX ADDING PARTICIPANTS TO TOURNAMENT XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                    challongeParticipantService.bulkAddParticipantsToTournament(t, allReadyTournamentTeams);
                    log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX RANDOMIZE PARTICIPANTS SEED IN TOURNAMENT XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                    challongeParticipantService.randomizeParticipantSeeding(t);
                    log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX STARTING TORUNAMENT XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                    challongeTournamentService.startChallongeTournament(t);
                    log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX SYNCING TORUNAMENT MATCHESXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                    challongeMatchService.createOrSyncAllActiveMatches(t);
                    log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX NOTIFYING USERS ABOUT TOURNAMENT XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                    for (User user : t.getUsersInTournament()) {
                        setTournamentStartedNotification(t, user);
                    }
                } else {
                    log.info("Cancel Tournament because not enough teams");
                    //cancel the tournament because not enough users
                    //return all the money to everyone who paid

                    for (TournamentTeam tournamentTeam : touranmentTeamsInTournament) {
                        if (!tournamentTeam.isTeamCancelled()) {
                            //get any player on a tournament team and call leave tournament, match invite, wh
                            long userPk = tournamentTeam.getTeam().getTeamLeaderPk();
                            User user = userService.findByPk(userPk);
                            leaveTournament(user, tournamentTeam);
                            tournamentTeam.setTeamCancelled(true);
                        }
                    }
                    t.getTournamentInfo().setTournamentStatus(TournamentStatusEnum.CANCELLED);
                }
            }
        }
    }

    public void startTournamentsForTest() throws Exception {
        List<Tournament> tournamentsToStart = tournamentRepo.findAllTournamentsToStart();
//        log.info("No. of Tournaments to start: " + tournamentsToStart.size());
        if (!tournamentsToStart.isEmpty()) {
            for (Tournament t : tournamentsToStart) {
                List<TournamentTeam> touranmentTeamsInTournament = tournamentTeamRepo.getAllTournamentTeamsForTournament(t.getPk());
                for (TournamentTeam tournamentTeam : touranmentTeamsInTournament) {
                    if (!tournamentTeam.isAllPlayersAccepted() && !tournamentTeam.isTeamCancelled()) {
                        long userPk = tournamentTeam.getPksOfPlayersWhoHaveAcceptedTournamentInviteOnTournamenTeam().get(0);
                        User user = userService.findByPk(userPk);
                        leaveTournament(user, tournamentTeam);
                        tournamentTeam.setTeamCancelled(true);
                    }
                }
                List<TournamentTeam> allReadyTournamentTeams = tournamentTeamRepo.getAllEligibleTournamentTeamsForTournament(t.getPk());
                int numberOfTeamsInTournament = allReadyTournamentTeams.size();
                t.getTournamentInfo().setNumOfTeamsInTournament(numberOfTeamsInTournament);
                int numberOfPlayersInTournament = numberOfTeamsInTournament * t.getTournamentInfo().getNumOfPlayersPerTeam();
                BigDecimal totalPotAmount = t.getTournamentInfo().getWagerAmountPerMember().multiply(new BigDecimal(numberOfPlayersInTournament)).setScale(2, RoundingMode.HALF_UP);
                t.getTournamentInfo().setPotAmount(totalPotAmount);
                for (TournamentTeam tournamentTeam : allReadyTournamentTeams) {
                    t.getListOfTeamsRemainingInTournament().add(tournamentTeam.getTeam().getPk());
                }
                if (allReadyTournamentTeams.size() > 1) {
                    //tournamentInviteService.setTournamentStatusToActive(t);
                    t.getTournamentInfo().setTournamentStatus(TournamentStatusEnum.ACTIVE);
                    t.getTournamentInfo().setTournamentStartTime(DateTimeUtil.getDefaultLocalDateTimeNow());
//                    log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX ADDING PARTICIPANTS TO TOURNAMENT XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//                    challongeParticipantService.bulkAddParticipantsToTournament(t, allReadyTournamentTeams);
//                    log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX RANDOMIZE PARTICIPANTS SEED IN TOURNAMENT XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//                    challongeParticipantService.randomizeParticipantSeeding(t);
//                    log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX STARTING TORUNAMENT XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//                    challongeTournamentService.startChallongeTournament(t);
//                    log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX SYNCING TORUNAMENT MATCHESXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//                    challongeMatchService.createOrSyncAllActiveMatches(t);
//                    log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX NOTIFYING USERS ABOUT TOURNAMENT XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                    for (User user : t.getUsersInTournament()) {
                        setTournamentStartedNotification(t, user);
                    }
                } else {
                    log.info("Cancel Tournament because not enough teams");
                    //cancel the tournament because not enough users
                    //return all the money to everyone who paid

                    for (TournamentTeam tournamentTeam : touranmentTeamsInTournament) {
                        if (!tournamentTeam.isTeamCancelled()) {
                            //get any player on a tournament team and call leave tournament, match invite, wh
                            long userPk = tournamentTeam.getTeam().getTeamLeaderPk();
                            User user = userService.findByPk(userPk);
                            leaveTournament(user, tournamentTeam);
                            tournamentTeam.setTeamCancelled(true);

                        }
                    }
                    t.getTournamentInfo().setTournamentStatus(TournamentStatusEnum.CANCELLED);
                }
            }
        }
    }

    private void setTournamentStartedNotification(Tournament t, User user) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.TOURNAMENT_STARTED);
        notification.setNotificationMessage("A " + t.getTournamentInfo().getGameEnum().getGameEnumDesc() + " tournament you are in has started.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setTournament(t);
        t.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    /**
     * @TODO need to create some kind of notification for the users in both
     * team, or at least to the team leader.
     */
    public void notifyUpcomingTournaments() {
        List<Tournament> upcomingTournaments = tournamentRepo.findAllUpcomingTournamentsToNotify();
        if (!upcomingTournaments.isEmpty()) {
            for (Tournament t : upcomingTournaments) {
                for (User user : t.getUsersInTournament()) {
                    Notification n = notificationRepo.getNotificationByUserPkAndTourmamentPkAndType(user.getPk(), t.getPk(), NotificationTypeEnum.UPCOMING_TOURNAMENT);
                    LocalDateTime dateTime = convertStringToLocalDateTime(t);
                    if (n == null && DateTimeUtil.getDefaultLocalDateTimeNow().plusMinutes(2).isBefore(dateTime)) {
                        setUpcomingTournamentNotification(t, user);
                    }
                }
            }
        }
    }

    private LocalDateTime convertStringToLocalDateTime(Tournament t) {
        String str = t.getTournamentInfo().getScheduledTournamentTimeForFE(null);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-d-yyyy h:mm a");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        return dateTime;
    }

    private void setUpcomingTournamentNotification(Tournament t, User user) {
        Notification notification = new Notification();
        notification.setNotificationType(NotificationTypeEnum.UPCOMING_TOURNAMENT);
        notification.setNotificationMessage("You have a " + t.getTournamentInfo().getGameEnum().getGameEnumDesc() + " tournament starting in less than 10 minutes.");
        notification.setUser(user);
        user.getNotification().add(notification);
        notification.setTournament(t);
        t.getNotification().add(notification);
        notificationRepo.persistNotification(notification);
    }

    public List<Tournament> getAllPendingAndActiveTournamentsForUser(long userPk) {
        return tournamentRepo.getAllPendingAndActiveTournamentsForUser(userPk);
    }

    //@Deprecated
//    public void startTournament(Tournament tournament) {
//        List<TournamentTeam> tournamentTeams = new ArrayList<>();
//        tournamentTeams.addAll(tournament.getTournamentTeams());
//        //create initial bracket
//        Collections.shuffle(tournamentTeams);
//        for (int i = 1; i <= tournamentTeams.size(); i++) {
//            tournamentTeams.get(i).setTournamentSeed(i);
//        }
//        
//        processNextRoundInBracket(tournamentTeams);
//    }
//
//    public void processNextRoundInBracket(List<TournamentTeam> teamsInCurrentRound) {
//
//        int numOfTeams = teamsInCurrentRound.size();
//        List<TournamentTeam> teamsToGetByes = new ArrayList<>();
//        List<TournamentTeam> teamsPlayingFirstRound = new ArrayList<>();
//
//        if (numOfTeams > 64 && numOfTeams < 128) {
//            //create 128 bracket
//            int numOfTeamsToGetByes = 128 - numOfTeams;
//            int numOfTeamsPlayingFirstRound128 = numOfTeams - numOfTeamsToGetByes;
//            teamsToGetByes = teamsInCurrentRound.subList(0, numOfTeamsToGetByes);
//            teamsPlayingFirstRound = teamsInCurrentRound.subList(numOfTeamsToGetByes, teamsInCurrentRound.size());
//        } else if (numOfTeams > 32 && numOfTeams < 64) {
//            //create 64 bracket
//            int numOfTeamsToGetByes = 64 - numOfTeams;
//            int numOfTeamsPlayingFirstRound64 = numOfTeams - numOfTeamsToGetByes;
//            teamsToGetByes = teamsInCurrentRound.subList(0, numOfTeamsToGetByes);
//            teamsPlayingFirstRound = teamsInCurrentRound.subList(numOfTeamsToGetByes, teamsInCurrentRound.size());
//        } else if (numOfTeams > 16 && numOfTeams < 32) {
//            //create 32 bracket
//            int numOfTeamsToGetByes = 32 - numOfTeams;
//            int numOfTeamsPlayingFirstRound32 = numOfTeams - numOfTeamsToGetByes;
//            teamsToGetByes = teamsInCurrentRound.subList(0, numOfTeamsToGetByes);
//            teamsPlayingFirstRound = teamsInCurrentRound.subList(numOfTeamsToGetByes, teamsInCurrentRound.size());
//        } else if (numOfTeams > 8 && numOfTeams < 16) {
//            //create 16 bracket
//            int numOfTeamsToGetByes = 16 - numOfTeams;
//            int numOfTeamsPlayingFirstRound16 = numOfTeams - numOfTeamsToGetByes;
//            teamsToGetByes = teamsInCurrentRound.subList(0, numOfTeamsToGetByes);
//            teamsPlayingFirstRound = teamsInCurrentRound.subList(numOfTeamsToGetByes, teamsInCurrentRound.size());
//        } else if (numOfTeams > 4 && numOfTeams < 8) {
//            //create 8 bracket
//            int numOfTeamsToGetByes = 8 - numOfTeams;
//            int numOfTeamsPlayingFirstRound8 = numOfTeams - numOfTeamsToGetByes;
//            teamsToGetByes = teamsInCurrentRound.subList(0, numOfTeamsToGetByes);
//            teamsPlayingFirstRound = teamsInCurrentRound.subList(numOfTeamsToGetByes, teamsInCurrentRound.size());
//        } else if (numOfTeams > 2 && numOfTeams < 4) {
//            //create 4 bracket
//            int numOfTeamsToGetByes = 4 - numOfTeams;
//            int numOfTeamsPlayingFirstRound4 = numOfTeams - numOfTeamsToGetByes;
//            teamsToGetByes = teamsInCurrentRound.subList(0, numOfTeamsToGetByes);
//            teamsPlayingFirstRound = teamsInCurrentRound.subList(numOfTeamsToGetByes, teamsInCurrentRound.size());
//        } else if (numOfTeams == 2) {
//            //base case
//        } else {
//            //pairs up normally in the case of 2^n
//        }
//        
//        pairUpTournamentTeams(teamsPlayingFirstRound);
//        
//    }
//
//    public void pairUpTournamentTeams(List<TournamentTeam> tournamentTeams) {
//        
//    }
//
//    public List<Long> validateAndStartTournament(long tournamentPk) {
//        boolean isValid = true;
//        List<Long> pksOfTournamentTeamsNotReady = new ArrayList<>();
//        Tournament tournament = tournamentRepo.findByPk(tournamentPk);
//        Set<TournamentTeam> tournamentTeams = tournament.getTournamentTeams();
//        if (tournamentTeams.size() >= tournament.getTournamentInfo().getNumOfTeamsNeededToStart()) {
//            for (TournamentTeam t : tournamentTeams) {
//                if (t.isTeamCancelled() == true || t.isAllPlayersAccepted() == false) {
//                    isValid = false;
//                    pksOfTournamentTeamsNotReady.add(t.getPk());
//                }
//            }
//        }
//
//        if (isValid) {
//            startTournament(tournament);
//        } else {
//            StringBuilder teamsNotReady = new StringBuilder();
//            teamsNotReady.append("Insufficient number of teams ready.  Pk(s) of team(s) not ready: ");
//            for (Long l : pksOfTournamentTeamsNotReady) {
//                teamsNotReady.append(l.longValue() + ",");
//            }
//            teamsNotReady.append("  Cannot start tournament.");
//            LOG.debug(teamsNotReady.toString());
//        }
//        return pksOfTournamentTeamsNotReady;
//    }
    public void test(TeamPojo teamPojo, String username, Tournament tournament, User user, BindingResult bindingResult) throws RuntimeException, Exception {
        Team newTeam = teamIWS.createTeam(teamPojo, username);

        tournamentFormValidator.validateJoinTournamentException(tournament, null, newTeam, user, bindingResult);

//            if (bindingResult.hasErrors()) {
//                model.addAttribute("teams", teams);
//                model.addAttribute("tournament", tournament);
//                return "tournaments/team-create";
//            }
        List<User> usersPlayingInTournament = new ArrayList<>();
        usersPlayingInTournament.add(user);
        tournamentIWS.joinTournament(newTeam, tournament, usersPlayingInTournament, user);
    }
}
