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
public enum CloudMediaCategory {
    
    USER_IMG(0, "USER_IMG"),
    TEAM_IMG(1, "TEAM_IMG"),
    DISPUTE_IMG(2, "DISPUTE_IMG"),
    DISPUTE_VID(3, "DISPUTE_VID");

    private final int id;
    private final String desc;

    private CloudMediaCategory(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getCloudMediaCategory() {
        return id;
    }

    public String getCloudMediaCategoryDesc() {
        return desc;
    }

    public static CloudMediaCategory getCloudMediaCategoryById(int id) {
        for (CloudMediaCategory rel : CloudMediaCategory.values()) {
            if (rel.getCloudMediaCategory()== id) {
                return rel;
            }
        }
        return null;
    }
}
