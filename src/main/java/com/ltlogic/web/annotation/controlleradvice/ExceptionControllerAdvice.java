/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.web.annotation.controlleradvice;

import com.ltlogic.web.exception.CustomErrorException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author raymond
 */
@ControllerAdvice(basePackages = {"com.ltlogic.controller"} )
public class ExceptionControllerAdvice {

    @ExceptionHandler(CustomErrorException.class)
    public ModelAndView exception(CustomErrorException e) {

        ModelAndView mav = new ModelAndView("errors/404-error");
        if(e != null) {
            mav.addObject("errorCode", e.getErrorCode());
            mav.addObject("message", e.getErrorMessage());   
        }

        return mav;
    }
}
