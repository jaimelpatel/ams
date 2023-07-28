/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.pojo.mwr;

import com.ltlogic.constants.GameModeEnum;
import com.ltlogic.db.entity.mwr.MWRMatch;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author Hoang
 */
@Embeddable
public class MWRMatchPojo {

    private boolean lethals;

    private boolean juggernaut;

    private boolean tacticals;

    private boolean stoppingPower;

    private boolean sniperOnly;

    private boolean radarOn;

    private ArrayList<String> mapNamesFor1sAnd2s = new ArrayList<>();
    
    private ArrayList<String> mapNamesFor3sAnd4s = new ArrayList<>();
    
    private ArrayList<String> mapsToPlayInMatchInOrder = new ArrayList<>();
    
    private ArrayList<String> hostNamesInOrder = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private GameModeEnum gameModeEnum;

    public MWRMatchPojo() {
        this.mapNamesFor1sAnd2s.add("Backlot");
        this.mapNamesFor1sAnd2s.add("Crash");
        this.mapNamesFor1sAnd2s.add("Vacant");
        this.mapNamesFor1sAnd2s.add("District");
        this.mapNamesFor3sAnd4s.add("Backlot");
        this.mapNamesFor3sAnd4s.add("Strike");
        this.mapNamesFor3sAnd4s.add("Crash");
        this.mapNamesFor3sAnd4s.add("District");
        this.mapNamesFor3sAnd4s.add("Overgrown");
        this.mapNamesFor3sAnd4s.add("Crossfire");
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

    public boolean isLethals() {
        return lethals;
    }

    public void setLethals(boolean lethals) {
        this.lethals = lethals;
    }

    public boolean isJuggernaut() {
        return juggernaut;
    }

    public void setJuggernaut(boolean juggernaut) {
        this.juggernaut = juggernaut;
    }

    public boolean isTacticals() {
        return tacticals;
    }

    public void setTacticals(boolean tacticals) {
        this.tacticals = tacticals;
    }

    public boolean isStoppingPower() {
        return stoppingPower;
    }

    public void setStoppingPower(boolean stoppingPower) {
        this.stoppingPower = stoppingPower;
    }

    public boolean isSniperOnly() {
        return sniperOnly;
    }

    public void setSniperOnly(boolean sniperOnly) {
        this.sniperOnly = sniperOnly;
    }

    public boolean isRadarOn() {
        return radarOn;
    }

    public void setRadarOn(boolean radarOn) {
        this.radarOn = radarOn;
    }

    public GameModeEnum getGameModeEnum() {
        return gameModeEnum;
    }

    public void setGameModeEnum(GameModeEnum gameModeEnum) {
        this.gameModeEnum = gameModeEnum;
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

}
