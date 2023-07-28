/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.db.entity;

import com.ltlogic.db.superentity.SuperEntity;
import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

/**
 *
 * @author Jaimel
 */
@Entity
public class TournamentMaps extends SuperEntity {

    private ArrayList<String> bestOf1MapsToPlay = new ArrayList<>();

    private ArrayList<ArrayList<String>> bestOf3MapsToPlay = new ArrayList<>();
    
    private ArrayList<String> bestOf5MapsToPlay = new ArrayList<>();

    private ArrayList<String> mwrMapNamesFor1sAnd2s = new ArrayList<>();

    private ArrayList<String> mwrMapNamesFor3sAnd4s = new ArrayList<>();

    private ArrayList<String> ww2MapNamesFor1sAnd2s = new ArrayList<>();

    private ArrayList<String> ww2MapNamesFor3sAnd4s = new ArrayList<>();

    private ArrayList<String> variantMaps = new ArrayList<>();

    private ArrayList<String> variantModes = new ArrayList<>();
    
    private ArrayList<String> variantModesBestOf5 = new ArrayList<>();

    @OneToOne(mappedBy = "tournamentMaps", fetch = FetchType.LAZY)//cascade=CascadeType.ALL
    private Tournament tournament;

    public TournamentMaps() {
        //mwr
        this.mwrMapNamesFor1sAnd2s.add("Backlot");
        this.mwrMapNamesFor1sAnd2s.add("Crash");
        this.mwrMapNamesFor1sAnd2s.add("Vacant");
        this.mwrMapNamesFor1sAnd2s.add("District");
        this.mwrMapNamesFor3sAnd4s.add("Backlot");
        this.mwrMapNamesFor3sAnd4s.add("Strike");
        this.mwrMapNamesFor3sAnd4s.add("Crash");
        this.mwrMapNamesFor3sAnd4s.add("District");
        this.mwrMapNamesFor3sAnd4s.add("Overgrown");
        this.mwrMapNamesFor3sAnd4s.add("Crossfire");
        //ww2
        this.ww2MapNamesFor1sAnd2s.add("Ardennes Forest");
        this.ww2MapNamesFor1sAnd2s.add("Gibraltar");
        this.ww2MapNamesFor1sAnd2s.add("London Docks");
        this.ww2MapNamesFor1sAnd2s.add("Saint Marie Du Mont");
        this.ww2MapNamesFor1sAnd2s.add("USS Texas");

        this.ww2MapNamesFor3sAnd4s.add("Ardennes Forest");
        this.ww2MapNamesFor3sAnd4s.add("Gibraltar");
        this.ww2MapNamesFor3sAnd4s.add("London Docks");
        this.ww2MapNamesFor3sAnd4s.add("Saint Marie Du Mont");
        this.ww2MapNamesFor3sAnd4s.add("USS Texas");

        this.variantMaps.add("Ardennes Forest");
        this.variantMaps.add("USS Texas");
        this.variantMaps.add("London Docks");
        this.variantMaps.add("Saint Marie Du Mont");
        this.variantMaps.add("Gibraltar");

        this.variantModes.add("S & D");
        this.variantModes.add("Hardpoint");
        this.variantModes.add("CTF");
        
        this.variantModesBestOf5.add("S & D");
        this.variantModesBestOf5.add("Hardpoint");
        this.variantModesBestOf5.add("CTF");
        this.variantModesBestOf5.add("S & D");
        this.variantModesBestOf5.add("Hardpoint");
        
        
    }

    public ArrayList<String> getVariantModesBestOf5() {
        return variantModesBestOf5;
    }

    public void setVariantModesBestOf5(ArrayList<String> variantModesBestOf5) {
        this.variantModesBestOf5 = variantModesBestOf5;
    }

    public ArrayList<String> getBestOf5MapsToPlay() {
        return bestOf5MapsToPlay;
    }

    public void setBestOf5MapsToPlay(ArrayList<String> bestOf5MapsToPlay) {
        this.bestOf5MapsToPlay = bestOf5MapsToPlay;
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

    public ArrayList<String> getBestOf1MapsToPlay() {
        return bestOf1MapsToPlay;
    }

    public void setBestOf1MapsToPlay(ArrayList<String> bestOf1MapsToPlay) {
        this.bestOf1MapsToPlay = bestOf1MapsToPlay;
    }

    public ArrayList<ArrayList<String>> getBestOf3MapsToPlay() {
        return bestOf3MapsToPlay;
    }

    public void setBestOf3MapsToPlay(ArrayList<ArrayList<String>> bestOf3MapsToPlay) {
        this.bestOf3MapsToPlay = bestOf3MapsToPlay;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public ArrayList<String> getMwrMapNamesFor1sAnd2s() {
        return mwrMapNamesFor1sAnd2s;
    }

    public void setMwrMapNamesFor1sAnd2s(ArrayList<String> mwrMapNamesFor1sAnd2s) {
        this.mwrMapNamesFor1sAnd2s = mwrMapNamesFor1sAnd2s;
    }

    public ArrayList<String> getMwrMapNamesFor3sAnd4s() {
        return mwrMapNamesFor3sAnd4s;
    }

    public void setMwrMapNamesFor3sAnd4s(ArrayList<String> mwrMapNamesFor3sAnd4s) {
        this.mwrMapNamesFor3sAnd4s = mwrMapNamesFor3sAnd4s;
    }

    public ArrayList<String> getWw2MapNamesFor1sAnd2s() {
        return ww2MapNamesFor1sAnd2s;
    }

    public void setWw2MapNamesFor1sAnd2s(ArrayList<String> ww2MapNamesFor1sAnd2s) {
        this.ww2MapNamesFor1sAnd2s = ww2MapNamesFor1sAnd2s;
    }

    public ArrayList<String> getWw2MapNamesFor3sAnd4s() {
        return ww2MapNamesFor3sAnd4s;
    }

    public void setWw2MapNamesFor3sAnd4s(ArrayList<String> ww2MapNamesFor3sAnd4s) {
        this.ww2MapNamesFor3sAnd4s = ww2MapNamesFor3sAnd4s;
    }

}
