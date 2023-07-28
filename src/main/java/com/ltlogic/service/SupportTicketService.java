/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service;

import com.google.common.base.Strings;
import com.ltlogic.db.entity.User;
import com.ltlogic.pojo.SupportTicketPojo;
import com.ltlogic.service.common.CommonService;
import com.ltlogic.service.core.EmailService;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Bishistha
 */
@Service
public class SupportTicketService {

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private CommonService commonService;
    
    private final static String SUPPORT_MESSAGE = "This issue has been reported successfully. A member from our team will get back to you soon. Thank you for your patience. ";

    public void submitSupportTicket(User user, SupportTicketPojo supportTicketPojo) throws UnsupportedEncodingException {
        if (user != null && supportTicketPojo != null) {
            String emailAddressSubmitted = supportTicketPojo.getEmail();
            String email = Strings.isNullOrEmpty(emailAddressSubmitted) ? user.getEmail() : emailAddressSubmitted;
            supportTicketPojo.setEmail(email);
            int ticketId = commonService.generateRandomId();
            supportTicketPojo.setSubject(supportTicketPojo.getSupportTicketType().getSupportTicketTypeDesc() + ": Ticket ID #" + ticketId);
            String messageBody = supportTicketPojo.getMessage();
            supportTicketPojo.setMessage(messageBody + "\n \n" + SUPPORT_MESSAGE);
            emailService.sendSupportTicketEmail(supportTicketPojo);
        }
    }
}
