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
public enum TeamStatusEnum {
    LIVE(0, "A live team"), 
    DISBANDED(1, "Disbanded team");
    
    private final int id;
    private final String desc;

    private TeamStatusEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getTeamStatusEnumId() {
        return id;
    }

    public String getTeamStatusEnumDesc() {
        return desc;
    }

    public static TeamStatusEnum getTeamStatusEnumById(int id) {
        for (TeamStatusEnum rel : TeamStatusEnum.values()) {
            if (rel.getTeamStatusEnumId()== id) {
                return rel;
            }
        }
        return null;
    }
}
