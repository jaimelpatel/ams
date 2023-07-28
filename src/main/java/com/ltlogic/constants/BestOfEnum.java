/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.constants;

/**
 *
 * @author Jaimel
 */
public enum BestOfEnum {
    
    BEST_OF_1(0, "Best of 1"),
    BEST_OF_3(1, "Best of 3"),
    BEST_OF_5(2, "Best of 5");
    
    private final int id;
    private final String desc;
    
    private BestOfEnum(int id, String desc){
        this.id = id;
        this.desc = desc;
    }

    public int getBestOfId() {
        return id;
    }

    public String getBestOfDesc() {
        return desc;
    }
    
    public static BestOfEnum getBestOfEnumById(int id){
        for(BestOfEnum cre: BestOfEnum.values()){
            if(cre.getBestOfId() == id)
                return cre;
        }
        return null;
    }
}
