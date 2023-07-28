/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.constants.MatchStatusEnum;
import com.ltlogic.constants.MatchTypeEnum;
import com.ltlogic.db.entity.Conversation;
import com.ltlogic.db.entity.Dispute;
import com.ltlogic.db.entity.Match;
import com.ltlogic.db.entity.Team;
import com.ltlogic.db.entity.User;
import com.ltlogic.fe.models.form.ConversationForm;
import com.ltlogic.fe.models.form.DisputeForm;
import com.ltlogic.fe.models.form.DisputeFormValidator;
import com.ltlogic.iws.ConversationIWS;
import com.ltlogic.iws.DisputeIWS;
import com.ltlogic.iws.MatchIWS;
import com.ltlogic.iws.TeamIWS;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.service.springsecurity.ImageValidator;
import com.ltlogic.web.exception.CustomErrorException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author raymond
 */
@Controller
public class DisputeController {

    @Autowired
    UserIWS userIWS;

    @Autowired
    TeamIWS teamIWS;

    @Autowired
    DisputeIWS disputeIWS;

    @Autowired
    MatchIWS matchIWS;
    
    @Autowired
    DisputeFormValidator disputeFormValidator;
    
    @Autowired
    ConversationIWS conversationIWS;
    
    @Autowired
    ImageValidator imageValidator;

    @RequestMapping(value = "/{username}/disputes/my-disputes", method = RequestMethod.GET)
    public String getDisputeByUser(@PathVariable String username, ModelMap model, Principal p) {

        if (p == null || (username != null && username.equalsIgnoreCase(p.getName()) == false)) {
            throw new CustomErrorException();
        }

        User user = userIWS.getUserByUsername(p.getName());
        List<Dispute> disputes = new ArrayList<>();

        disputes = disputeIWS.getAllDisputesForAUserPk(user.getPk());

        model.addAttribute("disputes", disputes);
        return "disputes/my-disputes";
    }

    @RequestMapping(value = "/disputes/{disputePk}", method = RequestMethod.GET)
    public String getDisputeDetails(@PathVariable long disputePk, ModelMap model, Principal p) {

        User user = userIWS.getUserByUsername(p.getName());

        List<Dispute> myDisputes = disputeIWS.getAllDisputesForAUserPk(user.getPk());
        Dispute dispute = disputeIWS.findDisputeByPk(disputePk);

        if (dispute == null) {
            throw new CustomErrorException();
        }

        //not part of the team
        if(dispute.getTeam().getMembers().contains(user) == false) {
            if(p.getName().equalsIgnoreCase("xpillowpants") || p.getName().equalsIgnoreCase("xjimrobs") || p.getName().equalsIgnoreCase("wtfhesoneshot")){
               
            }else{
                 throw new CustomErrorException(403, "You do not have access to this webpage.");
            }
        }
        
        Match match = dispute.getMatch();
        
        List<Conversation> conversations = conversationIWS.getAllConversationForTeamAndDispute(dispute.getTeam().getPk(), dispute.getPk());
        model.addAttribute("conversations", conversations);

        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
            model.addAttribute("isTournamentMatch", true);
        } else {
            model.addAttribute("isTournamentMatch", false);
        }
        List<Team> teamsInMatch = teamIWS.findTeamsByMatchPk(match.getPk());

        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }

        if (match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT && match.getUsersInMatch().contains(user) && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_SECOND_ACCEPT || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT)) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }

        if (match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ACTIVE || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.DISPUTED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.READY_TO_PLAY || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ENDED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.EXPIRED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.CANCELLED)) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("match", dispute.getMatch());
        model.addAttribute("dispute", dispute);
        return "disputes/dispute-details";
    }
    
    
    @RequestMapping(value = "/disputes/{disputePk}/create-message", method = RequestMethod.POST)
    public String createDisputeMessage(@Valid @ModelAttribute("conversationForm") ConversationForm conversationForm, 
            BindingResult bindingResult, @PathVariable long disputePk, ModelMap model, Principal p) {
       
        Dispute dispute = disputeIWS.findDisputeByPk(disputePk);
        Team team = dispute.getTeam();
        boolean isTeam = true;
        if(p.getName().equalsIgnoreCase("xpillowpants") || p.getName().equalsIgnoreCase("xjimrobs") || p.getName().equalsIgnoreCase("wtfhesoneshot")){
            isTeam = false;
        }
        String message = null;
        if(conversationForm != null)
            message = conversationForm.getMessage();
        
        System.out.println(message);
        disputeIWS.createDisputeConversation(team, dispute, message, isTeam);
        
        return "redirect:/disputes/" + disputePk;
    }
    
    @RequestMapping(value = "/disputes/{disputePk}/edit", method = RequestMethod.GET)
    public String updateDisputeForm(@ModelAttribute("editDisputeForm") DisputeForm editDisputeForm, @PathVariable long disputePk, ModelMap model, Principal p) {

        User user = userIWS.getUserByUsername(p.getName());
        
        Dispute dispute = disputeIWS.findDisputeByPk(disputePk);
        
        if(dispute == null) {
            throw new CustomErrorException();
        }
        
        if(dispute.getUser() != user) {
            throw new CustomErrorException(403, "You do not have the right to access. Please go back");
        }

        Match match = dispute.getMatch();

        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
            model.addAttribute("isTournamentMatch", true);
        } else {
            model.addAttribute("isTournamentMatch", false);
        }
        List<Team> teamsInMatch = teamIWS.findTeamsByMatchPk(match.getPk());

        if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }

        if (match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT && match.getUsersInMatch().contains(user) && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_SECOND_ACCEPT || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT)) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }

        if (match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ACTIVE || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.DISPUTED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.READY_TO_PLAY || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ENDED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.EXPIRED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.CANCELLED)) {
            if (teamsInMatch.size() == 0) {
                model.addAttribute("creatorTeam", "TBD");
                model.addAttribute("acceptorTeam", "TBD");
            } else if (teamsInMatch.size() == 1) {
                if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", "TBD");
                } else {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                }
            } else {
                model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
            }
        }
        
        editDisputeForm.setReason(dispute.getReasonForDispute());
        if(dispute.getLinks() != null && dispute.getLinks().isEmpty() == false) {
            if(dispute.getLinks().size() >= 1) {
                editDisputeForm.setUrl1(dispute.getLinks().get(0));
            }

            if(dispute.getLinks().size() >= 2) {
                editDisputeForm.setUrl2(dispute.getLinks().get(1));
            }

            if(dispute.getLinks().size() >= 3) {
                editDisputeForm.setUrl3(dispute.getLinks().get(2));
            }

            if(dispute.getLinks().size() >= 4) {
                editDisputeForm.setUrl4(dispute.getLinks().get(3));
            }
        }

        model.addAttribute("match", dispute.getMatch());
        model.addAttribute("dispute", dispute);
        
        return "disputes/edit_dispute";
    }
    
    @RequestMapping(value = "/disputes/{disputePk}/edit", method = RequestMethod.POST)
    public String updateDispute(@Valid @ModelAttribute("editDisputeForm") DisputeForm editDisputeForm, BindingResult bindingResult, @PathVariable long disputePk, ModelMap model, Principal p) {

        User user = userIWS.getUserByUsername(p.getName());
        
        Dispute dispute = disputeIWS.findDisputeByPk(disputePk);
        
        if(dispute == null) {
            throw new CustomErrorException();
        }
        
        if(dispute.getUser() != user) {
            throw new CustomErrorException(403, "You do not have the right to access. Please go back");
        }
        
        Match match = dispute.getMatch();
        
        disputeFormValidator.validate(editDisputeForm, bindingResult);
        
        if(bindingResult.hasErrors()) {
            if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
                model.addAttribute("isTournamentMatch", true);
            } else {
                model.addAttribute("isTournamentMatch", false);
            }
            List<Team> teamsInMatch = teamIWS.findTeamsByMatchPk(match.getPk());

            if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
                if (teamsInMatch.size() == 0) {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", "TBD");
                } else if (teamsInMatch.size() == 1) {
                    if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                        model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                        model.addAttribute("acceptorTeam", "TBD");
                    } else {
                        model.addAttribute("creatorTeam", "TBD");
                        model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    }
                } else {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
                }
            }

            if (match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT && match.getUsersInMatch().contains(user) && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_SECOND_ACCEPT || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT)) {
                if (teamsInMatch.size() == 0) {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", "TBD");
                } else if (teamsInMatch.size() == 1) {
                    if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                        model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                        model.addAttribute("acceptorTeam", "TBD");
                    } else {
                        model.addAttribute("creatorTeam", "TBD");
                        model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    }
                } else {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
                }
            }

            if (match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ACTIVE || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.DISPUTED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.READY_TO_PLAY || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ENDED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.EXPIRED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.CANCELLED)) {
                if (teamsInMatch.size() == 0) {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", "TBD");
                } else if (teamsInMatch.size() == 1) {
                    if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                        model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                        model.addAttribute("acceptorTeam", "TBD");
                    } else {
                        model.addAttribute("creatorTeam", "TBD");
                        model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    }
                } else {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
                }
            }


            model.addAttribute("match", dispute.getMatch());
            model.addAttribute("dispute", dispute);

            return "disputes/edit_dispute";
        }
        
        try {

            List<String> links = new ArrayList<>();
            
            if (editDisputeForm.getUrl1() != null && !editDisputeForm.getUrl1().equals("")) {
                links.add(editDisputeForm.getUrl1());
            }
            if (editDisputeForm.getUrl2() != null && !editDisputeForm.getUrl2().equals("")) {
                links.add(editDisputeForm.getUrl2());
            }
            if (editDisputeForm.getUrl3() != null && !editDisputeForm.getUrl3().equals("")) {
                links.add(editDisputeForm.getUrl3());
            }
            if (editDisputeForm.getUrl4() != null && !editDisputeForm.getUrl4().equals("")) {
                links.add(editDisputeForm.getUrl4());
            }

            disputeIWS.updateDisputeByPk(dispute.getPk(), editDisputeForm.getReason(), links);
            
            return "redirect:/disputes/" + dispute.getPk();
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            
            if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
                model.addAttribute("isTournamentMatch", true);
            } else {
                model.addAttribute("isTournamentMatch", false);
            }
            List<Team> teamsInMatch = teamIWS.findTeamsByMatchPk(match.getPk());

            if (match.getMatchInfo().getMatchType() == MatchTypeEnum.TOURNAMENT) {
                if (teamsInMatch.size() == 0) {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", "TBD");
                } else if (teamsInMatch.size() == 1) {
                    if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                        model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                        model.addAttribute("acceptorTeam", "TBD");
                    } else {
                        model.addAttribute("creatorTeam", "TBD");
                        model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    }
                } else {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
                }
            }

            if (match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT && match.getUsersInMatch().contains(user) && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.PENDING || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_SECOND_ACCEPT || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.WAITING_ON_FIRST_ACCEPT)) {
                if (teamsInMatch.size() == 0) {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", "TBD");
                } else if (teamsInMatch.size() == 1) {
                    if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                        model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                        model.addAttribute("acceptorTeam", "TBD");
                    } else {
                        model.addAttribute("creatorTeam", "TBD");
                        model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    }
                } else {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
                }
            }

            if (match.getMatchInfo().getMatchType() != MatchTypeEnum.TOURNAMENT && (match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ACTIVE || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.DISPUTED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.READY_TO_PLAY || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.ENDED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.EXPIRED || match.getMatchInfo().getMatchStatus() == MatchStatusEnum.CANCELLED)) {
                if (teamsInMatch.size() == 0) {
                    model.addAttribute("creatorTeam", "TBD");
                    model.addAttribute("acceptorTeam", "TBD");
                } else if (teamsInMatch.size() == 1) {
                    if (teamsInMatch.get(0).getPk() == match.getPkOfTeamThatCreatedMatch()) {
                        model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                        model.addAttribute("acceptorTeam", "TBD");
                    } else {
                        model.addAttribute("creatorTeam", "TBD");
                        model.addAttribute("acceptorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    }
                } else {
                    model.addAttribute("creatorTeam", teamsInMatch.get(0).getTeamPojo().getTeamName());
                    model.addAttribute("acceptorTeam", teamsInMatch.get(1).getTeamPojo().getTeamName());
                }
            }


            model.addAttribute("match", dispute.getMatch());
            model.addAttribute("dispute", dispute);

            return "disputes/edit_dispute";
        }
    }
    

    @RequestMapping(value = "/disputes/{disputePk}/upload-proof", method = RequestMethod.GET)
    public String uploadProof(@PathVariable long disputePk, ModelMap model, Principal p) {

        User user = userIWS.getUserByUsername(p.getName());
        List<Dispute> myDisputes = disputeIWS.getAllDisputesForAUserPk(user.getPk());
        Dispute dispute = disputeIWS.findDisputeByPk(disputePk);

        if (dispute == null || myDisputes.contains(dispute) == false) {
            throw new CustomErrorException();
        }

        model.addAttribute("dispute", dispute);
        return "disputes/upload-proof";
    }

    @RequestMapping(value = "/disputes/{disputePk}/upload-proof", method = RequestMethod.POST)
    public String saveUploadProof(@PathVariable long disputePk, @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes, ModelMap model, Principal p) {

        User user = userIWS.getUserByUsername(p.getName());
        List<Dispute> myDisputes = disputeIWS.getAllDisputesForAUserPk(user.getPk());
        Dispute dispute = disputeIWS.findDisputeByPk(disputePk);

        if (dispute == null || myDisputes.contains(dispute) == false) {
            throw new CustomErrorException();
        }
        try {
        imageValidator.validateContentTypeAndDimensionForImage2(file);

            //validate file here, not implemented yet
            byte[] bytes = file.getBytes();
            String displayUrl = disputeIWS.uploadDisputePicture(user.getPk(), disputePk, file.getContentType(), bytes);
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "' as dispute proof.");

            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/disputes/" + disputePk;
        } catch (Exception ex) {
            model.addAttribute("dispute", dispute);
            redirectAttributes.addFlashAttribute("messageFailure",
                    ex.getMessage());
            return "redirect:/disputes/" + disputePk;
        }

    }

}
