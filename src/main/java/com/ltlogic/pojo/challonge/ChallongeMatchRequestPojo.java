/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.pojo.challonge;

import javax.persistence.Embeddable;

/**
 * 
 * @author Hoang
 */
@Embeddable
public class ChallongeMatchRequestPojo {
    
    private long winner_id;
    
    private String scores_csv;

    public String getScores_csv() {
        return scores_csv;
    }

    public void setScores_csv(String scores_csv) {
        this.scores_csv = scores_csv;
    }
    
    public long getWinner_id() {
        return winner_id;
    }

    public void setWinner_id(long winner_id) {
        this.winner_id = winner_id;
    }

}
