/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.configs.WebSecurityConfig;
import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.constants.TokenVerificationEnum;
import org.springframework.context.MessageSource;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.RoleRepository;
import com.ltlogic.pojo.RegistrationPojo;
import com.ltlogic.applicationevent.event.OnRegistrationCompleteEvent;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.service.core.SecurityServiceImpl;
import com.ltlogic.service.core.UserService;
import com.ltlogic.service.springsecurity.SecurityContextAccessorImpl;
import com.ltlogic.service.springsecurity.UserValidator;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.io.UnsupportedEncodingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 *
 * @author God
 */
@Controller
public class RegistrationController {

    @Autowired
    UserService userService;
    
    @Autowired
    UserIWS userIWS;

    @Autowired
    SecurityServiceImpl securityService;

    @Autowired
    private MessageSource messages;
    
    @Autowired
    UserValidator userValidator;

    @Autowired
    WebSecurityConfig webSecurityConfig;
    
    @Autowired
    RoleRepository roleRepo;
    
    @Autowired
    ApplicationEventPublisher eventPublisher;

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registration(@ModelAttribute("registrationPojo") RegistrationPojo registrationPojo, BindingResult bindingResult, ModelMap model) {
        SecurityContextAccessorImpl securityContextAccessor = webSecurityConfig.securityContextAccessorImpl();
        if (securityContextAccessor.isCurrentAuthenticationAnonymous()) {
            model.addAttribute("timeZoneEnumList", TimeZoneEnum.values());
            return "registration";
        } else {
            model.addAttribute("timeZoneEnumList", TimeZoneEnum.values());
            return "redirect:/";
        }
    }
    @Async
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registration(@Valid @ModelAttribute("registrationPojo") RegistrationPojo registrationPojo, BindingResult bindingResult, HttpServletRequest request, ModelMap model, RedirectAttributes redirectAttributes) {
        long startTimeTotal = System.currentTimeMillis();
        
        userValidator.registrationValidation(registrationPojo, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationError", true);
//            model.addAttribute("error", errorMessage);
            model.addAttribute("un", registrationPojo.getUsername());
            model.addAttribute("em", registrationPojo.getEmail());
            model.addAttribute("timeZoneEnumList", TimeZoneEnum.values());
            return "registration";
        }
       User user = userIWS.registerUser(registrationPojo);
       eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), getAppUrl(request)));
       securityService.autologin(user.getUsername(), registrationPojo.getPassword());
       user.setLastLoggedIn(DateTimeUtil.getDefaultLocalDateTimeNow());
       model.addAttribute("user", user);
       model.addAttribute("un", "");

       redirectAttributes.addFlashAttribute("message", "You have successfully created an account. Thank you!");
       return "redirect:/";
    }
    
    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public String confirmRegistration(Locale locale, Model model, @RequestParam("token") String token,  RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        LOG.info("Token Value: "+token);
        TokenVerificationEnum result = userIWS.validateVerificationToken(token);
        User user = new User();
        if (result == TokenVerificationEnum.TOKEN_VALID) {
            user = userIWS.getUserFromVerificationToken(token);
            LOG.info(user.getUsername());
            redirectAttributes.addFlashAttribute("message", "Your account has been verified.");
            return "redirect:/login?lang=" + locale.getLanguage();
        }

        model.addAttribute("expired", result == TokenVerificationEnum.TOKEN_EXPIRED);
        model.addAttribute("token", token);
        model.addAttribute("user", user);
        redirectAttributes.addFlashAttribute("messageFailure", "Your verification token has expired.");
        return "redirect:/badUser.html?lang=" + locale.getLanguage();
    }

    
     private String getAppUrl(HttpServletRequest request) {
        String appURL = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        LOG.info("ApplicationURL: " + appURL + " XXXXXXXXXXXXXX");
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
