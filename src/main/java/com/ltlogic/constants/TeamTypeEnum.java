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
public enum TeamTypeEnum {
    XP(0, "XP Team"),
    CASH(1, "Cashout Team");
    
    private final int id;
    private final String desc;

    private TeamTypeEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getTeamTypeEnumId() {
        return id;
    }

    public String getTeamTypeEnumDesc() {
        return desc;
    }

    public static TeamTypeEnum getTeamTypeEnumById(int id) {
        for (TeamTypeEnum rel : TeamTypeEnum.values()) {
            if (rel.getTeamTypeEnumId()== id) {
                return rel;
            }
        }
        return null;
    }
}
