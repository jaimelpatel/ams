/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.constants;

/**
 *
 * @author Bishistha
 */
public enum SupportTicketType {
    
    Payment_Issues(0, "Payment Issues"),
    Account_Issues(1, "Account Issues"),
    Report_User(2, "Report User"),
    Game_Rules(3, "Game Rules"),
    Other_Issues(4, "Other Issues");

    private final int id;
    private final String desc;

    private SupportTicketType(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getSupportTicketType() {
        return id;
    }

    public String getSupportTicketTypeDesc() {
        return desc;
    }

    public static SupportTicketType getSupportTicketTypeById(int id) {
        for (SupportTicketType rel : SupportTicketType.values()) {
            if (rel.getSupportTicketType() == id) {
                return rel;
            }
        }
        return null;
    }
}
