/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.controller;


import com.ltlogic.configs.WebSecurityConfig;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.UserRepository;
import com.ltlogic.service.core.EmailService;
import com.ltlogic.service.core.UserService;
import com.ltlogic.web.exception.CustomErrorException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Jaimel
 */
@Controller
public class HomeController {
    
    @Autowired
    WebSecurityConfig webSecurityConfig;
    
    @Autowired
    EmailService emailService;
    
    @Autowired
    UserService userService;
    
    @Autowired
    UserRepository userRepo;
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(ModelMap model, Principal p) {
//        model.addAttribute("notificationNumber", 105);
        return "index";
    }
    
    @RequestMapping(value = "/resendVerificationEmail", method = RequestMethod.POST)
    public String resendVerificationEmail(ModelMap model, HttpServletRequest request, Principal p) {
        User user = userRepo.findByUsername(p.getName());
        String email = user.getEmail();
        emailService.resendVerificationEmail(email, request);
        return "index";
    }
    
    @RequestMapping(value = "/sendEmail", method = RequestMethod.POST)
    public String sendEmail(ModelMap model, Principal p) {
        emailService.sendEmailTest();
        return "index";
    }

    @RequestMapping(value = "/sendPasswordResetEmail", method = RequestMethod.POST)
    public String sendPasswordResetEmail(ModelMap model, HttpServletRequest request, Principal p) throws UnsupportedEncodingException {
        User user = userRepo.findByUsername(p.getName());
        emailService.sendPasswordResetEmail(user.getEmail(), request);
        return "index";
    }

}
