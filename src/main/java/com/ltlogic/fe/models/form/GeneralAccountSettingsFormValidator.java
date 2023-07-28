/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.models.form;

import com.ltlogic.iws.UserIWS;
import com.ltlogic.service.core.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author raymond
 */
@Component
public class GeneralAccountSettingsFormValidator implements Validator {
    
    @Autowired
    UserIWS userIWS;
    
    @Override
    public boolean supports(Class<?> aClass) {
        return GeneralAccountSettingsForm.class.equals(aClass);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        if(target != null && errors != null) {
            GeneralAccountSettingsForm generalAccountForm = (GeneralAccountSettingsForm) target;

            //filled old password, but no new password
            if(generalAccountForm != null) {
                if(generalAccountForm.getOldPassword() != null && !generalAccountForm.getOldPassword().isEmpty()) {
                    if(generalAccountForm.getPassword() == null || generalAccountForm.getPassword().isEmpty()) {
                        errors.reject("password", "You must enter a new password and retype it to confirm.");
                    }
                }
            }

            //check new password and password confirm same
            if(generalAccountForm.getPassword().length() != 0 || generalAccountForm.getPasswordConfirm().length() != 0) {
                if(!generalAccountForm.getPassword().equals(generalAccountForm.getPasswordConfirm())) {
                    errors.reject("password.no.match", "Password and Password Confirmation do not match.");
                }
            }
        }
    }
}
