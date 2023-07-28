/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.challonge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltlogic.HttpClient;
import com.ltlogic.db.entity.challonge.AttachmentRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.springframework.stereotype.Service;

/**
 *
 * @author Bishistha
 */
@Service
public class ChallongeAttachmentService {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChallongeAttachmentService.class);

    //POST https://api.challonge.com/v1/tournaments/{tournament}/matches/{match_id}/attachments.{json|xml}
    public void createAttachment(long challongeMatchId, long challongeTournamentId, AttachmentRequest attachmentRequest) throws IOException {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/" + challongeTournamentId + "/matches/" + challongeMatchId + "/attachments.json");
        String requestBody = gson.toJson(attachmentRequest, AttachmentRequest.class);
        String responseBody = HttpClient.makePostRestCall(url, requestBody);
        if (responseBody == null) {
            log.error("FAILED RESPONSE: " + responseBody);
            log.error("Could not update Challonge Match with Challonge Match Id: " + challongeMatchId + ", Challonge Tournament Id: " + challongeTournamentId + " with Request in JSON: " + requestBody);
            throw new RuntimeException("Exception while creating attachments.");
        } else {
            log.info("Response from challonge after updating match attachment with the url and description: " + responseBody);
        }
    }
}
