/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.pojo;

import com.ltlogic.constants.SupportTicketType;

/**
 *
 * @author Bishistha
 */
public class SupportTicketPojo {
    
    private SupportTicketType supportTicketType;
    private String message;
    private String subject;
    private String email;

    public SupportTicketType getSupportTicketType() {
        return supportTicketType;
    }

    public void setSupportTicketType(SupportTicketType supportTicketType) {
        this.supportTicketType = supportTicketType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
