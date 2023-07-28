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
public enum RegionEnum {
    
    NA(0, "North America"),
    EU(1, "Europe");

    private final int id;
    private final String desc;

    private RegionEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getRegionEnumId() {
        return id;
    }

    public String getRegionEnumDesc() {
        return desc;
    }

    public static RegionEnum getRegionEnumById(int id) {
        for (RegionEnum rel : RegionEnum.values()) {
            if (rel.getRegionEnumId()== id) {
                return rel;
            }
        }
        return null;
    }

}