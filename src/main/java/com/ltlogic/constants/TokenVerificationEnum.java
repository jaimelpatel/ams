/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.constants;

/**
 *
 * @author raymond
 */
public enum TokenVerificationEnum {
     TOKEN_INVALID(0, "Invalid Token"),
     TOKEN_EXPIRED(1,"Expired"),
     TOKEN_VALID(2, "valid");

    private final int id;
    private final String desc;

    private TokenVerificationEnum(int id, String desc) {
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
