/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.challonge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ltlogic.HttpClient;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.TournamentTeam;
import com.ltlogic.db.entity.challonge.ParticipantRequest;
import com.ltlogic.db.entity.challonge.ParticipantResponse;
import com.ltlogic.db.entity.challonge.TournamentResponse;
import com.ltlogic.db.repository.MatchRepository;
import com.ltlogic.db.repository.TournamentTeamRepository;
import com.ltlogic.db.repository.challonge.ChallongeParticipantRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jaimel
 */
@Service
@Transactional
public class ChallongeParticipantService {

    @Autowired
    ChallongeParticipantRepository challongeParticipantRepo;

    @Autowired
    TournamentTeamRepository tournamentTeamRepo;

    final GsonBuilder builder = new GsonBuilder();
    final Gson gson = builder.create();

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ChallongeParticipantService.class);

    public void syncAllParticipantsInTournament(Tournament tournament) {
        HttpUrl url = HttpClient.httpUrlBuilder("/tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + "/participants.json");
        String responseBody = HttpClient.makeGetRestCall(url);
        JSONArray jsonArray = new JSONArray(responseBody);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String jsonString = jsonObject.toString();
            ParticipantResponse participantResponseFromGet = gson.fromJson(jsonString, ParticipantResponse.class);
            ParticipantResponse participantResponseFromDB = challongeParticipantRepo.getParticipantResponseById(participantResponseFromGet.getParticipant().getId());
            if (participantResponseFromDB != null) {
                participantResponseFromDB.setParticipant(participantResponseFromGet.getParticipant());
                challongeParticipantRepo.updateChallongParticipant(participantResponseFromDB);
            } else {
                LOG.info("ChallongeParticipantService - syncAllParticipantsInTournament() - Participant with ID# " + participantResponseFromGet.getParticipant().getId() + " does not exist in our db.");
            }
        }
    }

    public void createParticipantForTournament(Tournament tournament, TournamentTeam tournamentTeam) {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + "/participants.json");
        ParticipantRequest participantRequest = new ParticipantRequest();
        String tournamentTeamName = tournamentTeam.getTeam().getTeamPojo().getTeamName();
        participantRequest.setName(tournamentTeamName);
        String requestBody = gson.toJson(participantRequest, ParticipantRequest.class);

        String responseBody = HttpClient.makePostRestCall(url, requestBody);
        if (responseBody != null) {
            ParticipantResponse participantResponse = gson.fromJson(responseBody, ParticipantResponse.class);
            associateTournamentTeamToParticipant(tournamentTeam, participantResponse);
            challongeParticipantRepo.persistChallongeParticipant(participantResponse);
        }

    }

    public void bulkAddParticipantsToTournament(Tournament tournament, List<TournamentTeam> tournamentTeams) {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + "/participants/bulk_add.json");
        List<ParticipantRequest> requestList = new ArrayList<>();
        for (TournamentTeam t : tournamentTeams) {
            ParticipantRequest request = new ParticipantRequest();
            request.setName(t.getTeam().getTeamPojo().getTeamName());
            requestList.add(request);
        }

        String bulkAddRequest = gson.toJson(requestList, new TypeToken<ArrayList<ParticipantRequest>>() {
        }.getType());
        JSONArray participants = new JSONArray(bulkAddRequest);
        JSONObject request = new JSONObject();
        request.put("participants", participants);
        String responseBody = HttpClient.makePostRestCall(url, request.toString());
        if (responseBody != null) {
            JSONArray jsonArray = new JSONArray(responseBody);
            for (int i = 0; i < jsonArray.length(); i++) {
                ParticipantResponse participantResponse = new ParticipantResponse();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                participantResponse = gson.fromJson(jsonObject.toString(), ParticipantResponse.class);
                TournamentTeam tournamentTeamOfParticipant = tournamentTeamRepo.getTournamentTeamByNameAndTournament(participantResponse.getParticipant().getName(), tournamentTeams.get(0).getTournament());
                associateTournamentTeamToParticipant(tournamentTeamOfParticipant, participantResponse);
                challongeParticipantRepo.persistChallongeParticipant(participantResponse);
            }
        }
    }

    //TournamentTeam gets updated, used to update our challonge Entities
    public void syncSingleParticipant(TournamentTeam tournamentTeam, Tournament tournament) {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + "/participants/" + tournamentTeam.getParticipantResponse().getParticipant().getId() + ".json");
        String responseBody = HttpClient.makeGetRestCall(url);
        if (responseBody != null) {
            ParticipantResponse participantResponse = gson.fromJson(responseBody, ParticipantResponse.class);
            ParticipantResponse participantFromDB = challongeParticipantRepo.getParticipantResponseById(participantResponse.getParticipant().getId());
            if (participantFromDB != null) {
                participantFromDB.setParticipant(participantResponse.getParticipant());
                challongeParticipantRepo.updateChallongParticipant(participantFromDB);
            } else {
                LOG.info("ChallongeParticipantService - getParticipantInTournament() - Participant with ID# " + participantResponse.getParticipant().getId() + " does not exist in our db.");
            }
        }
    }

    public void updateParticipantInTournament(TournamentTeam tournamentTeam, Tournament tournament, ParticipantRequest participantRequest) {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + "/participants/" + tournamentTeam.getParticipantResponse().getParticipant().getId() + ".json");
        String requestBody = gson.toJson(participantRequest, ParticipantRequest.class);
        String responseBody = HttpClient.makePutRestCall(url, requestBody);
    }

    public void deleteParticipantFromTournament(TournamentTeam tournamentTeam, Tournament tournament) {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + "/participants/" + tournamentTeam.getParticipantResponse().getParticipant().getId() + ".json");
        String responseBody = HttpClient.makeDeleteRestCall(url);
    }

    public void randomizeParticipantSeeding(Tournament tournament) {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + tournament.getTournamentResponse().getTournamentPojo().getId() + "/participants/randomize.json");
        String responseBody = HttpClient.makePostRestCall(url, "");
        if(responseBody != null){
            JSONArray jsonArray = new JSONArray(responseBody);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String jsonString = jsonObject.toString();
                ParticipantResponse participantResponseFromGet = gson.fromJson(jsonString, ParticipantResponse.class);
                ParticipantResponse participantResponseFromDB = challongeParticipantRepo.getParticipantResponseById(participantResponseFromGet.getParticipant().getId());
                if (participantResponseFromDB != null) {
                    participantResponseFromDB.setParticipant(participantResponseFromGet.getParticipant());
                    challongeParticipantRepo.updateChallongParticipant(participantResponseFromDB);
                } else {
                    LOG.info("ChallongeParticipantService - randomizeParticipantSeeding() - Participant with ID# " + participantResponseFromGet.getParticipant().getId() + " does not exist in our db.");
                }
            }
        }
    }

    public void associateTournamentTeamToParticipant(TournamentTeam t, ParticipantResponse p) {
        t.setParticipantResponse(p);
        p.setTournamentTeam(t);
    }

}
