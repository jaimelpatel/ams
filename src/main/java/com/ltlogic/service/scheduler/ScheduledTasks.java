/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.scheduler;

import com.ltlogic.service.core.BankService;
import com.ltlogic.service.core.MatchService;
import com.ltlogic.service.core.RankService;
import com.ltlogic.service.core.TournamentService;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author Bishistha
 */

/**
 * | Annotation | Meaning |
 * +------------+-----------------------------------------------------+ |
 * @Component | generic stereotype for any Spring-managed component | |
 * @Repository| stereotype for persistence layer | | @Service | stereotype for
 * service layer | | @Controller| stereotype for presentation layer (spring-mvc)
 * |
 *
 *
 */
@Component
public class ScheduledTasks {

    @Autowired
    private MatchService matchService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private RankService rankService;

    @Autowired
    private BankService bankService;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    //@Scheduled(fixedRate = 1000)
//    public void reportCurrentTime() {
//        log.info("The time is now {}", dateFormat.format(new Date()));
//    }
//
//    /**
//     * -----------------------------------------------------------
//     * -------------------------- MATCH ---------------------------
//     * ------------------------------------------------------------
//     */
//    @Scheduled(fixedRate = 1000) //Needs to be every second
//    public void startMatches() {
//        //log.info("The time is now {}", dateFormat.format(new Date()));
//        matchService.startMatches();
//    }
//
//    @Scheduled(fixedRate = 10000) //Needs to be every 55-60 seconds
//    public void killMatches() {
//        //log.info("The time is now {}", dateFormat.format(new Date()));
//        matchService.killMatches();
//    }
//    
//    @Scheduled(fixedRate = 10000) //Needs to be every 55-60 seconds
//    public void killPublicWagerMatches() {
//        //log.info("The time is now {}", dateFormat.format(new Date()));
//        matchService.killPublicWagerMatches();
//    }
//
//    @Scheduled(fixedRate = 100000) //Needs to be every 10 minutes
//    public void notifyUpcomingMatches() {
//        //log.info("The time is now {}", dateFormat.format(new Date()));
//        matchService.notifyUpcomingMatches();
//    }
//    
//    @Scheduled(fixedRate = 10000)
//    public void cancelMatches(){
//        matchService.cancelMatches();
//    }
//    
//    @Scheduled(fixedRate = 10000)
//    public void endMatches(){
//        matchService.endMatches();
//    }
//
//    /**
//     * -----------------------------------------------------------
//     * ----------------------- TOURNAMENT -------------------------
//     * ------------------------------------------------------------
//     */
//    @Scheduled(fixedRate = 10000) //Needs to be every 55-60 seconds
//    public void startTournaments() throws Exception {
//        //log.info("The time is now {}", dateFormat.format(new Date()));
//        tournamentService.startTournaments();
//    }
//
//    @Scheduled(fixedRate = 100000) //Needs to be every 10 minutes
//    public void notifyUpcomingTournaments() {
//        //log.info("The time is now {}", dateFormat.format(new Date()));
//        tournamentService.notifyUpcomingTournaments();
//    }
//
//    /**
//     * -----------------------------------------------------------
//     * -------------------------- RANK -----------------------------
//     * ------------------------------------------------------------
//     */
//    @Scheduled(cron = "0 0 * * * *") //Needs to be ran every 24 hours
//    public void updateUserRankByXP() {
//        rankService.updateUserRankByXP();
//    }
//
//    @Scheduled(cron = "0 0 * * * *") //Needs to be ran every 24 hours
//    public void updateUserRankByEarnings() {
//        rankService.updateUserRankByEarnings();
//    }
//
//    @Scheduled(cron = "0 0 * * * *") //Needs to be ran every 24 hours
//    public void updateTeamRanks() {
//        rankService.updateWW2TeamRankXp();
//    }
//
//    /**
//     * -----------------------------------------------------------
//     * -------------------------- BANK -----------------------------
//     * ------------------------------------------------------------
//     */
//    @Scheduled(cron = "0 0 12 * * *") //Needs to be ran every 24 hours
//    public void resetBankForAllUsers() {
////        log.info("The time is now {}", dateFormat.format(new Date()));
//        bankService.resetBankForAllUsers();
//    }

}
