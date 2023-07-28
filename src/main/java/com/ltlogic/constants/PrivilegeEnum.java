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
public enum PrivilegeEnum {
   USER_PRIVILEGE(0, "USER_PRIVILEGE"),
   REFEREE_PRIVILEGE(1,"REFEREE_PRIVILEGE"),
   ADMIN_PRIVILEGE(2, "ADMIN_PRIVILEGE");

    private final int id;
    private final String desc;

    private PrivilegeEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getPrivilegeEnumId() {
        return id;
    }

    public String getPrivilegeEnumDesc() {
        return desc;
    }

    public static PrivilegeEnum getPrivilegeEnumById(int id) {
        for (PrivilegeEnum rel : PrivilegeEnum.values()) {
            if (rel.getPrivilegeEnumId()== id) {
                return rel;
            }
        }
        return null;
    }
}
