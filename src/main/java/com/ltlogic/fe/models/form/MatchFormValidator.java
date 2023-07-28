/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.models.form;

import com.ltlogic.constants.InvitesEnum;
import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.TeamStatusEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.TournamentInvite;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.MatchRepository;
import com.ltlogic.db.repository.TournamentTeamRepository;
import com.ltlogic.db.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author raymond
 */
@Component
public class MatchFormValidator implements Validator {

    @Autowired
    TournamentTeamRepository tournamentTeamRepo;

    @Autowired
    MatchRepository matchRepo;

    @Autowired
    UserRepository userRepo;

    @Override
    public boolean supports(Class<?> aClass) {
        return MatchForm.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {

        //valid limit users allow to play the game
        //check no multiple matches created for same match info
    }

    //for match creation
    public void matchCreationValidation(Object target, Team team, User user, Errors errors) {
        MatchForm form = (MatchForm) target;

        if (team.getTeamPojo().getPlatform() == PlatformEnum.PS4) {
            if (user.getUserInfo().getPlayStation4Name() == null || user.getUserInfo().getPlayStation4Name().isEmpty()) {
                errors.reject("teamSize", "You must input your Playstation 4 name in your account settings before creating this match. ");
            }
        } else if (team.getTeamPojo().getPlatform() == PlatformEnum.XBOXONE) {
            if (user.getUserInfo().getXboxOneGamerTag() == null || user.getUserInfo().getXboxOneGamerTag().isEmpty()) {
                errors.reject("teamSize", "You must input your Xbox One gamertag in your account settings before creating this match. ");
            }
        }

        if (team.getTeamPojo().getTeamType() == TeamTypeEnum.CASH) {
            if (user != null && user.getBank() != null) {
                if (form.getWagerAmount() != null) {
                    if (user.getBank().getTotalAmount().compareTo(form.getWagerAmount()) == -1) {
                        errors.reject("teamSize", "You do not have enough cash to create this match. Wager amount per member is set to $" + form.getWagerAmount() + ". Please visit the shop to get more cash.");
                    }
                }
            }
        }

        if (team != null && team.getTeamPojo() != null && team.getTeamPojo().getTeamSize() != null) {
            switch (team.getTeamPojo().getTeamSize()) {
                case SINGLES:
                    if (form.getUsersInMatch().size() != 1) {
                        errors.reject("teamSize", "You must select a total of 1 member to participate in the match.");
                    }
                    break;
                case DOUBLES:
                    if (form.getUsersInMatch().size() != 2) {
                        errors.reject("teamSize", "You must select 1 other member to participate in the match with you.");
                    }
                    break;
                case TEAM:
                    if (form.getUsersInMatch().size() != 3 && form.getUsersInMatch().size() != 4) {
                        errors.reject("teamSize", "You must select 2-3 other members to participate in the match with you.");
                    }
                    break;
                default:
                    break;
            }
        }

        TournamentTeam tournamentTeamPending = tournamentTeamRepo.getAllTournamentTeamsForTeamByTournamentPendingStatus(team.getPk());
        if (tournamentTeamPending != null) {
            errors.reject("teamSize", "This team is in an upcoming tournament.");
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
                errors.reject("teamSize", "This team is in an active tournament.");
            }
        }
 
        List<Match> matchList = matchRepo.getMatchesByTeamPkAndMatchNotKilledStatus(team.getPk());
        if (!matchList.isEmpty()) {
            for(Match m : matchList){
                if(m.getPkOfTeamThatCreatedMatch() == team.getPk()){
                    if(m.getReportedScoreOfTeamCreated() == null){
                        errors.reject("teamSize", "This team is already in a match. ");
                        break;
                    }
                }else{
                   if(m.getReportedScoreOfTeamAccepted() == null){
                        errors.reject("teamSize", "This team is already in a match. ");
                        break;
                    } 
                }
            }
        }

        if (team != null && team.getTeamPojo() != null) {
            if (TeamTypeEnum.CASH == team.getTeamPojo().getTeamType()) {
                if (form.getWagerAmount() == null) {
                    errors.reject("wagerAmount", "Wager amount per member is required.");
                }

                if (form.getMatchPublic() == null) {
                    errors.reject("matchPublic", "Please select whether the match is public or private.");
                }

                if (form.getMatchPublic() == false) {
                    if (form.getOpponentTeamName() == null || form.getOpponentTeamName().isEmpty()) {
                        errors.reject("opponentName", "The name of the team you want to challenge is required for private matches.");
                    }
                }
                if(team.getTeamPojo().getTeamName().equalsIgnoreCase(form.getOpponentTeamName())){
                    errors.reject("opponentName", "You cannot challenge your own team.");
                }
            }

        }

    }

    public void joinMatchValidationGoToMatchDetails(List<User> userList, Match match, Team team, Errors errors) {
        if (userList == null) {
            userList = new ArrayList<>();
        }
        
        if(!match.getMatchInfo().isIsMatchPublic()){
            if (team.getTeamPojo().getTeamStatus() == TeamStatusEnum.DISBANDED){
                errors.reject("alreadyPlayedTeam", "You cannot join the match because this team has been disbanded.");
            }
        }
        
        if(match.getMatchInfo().getMatchType() == MatchTypeEnum.XP){
            long pkOfCreatorTeamOfCurrentMatch = match.getPkOfTeamThatCreatedMatch();
            List<Match> listOfEndedOrDisputedMatches = matchRepo.getXpMatchesByTeamPkAndActiveOrEndedOrDisputedStatus(team.getPk());
            if(!listOfEndedOrDisputedMatches.isEmpty()){
                for(Match m : listOfEndedOrDisputedMatches){
                    long pkOfCreatorTeamOfOldMatch = m.getPkOfTeamThatCreatedMatch();
                    long pkOfAcceptorTeamOfOldMatch = m.getPkOfTeamThatAcceptedMatch();
                    if(pkOfCreatorTeamOfCurrentMatch == pkOfCreatorTeamOfOldMatch || pkOfCreatorTeamOfCurrentMatch == pkOfAcceptorTeamOfOldMatch){
                        errors.reject("alreadyPlayedTeam", "You have already played against this team in your recent match history.");
                        break;
                    }
                }
            }
        }

        List<User> usersInMatch = userRepo.getUsersByMatchPk(match.getPk());
        for (User user : userList) {
            if (usersInMatch.contains(user)) {
                errors.reject("alreadyInMatch", "User " + "'" + user.getUsername() + "'" + " is already in this match on a different team.");
            }
        }

        switch (match.getMatchInfo().getMatchSizeEnum()) {
            case SINGLES:
                if (userList.size() != 1) {
                    errors.reject("teamSize", "A total of 1 member must be selected to participate in the match.");
                }
                break;
            case DOUBLES:
                if (userList.size() != 2) {
                    errors.reject("teamSize", "A total of 2 members must be selected to participate in the match.");
                }
                break;
            case THREES:
                if (userList.size() != 3) {
                    errors.reject("teamSize", "A total of 3 members must be selected to participate in the match.");
                }
                break;
            case FOURS:
                if (userList.size() != 4) {
                    errors.reject("teamSize", "A total of 4 members must be selected to participate in the match.");
                }
                break;
            default:
                break;
        }

        if (match.getMatchInfo().getPlatform() == PlatformEnum.PS4) {
            if (userList.get(0).getUserInfo().getPlayStation4Name() == null || userList.get(0).getUserInfo().getPlayStation4Name().isEmpty()) {
                errors.reject("teamSize", "You must input your Playstation 4 name in your account settings before accepting this match. ");
            }
        } else if (match.getMatchInfo().getPlatform() == PlatformEnum.XBOXONE) {
            if (userList.get(0).getUserInfo().getXboxOneGamerTag() == null || userList.get(0).getUserInfo().getXboxOneGamerTag().isEmpty()) {
                errors.reject("teamSize", "You must input your Xbox One gamertag in your account settings before accepting this match. ");
            }
        }

    }

    public void joinMatchValidationGoToCreateNewTeamPage(List<User> userList, Match match, Team team, Errors errors) {

        TournamentTeam tournamentTeamPending = tournamentTeamRepo.getAllTournamentTeamsForTeamByTournamentPendingStatus(team.getPk());
        if (tournamentTeamPending != null) {
            errors.reject("teamSize", "This team is in an upcoming tournament.");
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
                errors.reject("teamSize", "This team is in an active tournament.");
            }
        }

        List<Match> matchList = matchRepo.getMatchesByTeamPkAndMatchNotKilledStatus(team.getPk());
        if (!matchList.isEmpty()) {
            for(Match m : matchList){
                if(m.getPkOfTeamThatCreatedMatch() == team.getPk()){
                    if(m.getReportedScoreOfTeamCreated() == null){
                        errors.reject("teamSize", "This team is already in a match. ");
                        break;
                    }
                }else{
                   if(m.getReportedScoreOfTeamAccepted() == null){
                        errors.reject("teamSize", "This team is already in a match. ");
                        break;
                    } 
                }
            }
        }
    }

    //for public cash match accept/join and private cash match accept
    public void validateMatchAcceptForWager(User user, Match match, Errors errors) {
        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.WAGER) {
            if (user.getBank().getTotalAmount().compareTo(match.getMatchInfo().getWagerAmountPerMember()) == -1) {
                errors.reject("teamSize", "You do not have enough cash to accept this match invite. Wager amount per member is $" + match.getMatchInfo().getWagerAmountPerMember() + ". Please visit the shop to get more cash.");
            }
        }

    }

    public void validateMatchAcceptForWager2(User user, Match match, Errors errors) throws Exception {
        if (match.getMatchInfo().getMatchStatus() != MatchStatusEnum.WAITING_ON_FIRST_ACCEPT && match.getMatchInfo().getMatchStatus() != MatchStatusEnum.PENDING && match.getMatchInfo().getMatchStatus() != MatchStatusEnum.WAITING_ON_SECOND_ACCEPT) {
            errors.reject("teamSize", "This match is no longer available.");
            throw new Exception("This match is no longer available.");
        }

        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.WAGER) {
            if (user.getBank().getTotalAmount().compareTo(match.getMatchInfo().getWagerAmountPerMember()) == -1) {
                errors.reject("teamSize", "You do not have enough cash to accept this match invite. Wager amount per member is $" + match.getMatchInfo().getWagerAmountPerMember() + ". Please visit the shop to get more cash.");
                throw new Exception("You do not have enough cash to accept this match invite. Wager amount per member is $" + match.getMatchInfo().getWagerAmountPerMember() + ". Please visit the shop to get more cash.");
            }
        }

        if (match.getMatchInfo().getPlatform() == PlatformEnum.PS4) {
            if (user.getUserInfo().getPlayStation4Name() == null || user.getUserInfo().getPlayStation4Name().isEmpty()) {
                errors.reject("teamSize", "You must input your Playstation 4 name in your account settings before accepting this match.");
                throw new Exception("You must input your Playstation 4 name in your account settings before accepting this match.");
            }
        } else if (match.getMatchInfo().getPlatform() == PlatformEnum.XBOXONE) {
            if (user.getUserInfo().getXboxOneGamerTag() == null || user.getUserInfo().getXboxOneGamerTag().isEmpty()) {
                errors.reject("teamSize", "You must input your Xbox One gamertag in your account settings before accepting this match.");
                throw new Exception("You must input your Xbox One gamertag in your account settings before accepting this match.");
            }
        }

    }

    public void validateDeclineMatchInvite(User user, Match match, Errors errors) throws Exception {
        if (match.getMatchInfo().getMatchStatus() != MatchStatusEnum.WAITING_ON_FIRST_ACCEPT && match.getMatchInfo().getMatchStatus() != MatchStatusEnum.PENDING && match.getMatchInfo().getMatchStatus() != MatchStatusEnum.WAITING_ON_SECOND_ACCEPT) {
            errors.reject("teamSize", "This match is no longer available.");
            throw new Exception("This match is no longer available.");
        }
    }
}
