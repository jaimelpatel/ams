 package com.ltlogic.fe.models.form;

import com.ltlogic.constants.CountryEnum;
import com.ltlogic.constants.StateOrProvinceEnum;
import com.ltlogic.constants.TimeZoneEnum;
import com.ltlogic.db.entity.User;
import com.ltlogic.pojo.UserPojo;
import org.hibernate.validator.constraints.NotEmpty;
import java.time.LocalDateTime;
import javax.validation.constraints.Pattern;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author raymond
 */
public class GeneralAccountSettingsForm {

    @NotEmpty(message = "Username is required.")
    @Pattern(regexp = "^[A-Za-z0-9]*$", message="Username can only be in letters and digits.")
    protected String username;
    @Pattern(regexp = "^[A-Za-z0-9]*$", message="Password can only be in letters and digits.")
    protected String password;
    @Pattern(regexp = "^[A-Za-z0-9]*$", message="Password Confirmation can only be in letters and digits.")
    protected String passwordConfirm;
    private String oldPassword;
    @NotEmpty(message = "Email Address is required.")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message="E-mail Address can only be in letters, digits, dot(.), dash(-), and underscore(_) only")
    protected String email;
    protected Integer timeZone;
    @Pattern(regexp = "^[A-Za-z0-9+_-]*$", message="PSN ID can only be in letters, digits, dashes and underscores.")
    protected String playStation4Name;
    @Pattern(regexp = "^[A-Za-z0-9\\s]*$", message="Xbox ID can only be in letters, digits, and spaces.")
    protected String xboxOneGamerTag;
    
    //UserPojo details
    protected String firstName;
    protected String lastName;
    protected int age;
    protected LocalDateTime dateOfBirth;
    protected String address;
    protected String city;
    protected Integer stateOrProvince;
    protected Integer country;
    protected String zip1;
    protected String zip2;
    protected String phoneNumber;
    protected boolean autoRenewPremium;
    protected String paypayEmail;

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * @return the oldPassword
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * @param oldPassword the oldPassword to set
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }


    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the passwordConfirm
     */
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    /**
     * @param passwordConfirm the passwordConfirm to set
     */
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the timeZone
     */
    public Integer getTimeZone() {
        return timeZone;
    }

    /**
     * @param timeZone the timeZone to set
     */
    public void setTimeZone(Integer timeZone) {
        this.timeZone = timeZone;
    }
    
     /**
     * @return the playStation4Name
     */
    public String getPlayStation4Name() {
        return playStation4Name;
    }

    /**
     * @param playStation4Name the playStation4Name to set
     */
    public void setPlayStation4Name(String playStation4Name) {
        this.playStation4Name = playStation4Name;
    }

    /**
     * @return the xboxOneGamerTag
     */
    public String getXboxOneGamerTag() {
        return xboxOneGamerTag;
    }

    /**
     * @param xboxOneGamerTag the xboxOneGamerTag to set
     */
    public void setXboxOneGamerTag(String xboxOneGamerTag) {
        this.xboxOneGamerTag = xboxOneGamerTag;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @return the dateOfBirth
     */
    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * @param dateOfBirth the dateOfBirth to set
     */
    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the stateOrProvince
     */
    public Integer getStateOrProvince() {
        return stateOrProvince;
    }

    /**
     * @param stateOrProvince the stateOrProvince to set
     */
    public void setStateOrProvince(Integer stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    /**
     * @return the country
     */
    public Integer getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(Integer country) {
        this.country = country;
    }

    /**
     * @return the zip1
     */
    public String getZip1() {
        return zip1;
    }

    /**
     * @param zip1 the zip1 to set
     */
    public void setZip1(String zip1) {
        this.zip1 = zip1;
    }

    /**
     * @return the zip2
     */
    public String getZip2() {
        return zip2;
    }

    /**
     * @param zip2 the zip2 to set
     */
    public void setZip2(String zip2) {
        this.zip2 = zip2;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the autoRenewPremium
     */
    public boolean isAutoRenewPremium() {
        return autoRenewPremium;
    }

    /**
     * @param autoRenewPremium the autoRenewPremium to set
     */
    public void setAutoRenewPremium(boolean autoRenewPremium) {
        this.autoRenewPremium = autoRenewPremium;
    }

    /**
     * @return the paypayEmail
     */
    public String getPaypayEmail() {
        return paypayEmail;
    }

    /**
     * @param paypayEmail the paypayEmail to set
     */
    public void setPaypayEmail(String paypayEmail) {
        this.paypayEmail = paypayEmail;
    }


    public static User toUserEntity(GeneralAccountSettingsForm formData, User user) {

        if (formData != null && user != null) {
            
            if(user.getUserInfo() == null) {
                user.setUserInfo(new UserPojo());
            }
            
            if(formData.getTimeZone() != 0){
                user.getUserInfo().setTimeZone(TimeZoneEnum.getTimeZonesEnumById(formData.getTimeZone()));
            }else{
                user.getUserInfo().setTimeZone(TimeZoneEnum.UTC_MINUS_7_LA);
            }
            user.getUserInfo().setXboxOneGamerTag(formData.getXboxOneGamerTag());
            user.getUserInfo().setPlayStation4Name(formData.getPlayStation4Name());
            user.getUserInfo().setFirstName(formData.getFirstName());
            user.getUserInfo().setLastName(formData.getLastName());
            user.getUserInfo().setPaypayEmail(formData.getPaypayEmail());
            user.getUserInfo().setAutoRenewPremium(formData.isAutoRenewPremium());
            user.getUserInfo().setAddress(formData.getAddress());
            user.getUserInfo().setCity(formData.getCity());
            user.getUserInfo().setZip1(formData.getZip1());
            user.getUserInfo().setCountry(CountryEnum.getCountryEnumById(formData.getCountry()));
            user.getUserInfo().setStateOrProvince(StateOrProvinceEnum.getStateEnumById(formData.getStateOrProvince()));
            user.getUserInfo().setPhoneNumber(formData.getPhoneNumber());
        }

        return user;
    }

    public static GeneralAccountSettingsForm fromUserEntityToGeneralAccountSettingsForm(User user) {
        GeneralAccountSettingsForm form = new GeneralAccountSettingsForm();

        if (user != null) {
            form.setEmail(user.getEmail());
            form.setUsername(user.getUsername());
            if(user.getUserInfo().getTimeZone() != null)
                form.setTimeZone(user.getUserInfo().getTimeZone().getTimeZonesEnumId());
            
            //UserInfo
            if(user.getUserInfo() != null) {
                form.setFirstName(user.getUserInfo().getFirstName());
                form.setLastName(user.getUserInfo().getLastName());
                form.setAddress(user.getUserInfo().getAddress());
                form.setCity(user.getUserInfo().getCity());
                form.setZip1(user.getUserInfo().getZip1());
                if(user.getUserInfo().getStateOrProvince() != null)
                    form.setStateOrProvince(user.getUserInfo().getStateOrProvince().getStateEnumId());
                if(user.getUserInfo().getCountry() != null)
                    form.setCountry(user.getUserInfo().getCountry().getCountryEnumId());
                form.setPhoneNumber(user.getUserInfo().getPhoneNumber());
                form.setPaypayEmail(user.getUserInfo().getPaypayEmail());
                form.setXboxOneGamerTag(user.getUserInfo().getXboxOneGamerTag());
                form.setPlayStation4Name(user.getUserInfo().getPlayStation4Name());
                form.setAutoRenewPremium(user.getUserInfo().isAutoRenewPremium());
            }
        }

        return form;
    }

}
