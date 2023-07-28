/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.db.entity.MatchCancellationRequest;
import com.ltlogic.db.entity.User;
import com.ltlogic.iws.MatchCancellationRequestIWS;
import com.ltlogic.iws.MatchIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.web.exception.CustomErrorException;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author raymond
 */
@Controller
public class MatchCancellationRequestsController {
    
    @Autowired
    MatchCancellationRequestIWS matchCancellationIWS;
    
    @Autowired
    MatchIWS matchIWS;
    
    @Autowired
    UserIWS userIWS;
    
    @RequestMapping(value = "/match-cancellation-request/{pk}/accept", method = RequestMethod.GET)
    public String acceptMatchCancellationRequest(@PathVariable Long pk, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {
        
        User user = userIWS.getUserByUsername(p.getName());
        List<MatchCancellationRequest> requests = matchCancellationIWS.getPendingMatchCancellationRequestForAUser(user.getPk());
        MatchCancellationRequest request = matchCancellationIWS.getMatchCancellationRequestByMatchPk(pk);
        
        if(request == null || requests.contains(request) == false) {
            throw new CustomErrorException();
        }
        
        try {
            matchIWS.acceptCancellationRequest(request.getMatch().getPk(), p.getName());
            redirectAttributes.addFlashAttribute("message", "You have accepted the match cancellation request.");
            return "redirect:/" + p.getName() + "/notifications";
        } catch(Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error processing match cancellation request. " + ex.getMessage());
            return "redirect:/" + p.getName() + "/notifications";
        }
    }
    
    @RequestMapping(value = "/match-cancellation-request/{pk}/decline", method = RequestMethod.GET)
    public String declineMatchCancellationRequest(@PathVariable Long pk, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {
        
        User user = userIWS.getUserByUsername(p.getName());
        List<MatchCancellationRequest> requests = matchCancellationIWS.getPendingMatchCancellationRequestForAUser(user.getPk());
        MatchCancellationRequest request = matchCancellationIWS.getMatchCancellationRequestByMatchPk(pk);
        
        if(request == null || requests.contains(request) == false) {
            throw new CustomErrorException();
        }
        
        try {
            matchIWS.declineCancellationRequest(request.getMatch().getPk(), p.getName());
            redirectAttributes.addFlashAttribute("message", "You have declined the match cancellation request.");
            return "redirect:/" + p.getName() + "/notifications";
        } catch(Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Error processing match cancellation request. " + ex.getMessage());
            return "redirect:/" + p.getName() + "/notifications";
        }
    }
}
