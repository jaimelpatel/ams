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
public enum MatchStatusEnum {
    
    WAITING_ON_FIRST_ACCEPT(0, "WAITING ON FIRST ACCEPT"),//waiting for match creating team to accept first before posting match publicly or sending to provate team for cash matches
    PENDING(1, "PENDING"),//only 1 team is in the match, no one else has accepted yet
    WAITING_ON_SECOND_ACCEPT(2, "WAITING ON SECOND ACCEPT"),//when 2 teams are in the match but waiting on accepting team to all accept, if they dont all accept in 10 minutes, match should go back to pending
    READY_TO_PLAY(3, "READY TO PLAY"), //both teams accepted and waiting to play
    ACTIVE(4, "ACTIVE"),//match has started
    ENDED(5, "ENDED"),//match has ended because both teams have reported
    EXPIRED(6, "EXPIRED"),
    CANCELLED(7, "CANCELLED"),
    DISPUTED(8, "DISPUTED");
    
    
    private final int id;
    private final String desc;
    
    private MatchStatusEnum(int id, String desc){
        this.id = id;
        this.desc = desc;
    }

    public int getMatchStatusId() {
        return id;
    }

    public String getMatchStatusDesc() {
        return desc;
    }
    
    public static MatchStatusEnum getMatchStatusEnumById(int id){
        for(MatchStatusEnum status: MatchStatusEnum.values()){
            if(status.getMatchStatusId() == id)
                return status;
        }
        return null;
    }
    
}
