/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity.challonge;

import com.ltlogic.pojo.challonge.ChallongeMatchRequestPojo;
import javax.persistence.Embedded;

/**
 * 
 * @author Hoang
 */
public class MatchRequest {
    
    @Embedded
    ChallongeMatchRequestPojo match = new ChallongeMatchRequestPojo();

    public ChallongeMatchRequestPojo getMatch() {
        return match;
    }

    public void setMatch(ChallongeMatchRequestPojo match) {
        this.match = match;
    }
    

    
    
}
