/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.models.form;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.constants.MatchSizeEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentInvite;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.MatchRepository;
import com.ltlogic.db.repository.TournamentInviteRepository;
import com.ltlogic.db.repository.TournamentTeamRepository;
import com.ltlogic.iws.UserIWS;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author raymond
 */
@Component
public class TournamentFormValidator implements Validator {

    @Autowired
    TournamentTeamRepository tournamentTeamRepo;

    @Autowired
    TournamentInviteRepository tournamentInviteRepo;

    @Autowired
    MatchRepository matchRepo;

    @Override
    public boolean supports(Class<?> aClass) {
        return TournamentForm.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }

    //for accepting tournament invites after leader has chosen to join
    public void validateTournamentAccept(User user, Tournament tournament, Errors errors) throws Exception {
        if (user.getBank().getTotalAmount().compareTo(tournament.getTournamentInfo().getWagerAmountPerMember()) == -1) {
            errors.reject("tournamentStarted", "You do not have enough cash to accept this match. Tournament buy-in amount per member is $" + tournament.getTournamentInfo().getWagerAmountPerMember() + ". Please visit the shop to get more cash.");
            throw new Exception("You do not have enough cash to accept this match. Tournament buy-in amount per member is $" + tournament.getTournamentInfo().getWagerAmountPerMember() + ". Please visit the shop to get more cash.");
        }

        if (tournament.getTournamentInfo().getPlatform() == PlatformEnum.PS4) {
            if (user.getUserInfo().getPlayStation4Name() == null || user.getUserInfo().getPlayStation4Name().isEmpty()) {
                errors.reject("tournamentStarted", "You must input your Playstation 4 name in your account settings before accepting this tournament invite.");
                throw new Exception("You must input your Playstation 4 name in your account settings before accepting this tournament invite. ");

            }
        } else if (tournament.getTournamentInfo().getPlatform() == PlatformEnum.XBOXONE) {
            if (user.getUserInfo().getXboxOneGamerTag() == null || user.getUserInfo().getXboxOneGamerTag().isEmpty()) {
                errors.reject("tournamentStarted", "You must input your Xbox One gamertag in your account settings before accepting this tournament invite.");
                throw new Exception("You must input your Xbox One gamertag in your account settings before accepting this tournament invite. ");

            }
        }

        if (tournament.getUsersInTournament().contains(user)) {
            errors.reject("tournamentStarted", "You cannot join this tournament because you are already in it on a different team.");
            throw new Exception("You cannot join this tournament because you are already in it on a different team.");
        }

    }

    //for when the leader of a team chooses to join a tournament 
    public void validateJoinTournament(Tournament tournament, List<User> usersPlayigInTournament, Team team, User user, Errors errors) {

        if (user.getBank().getTotalAmount().compareTo(tournament.getTournamentInfo().getWagerAmountPerMember()) == -1) {
            errors.reject("tournamentStarted", "You do not have enough cash to accept this match. Tournament buy-in amount per member is $" + tournament.getTournamentInfo().getWagerAmountPerMember() + ". Please visit the shop to get more cash.");
        }

        if (tournament.getUsersInTournament().contains(user)) {
            errors.reject("tournamentStarted", "You cannot join this tournament because you are already in it on a different team.");
        }
        if (tournament.getTournamentInfo().getPlatform() == PlatformEnum.PS4) {
            if (user.getUserInfo().getPlayStation4Name() == null || user.getUserInfo().getPlayStation4Name().isEmpty()) {
                errors.reject("tournamentStarted", "You must input your Playstation 4 name in your account settings before joining this tournament.");
            }
        } else if (tournament.getTournamentInfo().getPlatform() == PlatformEnum.XBOXONE) {
            if (user.getUserInfo().getXboxOneGamerTag() == null || user.getUserInfo().getXboxOneGamerTag().isEmpty()) {
                errors.reject("tournamentStarted", "You must input your Xbox One gamertag in your account settings before joining this tournament.");
            }
        }

        if (DateTimeUtil.getDefaultLocalDateTimeNow().isAfter(tournament.getTournamentInfo().getScheduledTournamentTime())) {
            errors.reject("tournamentStarted", "You cannot join because this tournament has already started.");
        }

//        if (tournament != null) {
//            if (tournament.getTournamentInfo() != null) {
//                MatchSizeEnum matchSize = tournament.getTournamentInfo().getMatchSize();
//                Integer numberOfUsersPlayingInTournament = 0;
//                if (usersPlayigInTournament != null) {
//                    numberOfUsersPlayingInTournament = usersPlayigInTournament.size();
//                }
//
//                if (matchSize == MatchSizeEnum.SINGLES && numberOfUsersPlayingInTournament != 1) {
//                    errors.reject("tournament.totalPlayingPlayers", "A total of 1 member must be selected to participate in the tournament.");
//                } else if (matchSize == MatchSizeEnum.DOUBLES && numberOfUsersPlayingInTournament != 2) {
//                    errors.reject("tournament.totalPlayingPlayers", "A total of 2 members must be selected to participate in the tournament.");
//                } else if (matchSize == MatchSizeEnum.THREES && numberOfUsersPlayingInTournament != 3) {
//                    errors.reject("tournament.totalPlayingPlayers", "A total of 3 members must be selected to participate in the tournament.");
//                } else if (matchSize == MatchSizeEnum.FOURS && numberOfUsersPlayingInTournament != 4) {
//                    errors.reject("tournament.totalPlayingPlayers", "A total of 4 members must be selected to participate in the tournament.");
//                }
//            }
//        }
//        for (User u : usersPlayigInTournament) {
//            List<TournamentInvite> tournamentInviteList = tournamentInviteRepo.findAllTournamentInvitesByTournamentPkAndUserPk(tournament.getPk(), u.getPk());
//            for (TournamentInvite tournamentInvite : tournamentInviteList) {
//                if (tournamentInvite.getInviteEnum() == InvitesEnum.ACCEPTED) {
//                    if (!tournamentInvite.getTournamentTeam().isTeamCancelled()) {
//                        errors.reject("tournament.totalPlayingPlayers", "User " + "'" + u.getUsername() + "'" + " is already in this tournament on a different team.");
//                    }
//                }
//            }
//        }
        TournamentTeam tournamentTeamPending = tournamentTeamRepo.getAllTournamentTeamsForTeamByTournamentPendingStatus(team.getPk());
        if (tournamentTeamPending != null) {
            errors.reject("teamSize", "This team is already in an upcoming tournament.");
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
                errors.reject("teamSize", "This team is already in an active tournament.");
            }
        }

//        List<Match> liveMatches = matchRepo.getMatchesByTeamPkAndMatchNotKilledStatus(team.getPk());
//        if (!liveMatches.isEmpty()) {
//            errors.reject("tournament.totalPlayingPlayers", "This team is in a match.");
//        }
        List<Match> matchList = matchRepo.getMatchesByTeamPkAndMatchNotKilledStatus(team.getPk());
        if (!matchList.isEmpty()) {
            for (Match m : matchList) {
                if (m.getPkOfTeamThatCreatedMatch() == team.getPk()) {
                    if (m.getReportedScoreOfTeamCreated() == null) {
                        errors.reject("teamSize", "This team is already in a match. ");
                        break;
                    }
                } else {
                    if (m.getReportedScoreOfTeamAccepted() == null) {
                        errors.reject("teamSize", "This team is already in a match. ");
                        break;
                    }
                }
            }
        }

        if (tournament.getTournamentTeams().size() >= tournament.getTournamentInfo().getMaxTeamCount()) {
            errors.reject("teamSize", "You cannot join this tournament because it is full. (Max. " + tournament.getTournamentInfo().getMaxTeamCount() + " teams)");
        }

    }

    //for when the leader of a team chooses to join a tournament 
    public void validateJoinTournamentException(Tournament tournament, List<User> usersPlayigInTournament, Team team, User user, Errors errors) throws RuntimeException {

        if (user.getBank().getTotalAmount().compareTo(tournament.getTournamentInfo().getWagerAmountPerMember()) == -1) {
            throw new RuntimeException("You do not have enough cash to accept this match. Tournament buy-in amount per member is $" + tournament.getTournamentInfo().getWagerAmountPerMember() + ". Please visit the shop to get more cash.");
        }

        if (tournament.getUsersInTournament().contains(user)) {
            throw new RuntimeException("You cannot join this tournament because you are already in it on a different team.");
        }

        if (tournament.getTournamentInfo().getPlatform() == PlatformEnum.PS4) {
            if (user.getUserInfo().getPlayStation4Name() == null || user.getUserInfo().getPlayStation4Name().isEmpty()) {
                throw new RuntimeException("You must input your Playstation 4 name in your account settings before joining this tournament.");
            }
        } else if (tournament.getTournamentInfo().getPlatform() == PlatformEnum.XBOXONE) {
            if (user.getUserInfo().getXboxOneGamerTag() == null || user.getUserInfo().getXboxOneGamerTag().isEmpty()) {
                throw new RuntimeException("You must input your Xbox One gamertag in your account settings before joining this tournament.");
            }
        }

        if (DateTimeUtil.getDefaultLocalDateTimeNow().isAfter(tournament.getTournamentInfo().getScheduledTournamentTime())) {
            throw new RuntimeException("You cannot join because this tournament has already started.");
        }

//        if (tournament != null) {
//            if (tournament.getTournamentInfo() != null) {
//                MatchSizeEnum matchSize = tournament.getTournamentInfo().getMatchSize();
//                Integer numberOfUsersPlayingInTournament = 0;
//                if (usersPlayigInTournament != null) {
//                    numberOfUsersPlayingInTournament = usersPlayigInTournament.size();
//                }
//
//                if (matchSize == MatchSizeEnum.SINGLES && numberOfUsersPlayingInTournament != 1) {
//                    errors.reject("tournament.totalPlayingPlayers", "A total of 1 member must be selected to participate in the tournament.");
//                } else if (matchSize == MatchSizeEnum.DOUBLES && numberOfUsersPlayingInTournament != 2) {
//                    errors.reject("tournament.totalPlayingPlayers", "A total of 2 members must be selected to participate in the tournament.");
//                } else if (matchSize == MatchSizeEnum.THREES && numberOfUsersPlayingInTournament != 3) {
//                    errors.reject("tournament.totalPlayingPlayers", "A total of 3 members must be selected to participate in the tournament.");
//                } else if (matchSize == MatchSizeEnum.FOURS && numberOfUsersPlayingInTournament != 4) {
//                    errors.reject("tournament.totalPlayingPlayers", "A total of 4 members must be selected to participate in the tournament.");
//                }
//            }
//        }
//        for (User u : usersPlayigInTournament) {
//            List<TournamentInvite> tournamentInviteList = tournamentInviteRepo.findAllTournamentInvitesByTournamentPkAndUserPk(tournament.getPk(), u.getPk());
//            for (TournamentInvite tournamentInvite : tournamentInviteList) {
//                if (tournamentInvite.getInviteEnum() == InvitesEnum.ACCEPTED) {
//                    if (!tournamentInvite.getTournamentTeam().isTeamCancelled()) {
//                        errors.reject("tournament.totalPlayingPlayers", "User " + "'" + u.getUsername() + "'" + " is already in this tournament on a different team.");
//                    }
//                }
//            }
//        }
        TournamentTeam tournamentTeamPending = tournamentTeamRepo.getAllTournamentTeamsForTeamByTournamentPendingStatus(team.getPk());
        if (tournamentTeamPending != null) {
            throw new RuntimeException("This team is already in an upcoming tournament.");
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
                throw new RuntimeException("This team is already in an active tournament.");
            }
        }

//        List<Match> liveMatches = matchRepo.getMatchesByTeamPkAndMatchNotKilledStatus(team.getPk());
//        if (!liveMatches.isEmpty()) {
//            errors.reject("tournament.totalPlayingPlayers", "This team is in a match.");
//        }
        List<Match> matchList = matchRepo.getMatchesByTeamPkAndMatchNotKilledStatus(team.getPk());
        if (!matchList.isEmpty()) {
            for (Match m : matchList) {
                if (m.getPkOfTeamThatCreatedMatch() == team.getPk()) {
                    if (m.getReportedScoreOfTeamCreated() == null) {
                        throw new RuntimeException("This team is already in a match.");
                    }
                } else {
                    if (m.getReportedScoreOfTeamAccepted() == null) {
                        throw new RuntimeException("This team is already in a match.");
                    }
                }
            }
        }

        if (tournament.getTournamentTeams().size() >= tournament.getTournamentInfo().getMaxTeamCount()) {
            throw new RuntimeException("You cannot join this tournament because it is full. (Max. " + tournament.getTournamentInfo().getMaxTeamCount() + " teams)");
        }

    }


}
