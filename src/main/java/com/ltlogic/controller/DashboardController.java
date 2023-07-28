/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.TournamentStatusEnum;
import com.ltlogic.db.entity.Dispute;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Tournament;
import com.ltlogic.db.entity.User;
import com.ltlogic.fe.helpers.Paginator;
import com.ltlogic.iws.DisputeIWS;
import com.ltlogic.iws.MatchIWS;
import com.ltlogic.iws.MatchInviteIWS;
import com.ltlogic.iws.TournamentServiceIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.web.exception.CustomErrorException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author raymond
 */
@Controller
public class DashboardController {
    
    @Autowired
    UserIWS userIWS;
    
    @Autowired
    MatchIWS matchIWS;
    
    @Autowired
    TournamentServiceIWS tournamentIWS;
    
    @Autowired
    DisputeIWS disputeIWS;
    
    @Autowired
    Paginator paginator;
    
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String index(ModelMap model, HttpServletRequest request, Principal p,
            @RequestParam(value = "d_page", required = false) Integer page) {

        Integer currentPage = 1;
        
        if(page != null) {
            currentPage = page;
        }
        
        User user = userIWS.getUserByUsername(p.getName());
        //need active matches??
        List<Match> matches = matchIWS.getMatchesByUserPkAndMatchNotKilledStatus(user.getPk());

        List<Tournament> tournaments = tournamentIWS.getAllPendingAndActiveTournamentsForUser(user.getPk());
        
        List<Dispute> disputes = disputeIWS.getAllOpenMatchDisputesByUserPkAndPageNumber(user.getPk(), currentPage-1);
        
        Integer totalOpenMatchDisputes = disputeIWS.getTotalOpenMatchDisputesByUserPk(user.getPk());
        
        paginator.setPagination(null,currentPage, totalOpenMatchDisputes, model);
        
        model.addAttribute("user", user);
        model.addAttribute("tournaments", tournaments);
        model.addAttribute("matches", matches);
        model.addAttribute("disputes", disputes);
        
        return "dashboard/index";
    }
}
