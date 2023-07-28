/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author raymond
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CustomErrorException extends RuntimeException {

    private Integer errorCode = 404;
    private String errorMessage = "The page you're looking for could not be found."; //default

    
    /**
     * @return the errorCode
     */
    public Integer getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public CustomErrorException() {
        super();
    }
    
    public CustomErrorException(Integer errCode, String errMsg) {
        super();
        
        if(errCode != null && errMsg != null) {
            this.errorCode = errCode;
            this.errorMessage = errMsg;
        }
    }

}
