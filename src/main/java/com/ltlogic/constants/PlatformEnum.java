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
public enum PlatformEnum {
    
    PC(0, "PC"),
    XBOX360(1, "Xbox 360"),
    XBOXONE(2, "Xbox One"),
    PS3(3, "PlayStation 3"),
    PS4(4, "Playstation 4");

    private final int id;
    private final String desc;

    private PlatformEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getPlatformEnumId() {
        return id;
    }

    public String getPlatformEnumDesc() {
        return desc;
    }

    public static PlatformEnum getPlatformEnumById(int id) {
        for (PlatformEnum rel : PlatformEnum.values()) {
            if (rel.getPlatformEnumId()== id) {
                return rel;
            }
        }
        return null;
    }

}
