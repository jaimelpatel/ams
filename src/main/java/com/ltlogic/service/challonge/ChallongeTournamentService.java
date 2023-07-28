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
import com.ltlogic.constants.TournamentStatusEnum;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.challonge.TournamentRequest;
import com.ltlogic.db.entity.challonge.TournamentResponse;
import com.ltlogic.db.repository.challonge.ChallongeTournamentRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jaimel
 */
@Service
@Transactional
public class ChallongeTournamentService {

    @Autowired
    ChallongeTournamentRepository challongeTournamentRepo;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChallongeTournamentService.class);

    public void syncAllChallongeTournaments() {
        Map<String, String> map = new HashMap<>();
        map.put("state", "in_progress");//returns pending=pending, in_progress=underway, ended=complete request=response----including all archived tournaments
        HttpUrl url = HttpClient.httpUrlBuilderWithQuery("tournaments.json", map);
        String responseBody = HttpClient.makeGetRestCall(url);
        if (responseBody != null) {
            System.out.println("RESPONSE-------------: " + responseBody);
            JSONArray jsonArray = new JSONArray(responseBody);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String jsonString = jsonObject.toString();
                System.out.println("TOURNAMENT " + i + ": " + jsonString);
                TournamentResponse tournamentResponseFromGet = gson.fromJson(jsonString, TournamentResponse.class);
                TournamentResponse tournamentResponseFromDB = challongeTournamentRepo.getTournamentResponseById(tournamentResponseFromGet.getTournamentPojo().getId());
                if (tournamentResponseFromDB != null) {
                    tournamentResponseFromDB.setTournamentPojo(tournamentResponseFromGet.getTournamentPojo());
                    challongeTournamentRepo.updateChallongeTournament(tournamentResponseFromDB);
                } else {
                    log.info("ChallongeTournamentService - syncAllTournaments() - Tournament with ID# " + tournamentResponseFromGet.getTournamentPojo().getId() + " does not exist in our db.");
                }
            }
        }
        else{
            log.error("Error while syncAllChallongeTournaments() for all tournaments in state: " + "in_progress");
        }
    }

    public void syncSingleChallongeTournament(Tournament tournament) {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + ".json");
        String responseBody = HttpClient.makeGetRestCall(url);
        if (responseBody != null) {
            TournamentResponse tournamentResponseFromGet = gson.fromJson(responseBody, TournamentResponse.class);
            TournamentResponse tournamentResponseFromDB = challongeTournamentRepo.getTournamentResponseById(tournament.getTournamentResponse().getTournamentPojo().getId());
            if (tournamentResponseFromDB != null) {
                tournamentResponseFromDB.setTournamentPojo(tournamentResponseFromGet.getTournamentPojo());
                challongeTournamentRepo.updateChallongeTournament(tournamentResponseFromDB);
            } else {
                log.info("ChallongeTournamentService - syncSingleChallongeTournament() - Tournament with ID# " + tournamentResponseFromGet.getTournamentPojo().getId() + " does not exist in our db.");
            }
        }
    }
    
    public void finalizeChallongeTournament(Tournament tournament){
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + "/finalize.json");
        String requestBody = "";
        String responseBody = HttpClient.makePostRestCall(url, requestBody);
        if (responseBody != null) {
            try {
                TournamentResponse tournamentResponse = gson.fromJson(responseBody, TournamentResponse.class);
                TournamentResponse tournamentResponseFromDB = challongeTournamentRepo.getTournamentResponseById(tournamentResponse.getTournamentPojo().getId());
                tournamentResponseFromDB.setTournamentPojo(tournamentResponse.getTournamentPojo());
                challongeTournamentRepo.updateChallongeTournament(tournamentResponseFromDB);
            } catch (Exception ex) {
                log.error("Exception in start challonge tournament: " + ex.getMessage());
                throw new RuntimeException(ex);
            }
            log.info("ChallongeTournamentService - finalizeTournament() - Tournament with ID# " + tournament.getTournamentResponse().getTournamentPojo().getId() + " has been finalized.");
        }
    }

    public boolean createChallongeTournament(Tournament tournament) {
        TournamentRequest tournamentRequest = createChallongeTournamentRequestObject(tournament);
        String request = gson.toJson(tournamentRequest, TournamentRequest.class);
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments.json");
        String responseBody = HttpClient.makePostRestCall(url, request);
        if (responseBody != null) {
            TournamentResponse tournamentResponse = gson.fromJson(responseBody, TournamentResponse.class);
            associateChallongeTournamentToTournament(tournamentResponse, tournament);
            challongeTournamentRepo.persistChallongeTournament(tournamentResponse);
            log.info("ChallongeTournamentService - createChallongeTournament() - Tournament with ID# " + tournament.getTournamentResponse().getTournamentPojo().getId() + " has been created.");
            return true;
        } else {
            tournament.getTournamentInfo().setTournamentStatus(TournamentStatusEnum.CANCELLED);
            return false;
        } 
    }

    private TournamentRequest createChallongeTournamentRequestObject(Tournament tournament) {
        String uuid = UUID.randomUUID().toString();
        String uuidWithoutDash = uuid.replaceAll("-", "");
        TournamentRequest tr = new TournamentRequest();
        tr.setName("Tournament_" + DateTimeUtil.getDefaultLocalDateTimeNow() + "_" + tournament.getTournamentId());
        tr.setUrl(uuidWithoutDash);
        tr.setPrivate(Boolean.FALSE);
        tr.setShow_rounds(Boolean.TRUE);
        tr.setTournament_type("single elimination");
        tr.setAllow_participant_match_reporting(Boolean.FALSE);
        tr.setAccept_attachments(Boolean.TRUE);
        tr.setQuick_advance(Boolean.TRUE);
        return tr;
    }

    //can update through fe
    public void updateTournament(Tournament tournament) {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + ".json");
        String requestBody = "";
        String responseBody = HttpClient.makePutRestCall(url, requestBody);
    }

    public void deleteChallongeTournament(Tournament tournament) {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + ".json");
        String responseBody = HttpClient.makeDeleteRestCall(url);
        if (responseBody != null) {
            log.info("ChallongeTournamentService - deleteTournament() - Tournament with ID# " + tournament.getTournamentResponse().getTournamentPojo().getId() + " has been deleted on challonge.");
            log.info("Response body after deleting challonge tournament: " + responseBody);
        }
    }

    public void startChallongeTournament(Tournament tournament) {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + "/start.json");
        String requestBody = "";
        String responseBody = HttpClient.makePostRestCall(url, requestBody);
        if (responseBody != null) {
            try {
                TournamentResponse tournamentResponse = gson.fromJson(responseBody, TournamentResponse.class);
                TournamentResponse tournamentResponseFromDB = challongeTournamentRepo.getTournamentResponseById(tournamentResponse.getTournamentPojo().getId());
                tournamentResponseFromDB.setTournamentPojo(tournamentResponse.getTournamentPojo());
                challongeTournamentRepo.updateChallongeTournament(tournamentResponseFromDB);
            } catch (Exception ex) {
                log.error("Exception in start challonge tournament: " + ex.getMessage());
                throw new RuntimeException(ex);
            }
            log.info("ChallongeTournamentService - startTournament() - Tournament with ID# " + tournament.getTournamentResponse().getTournamentPojo().getId() + " has started on challonge.");
        }
    }

    public void resetChallongeTournament(Tournament tournament) {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + "/reset.json");
        String requestBody = "";
        String responseBody = HttpClient.makePostRestCall(url, requestBody);
        if (responseBody != null) {
            log.info("ChallongeTournamentService - finalizeTournament() - Tournament with ID# " + tournament.getTournamentResponse().getTournamentPojo().getId() + " has been reset.");
        }
    }

    private void associateChallongeTournamentToTournament(TournamentResponse tournamentResponse, Tournament tournament) {
        tournamentResponse.setTournament(tournament);
        tournament.setTournamentResponse(tournamentResponse);
    }
}
