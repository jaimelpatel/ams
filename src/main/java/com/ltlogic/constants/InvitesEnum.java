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
public enum InvitesEnum {
    
    PENDING(0, "PENDING"),
    ACCEPTED(1, "ACCEPTED"),
    DECLINED(2, "DECLINED");
    
    //Maybe we will need this status later 
   // EXPIRED(3, "EXPIRED");
    
    private final int id;
    private final String desc;
    
    private InvitesEnum(int id, String desc){
        this.id = id;
        this.desc = desc;
    }

    public int getInvitesId() {
        return id;
    }

    public String getInvitesDesc() {
        return desc;
    }
    
    public static InvitesEnum getInvitesEnumById(int id){
        for(InvitesEnum inv: InvitesEnum.values()){
            if(inv.getInvitesId() == id)
                return inv;
        }
        return null;
    }
    
}
