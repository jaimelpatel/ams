/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.models.form;

import com.ltlogic.iws.UserIWS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author raymond
 */
@Component
public class ProfileSettingsFormValidator implements Validator {
    @Autowired
    UserIWS userIWS;
    
    @Override
    public boolean supports(Class<?> aClass) {
        return ProfileSettingsForm.class.equals(aClass);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        if(target != null) {
            //more validations here later
        }
    }
}
