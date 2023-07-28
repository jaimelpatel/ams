/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.applicationevent.listener;

/**
 * 
 * @author Hoang
 */
import com.ltlogic.db.entity.User;
import com.ltlogic.applicationevent.event.OnRegistrationCompleteEvent;
import com.ltlogic.service.core.EmailService;
import com.ltlogic.service.core.UserService;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    
    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

    
    @Autowired
    private EmailService emailService;

    @Autowired
    private Environment env;
    
   private static final Logger LOG = LoggerFactory.getLogger(RegistrationListener.class);


    // API

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        try {
            confirmRegistration(event);
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(RegistrationListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) throws UnsupportedEncodingException {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);

        //final SimpleMailMessage email = constructEmailMessage(event, user, token);
        //mailSender.send(email);
        System.out.println("###########" +user.getvToken().getToken() + "#################");
        emailService.sendVerificationEmail(event, user, token);        
    }

    

//    private final SimpleMailMessage constructEmailMessage(OnRegistrationCompleteEvent event, User user, String token) {
//        final String recipientAddress = user.getEmail();
//        final String subject = "Registration Confirmation";
//        final String confirmationUrl = event.getAppUrl() + "/registrationConfirm.html?token=" + token;
//        final String message = messages.getMessage("message.regSucc", null, event.getLocale());
//        final SimpleMailMessage email = new SimpleMailMessage();
//        email.setTo(recipientAddress);
//        email.setSubject(subject);
//        email.setText(message + " \r\n" + confirmationUrl);
//        email.setFrom(env.getProperty("support.email"));
//        return email;
//    }
}

