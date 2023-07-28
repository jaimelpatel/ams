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
public enum MatchRoundCountPerMapEnum {
    FOUR_ROUNDS(0, "4 Rounds"), 
    SIX_ROUNDS(1, "6 Rounds");
    
    private final int id;
    private final String desc;

    private MatchRoundCountPerMapEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getMatchRoundCountPerMapEnumId() {
        return id;
    }

    public String getMatchRoundCountPerMapEnumDesc() {
        return desc;
    }

    public static MatchRoundCountPerMapEnum getMatchRoundCountPerMapEnumById(int id) {
        for (MatchRoundCountPerMapEnum rel : MatchRoundCountPerMapEnum.values()) {
            if (rel.getMatchRoundCountPerMapEnumId()== id) {
                return rel;
            }
        }
        return null;
    }
}
