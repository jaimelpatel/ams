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
public enum StateOrProvinceEnum {
    AL(0,"Alabama"),
    MT(1,"Montana"),
    AK(2,"Alaska"),
    NE(3,"Nebraska"),
    AZ(4,"Arizona"),
    NV(5,"Nevada"),
    AR(6,"Arkansas"),
    NH(7,"NewHampshire"),
    CA(8,"California"),
    NJ(9,"NewJersey"),
    CO(10,"Colorado"),
    NM(11,"NewMexico"),
    CT(12,"Connecticut"),
    NY(13,"NewYork"),
    DE(14,"Delaware"),
    NC(15,"NorthCarolina"),
    FL(16,"Florida"),
    ND(17,"NorthDakota"),
    GA(18,"Georgia"),
    OH(19,"Ohio"),
    HI(20,"Hawaii"),
    OK(21,"Oklahoma"),
    ID(22,"Idaho"),
    OR(23,"Oregon"),
    IL(24,"Illinois"),
    PA(25,"Pennsylvania"),
    IN(26,"Indiana"),
    RI(27,"RhodeIsland"),
    IA(28,"Iowa"),
    SC(29,"SouthCarolina"),
    KS(30,"Kansas"),
    SD(31,"SouthDakota"),
    KY(32,"Kentucky"),
    TN(33,"Tennessee"),
    LA(34,"Louisiana"),
    TX(35,"Texas"),
    ME(36,"Maine"),
    UT(37,"Utah"),
    MD(38,"Maryland"),
    VT(39,"Vermont"),
    MA(40,"Massachusetts"),
    VA(41,"Virginia"),
    MI(42,"Michigan"),
    WA(43,"Washington"),
    MN(44,"Minnesota"),
    WV(45,"WestVirginia"),
    MS(46,"Mississippi"),
    WI(47,"Wisconsin"),
    MO(48,"Missouri"),
    WY(49,"Wyoming");

    private final int id;
    private final String desc;

    private StateOrProvinceEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getStateEnumId() {
        return id;
    }

    public String getStateEnumDesc() {
        return desc;
    }

    public static StateOrProvinceEnum getStateEnumById(int id) {
        for (StateOrProvinceEnum rel : StateOrProvinceEnum.values()) {
            if (rel.getStateEnumId()== id) {
                return rel;
            }
        }
        return null;
    }
}
