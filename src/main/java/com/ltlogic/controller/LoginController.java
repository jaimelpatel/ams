/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;

import com.ltlogic.DateTimeUtil;
import com.ltlogic.configs.WebSecurityConfig;
import com.ltlogic.db.entity.User;
import com.ltlogic.pojo.LoginDetailsPojo;
import com.ltlogic.service.core.SecurityServiceImpl;
import com.ltlogic.service.core.UserService;
import com.ltlogic.service.springsecurity.SecurityContextAccessorImpl;
import com.ltlogic.service.springsecurity.UserValidator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author jaimel
 */
@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    SecurityServiceImpl securityService;

    @Autowired
    UserValidator loginValidator;

    @Autowired
    WebSecurityConfig webSecurityConfig;

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    private boolean isAnonymous(){
        SecurityContextAccessorImpl securityContextAccessor = webSecurityConfig.securityContextAccessorImpl();
        return securityContextAccessor.isCurrentAuthenticationAnonymous();
    }
    
//    @RequestMapping(value = "/login", method = RequestMethod.GET)
//    public String login() {
//        if (isAnonymous()) {
//            return "login";
//        } else {
//            return "redirect:/";
//        }
//    }
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        if (isAnonymous()) {
            return "login";
        } else {
            return "index";
        }
    }
    
    //not being used
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@Valid LoginDetailsPojo loginDetailsPojo, ModelMap model) {
        System.out.println("asddasadsdasadsadsadssd");
        boolean isLoggedIn = true;
        try{
            securityService.autologin(loginDetailsPojo.getUsername(), loginDetailsPojo.getPassword());
        }catch(NullPointerException | AuthenticationException ex ){
            isLoggedIn = false;
        }
        if (isLoggedIn) {
            User user = userService.findByUsername(loginDetailsPojo.getUsername());
            user.setLastLoggedIn(DateTimeUtil.getDefaultLocalDateTimeNow());
            model.addAttribute("user", user);
            return "redirect:/";
        } else {
            model.addAttribute("loginError", true);
            return "login";
        }
    }

    @RequestMapping(value = "/loginerror", method = RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("failureMessage", "Incorrect username/password.");
        model.addAttribute("loginError", true);
        return "login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("IN LOGOT-------------------");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            System.out.println("IN LOGOT111111111111111111111111111");
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/";//You can redirect wherever you want, but generally it's a good practice to show login screen again.
    }
}
