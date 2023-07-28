/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller.admin;

import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.db.entity.Dispute;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.fe.models.form.AdminNotificationForm;
import com.ltlogic.fe.models.form.CancelMatchForm;
import com.ltlogic.fe.models.form.ReportMatchForm;
import com.ltlogic.fe.models.form.TournamentForm;
import com.ltlogic.iws.DisputeIWS;
import com.ltlogic.iws.MatchIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.pojo.TournamentPojo;
import com.ltlogic.service.core.MatchService;
import com.ltlogic.service.core.TeamService;
import com.ltlogic.web.exception.CustomErrorException;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author jaimel
 */
@Controller
public class AdminReportMatchController {

    @Autowired
    private UserIWS userIWS;

    @Autowired
    private MatchIWS matchIWS;

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private DisputeIWS disputeIWS;

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage(ModelMap model, Principal p) {
        if (p == null) {
            throw new CustomErrorException();
        }

        if (p.getName().equalsIgnoreCase("xpillowpants") || p.getName().equalsIgnoreCase("xjimrobs") || p.getName().equalsIgnoreCase("wtfhesoneshot")) {
            User user = userIWS.getUserByUsername(p.getName());
            model.addAttribute("user", user);
        } else {
            throw new CustomErrorException();
        }
        return "admin/admin";
    }

    @RequestMapping(value = "/admin/match/disputes", method = RequestMethod.GET)
    public String createReportMatchForm(ModelMap model, Principal p) {
        if (p == null) {
            throw new CustomErrorException();
        }

        if (p.getName().equalsIgnoreCase("xpillowpants") || p.getName().equalsIgnoreCase("xjimrobs") || p.getName().equalsIgnoreCase("wtfhesoneshot")) {
            User user = userIWS.getUserByUsername(p.getName());
            model.addAttribute("user", user);
        } else {
            throw new CustomErrorException();
        }

        List<Dispute> openTournamentDisputes = disputeIWS.getAllOpenMatchDisputesByMatchType(MatchTypeEnum.TOURNAMENT);
        List<Dispute> openWagerDisputes = disputeIWS.getAllOpenMatchDisputesByMatchType(MatchTypeEnum.WAGER);
        List<Dispute> openXPDisputes = disputeIWS.getAllOpenMatchDisputesByMatchType(MatchTypeEnum.XP);

        model.addAttribute("openTournamentDisputes", openTournamentDisputes);
        model.addAttribute("openWagerDisputes", openWagerDisputes);
        model.addAttribute("openXPDisputes", openXPDisputes);
        return "admin/matches/disputes";
    }

    @RequestMapping(value = "/admin/match/report", method = RequestMethod.GET)
    public String createReportMatchForm(@ModelAttribute("reportMatchForm") ReportMatchForm reportMatchForm, @ModelAttribute("cancelMatchForm") CancelMatchForm cancelMatchForm,
            @ModelAttribute("adminNotificationMatchForm") AdminNotificationForm adminNotificationForm, BindingResult bindingResult, ModelMap model, Principal p) {
        if (p == null) {
            throw new CustomErrorException();
        }

        if (p.getName().equalsIgnoreCase("xpillowpants") || p.getName().equalsIgnoreCase("xjimrobs") || p.getName().equalsIgnoreCase("wtfhesoneshot")) {
//            User user = userIWS.getUserByUsername(p.getName());
//            model.addAttribute("user", user);
        } else {
            throw new CustomErrorException();
        }

        return "admin/matches/report-matches";
    }

    @RequestMapping(value = "/admin/match/report", method = RequestMethod.POST)
    public String saveReportMatch(@Valid @ModelAttribute("reportMatchForm") ReportMatchForm reportMatchForm, @ModelAttribute("cancelMatchForm") CancelMatchForm cancelMatchForm,
            @ModelAttribute("adminNotificationMatchForm") AdminNotificationForm adminNotificationForm, BindingResult bindingResult, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {

        if (p == null) {
            throw new CustomErrorException();
        }

        if (p.getName().equalsIgnoreCase("xpillowpants") || p.getName().equalsIgnoreCase("xjimrobs") || p.getName().equalsIgnoreCase("wtfhesoneshot")) {
//            User user = userIWS.getUserByUsername(p.getName());
//            model.addAttribute("user", user);
        } else {
            throw new CustomErrorException();
        }

        String username = p.getName();
        User user = userIWS.getUserByUsername(username);

        Match m = matchService.findMatchById(reportMatchForm.getMatchID());
        if (m == null) {
            model.addAttribute("error", "No Match was found for the given ID.");
            return "admin/matches/report-matches";
        }

        Team t = teamService.findTeamById(reportMatchForm.getTeamID());
        if (t == null) {
            model.addAttribute("error", "No Team was found for the given ID.");
            return "admin/matches/report-matches";
        }

        if (!m.getTeamsInMatch().contains(t)) {
            model.addAttribute("error", "This team is not in this match.");
            return "admin/matches/report-matches";
        }

        try {
            matchIWS.reportMatchScore(user, m, t, false, true);
            redirectAttributes.addFlashAttribute("message", "Match with ID #" + m.getMatchId() + " has been reported. Team with ID #" + t.getTeamId() + " has lost the match.");
            return "redirect:/admin/match/report";
        } catch (Exception ex) {
            model.addAttribute("user", user);
            model.addAttribute("error", ex.getMessage());
            ex.printStackTrace();
            return "admin/matches/report-matches";
        }
    }

    @RequestMapping(value = "/admin/match/cancel", method = RequestMethod.GET)
    public String cancelMatchForm(@ModelAttribute("cancelMatchForm") CancelMatchForm cancelMatchForm,
            BindingResult bindingResult, ModelMap model, Principal p) {
        if (p == null) {
            throw new CustomErrorException();
        }

        if (p.getName().equalsIgnoreCase("xpillowpants") || p.getName().equalsIgnoreCase("xjimrobs") || p.getName().equalsIgnoreCase("wtfhesoneshot")) {
//            User user = userIWS.getUserByUsername(p.getName());
//            model.addAttribute("user", user);
        } else {
            throw new CustomErrorException();
        }
//        model.addAttribute("cancelMatchForm", new CancelMatchForm());

        return "admin/matches/report-matches";
    }

    @RequestMapping(value = "/admin/match/cancel", method = RequestMethod.POST)
    public String cancelMatch(@Valid @ModelAttribute("cancelMatchForm") CancelMatchForm cancelMatchForm, @ModelAttribute("reportMatchForm") ReportMatchForm reportMatchForm,
            @ModelAttribute("adminNotificationMatchForm") AdminNotificationForm adminNotificationForm, BindingResult bindingResult, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {

//        model.addAttribute("cancelMatchForm", new CancelMatchForm());
        if (p == null) {
            throw new CustomErrorException();
        }

        if (p.getName().equalsIgnoreCase("xpillowpants") || p.getName().equalsIgnoreCase("xjimrobs") || p.getName().equalsIgnoreCase("wtfhesoneshot")) {
//            User user = userIWS.getUserByUsername(p.getName());
//            model.addAttribute("user", user);
        } else {
            throw new CustomErrorException();
        }

        String username = p.getName();
        User user = userIWS.getUserByUsername(username);

        Match m = matchService.findMatchById(cancelMatchForm.getMatchID());
        if (m == null) {
            model.addAttribute("error", "No Match was found for the given ID.");
            return "admin/matches/report-matches";
        }

        try {
            matchService.cancelMatchByAdmin(m);
            redirectAttributes.addFlashAttribute("message", "Match with ID #" + m.getMatchId() + " has been cancelled.");
            return "redirect:/admin/match/report";
        } catch (Exception ex) {
            model.addAttribute("user", user);
            model.addAttribute("error", ex.getMessage());
            ex.printStackTrace();
            return "admin/matches/report-matches";
        }
    }

    @RequestMapping(value = "/admin/match/notification", method = RequestMethod.GET)
    public String adminNotificationMatch(@ModelAttribute("adminNotificationMatchForm") AdminNotificationForm adminNotificationForm,
            BindingResult bindingResult, ModelMap model, Principal p) {
        if (p == null) {
            throw new CustomErrorException();
        }

        if (p.getName().equalsIgnoreCase("xpillowpants") || p.getName().equalsIgnoreCase("xjimrobs") || p.getName().equalsIgnoreCase("wtfhesoneshot")) {
//            User user = userIWS.getUserByUsername(p.getName());
//            model.addAttribute("user", user);
        } else {
            throw new CustomErrorException();
        }
//        model.addAttribute("cancelMatchForm", new CancelMatchForm());

        return "admin/matches/report-matches";
    }

    @RequestMapping(value = "/admin/match/notification", method = RequestMethod.POST)
    public String adminNotificationMatch(@Valid @ModelAttribute("adminNotificationMatchForm") AdminNotificationForm adminNotificationForm, @ModelAttribute("cancelMatchForm") CancelMatchForm cancelMatchForm, @ModelAttribute("reportMatchForm") ReportMatchForm reportMatchForm,
            BindingResult bindingResult, ModelMap model, Principal p, RedirectAttributes redirectAttributes) {

//        model.addAttribute("cancelMatchForm", new CancelMatchForm());
        if (p == null) {
            throw new CustomErrorException();
        }

        if (p.getName().equalsIgnoreCase("xpillowpants") || p.getName().equalsIgnoreCase("xjimrobs") || p.getName().equalsIgnoreCase("wtfhesoneshot")) {
//            User user = userIWS.getUserByUsername(p.getName());
//            model.addAttribute("user", user);
        } else {
            throw new CustomErrorException();
        }

        String username = p.getName();
        User user = userIWS.getUserByUsername(username);

        Match m = matchService.findMatchById(adminNotificationForm.getMatchID());
        if (m == null) {
            model.addAttribute("error", "No Match was found for the given ID.");
            return "admin/matches/report-matches";
        }

        try {
            matchService.sendAdminNotificationToAllUsersInMatch(m, adminNotificationForm.getAdminName());
            redirectAttributes.addFlashAttribute("message", "A notification has been sent to all members of Match ID #" + m.getMatchId() + " that their admin is " + adminNotificationForm.getAdminName());
            return "redirect:/admin/match/report";
        } catch (Exception ex) {
            model.addAttribute("user", user);
            model.addAttribute("error", ex.getMessage());
            ex.printStackTrace();
            return "admin/matches/report-matches";
        }
    }

}
