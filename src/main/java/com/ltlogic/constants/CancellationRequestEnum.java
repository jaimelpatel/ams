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
public enum CancellationRequestEnum {
    
    PENDING(0, "PENDING"),
    ACCEPTED(1, "ACCEPTED"),
    DECLINED(2, "DECLINED"),
    EXPIRED(3, "EXPIRED");
    
    
    private final int id;
    private final String desc;
    
    private CancellationRequestEnum(int id, String desc){
        this.id = id;
        this.desc = desc;
    }

    public int getCancellationRequestId() {
        return id;
    }

    public String getCancellationRequestDesc() {
        return desc;
    }
    
    public static CancellationRequestEnum getCancellationRequestEnumById(int id){
        for(CancellationRequestEnum cre: CancellationRequestEnum.values()){
            if(cre.getCancellationRequestId() == id)
                return cre;
        }
        return null;
    }
}
