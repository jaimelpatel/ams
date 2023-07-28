/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.constants;

/**
 *
 * @author Bishistha
 */
public enum CloudMediaType {
    
    IMAGE(0, "image"),
    VIDEO(1, "video");

    private final int id;
    private final String desc;

    private CloudMediaType(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getCloudMediaType() {
        return id;
    }

    public String getCloudMediaTypeDesc() {
        return desc;
    }

    public static CloudMediaType getCloudMediaTypeById(int id) {
        for (CloudMediaType rel : CloudMediaType.values()) {
            if (rel.getCloudMediaType()== id) {
                return rel;
            }
        }
        return null;
    }
}
