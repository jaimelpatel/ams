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
public enum DisputeStatus {

    OPEN(0, "open"),
    RESOLVED(1, "resolved");

    private final int id;
    private final String desc;

    private DisputeStatus(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }
    
    public int getDisputeStatus() {
        return id;
    }

    public String getDisputeStatusDesc() {
        return desc;
    }

    public static DisputeStatus getDisputeStatusById(int id) {
        for (DisputeStatus rel : DisputeStatus.values()) {
            if (rel.getDisputeStatus() == id) {
                return rel;
            }
        }
        return null;
    }
    
    
}
