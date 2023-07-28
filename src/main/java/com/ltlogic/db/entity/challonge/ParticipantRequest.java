/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.db.entity.challonge;

/**
 * 
 * @author Hoang
 */
public class ParticipantRequest {
    
    private String name;
    private String challonge_username;
    private String email;
    private Integer seed;
    private String misc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChallonge_username() {
        return challonge_username;
    }

    public void setChallonge_username(String challonge_username) {
        this.challonge_username = challonge_username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }
    
    
}
