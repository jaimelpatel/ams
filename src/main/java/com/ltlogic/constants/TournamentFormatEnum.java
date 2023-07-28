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
public enum TournamentFormatEnum {
    
            
    SINGLEELIMINATION(0, "SINGLEELIMINATION"),
    DOUBLEELIMINATION(1, "DOUBLEELIMINATION");

    
    
    private final int id;
    private final String desc;
    
    private TournamentFormatEnum(int id, String desc){
        this.id = id;
        this.desc = desc;
    }

    public int getTournamentFormatId() {
        return id;
    }

    public String getTournamentFormatDesc() {
        return desc;
    }
    
    public static TournamentFormatEnum getTournamentFormatEnumById(int id){
        for(TournamentFormatEnum format: TournamentFormatEnum.values()){
            if(format.getTournamentFormatId() == id)
                return format;
        }
        return null;
    }
    
    
}
