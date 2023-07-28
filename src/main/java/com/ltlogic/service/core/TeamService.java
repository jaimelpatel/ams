/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.core;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.NotificationTypeEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.TeamPermissionsEnum;
import com.ltlogic.constants.TeamSizeEnum;
import com.ltlogic.constants.TeamStatusEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Notification;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.TeamPermissions;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.iw.IWTeam;
import com.ltlogic.db.entity.mwr.MWRTeam;
import com.ltlogic.db.entity.rank.team.IWTeamRankXP;
import com.ltlogic.db.entity.rank.team.MWRTeamRankXP;
import com.ltlogic.db.entity.rank.team.WW2TeamRankXP;
import com.ltlogic.db.entity.rank.user.UserRankXP;
import com.ltlogic.db.entity.ww2.WW2Team;
import com.ltlogic.db.repository.MatchRepository;
import com.ltlogic.db.repository.NotificationRepository;
import com.ltlogic.db.repository.TeamRankRepository;
import com.ltlogic.db.repository.TeamRepository;
import com.ltlogic.db.repository.TournamentTeamRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.pojo.TeamPojo;
import com.ltlogic.service.common.CommonService;
import com.ltlogic.service.springsecurity.UserValidator;
import static java.lang.Math.toIntExact;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import javax.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

/**
 *
 * @author Jaimel
 */
@Service
@Transactional
public class TeamService {

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private MatchRepository matchRepo;

    @Autowired
    private TournamentTeamRepository tournamentTeamRepo;

    @Autowired
    private TeamRankRepository teamRankRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonService commonService;

    public void persistTeam(Team team) {
        teamRepo.persistTeam(team);
    }

    public void persistTeamPermissions(TeamPermissions tp) {
        teamRepo.persistTeamPermissions(tp);
    }

    public Team createTeam(TeamPojo teamPojo, String username) {
//        String lowerCaseTeamName = teamPojo.getTeamName().toLowerCase();
//        teamPojo.setTeamName(lowerCaseTeamName);
        User user = userService.findByUsername(username);
        if (teamPojo.getGame() == GameEnum.COD_MWR) {
            MWRTeam mwrTeam = new MWRTeam();
            boolean doesIdExist = true;
            while (doesIdExist) {
                int teamId = commonService.generateRandomId();
                Team team = teamRepo.findTeamByTeamId(teamId);
                if (team == null) {
                    mwrTeam.setTeamId(teamId);
                    doesIdExist = false;
                }
            }
            mwrTeam.setTeamPojo(teamPojo);
            if (user != null) {
                mwrTeam.setTeamLeaderPk(user.getPk());
                TeamPermissions tp = new TeamPermissions();
                tp.setTeam(mwrTeam);
                mwrTeam.getTeamPermissions().add(tp);
                tp.setUser(user);
                user.getTeamPermissions().add(tp);
                tp.setHasLeaderPermissions(true);
                persistTeamPermissions(tp);
            }

            if (teamPojo.getTeamSize() == TeamSizeEnum.SINGLES) {
                mwrTeam.setMinimumPlayers(1);
                mwrTeam.setMaximumPlayers(1);
            }

            if (teamPojo.getTeamSize() == TeamSizeEnum.DOUBLES) {
                mwrTeam.setMinimumPlayers(2);
                mwrTeam.setMaximumPlayers(4);
            }

            if (teamPojo.getTeamSize() == TeamSizeEnum.TEAM) {
                mwrTeam.setMinimumPlayers(3);
                mwrTeam.setMaximumPlayers(8);
            }

            persistTeam(mwrTeam);
            associateUserToTeam(mwrTeam, user);

            Long countOfTeamsInLadder = teamRepo.findCountOfTeamsByRegionAndGameAndPlatformAndTypeAndSize(user.getUserInfo().getRegion(), teamPojo.getGame(), teamPojo.getPlatform(), teamPojo.getTeamSize(), teamPojo.getTeamType());
            int count = toIntExact(countOfTeamsInLadder);
            MWRTeamRankXP mwrTeamRankXP = new MWRTeamRankXP();
            mwrTeamRankXP.setRank(count);
            mwrTeamRankXP.setMwrTeam(mwrTeam);
            mwrTeam.setRankXP(mwrTeamRankXP);
            teamRankRepo.persistMWRTeamRankXP(mwrTeamRankXP);

            Notification notification = new Notification();
            notification.setNotificationType(NotificationTypeEnum.CREATE_TEAM);
            notification.setNotificationMessage("You have created a new " + mwrTeam.getTeamPojo().getGame().getGameEnumDesc() + " " + mwrTeam.getTeamPojo().getTeamType().getTeamTypeEnumDesc() + " named '" + mwrTeam.getTeamPojo().getTeamName() + "'.");
            notification.setUser(user);
            user.getNotification().add(notification);
            notification.setTeam(mwrTeam);
            mwrTeam.getNotification().add(notification);
            notificationRepo.persistNotification(notification);

            return mwrTeam;
        }

        if (teamPojo.getGame() == GameEnum.COD_IW) {
            IWTeam iwTeam = new IWTeam();
            boolean doesIdExist = true;
            while (doesIdExist) {
                int teamId = commonService.generateRandomId();
                Team team = teamRepo.findTeamByTeamId(teamId);
                if (team == null) {
                    iwTeam.setTeamId(teamId);
                    doesIdExist = false;
                }
            }
            iwTeam.setTeamPojo(teamPojo);

            if (user != null) {
                iwTeam.setTeamLeaderPk(user.getPk());
                TeamPermissions tp = new TeamPermissions();
                tp.setTeam(iwTeam);
                iwTeam.getTeamPermissions().add(tp);
                tp.setUser(user);
                user.getTeamPermissions().add(tp);
                tp.setHasLeaderPermissions(true);
                persistTeamPermissions(tp);
            }

            if (teamPojo.getTeamSize() == TeamSizeEnum.SINGLES) {
                iwTeam.setMinimumPlayers(1);
                iwTeam.setMaximumPlayers(1);
            }

            if (teamPojo.getTeamSize() == TeamSizeEnum.DOUBLES) {
                iwTeam.setMinimumPlayers(2);
                iwTeam.setMaximumPlayers(4);
            }

            if (teamPojo.getTeamSize() == TeamSizeEnum.TEAM) {
                iwTeam.setMinimumPlayers(3);
                iwTeam.setMaximumPlayers(8);
            }

            persistTeam(iwTeam);
            associateUserToTeam(iwTeam, user);

            Long countOfTeamsInLadder = teamRepo.findCountOfTeamsByRegionAndGameAndPlatformAndTypeAndSize(user.getUserInfo().getRegion(), teamPojo.getGame(), teamPojo.getPlatform(), teamPojo.getTeamSize(), teamPojo.getTeamType());
            int count = toIntExact(countOfTeamsInLadder);
            IWTeamRankXP iwTeamRankXP = new IWTeamRankXP();
            iwTeamRankXP.setRank(count);
            iwTeamRankXP.setIwTeam(iwTeam);
            iwTeam.setRankXP(iwTeamRankXP);
            teamRankRepo.persistIWTeamRankXP(iwTeamRankXP);

            Notification notification = new Notification();
            notification.setNotificationType(NotificationTypeEnum.CREATE_TEAM);
            notification.setNotificationMessage("You have created a new " + iwTeam.getTeamPojo().getGame().getGameEnumDesc() + " " + iwTeam.getTeamPojo().getTeamType().getTeamTypeEnumDesc() + " named '" + iwTeam.getTeamPojo().getTeamName() + "'.");
            notification.setUser(user);
            user.getNotification().add(notification);
            notification.setTeam(iwTeam);
            iwTeam.getNotification().add(notification);
            notificationRepo.persistNotification(notification);

            return iwTeam;
        }

        if (teamPojo.getGame() == GameEnum.COD_WW2) {
            WW2Team ww2Team = new WW2Team();
            boolean doesIdExist = true;
            while (doesIdExist) {
                int teamId = commonService.generateRandomId();
                Team team = teamRepo.findTeamByTeamId(teamId);
                if (team == null) {
                    ww2Team.setTeamId(teamId);
                    doesIdExist = false;
                }
            }
            ww2Team.setTeamPojo(teamPojo);

            if (user != null) {
                ww2Team.setTeamLeaderPk(user.getPk());
                TeamPermissions tp = new TeamPermissions();
                tp.setTeam(ww2Team);
                ww2Team.getTeamPermissions().add(tp);
                tp.setUser(user);
                user.getTeamPermissions().add(tp);
                tp.setHasLeaderPermissions(true);
                persistTeamPermissions(tp);
            }

            if (teamPojo.getTeamSize() == TeamSizeEnum.SINGLES) {
                ww2Team.setMinimumPlayers(1);
                ww2Team.setMaximumPlayers(1);
            }

            if (teamPojo.getTeamSize() == TeamSizeEnum.DOUBLES) {
                ww2Team.setMinimumPlayers(2);
                ww2Team.setMaximumPlayers(4);
            }

            if (teamPojo.getTeamSize() == TeamSizeEnum.TEAM) {
                ww2Team.setMinimumPlayers(3);
                ww2Team.setMaximumPlayers(8);
            }

            persistTeam(ww2Team);
            associateUserToTeam(ww2Team, user);

            Long countOfTeamsInLadder = teamRepo.findCountOfTeamsByRegionAndGameAndPlatformAndTypeAndSize(user.getUserInfo().getRegion(), teamPojo.getGame(), teamPojo.getPlatform(), teamPojo.getTeamSize(), teamPojo.getTeamType());
            int count = toIntExact(countOfTeamsInLadder);
            WW2TeamRankXP ww2TeamRankXP = new WW2TeamRankXP();
            ww2TeamRankXP.setRank(count);
            ww2TeamRankXP.setWw2Team(ww2Team);
            ww2Team.setRankXP(ww2TeamRankXP);
            teamRankRepo.persistWW2TeamRankXP(ww2TeamRankXP);

            Notification notification = new Notification();
            notification.setNotificationType(NotificationTypeEnum.CREATE_TEAM);
            notification.setNotificationMessage("You have created a new " + ww2Team.getTeamPojo().getGame().getGameEnumDesc() + " " + ww2Team.getTeamPojo().getTeamType().getTeamTypeEnumDesc() + " named '" + ww2Team.getTeamPojo().getTeamName() + "'.");
            notification.setUser(user);
            user.getNotification().add(notification);
            notification.setTeam(ww2Team);
            ww2Team.getNotification().add(notification);
            notificationRepo.persistNotification(notification);

            return ww2Team;
        }

        return null;

    }

    public void associateUserToTeam(Team t, User user) {
        user.getTeams().add(t);
        t.getMembers().add(user);

    }

    public void dissociateUserFromTeam(Team t, User user) {
        t.getMembers().remove(user);
        user.getTeams().remove(t);
    }

    public Team getTeamByPk(long pk) {
        Team team = teamRepo.findTeamByPk(pk);
        return team;
    }

    public void promoteTeammateToLeader(long userPk, long teamPk) {
        Team team = teamRepo.findTeamByPk(teamPk);
        boolean isUserOnTeam = false;
        for (User user : team.getMembers()) {
            if (user.getPk() == userPk) {
                isUserOnTeam = true;
            }
        }
        if (isUserOnTeam) {
            team.setTeamLeaderPk(userPk);
        }
    }

    //need to make validation for this, make sure they cant remove leader
    public void removeUserFromTeam(long userPk, long teamPk) {
        Team team = teamRepo.findTeamByPk(teamPk);
        User u = userRepo.findByPk(userPk);
        boolean isUserOnTeam = false;
        for (User user : team.getMembers()) {
            if (user.getPk() == userPk) {
                isUserOnTeam = true;
            }
        }
        if (isUserOnTeam) {
            dissociateUserFromTeam(team, u);
        }
    }

    public boolean isTeamEligibleForPlay(long teamPk) {
        Team team = teamRepo.findTeamByPk(teamPk);
        boolean isTeamEligibleForPlay = true;
        for (User user : team.getMembers()) {
            if (team.getTeamPojo().getPlatform() == PlatformEnum.PS4) {
                if (user.getUserInfo().getPlayStation4Name().trim().isEmpty() || user.getUserInfo().getPlayStation4Name() == null) {
                    isTeamEligibleForPlay = false;
                }
            }
            if (team.getTeamPojo().getPlatform() == PlatformEnum.XBOXONE) {
                if (user.getUserInfo().getXboxOneGamerTag().trim().isEmpty() || user.getUserInfo().getXboxOneGamerTag() == null) {
                    isTeamEligibleForPlay = false;
                }
            }

        }
        return isTeamEligibleForPlay;
    }

    public MWRTeam getMWRTeamByTeamPk(long teamPk) {
        return teamRepo.getMWRTeamByTeamPk(teamPk);
    }

    public void giveUserLeaderPermissions(long userPk, long teamPk) {
        Team team = teamRepo.findTeamByPk(teamPk);
        User user = userRepo.findByPk(userPk);

        TeamPermissions tp = teamRepo.findTeamPermissionsByUserPkAndTeamPk(userPk, teamPk);
        if (tp == null) {
            tp.setUser(user);
            user.getTeamPermissions().add(tp);
            tp.setTeam(team);
            team.getTeamPermissions().add(tp);
            tp.setHasLeaderPermissions(true);
            teamRepo.persistTeamPermissions(tp);
        } else {
            tp.setHasLeaderPermissions(true);
        }
    }

    //only the teams leader can give and take leader permissions
    public void removeLeaderPermissionsfromUser(long userPk, long teamPk) {
        Team team = teamRepo.findTeamByPk(teamPk);
        User user = userRepo.findByPk(userPk);

        TeamPermissions tp = teamRepo.findTeamPermissionsByUserPkAndTeamPk(userPk, teamPk);
        if (tp == null) {
            tp.setUser(user);
            user.getTeamPermissions().add(tp);
            tp.setTeam(team);
            team.getTeamPermissions().add(tp);
            tp.setHasLeaderPermissions(false);
            teamRepo.persistTeamPermissions(tp);
        } else {
            tp.setHasLeaderPermissions(false);
        }
    }

    public void disbandTeam(long teamPk, long userPk) throws Exception {
        Team team = teamRepo.findTeamByPk(teamPk);
        if (userPk == team.getTeamLeaderPk()) {
            team.getTeamPojo().setTeamStatus(TeamStatusEnum.DISBANDED);
            List<User> usersInTeam = userRepo.getAllUsersOnTeam(teamPk);
            for (User user : usersInTeam) {
                dissociateUserFromTeam(team, user);
            }
        } else {
            throw new Exception("You cannot disband the team because you are not the leader.");
        }
    }

    public void turnXpGainOn(long teamPk) {
        Team team = teamRepo.findTeamByPk(teamPk);
        team.setXpGainOn(true);
    }
    
    public void turnXpGainOff(long teamPk){
        Team team = teamRepo.findTeamByPk(teamPk);
        team.setXpGainOn(false);
    }

    public List<Team> getAllTeamsOfUser(String username) {
        return teamRepo.findTeamsByUsername(username);
    }

    public List<Team> getAllTeamsOfUserByTeamType(String username, TeamTypeEnum tte) {
        return teamRepo.findTeamsByUsernameAndTeamType(username, tte);
    }

    public Team findTeamByNameAndGameAndPlatformAndTypeAndSize(String teamName, GameEnum gameEnum, PlatformEnum platformEnum, TeamSizeEnum teamSizeEnum, TeamTypeEnum teamTypeEnum, RegionEnum regionEnum, TeamStatusEnum teamStatusEnum) {
        return teamRepo.findTeamByNameAndGameAndPlatformAndTypeAndSizeAndStatus(teamName, gameEnum, platformEnum, teamSizeEnum, teamTypeEnum, regionEnum, teamStatusEnum);
    }

    public List<Team> findTeamByUsernameAndGameAndPlatformAndTypeAndSize(String username, GameEnum gameEnum, PlatformEnum platformEnum, TeamSizeEnum teamSizeEnum, TeamTypeEnum teamTypeEnum, RegionEnum regionEnum) {
        return teamRepo.findTeamByUsernameAndGameAndPlatformAndTypeAndSize(username, gameEnum, platformEnum, teamSizeEnum, teamTypeEnum, regionEnum);
    }

    public List<Team> findTeamsByMatchPk(long matchPk) {
        return teamRepo.findTeamsByMatchPk(matchPk);
    }

    public boolean isTeamInMatchOrTournament(Team team) {
        TournamentTeam tournamentTeamPending = tournamentTeamRepo.getAllTournamentTeamsForTeamByTournamentPendingStatus(team.getPk());
        if (tournamentTeamPending != null) {
            return true;
        }

        TournamentTeam tournamentTeamActive = tournamentTeamRepo.getAllTournamentTeamsForTeamByTournamentActiveStatus(team.getPk());
        if (tournamentTeamActive != null) {
            List<Long> listOfTeamPksRemainingInTournament = tournamentTeamActive.getTournament().getListOfTeamsRemainingInTournament();
            boolean isTeamStillInTournament = false;
            for (Long pk : listOfTeamPksRemainingInTournament) {
                if (team.getPk() == pk) {
                    isTeamStillInTournament = true;
                    break;
                }
            }
            if (isTeamStillInTournament) {
                return true;
            }
        }

        List<Match> liveMatches = matchRepo.getMatchesByTeamPkAndMatchNotKilledStatus(team.getPk());
        if (!liveMatches.isEmpty()) {
            for (Match m : liveMatches) {
                if (m.getPkOfTeamThatCreatedMatch() == team.getPk()) {
                    if (m.getReportedScoreOfTeamCreated() == null) {
                        return true;
                    }
                } else {
                    if (m.getReportedScoreOfTeamAccepted() == null) {
                        return true;
                    }
                }

            }
        }

        return false;
    }

    public boolean isUserOnTeamInMatchOrTournament(Team team, User user) {
        TournamentTeam tournamentTeamPending = tournamentTeamRepo.getAllTournamentTeamsForTeamByTournamentPendingStatus(team.getPk());
        if (tournamentTeamPending != null) {
            for (long pk : tournamentTeamPending.getPksOfTeamMembersPlaying()) {
                if (pk == user.getPk()) {
                    return true;
                }
            }
        }

        TournamentTeam tournamentTeamActive = tournamentTeamRepo.getAllTournamentTeamsForTeamByTournamentActiveStatus(team.getPk());
        if (tournamentTeamActive != null) {
            List<Long> listOfTeamPksRemainingInTournament = tournamentTeamActive.getTournament().getListOfTeamsRemainingInTournament();
            boolean isTeamStillInTournament = false;
            for (Long pk : listOfTeamPksRemainingInTournament) {
                if (team.getPk() == pk) {
                    isTeamStillInTournament = true;
                    break;
                }
            }
            if (isTeamStillInTournament) {
                for (long pk : tournamentTeamActive.getPksOfTeamMembersPlaying()) {
                    if (pk == user.getPk()) {
                        return true;
                    }
                }
            }
        }

        List<Match> liveMatches = matchRepo.getMatchesByTeamPkAndMatchNotKilledStatus(team.getPk());
        if (!liveMatches.isEmpty()) {
            for (Match m : liveMatches) {
                if (m.getUsersInMatch().contains(user)) {
                    if (m.getPkOfTeamThatCreatedMatch() == team.getPk()) {
                        if (m.getReportedScoreOfTeamCreated() == null) {
                            return true;
                        }
                    } else {
                        if (m.getReportedScoreOfTeamAccepted() == null) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public Team findTeamById(int teamId) {
        return teamRepo.findTeamByTeamId(teamId);
    }
}
