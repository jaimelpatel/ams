/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.models.paypal;

/**
 *
 * @author raymond
 */
public class PayerInfo {
    
    private String country_code;
    private String email;
    private String first_name;
    private String last_name;
    private String middle_name;
    private String payer_id;
    
    /**
     * @return the country_code
     */
    public String getCountry_code() {
        return country_code;
    }

    /**
     * @param country_code the country_code to set
     */
    public void setCountry_code(String country_code) {
        this.country_code = country_code;
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
     * @return the first_name
     */
    public String getFirst_name() {
        return first_name;
    }

    /**
     * @param first_name the first_name to set
     */
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    /**
     * @return the last_name
     */
    public String getLast_name() {
        return last_name;
    }

    /**
     * @param last_name the last_name to set
     */
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    /**
     * @return the middle_name
     */
    public String getMiddle_name() {
        return middle_name;
    }

    /**
     * @param middle_name the middle_name to set
     */
    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    /**
     * @return the payer_id
     */
    public String getPayer_id() {
        return payer_id;
    }

    /**
     * @param payer_id the payer_id to set
     */
    public void setPayer_id(String payer_id) {
        this.payer_id = payer_id;
    }
    
}
