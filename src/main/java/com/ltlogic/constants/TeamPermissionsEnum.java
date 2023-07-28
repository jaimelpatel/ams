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
public enum TeamPermissionsEnum {
    ALL_LEADER_PERMISSIONS(0, "Leader Permissions"); // has all permissions below
//    INVITE_PLAYERS(1, "Invite Players"),
//    KICK_PLAYERS(2, "Kick Players"),
//    EDIT_TEAM_INFO(3, "Edit Team Information"),
//    CREATE_MATCH(4, "Schedule Match"),
//    ACCEPT_MATCH(5, "Accept Match"),
//    REPORT_MATCH_OUTCOME(6, "Report Match");
    
    private final int id;
    private final String desc;

    private TeamPermissionsEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getTeamPermissionsEnumId() {
        return id;
    }

    public String getTeamPermissionsEnumDesc() {
        return desc;
    }

    public static TeamPermissionsEnum getTeamPermissionsEnumById(int id) {
        for (TeamPermissionsEnum rel : TeamPermissionsEnum.values()) {
            if (rel.getTeamPermissionsEnumId()== id) {
                return rel;
            }
        }
        return null;
    }
}
