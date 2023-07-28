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
public enum GameModeEnum {
        /*
    //Call of Duty Match Types
     Team Deathmatch.
     Kill Confirmed.
     Capture the Flag.
     Search and Rescue.
     Momentum.
     Uplink.
     Hardpoint.
     Search and Destroy.
     */
    SearchAndDestroy(0, "Search And Destroy"),
    Variant(1, "Variant");

    private final int id;
    private final String desc;

    private GameModeEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getGameModeEnumId() {
        return id;
    }

    public String getGameModeEnumDesc() {
        return desc;
    }

    public static GameModeEnum getGameModeEnumById(int id) {
        for (GameModeEnum rel : GameModeEnum.values()) {
            if (rel.getGameModeEnumId() == id) {
                return rel;
            }
        }
        return null;
    }
}
