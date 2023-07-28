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
public enum MatchTypeEnum {

    XP(0, "XP"),
    WAGER(1, "Wager"),
    TOURNAMENT(2, "Tournament");

    private final int id;
    private final String desc;

    private MatchTypeEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getMatchTypeEnumId() {
        return id;
    }

    public String getMatchTypeEnumDesc() {
        return desc;
    }

    public static MatchTypeEnum getMatchTypeEnumById(int id) {
        for (MatchTypeEnum rel : MatchTypeEnum.values()) {
            if (rel.getMatchTypeEnumId() == id) {
                return rel;
            }
        }
        return null;
    }
}
