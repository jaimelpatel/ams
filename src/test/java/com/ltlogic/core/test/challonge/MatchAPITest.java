 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.core.test.challonge;

import com.ltlogic.constants.GameEnum;
import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.PlatformEnum;
import com.ltlogic.constants.TeamTypeEnum;
import com.ltlogic.core.test.team.TeamComponent;
import com.ltlogic.core.test.team.mwr.MWRTeamComponent;
import com.ltlogic.core.test.user.UserComponent;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.TournamentTeamRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.db.repository.challonge.ChallongeParticipantRepository;
import com.ltlogic.iws.TournamentServiceIWS;
import com.ltlogic.pojo.TournamentPojo;
import com.ltlogic.service.challonge.ChallongeMatchService;
import com.ltlogic.service.challonge.ChallongeParticipantService;
import com.ltlogic.service.challonge.ChallongeTournamentService;
import com.ltlogic.service.core.MatchService;
import com.ltlogic.service.core.TournamentTeamService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 *
 * @author Hoang
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TransactionConfiguration(defaultRollback = false)
@Service
public class MatchAPITest {

    @Autowired
    ChallongeTournamentService challongeTournamentService;

    @Autowired
    ChallongeMatchService challongeMatchService;

    @Autowired
    ChallongeParticipantService challongeParticipantService;

    @Autowired
    TournamentTeamService tournamentTeamService;

    @Autowired
    TournamentTeamRepository tournamentTeamRepository;

    @Autowired
    TeamComponent teamComponent;

    @Autowired
    UserComponent userComponent;

    @Autowired
    TournamentServiceIWS tournamentIWS;

    @Autowired
    MatchService matchService;
    
    @Autowired
    UserRepository userRepo;

    @Autowired
    ChallongeParticipantRepository challongeParticipantRepo;

    @Ignore
    @Test
    public void createChallongeMatches() throws IOException {

        TournamentPojo tournamentPojo = new TournamentPojo();

        Tournament tournament = tournamentIWS.createTournament(tournamentPojo);
        challongeTournamentService.createChallongeTournament(tournament);

        List<Team> teams = teamComponent.createAndAssociateDoublesTeams(10, GameEnum.COD_MWR, PlatformEnum.PS4, TeamTypeEnum.CASH);

        for (Team team : teams) {
            List<User> usersPlayingOnTeam = new ArrayList<>(team.getMembers());
            List<Long> usersPlayingPks = new ArrayList<>();
            for (User user : usersPlayingOnTeam) {
                usersPlayingPks.add(user.getPk());
            }
            TournamentTeam tournamentTeam = tournamentTeamService.createTournamentTeam(team, tournament.getPk(), usersPlayingPks);
            challongeParticipantService.createParticipantForTournament(tournament, tournamentTeam);
        }
        challongeTournamentService.startChallongeTournament(tournament);
        challongeMatchService.createOrSyncAllActiveMatches(tournament);

        //Set<Match> matches = tournament.getMatches();
        for (int i = 0; i < tournament.getTournamentInfo().getMaxRounds(); i++) {
            List<Match> matchList = matchService.findMatchesByStatusInTournament(MatchStatusEnum.READY_TO_PLAY, tournament.getPk());

            int count = 0;
            long id = 0;
            long mathcid = 0;
            long tId = 0;
            TournamentTeam tt = null;
            Match matchToReport = null;
            for (Match m : matchList) {
                if (m.getMatchInfo().getMatchStatus() == MatchStatusEnum.READY_TO_PLAY) {
                    System.out.println("NUMBER OF TIMES FOR READY TO PLAY: " + count++);
                    id = m.getMatchResponse().getMatchPojo().getPlayer1_id();
                    matchToReport = m;
                    tt = challongeParticipantRepo.getParticipantResponseById(id).getTournamentTeam();
                    User user = userRepo.findByPk(tt.getPksOfTeamMembersPlaying().get(0));
                    mathcid = m.getMatchResponse().getMatchPojo().getId();
                    tId = m.getTournament().getTournamentResponse().getTournamentPojo().getId();
                    
                    matchService.reportMatchScore(user, m, tt.getTeam(), false, false);
                    //challongeMatchService.reportMatchWinner(tournament, matchToReport, tt, true);
                }
            }
            System.out.println("Total matches: " + matchList.size());
            System.out.println("COUNT: " + count);
        }
        challongeTournamentService.finalizeChallongeTournament(tournament);
    }
}
