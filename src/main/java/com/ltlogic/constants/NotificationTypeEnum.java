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
public enum NotificationTypeEnum {
    CREATE_TEAM(0, "Create Team"),
    JOINED_TEAM(1, "Joined Team"),
    JOINED_WAGER(2, "Joined Cash Match"),
    JOINED_XP(3, "Joined XP Match"),
    JOINED_TOURNAMENT(4, "Joined Tournament"),
    TEAM_LEFT_WAGER(5, "Someone declined wager invite"),//or everyone didnt accept within 15 minutes so team is removed from match
    TEAM_LEFT_TOURNAMENT(6, "Someone declined tournament invite."),
    CANCELLED_MATCH(7, "Cancel request went through for match."),
    UPCOMING_MATCH(8, "Upcoming Match"),//need to add this still
    UPCOMING_TOURNAMENT(9, "Upcoming tournament"),//need to add this still
    CREATE_WAGER_MATCH(10, "Create Cash Match"),
    CREATE_XP_MATCH(11, "Create XP Match"),
    MATCH_STARTED(12, "Match Started"),
    TOURNAMENT_STARTED(13, "Tournament Started"),
    CANCELLED_TOURNAMENT(14, "Tournament Cancelled"),
    EXPIRED_MATCH(15, "Match Expired"),
    DISPUTED_MATCH_OUTCOME(16, "Disputed Match Outcome"),
    DISPUTED_MATCH(17, "Disputed Match"),
    DISPUTED_MATCH_BY_OTHER_TEAM(17, "Disputed Match By Other Team"),
    MATCH_WIN(18, "Match Win"),
    MATCH_LOSS(19, "Match Loss"),
    TOURNAMENT_WIN(20, "Tournament Win"),
    TEAM_INVITE(21, "Team Invite"),
    MATCH_INVITE(22, "Match Invite"),
    TOURNAMENT_INVITE(23, "Tournament Invite");


    private final int id;
    private final String desc;

    private NotificationTypeEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getNotificationTypeEnumId() {
        return id;
    }

    public String getNotificationTypeEnumDesc() {
        return desc;
    }

    public static NotificationTypeEnum getNotificationTypeEnumById(int id) {
        for (NotificationTypeEnum rel : NotificationTypeEnum.values()) {
            if (rel.getNotificationTypeEnumId() == id) {
                return rel;
            }
        }
        return null;
    }
}
