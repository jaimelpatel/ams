/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic;

import com.ltlogic.constants.TimeZoneEnum;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bishistha
 */
public class DateTimeUtil {
    
    private static final boolean isProduction = Boolean.FALSE;
     
    private static ZoneId DEFAULT_TIME_ZONE = isProduction ? ZoneOffset.UTC : ZoneId.of("America/Los_Angeles");
    public static TimeZoneEnum DEFAULT_TIME_ZONE_ENUM = isProduction ? TimeZoneEnum.UTC_0_SH : TimeZoneEnum.UTC_MINUS_7_LA;
    private static String DEFAULT_DATE_TIME_FORMAT = "M-d-yyyy h:mm a";
    
    private static final Logger log = LoggerFactory.getLogger(DateTimeUtil.class);
    
    // To get the current date time to get the MILLIS for RANKs
    public static ZonedDateTime getDefaultZonedDateTimeNow() {
        return ZonedDateTime.now(DEFAULT_TIME_ZONE);
    }
    
    // To get the current date time to SAVE to the db
    public static LocalDateTime getDefaultLocalDateTimeNow() {
        ZonedDateTime nowUTC = ZonedDateTime.now(DEFAULT_TIME_ZONE);
        return nowUTC.toLocalDateTime();
    }
    
    // When you want to SAVE to the db, Call this method.
    public static LocalDateTime getDefaultLocalDateTime(LocalDateTime userInputLocalDateTime, TimeZoneEnum userTimeZoneEnum){
        String[] splitTimeZoneEnumDesc = userTimeZoneEnum.getTimeZonesEnumDesc().split(" ");
        ZonedDateTime userInputDateTime = userInputLocalDateTime.atZone(ZoneId.of(splitTimeZoneEnumDesc[1]));
        ZonedDateTime defaultDateTime = userInputDateTime.withZoneSameInstant(DEFAULT_TIME_ZONE);
        //setMatchEndTime(utcDateTime.plusHours(3L).toLocalDateTime());
        return defaultDateTime.toLocalDateTime();
    }
    
    // When you want to DISPLAY to the user, Call this method.
    public static LocalDateTime getUserLocalDateTime(LocalDateTime defaultLocalDateTime, TimeZoneEnum userTimeZoneEnum){
        String[] splitTimeZoneEnumDesc = userTimeZoneEnum.getTimeZonesEnumDesc().split(" ");
        ZonedDateTime defaultDateTime = defaultLocalDateTime.atZone(DEFAULT_TIME_ZONE);
        ZonedDateTime userDateTime = defaultDateTime.withZoneSameInstant(ZoneId.of(splitTimeZoneEnumDesc[1]));
        return userDateTime.toLocalDateTime();
    }
    
    // This is to be called to ease FE when displaying LocalDateTime in a DEFAULT format
    public static String formatLocalDateTime(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);
        return localDateTime.format(formatter).toString().trim();
    }
    
    // This is to be called to get LocalDateTime in a certain format
    public static String formatLocalDateTime(LocalDateTime localDateTime, String pattern){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(formatter).toString().trim();
    }
}
