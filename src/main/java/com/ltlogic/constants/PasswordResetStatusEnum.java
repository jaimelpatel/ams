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
public enum PasswordResetStatusEnum {
    PASSSWORD_MATCH(0, "Password Match"),
    PASSWORD_MISMATCH(1, "Password Mismatch");
    
    private final int id;
    private final String desc;

    private PasswordResetStatusEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getMatchTypeEnumId() {
        return id;
    }

    public String getMatchTypeEnumDesc() {
        return desc;
    }

    public static MatchTypeEnum getMatchTypeEnumById(int id) {
        for (MatchTypeEnum rel : MatchTypeEnum.values()) {
            if (rel.getMatchTypeEnumId() == id) {
                return rel;
            }
        }
        return null;
    }
}
