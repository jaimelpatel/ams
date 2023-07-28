/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.applicationevent.event.OnRegistrationCompleteEvent;
import com.ltlogic.configs.WebSecurityConfig;
import com.ltlogic.constants.SupportTicketType;
import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.db.entity.User;
import com.ltlogic.fe.models.form.SupportTicketForm;
import com.ltlogic.fe.models.form.TeamForm;
import com.ltlogic.pojo.RegistrationPojo;
import com.ltlogic.pojo.SupportTicketPojo;
import com.ltlogic.service.SupportTicketService;
import com.ltlogic.service.core.SecurityServiceImpl;
import com.ltlogic.service.core.UserService;
import com.ltlogic.service.springsecurity.SecurityContextAccessorImpl;
import com.ltlogic.service.springsecurity.UserValidator;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
public class SupportController {

    @Autowired
    UserService userService;

    @Autowired
    SecurityServiceImpl securityService;

    @Autowired
    UserValidator loginValidator;

    @Autowired
    WebSecurityConfig webSecurityConfig;
    
    @Autowired
    SupportTicketService supportTicketService;

    private static final Logger LOG = LoggerFactory.getLogger(SupportController.class);

    private boolean isAnonymous(){
        SecurityContextAccessorImpl securityContextAccessor = webSecurityConfig.securityContextAccessorImpl();
        return securityContextAccessor.isCurrentAuthenticationAnonymous();
    }
    
    @RequestMapping(value = "/support", method = RequestMethod.GET)
    public String support(@ModelAttribute("supportTicketForm") SupportTicketForm supportTicketForm, BindingResult bindingResult, 
            ModelMap model, Principal p) {
        
        User user = null;
        
        if(p != null) {
            user = userService.findByUsername(p.getName());
            supportTicketForm.setEmail(user.getEmail());
        }
        
        model.addAttribute("user", user);
        return "support";
    }
    
    @RequestMapping(value = "/support", method = RequestMethod.POST)
    public String submitTicket(@Valid @ModelAttribute("supportTicketForm") SupportTicketForm supportTicketForm, BindingResult bindingResult, 
            ModelMap model, Principal p, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {

        User user = null;
        
        if(p != null) {
            user = userService.findByUsername(p.getName());
        }
        
        if(bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "support";
        }
        
        if(user == null) {
            user = new User();
            user.setEmail(supportTicketForm.getEmail());
        }
        
        SupportTicketPojo ticket = new SupportTicketPojo();
        ticket.setEmail(supportTicketForm.getEmail());
        ticket.setMessage(supportTicketForm.getMessage());
        SupportTicketType ticketType = null;
        
        if(supportTicketForm.getTicketType() != null) {
            ticketType = SupportTicketType.getSupportTicketTypeById(supportTicketForm.getTicketType());
        }
    
        ticket.setSupportTicketType(ticketType);
        System.out.println();
        supportTicketService.submitSupportTicket(user, ticket);
        
        redirectAttributes.addFlashAttribute("message", "Your ticket has been submitted.");
        return "redirect:/support";
    }
}
