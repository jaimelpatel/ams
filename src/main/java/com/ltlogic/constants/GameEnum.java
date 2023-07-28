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
public enum GameEnum {
    
    COD_MWR(0, "Call of Duty: Modern Warfare Remastered"),
    COD_IW(1, "Call of Duty: Infinite Warfare"),
    COD_WW2(2, "Call of Duty: World War II");

    private final int id;
    private final String desc;

    private GameEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getGameEnumId() {
        return id;
    }

    public String getGameEnumDesc() {
        return desc;
    }

    public static GameEnum getGameEnumById(int id) {
        for (GameEnum rel : GameEnum.values()) {
            if (rel.getGameEnumId()== id) {
                return rel;
            }
        }
        return null;
    }
}
