/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;


import com.ltlogic.db.entity.User;
import com.ltlogic.service.core.EmailService;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 *
 * @author raymond
 */

@Service
public class EmailIWS {
    
    @Autowired
    private EmailService emailService;
    
    public void resendVerificationEmail(String email, HttpServletRequest request) {
        if(email != null && request != null) {
            emailService.resendVerificationEmail(email, request);
        }
    }
    
    public void sendPasswordResetEmail(String email, HttpServletRequest request) throws UnsupportedEncodingException {
        if(email != null && request != null) {
            emailService.sendPasswordResetEmail(email, request);
        }
    }
    
    public void sendForgottenUsernameEmail(String email, HttpServletRequest request) throws UnsupportedEncodingException {
        if(email != null && request != null) {
            emailService.sendForgottenUsernameEmail(email, request);
        }
    }
}
