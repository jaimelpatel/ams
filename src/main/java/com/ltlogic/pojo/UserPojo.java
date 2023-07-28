/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ltlogic.pojo;

import com.ltlogic.constants.CountryEnum;
import com.ltlogic.constants.RegionEnum;
import com.ltlogic.constants.StateOrProvinceEnum;
import com.ltlogic.constants.TimeZoneEnum;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

/**
 * 
 * @author Hoang
 */
@Embeddable
public class UserPojo {
    
    private String firstName;
    private String lastName;
    private int age;
    private LocalDateTime dateOfBirth;
    private String address;
    private String city;
    private StateOrProvinceEnum stateOrProvince;
    private CountryEnum country;
    private String zip1;
    private String zip2;
    private String phoneNumber;
    private boolean autoRenewPremium;
    private String paypayEmail;
    private String playStation4Name;
    private String xboxOneGamerTag;
    private String twitchUrl;
    private String youtubeUrl;
    private String twitterUrl;
    private String facebookUrl;
    @Enumerated(EnumType.STRING)
    private TimeZoneEnum timeZone = TimeZoneEnum.UTC_MINUS_7_LA;
    @Enumerated(EnumType.STRING)
    private RegionEnum region;

    
    public RegionEnum getRegion() {
        return region;
    }

    public void setRegion(RegionEnum region) {
        this.region = region;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public StateOrProvinceEnum getStateOrProvince() {
        return stateOrProvince;
    }

    public void setStateOrProvince(StateOrProvinceEnum stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    public CountryEnum getCountry() {
        return country;
    }

    public void setCountry(CountryEnum country) {
        this.country = country;
    }

    public String getZip1() {
        return zip1;
    }

    public void setZip1(String zip1) {
        this.zip1 = zip1;
    }

    public String getZip2() {
        return zip2;
    }

    public void setZip2(String zip2) {
        this.zip2 = zip2;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAutoRenewPremium() {
        return autoRenewPremium;
    }

    public void setAutoRenewPremium(boolean autoRenewPremium) {
        this.autoRenewPremium = autoRenewPremium;
    }

    public String getPaypayEmail() {
        return paypayEmail;
    }

    public void setPaypayEmail(String paypayEmail) {
        this.paypayEmail = paypayEmail;
    }

    public String getPlayStation4Name() {
        return playStation4Name;
    }

    public void setPlayStation4Name(String playStation4Name) {
        this.playStation4Name = playStation4Name;
    }

    public String getXboxOneGamerTag() {
        return xboxOneGamerTag;
    }

    public void setXboxOneGamerTag(String xboxOneGamerTag) {
        this.xboxOneGamerTag = xboxOneGamerTag;
    }

    public String getTwitchUrl() {
        return twitchUrl;
    }

    public void setTwitchUrl(String twitchUrl) {
        this.twitchUrl = twitchUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public TimeZoneEnum getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZoneEnum timeZone) {
        this.timeZone = timeZone;
    }
    
}
