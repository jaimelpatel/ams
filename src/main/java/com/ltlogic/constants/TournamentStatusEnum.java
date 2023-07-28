/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.constants;

/**
 * 
 * @author Hoang
 */
public enum TournamentStatusEnum {

        
    PENDING(0, "Pending"),
    ACTIVE(1, "Active"),
    ENDED(2, "Ended"),
    CANCELLED(3, "Cancelled");
    
    
    private final int id;
    private final String desc;
    
    private TournamentStatusEnum(int id, String desc){
        this.id = id;
        this.desc = desc;
    }

    public int getTournamentStatusId() {
        return id;
    }

    public String getTournamentStatusDesc() {
        return desc;
    }
    
    public static TournamentStatusEnum getTournamentStatusEnumById(int id){
        for(TournamentStatusEnum status: TournamentStatusEnum.values()){
            if(status.getTournamentStatusId() == id)
                return status;
        }
        return null;
    }
    
    
}
