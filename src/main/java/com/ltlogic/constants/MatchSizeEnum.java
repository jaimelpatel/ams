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
public enum MatchSizeEnum {
    SINGLES(0, "Singles (1v1)"), //1v1
    DOUBLES(1, "Doubles (2v2)"), //2v2
    THREES(3, "Threes (3v3)"), //3v3
    FOURS(4, "Fours (4v4)"); //4v4
    
    private final int id;
    private final String desc;

    private MatchSizeEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getMatchSizeEnumId() {
        return id;
    }

    public String getMatchSizeEnumDesc() {
        return desc;
    }

    public static MatchSizeEnum getMatchSizeEnumById(int id) {
        for (MatchSizeEnum rel : MatchSizeEnum.values()) {
            if (rel.getMatchSizeEnumId()== id) {
                return rel;
            }
        }
        return null;
    }
    
}
