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
public enum RoleEnum {
     ROLE_USER(0, "ROLE_USER"),
     ROLE_REFEREE(1,"ROLE_REFEREE"),
     ROLE_ADMIN(2, "ROLE_ADMIN");

    private final int id;
    private final String desc;

    private RoleEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getRoleEnumId() {
        return id;
    }

    public String getRoleEnumDesc() {
        return desc;
    }

    public static RoleEnum getRoleEnumById(int id) {
        for (RoleEnum rel : RoleEnum.values()) {
            if (rel.getRoleEnumId()== id) {
                return rel;
            }
        }
        return null;
    }
}
