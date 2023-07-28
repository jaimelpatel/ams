/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.constants;

/**
 *
 * @author jaimel
 */
public enum TeamSizeEnum {
    SINGLES(0, "Singles (1v1)"), //1v1
    DOUBLES(1, "Doubles (2v2)"), //2v2
    TEAM(2, "Team (3v3/4v4)"); //3v3, 4v4, etc.
    
    private final int id;
    private final String desc;

    private TeamSizeEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getTeamSizeEnumId() {
        return id;
    }

    public String getTeamSizeEnumDesc() {
        return desc;
    }

    public static TeamSizeEnum getTeamSizeEnumById(int id) {
        for (TeamSizeEnum rel : TeamSizeEnum.values()) {
            if (rel.getTeamSizeEnumId()== id) {
                return rel;
            }
        }
        return null;
    }
}
