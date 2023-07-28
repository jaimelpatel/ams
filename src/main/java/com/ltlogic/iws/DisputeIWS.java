/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;

import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.db.entity.CloudMedia;
import com.ltlogic.db.entity.Dispute;
import com.ltlogic.db.entity.Team;
import com.ltlogic.service.core.CloudMediaService;
import com.ltlogic.service.core.DisputeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Bishistha
 */
@Service
public class DisputeIWS {

    @Autowired
    private DisputeService disputeService;

    @Autowired
    private CloudMediaService cloudMediaService;

    // called when a user during/after the match wants to dispute the match; FE needs to set a limit on the no. of characters for the reasonForDispute
    public void createDispute(long userPk, long matchPk, long teamPk, String reasonForDispute, List<String> links, boolean isReportMatchDispute) {
        disputeService.createDispute(userPk, matchPk, teamPk, reasonForDispute, links, isReportMatchDispute);
    }
    
    public void updateDisputeByPk(long disputePk, String reason, List<String> links) {
        disputeService.updateDisputeByPk(disputePk, reason, links);
    }
    
    public void createDisputeConversation(Team team, Dispute dispute, String message, boolean isTeam){
        disputeService.createDisputeConversation(team, dispute, message, isTeam);
    }

    public Dispute findDisputeByPk(long pk) {
        return disputeService.findDisputeByPk(pk);
    }

    public Dispute findDisputeByDisputeId(int disputeId) {
        return disputeService.findDisputeByDisputeId(disputeId);
    }

    public List<Dispute> getAllMatchDisputesByMatchPk(long matchPk) {
        return disputeService.getAllMatchDisputesByMatchPk(matchPk);
    }

    public Dispute findDisputeForMatchAndTeam(long matchPk, long teamPk) {
        return disputeService.findDisputeForMatchAndTeam(matchPk, teamPk);
    }

    // upload evidence/picture for a dispute
    public String uploadDisputePicture(long userPk, long disputePk, String contentType, byte[] fileContent) {
        String displayUrl = cloudMediaService.uploadDisputePicture(userPk, disputePk, contentType, fileContent);
        return displayUrl;
    }

    // DISPUTE PAGE for a user to view all disputes; its ordered by createdDateTime to show eariest at the top
    //--> This page should be a tabular format showing dispute info like dispute ID, Match ID, name of the team, user
    public List<Dispute> getAllDisputesForAUserPk(long userPk) {
        List<Dispute> disputes = disputeService.getAllDisputesForAUserPk(userPk);
        return disputes;
    }

    public int getTotalOpenMatchDisputesByUserPk(long userPk) {
        return disputeService.getTotalOpenMatchDisputesByUserPk(userPk);
    }
    
    public List<Dispute> getAllOpenMatchDisputesByUserPkAndPageNumber(long userPk, int pageNumber) {
        return disputeService.getAllOpenMatchDisputesByUserPkAndPageNumber(userPk, pageNumber);
    }
    
    // In DISPUTE PAGE for a user to view all the dispute pictures <Like "View uploaded pictures" kind of button>
    public List<String> getDisputePicturesForDisputePk(long disputePk) throws Exception {
        List<String> displayUrls = disputeService.getDisputePicturesForDispute(disputePk);
        return displayUrls;
    }

    // A button in MATCH HISTORY PAGE to enable if isDisputed in match is true
    //  --> creates a drop down with some dispute info display dispute ID, Match ID, name of the team, user
    public Dispute getDisputeForUserAndMatchPk(long userPk, long matchPk) {
        Dispute dispute = disputeService.getDisputeForUserAndMatchPk(userPk, matchPk);
        return dispute;
    }

    // A button in MATCH HISTORY PAGE after user has the dropdown to view the actual pictures they had submitted
    // use the display urls to show the uploaded contents 
    public List<String> getDisputePicturesForUserAndMatchPk(long userPk, long matchPk) throws Exception {
        List<String> displayUrls = disputeService.getDisputePicturesForUserAndMatchPk(userPk, matchPk);
        return displayUrls;
    }
    
    public List<Dispute> getAllOpenMatchDisputesByMatchType(MatchTypeEnum matchType) {
        return disputeService.getAllOpenMatchDisputesByMatchType(matchType);
    }


}
