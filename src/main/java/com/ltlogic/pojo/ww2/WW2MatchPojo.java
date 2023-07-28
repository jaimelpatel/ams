/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.pojo.ww2;

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
public class WW2MatchPojo {

    private boolean scoreStreaks;

    private boolean overkill;

    private ArrayList<String> mapNamesFor1sAnd2s = new ArrayList<>();

    private ArrayList<String> mapNamesFor3sAnd4s = new ArrayList<>();

    private ArrayList<String> variantMaps = new ArrayList<>();

    private ArrayList<String> variantModes = new ArrayList<>();

    private ArrayList<String> hostNamesInOrder = new ArrayList<>();

    private ArrayList<String> mapsToPlayInMatchInOrder = new ArrayList<>();
    
    private ArrayList<String> modesToPlayInMatchInOrder = new ArrayList<>();
    
    private ArrayList<String> bestOf5MapsToPlay = new ArrayList<>();

    private ArrayList<String> bestOf5HostNames = new ArrayList<>();
    
    private ArrayList<String> bestOf5ModesToPlay = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private GameModeEnum gameModeEnum;

    public WW2MatchPojo() {
        //add ww2 maps here
        this.mapNamesFor1sAnd2s.add("Ardennes Forest");
        this.mapNamesFor1sAnd2s.add("Gibraltar");
        this.mapNamesFor1sAnd2s.add("London Docks");
        this.mapNamesFor1sAnd2s.add("Saint Marie Du Mont");
        this.mapNamesFor1sAnd2s.add("USS Texas");

        this.mapNamesFor3sAnd4s.add("Ardennes Forest");
        this.mapNamesFor3sAnd4s.add("Gibraltar");
        this.mapNamesFor3sAnd4s.add("London Docks");
        this.mapNamesFor3sAnd4s.add("Saint Marie Du Mont");
        this.mapNamesFor3sAnd4s.add("USS Texas");

        this.variantMaps.add("Ardennes Forest");
        this.variantMaps.add("Gibraltar");
        this.variantMaps.add("London Docks");
        this.variantMaps.add("Saint Marie Du Mont");
        this.variantMaps.add("USS Texas");

        this.variantModes.add("S & D");
        this.variantModes.add("Hardpoint");
        this.variantModes.add("CTF");

    }

    public ArrayList<String> getBestOf5MapsToPlay() {
        return bestOf5MapsToPlay;
    }

    public void setBestOf5MapsToPlay(ArrayList<String> bestOf5MapsToPlay) {
        this.bestOf5MapsToPlay = bestOf5MapsToPlay;
    }

    public ArrayList<String> getBestOf5HostNames() {
        return bestOf5HostNames;
    }

    public void setBestOf5HostNames(ArrayList<String> bestOf5HostNames) {
        this.bestOf5HostNames = bestOf5HostNames;
    }

    public ArrayList<String> getBestOf5ModesToPlay() {
        return bestOf5ModesToPlay;
    }

    public void setBestOf5ModesToPlay(ArrayList<String> bestOf5ModesToPlay) {
        this.bestOf5ModesToPlay = bestOf5ModesToPlay;
    }

    public ArrayList<String> getModesToPlayInMatchInOrder() {
        return modesToPlayInMatchInOrder;
    }

    public void setModesToPlayInMatchInOrder(ArrayList<String> modesToPlayInMatchInOrder) {
        this.modesToPlayInMatchInOrder = modesToPlayInMatchInOrder;
    }

    public ArrayList<String> getVariantMaps() {
        return variantMaps;
    }

    public void setVariantMaps(ArrayList<String> variantMaps) {
        this.variantMaps = variantMaps;
    }

    public ArrayList<String> getVariantModes() {
        return variantModes;
    }

    public void setVariantModes(ArrayList<String> variantModes) {
        this.variantModes = variantModes;
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

    public ArrayList<String> getMapsToPlayInMatchInOrder() {
        return mapsToPlayInMatchInOrder;
    }

    public void setMapsToPlayInMatchInOrder(ArrayList<String> mapsToPlayInMatchInOrder) {
        this.mapsToPlayInMatchInOrder = mapsToPlayInMatchInOrder;
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

    public ArrayList<String> getHostNamesInOrder() {
        return hostNamesInOrder;
    }

    public void setHostNamesInOrder(ArrayList<String> hostNamesInOrder) {
        this.hostNamesInOrder = hostNamesInOrder;
    }

    public GameModeEnum getGameModeEnum() {
        return gameModeEnum;
    }

    public void setGameModeEnum(GameModeEnum gameModeEnum) {
        this.gameModeEnum = gameModeEnum;
    }
}
