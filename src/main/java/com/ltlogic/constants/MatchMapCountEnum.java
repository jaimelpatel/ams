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
public enum MatchMapCountEnum {
    BEST_OF_1(0, "Best of 1"), 
    BEST_OF_3(1, "Best of 3"), 
    BEST_OF_5(2, "Best of 5");
    
    private final int id;
    private final String desc;

    private MatchMapCountEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getMatchMapCountEnumId() {
        return id;
    }

    public String getMatchMapCountEnumDesc() {
        return desc;
    }

    public static MatchMapCountEnum getMatchMapCountEnumById(int id) {
        for (MatchMapCountEnum rel : MatchMapCountEnum.values()) {
            if (rel.getMatchMapCountEnumId()== id) {
                return rel;
            }
        }
        return null;
    }
    
}
