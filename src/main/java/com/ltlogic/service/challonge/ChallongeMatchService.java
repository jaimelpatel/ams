/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.challonge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltlogic.DateTimeUtil;
import com.ltlogic.HttpClient;
import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.entity.challonge.AttachmentRequest;
import com.ltlogic.db.entity.challonge.MatchRequest;
import com.ltlogic.db.entity.challonge.MatchResponse;
import com.ltlogic.db.repository.MatchRepository;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.db.repository.challonge.ChallongeMatchRepository;
import com.ltlogic.db.repository.challonge.ChallongeParticipantRepository;
import com.ltlogic.pojo.challonge.ChallongeMatchRequestPojo;
import com.ltlogic.service.core.MatchService;
import com.ltlogic.service.core.TournamentService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jaimel
 */
@Service
@Transactional
public class ChallongeMatchService implements InitializingBean {

    @Value("${envTarget:local}")
    private String targetEnv;

    private static boolean isProduction;

    private boolean isProduction(String env) {
        return "production".equalsIgnoreCase(env);
    }

    private static String APP_HOST_MATCH_URL = "";

    @Autowired
    private ChallongeMatchRepository challongeMatchRepository;

    @Autowired
    private ChallongeParticipantRepository challongeParticipantRepo;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private ChallongeAttachmentService challongeAttachmentService;

    @Autowired
    private MatchRepository matchRepo;

    @Autowired
    private MatchService matchService;

    @Autowired
    private UserRepository userRepo;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChallongeMatchService.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        isProduction = isProduction(targetEnv);
        APP_HOST_MATCH_URL = isProduction ? "http://nextlevelgamingonline.com/matches/" : "http://www.nextlevelgamingonline.com/matches/";
    }

    public MatchResponse getNextMatchByCurrentMatchResponseId(long matchResponseId) {
        return challongeMatchRepository.getNextMatchByCurrentMatchId(matchResponseId);
    }

    public void createOrSyncAllActiveMatches(Tournament tournament) {
        long challongeTournamentId = tournament.getTournamentResponse().getTournamentPojo().getId();
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + challongeTournamentId + "/matches.json");
        String responseBody = HttpClient.makeGetRestCall(url);
        if (responseBody != null) {
            try {
                int maxTournamentRoundCount = 0;
                JSONArray jsonArray = new JSONArray(responseBody);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    System.out.println("each jsonObject from match json array: " + jsonObject.toString());
                    MatchResponse matchResponseFromGET = gson.fromJson(jsonObject.toString(), MatchResponse.class);
                    long challongeMatchID = matchResponseFromGET.getMatchPojo().getId();
                    MatchResponse matchResponseFromDB = challongeMatchRepository.getMatchResponseById(challongeMatchID);
                    // This block only 
                    if (matchResponseFromDB == null) {
                        //calculate max rounds
                        int round = matchResponseFromGET.getMatchPojo().getRound();
                        if (round > maxTournamentRoundCount) {
                            maxTournamentRoundCount = round;
                        }
                        tournament.getTournamentInfo().setMaxRounds(maxTournamentRoundCount);
                        log.info("ChallongeMatchService - getAllMatches() - Match with ID# " + challongeMatchID + " does not exist in the db. So lets create it.");
                        long player_1_ID = matchResponseFromGET.getMatchPojo().getPlayer1_id();
                        long player_2_ID = matchResponseFromGET.getMatchPojo().getPlayer2_id();

                        // Factor for all cases for matches. We create all the matches even the ones without any participants. 
                        TournamentTeam team1 = null;
                        TournamentTeam team2 = null;
                        if (player_1_ID != 0 && player_2_ID != 0) {
                            team1 = challongeParticipantRepo.getParticipantResponseById(player_1_ID).getTournamentTeam();
                            team2 = challongeParticipantRepo.getParticipantResponseById(player_2_ID).getTournamentTeam();
                        } else if (player_1_ID != 0 && player_2_ID == 0) {
                            team1 = challongeParticipantRepo.getParticipantResponseById(player_1_ID).getTournamentTeam();
                        } else if (player_1_ID == 0 && player_2_ID != 0) {
                            team2 = challongeParticipantRepo.getParticipantResponseById(player_2_ID).getTournamentTeam();
                        }

                        //create new match and associate here
                        Match match = tournamentService.createTournamentMatch(tournament, team1, team2, round);
                        associateMatchToChallongeMatch(match, matchResponseFromGET);
                        challongeMatchRepository.persistChallongeMatch(matchResponseFromGET);

                        //Update Challonge Match with URL to the Match Detail Page 
                        AttachmentRequest attachmentRequest = new AttachmentRequest();
                        attachmentRequest.setUrl(APP_HOST_MATCH_URL + match.getPk());
                        attachmentRequest.setDescription("Match ID #" + match.getMatchId());
                        challongeAttachmentService.createAttachment(challongeMatchID, challongeTournamentId, attachmentRequest);
                        log.info("Challonge match created with Attachments...Match Pk: " + match.getPk() + " .Challonge Match Response Pk: " + matchResponseFromGET.getPk());
                    } else {
                        log.info("ChallongeMatchService - getAllMatches() - Match with ID# " + challongeMatchID + " already exists in the db. So lets update it.");
                        matchResponseFromDB.setMatchPojo(matchResponseFromGET.getMatchPojo());
                        challongeMatchRepository.updateChallongeMatch(matchResponseFromDB);
                        int round = matchResponseFromGET.getMatchPojo().getRound();

                        Match matchFromDB = matchResponseFromDB.getMatch();
                        if (matchFromDB.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING) {
                            long player_1_ID = matchResponseFromGET.getMatchPojo().getPlayer1_id();
                            long player_2_ID = matchResponseFromGET.getMatchPojo().getPlayer2_id();

                            // Factor for all cases for matches. We create all the matches even the ones without any participants. 
                            TournamentTeam tournamentTeam1 = null;
                            TournamentTeam tournamentTeam2 = null;
                            if (player_1_ID != 0 && player_2_ID != 0) {
                                tournamentTeam1 = challongeParticipantRepo.getParticipantResponseById(player_1_ID).getTournamentTeam();
                                tournamentTeam2 = challongeParticipantRepo.getParticipantResponseById(player_2_ID).getTournamentTeam();
                                matchService.associateTeamToMatch(tournamentTeam1.getTeam(), matchFromDB);
                                matchService.associateTeamToMatch(tournamentTeam2.getTeam(), matchFromDB);
                                associateTournamentTeamUsersToMatch(tournamentTeam1, matchFromDB);
                                associateTournamentTeamUsersToMatch(tournamentTeam2, matchFromDB);
                                for (long pkOfCreatorTeamMembers : tournamentTeam1.getPksOfTeamMembersPlaying()) {
                                    if (!matchFromDB.getPksOfCreatorTeamMembersPlaying().contains(pkOfCreatorTeamMembers)) {
                                        matchFromDB.getPksOfCreatorTeamMembersPlaying().add(pkOfCreatorTeamMembers);
                                    }
                                }
                                for (long pkOfAcceptorTeamMembers : tournamentTeam2.getPksOfTeamMembersPlaying()) {
                                    if (!matchFromDB.getPksOfAcceptorTeamMembersPlaying().contains(pkOfAcceptorTeamMembers)) {
                                        matchFromDB.getPksOfAcceptorTeamMembersPlaying().add(pkOfAcceptorTeamMembers);
                                    }
                                }
                                matchFromDB.setPkOfTeamThatCreatedMatch(tournamentTeam1.getTeam().getPk());
                                matchFromDB.setPkOfTeamThatAcceptedMatch(tournamentTeam2.getTeam().getPk());
                                matchFromDB.getMatchInfo().setMatchStatus(MatchStatusEnum.READY_TO_PLAY);
                                matchFromDB.setHaveAllPlayersAccepted(true);
                                matchFromDB.getMatchInfo().setScheduledMatchTime(DateTimeUtil.getDefaultLocalDateTimeNow(), DateTimeUtil.DEFAULT_TIME_ZONE_ENUM);
                                matchService.selectHostAndMapsForMatch(matchFromDB, round, tournamentTeam1.getParticipantResponse().getParticipant().getSeed(), tournamentTeam2.getParticipantResponse().getParticipant().getSeed());
                            } else if (player_1_ID != 0 && player_2_ID == 0) {
                                tournamentTeam1 = challongeParticipantRepo.getParticipantResponseById(player_1_ID).getTournamentTeam();
                                matchService.associateTeamToMatch(tournamentTeam1.getTeam(), matchFromDB);
                                associateTournamentTeamUsersToMatch(tournamentTeam1, matchFromDB);
                                for (long pkOfCreatorTeamMembers : tournamentTeam1.getPksOfTeamMembersPlaying()) {
                                    if (!matchFromDB.getPksOfCreatorTeamMembersPlaying().contains(pkOfCreatorTeamMembers)) {
                                        matchFromDB.getPksOfCreatorTeamMembersPlaying().add(pkOfCreatorTeamMembers);
                                    }
                                }
                                matchFromDB.setPkOfTeamThatCreatedMatch(tournamentTeam1.getTeam().getPk());
                                matchFromDB.getMatchInfo().setMatchStatus(MatchStatusEnum.PENDING);
                            } else if (player_1_ID == 0 && player_2_ID != 0) {
                                tournamentTeam2 = challongeParticipantRepo.getParticipantResponseById(player_2_ID).getTournamentTeam();
                                matchService.associateTeamToMatch(tournamentTeam2.getTeam(), matchFromDB);
                                associateTournamentTeamUsersToMatch(tournamentTeam2, matchFromDB);
                                for (long pkOfAcceptorTeamMembers : tournamentTeam2.getPksOfTeamMembersPlaying()) {
                                    if (!matchFromDB.getPksOfAcceptorTeamMembersPlaying().contains(pkOfAcceptorTeamMembers)) {
                                        matchFromDB.getPksOfAcceptorTeamMembersPlaying().add(pkOfAcceptorTeamMembers);
                                    }
                                }
                                matchFromDB.setPkOfTeamThatAcceptedMatch(tournamentTeam2.getTeam().getPk());
                                matchFromDB.getMatchInfo().setMatchStatus(MatchStatusEnum.PENDING);
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                log.error("IOException in Create or Sync Match(): ", ex);
            } catch (Exception ex) {
                log.error("Exception in Create or Sync Match(): ", ex);
                throw ex;
            }
        }
    }

    public void associateTournamentTeamUsersToMatch(TournamentTeam tTeam, Match match) {
        for (Long userPk : tTeam.getPksOfTeamMembersPlaying()) {
            User user = userRepo.findByPk(userPk);
            matchService.associateUserToMatch(user, match);
        }
    }

    public void syncSingleMatch(Match match, Tournament tournament) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChallongeMatchService.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.info("MATCH BEING SYNCED ID: " +  match.getMatchResponse().getMatchPojo().getId() + " PK: " + match.getPk());
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + "/matches/" + match.getMatchResponse().getMatchPojo().getId() + ".json");
        String responseBody = HttpClient.makeGetRestCall(url);
        log.info("RESPONSE BODY OF SYNC SINGLE: " + responseBody);
        if (responseBody != null) {
            MatchResponse matchResponse = gson.fromJson(responseBody, MatchResponse.class);
            MatchResponse matchResponseFromDB = challongeMatchRepository.getMatchResponseById(matchResponse.getMatchPojo().getId());
            if (matchResponseFromDB != null) {
                matchResponseFromDB.setMatchPojo(matchResponse.getMatchPojo());
                challongeMatchRepository.updateChallongeMatch(matchResponseFromDB);
                int round = matchResponse.getMatchPojo().getRound();
                Match matchFromDB = matchResponseFromDB.getMatch();
                if (matchFromDB.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING) {
                    long player_1_ID = matchResponse.getMatchPojo().getPlayer1_id();
                    long player_2_ID = matchResponse.getMatchPojo().getPlayer2_id();

                    // Factor for all cases for matches. We create all the matches even the ones without any participants. 
                    TournamentTeam tournamentTeam1 = null;
                    TournamentTeam tournamentTeam2 = null;
                    if (player_1_ID != 0 && player_2_ID != 0) {
                        log.info("PLAYER 1 AND PLAYER 2 NOT NULL----------------------------");
                        tournamentTeam1 = challongeParticipantRepo.getParticipantResponseById(player_1_ID).getTournamentTeam();
                        tournamentTeam2 = challongeParticipantRepo.getParticipantResponseById(player_2_ID).getTournamentTeam();
                        matchService.associateTeamToMatch(tournamentTeam1.getTeam(), matchFromDB);
                        matchService.associateTeamToMatch(tournamentTeam2.getTeam(), matchFromDB);
                        associateTournamentTeamUsersToMatch(tournamentTeam1, matchFromDB);
                        associateTournamentTeamUsersToMatch(tournamentTeam2, matchFromDB);
                        for (long pkOfCreatorTeamMembers : tournamentTeam1.getPksOfTeamMembersPlaying()) {
                            if (!matchFromDB.getPksOfCreatorTeamMembersPlaying().contains(pkOfCreatorTeamMembers)) {
                                matchFromDB.getPksOfCreatorTeamMembersPlaying().add(pkOfCreatorTeamMembers);
                            }
                        }
                        for (long pkOfAcceptorTeamMembers : tournamentTeam2.getPksOfTeamMembersPlaying()) {
                            if (!matchFromDB.getPksOfAcceptorTeamMembersPlaying().contains(pkOfAcceptorTeamMembers)) {
                                matchFromDB.getPksOfAcceptorTeamMembersPlaying().add(pkOfAcceptorTeamMembers);
                            }
                        }
                        matchFromDB.setPkOfTeamThatCreatedMatch(tournamentTeam1.getTeam().getPk());
                        matchFromDB.setPkOfTeamThatAcceptedMatch(tournamentTeam2.getTeam().getPk());
                        matchFromDB.getMatchInfo().setMatchStatus(MatchStatusEnum.READY_TO_PLAY);
                        matchFromDB.setHaveAllPlayersAccepted(true);
                        matchFromDB.getMatchInfo().setScheduledMatchTime(DateTimeUtil.getDefaultLocalDateTimeNow(), DateTimeUtil.DEFAULT_TIME_ZONE_ENUM);
                        matchService.selectHostAndMapsForMatch(matchFromDB, round, tournamentTeam1.getParticipantResponse().getParticipant().getSeed(), tournamentTeam2.getParticipantResponse().getParticipant().getSeed());
                    } else if (player_1_ID != 0 && player_2_ID == 0) {
                        log.info("PLAYER 1 NOT NULL AND PLAYER 2 IS NULL----------------------------");
                        tournamentTeam1 = challongeParticipantRepo.getParticipantResponseById(player_1_ID).getTournamentTeam();
                        matchService.associateTeamToMatch(tournamentTeam1.getTeam(), matchFromDB);
                        associateTournamentTeamUsersToMatch(tournamentTeam1, matchFromDB);
                        for (long pkOfCreatorTeamMembers : tournamentTeam1.getPksOfTeamMembersPlaying()) {
                            if (!matchFromDB.getPksOfCreatorTeamMembersPlaying().contains(pkOfCreatorTeamMembers)) {
                                matchFromDB.getPksOfCreatorTeamMembersPlaying().add(pkOfCreatorTeamMembers);
                            }
                        }
                        matchFromDB.setPkOfTeamThatCreatedMatch(tournamentTeam1.getTeam().getPk());
                        matchFromDB.getMatchInfo().setMatchStatus(MatchStatusEnum.PENDING);
                    } else if (player_1_ID == 0 && player_2_ID != 0) {
                        log.info("PLAYER 1 IS NULL AND PLAYER 2 IS NOT NULL----------------------------------");
                        tournamentTeam2 = challongeParticipantRepo.getParticipantResponseById(player_2_ID).getTournamentTeam();
                        matchService.associateTeamToMatch(tournamentTeam2.getTeam(), matchFromDB);
                        associateTournamentTeamUsersToMatch(tournamentTeam2, matchFromDB);
                        for (long pkOfAcceptorTeamMembers : tournamentTeam2.getPksOfTeamMembersPlaying()) {
                            if (!matchFromDB.getPksOfAcceptorTeamMembersPlaying().contains(pkOfAcceptorTeamMembers)) {
                                matchFromDB.getPksOfAcceptorTeamMembersPlaying().add(pkOfAcceptorTeamMembers);
                            }
                        }
                        matchFromDB.setPkOfTeamThatAcceptedMatch(tournamentTeam2.getTeam().getPk());
                        matchFromDB.getMatchInfo().setMatchStatus(MatchStatusEnum.PENDING);
                    }
                }
            } else {
                log.info("ChallongeMatchService - syncMatchWithChallonge() - Match with ID# " + matchResponse.getMatchPojo().getId() + " does not exist in our db.");
            }

        }
    }

    public void reportMatchWinner(Tournament tournament, Match match, TournamentTeam tournamentTeam, boolean isPlayer1Winner) {
        MatchRequest matchRequest = new MatchRequest();
//        ChallongeMatchRequestPojo matchRequestMatchPojo = new ChallongeMatchRequestPojo();
//        matchRequest.setMatch(matchRequestMatchPojo);
        matchRequest.getMatch().setWinner_id(tournamentTeam.getParticipantResponse().getParticipant().getId());
        if (isPlayer1Winner) {
            matchRequest.getMatch().setScores_csv("1-0");
        } else {
            matchRequest.getMatch().setScores_csv("0-1");
        }
        updateMatch(tournament, match, tournamentTeam, matchRequest);
    }

    public void updateMatch(Tournament tournament, Match match, TournamentTeam tournamentTeam, MatchRequest matchRequest) {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + "/matches/" + match.getMatchResponse().getMatchPojo().getId() + ".json");
        String requestBody = gson.toJson(matchRequest, MatchRequest.class);
        log.info("################# UPDATE MATCH REQUEST: " + requestBody);
        String responseBody = HttpClient.makePutRestCall(url, requestBody);
        if (responseBody != null) {
            log.info("RESPONSE BODY OF UPDATE: " + responseBody);
            MatchResponse matchResponse = gson.fromJson(responseBody, MatchResponse.class);
            MatchResponse matchResponseFromDB = challongeMatchRepository.getMatchResponseById(matchResponse.getMatchPojo().getId());
            if (matchResponseFromDB != null) {
                matchResponseFromDB.setMatchPojo(matchResponse.getMatchPojo());
                challongeMatchRepository.updateChallongeMatch(matchResponseFromDB);
                MatchResponse matchResponseToUpdate = getNextMatchByCurrentMatchResponseId(matchResponse.getMatchPojo().getId());
                if (matchResponseToUpdate != null) {
                    Match nextMatchToUpdate = matchResponseToUpdate.getMatch();
                    syncSingleMatch(nextMatchToUpdate, tournament);
                }
                //createOrSyncAllActiveMatches(tournament);
            } else {
                log.info("ChallongeMatchService - syncMatchWithChallonge() - Match with ID# " + matchResponse.getMatchPojo().getId() + " does not exist in our db.");
            }
        }
    }

    public void reopenMatch(Match match, Tournament tournament) {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + "/matches/" + match.getMatchResponse().getMatchPojo().getId() + "/reopen.json");
        String requestBody = "";
        String responseBody = HttpClient.makePostRestCall(url, requestBody);
    }

    public void associateMatchToChallongeMatch(Match m, MatchResponse mr) {
        m.setMatchResponse(mr);
        mr.setMatch(m);

    }

}
