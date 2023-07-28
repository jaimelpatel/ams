/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.common;

import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.db.entity.User;
import com.ltlogic.db.repository.UserRepository;
import java.util.Random;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Hoang
 */
@Service
public class CommonService {

    @Autowired
    public UserRepository userRepo;

    //in db 7/27/2017 3:44:27 PM
    private static final String DATE_FORMAT = "dd-M-yyyy hh:mm:ss a";

    public static void main(String[] args) {

//        TimeZoneEnum tze = TimeZoneEnum.UTC_4_ET;
//        CommonService cs = new CommonService();
//        cs.convertToParticularTimeZone();
    }

    public int generateRandomId() {
        Random r = new Random();
        int low = 999999;
        int high = 10000000;
        int result = r.nextInt(high - low) + low;
        return result;
    }

    public int generateRandomXpWinForUser() {
        Random r = new Random();
        int low = 59;
        int high = 80;
        int result = r.nextInt(high - low) + low;
        return result;
    }

    public int generateRandomXpLossForUser() {
        Random r = new Random();
        int low = 39;
        int high = 55;
        int result = r.nextInt(high - low) + low;
        return result;
    }

    public int generateRandomXpWinForCloseTeam() {
        Random r = new Random();
        int low = 59;
        int high = 75;
        int result = r.nextInt(high - low) + low;
        return result;
    }

    public int generateRandomXpLossForCloseTeam() {
        Random r = new Random();
        int low = 49;
        int high = 59;
        int result = r.nextInt(high - low) + low;
        return result;
    }

    public int generateRandomXpWinForExpectedWin() {
        Random r = new Random();
        int low = 44;
        int high = 55;
        int result = r.nextInt(high - low) + low;
        return result;
    }

    public int generateRandomXpLossForExpectedWin() {
        Random r = new Random();
        int low = 34;
        int high = 45;
        int result = r.nextInt(high - low) + low;
        return result;
    }

    public int generateRandomXpWinForUpsetWin() {
        Random r = new Random();
        int low = 84;
        int high = 100;
        int result = r.nextInt(high - low) + low;
        return result;
    }

    public int generateRandomXpLossForUpsetWin() {
        Random r = new Random();
        int low = 70;
        int high = 85;
        int result = r.nextInt(high - low) + low;
        return result;
    }

}
