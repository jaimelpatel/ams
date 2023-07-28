/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.models.form;

import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author raymond
 */
public class DisputeForm {
    
    @NotEmpty(message = "Reason is required.")
    private String reason;
    private String url1;
    private String url2;
    private String url3;
    private String url4;
    
    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the url1
     */
    public String getUrl1() {
        return url1;
    }

    /**
     * @param url1 the url1 to set
     */
    public void setUrl1(String url1) {
        this.url1 = url1;
    }

    /**
     * @return the url2
     */
    public String getUrl2() {
        return url2;
    }

    /**
     * @param url2 the url2 to set
     */
    public void setUrl2(String url2) {
        this.url2 = url2;
    }

    /**
     * @return the url3
     */
    public String getUrl3() {
        return url3;
    }

    /**
     * @param url3 the url3 to set
     */
    public void setUrl3(String url3) {
        this.url3 = url3;
    }

    /**
     * @return the url4
     */
    public String getUrl4() {
        return url4;
    }

    /**
     * @param url4 the url4 to set
     */
    public void setUrl4(String url4) {
        this.url4 = url4;
    }
    
}
