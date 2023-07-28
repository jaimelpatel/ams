/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.pojo.iw;

import com.ltlogic.constants.GameModeEnum;
import java.util.ArrayList;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author jaimel
 */
@Embeddable
public class IWMatchPojo {
    
    private boolean scoreStreaks;
    
    private boolean overkill;
    
    private ArrayList<String> mapNamesFor1sAnd2s = new ArrayList<>();
    
    private ArrayList<String> mapNamesFor3sAnd4s = new ArrayList<>();
    
    private ArrayList<String> hostNamesInOrder = new ArrayList<>();
    
    private ArrayList<String> mapsToPlayInMatchInOrder = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private GameModeEnum gameModeEnum;

    public IWMatchPojo() {
        this.mapNamesFor1sAnd2s.add("Crusher");
        this.mapNamesFor1sAnd2s.add("Retaliation");
        this.mapNamesFor1sAnd2s.add("Scorch");
        this.mapNamesFor1sAnd2s.add("Throwback");
        this.mapNamesFor1sAnd2s.add("Breakout");
        this.mapNamesFor3sAnd4s.add("Crusher");
        this.mapNamesFor3sAnd4s.add("Retaliation");
        this.mapNamesFor3sAnd4s.add("Scorch");
        this.mapNamesFor3sAnd4s.add("Throwback");
        this.mapNamesFor3sAnd4s.add("Breakout");
    }

    public ArrayList<String> getMapsToPlayInMatchInOrder() {
        return mapsToPlayInMatchInOrder;
    }

    public void setMapsToPlayInMatchInOrder(ArrayList<String> mapsToPlayInMatchInOrder) {
        this.mapsToPlayInMatchInOrder = mapsToPlayInMatchInOrder;
    }

    public ArrayList<String> getHostNamesInOrder() {
        return hostNamesInOrder;
    }

    public void setHostNamesInOrder(ArrayList<String> hostNamesInOrder) {
        this.hostNamesInOrder = hostNamesInOrder;
    }  

    public boolean isScoreStreaks() {
        return scoreStreaks;
    }

    public void setScoreStreaks(boolean scoreStreaks) {
        this.scoreStreaks = scoreStreaks;
    }

    public boolean isOverkill() {
        return overkill;
    }

    public void setOverkill(boolean overkill) {
        this.overkill = overkill;
    }

    public ArrayList<String> getMapNamesFor1sAnd2s() {
        return mapNamesFor1sAnd2s;
    }

    public void setMapNamesFor1sAnd2s(ArrayList<String> mapNamesFor1sAnd2s) {
        this.mapNamesFor1sAnd2s = mapNamesFor1sAnd2s;
    }

    public ArrayList<String> getMapNamesFor3sAnd4s() {
        return mapNamesFor3sAnd4s;
    }

    public void setMapNamesFor3sAnd4s(ArrayList<String> mapNamesFor3sAnd4s) {
        this.mapNamesFor3sAnd4s = mapNamesFor3sAnd4s;
    }

    public GameModeEnum getGameModeEnum() {
        return gameModeEnum;
    }

    public void setGameModeEnum(GameModeEnum gameModeEnum) {
        this.gameModeEnum = gameModeEnum;
    }
    
}
