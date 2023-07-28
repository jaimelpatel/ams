/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.models.form;

import com.ltlogic.fe.models.form.PasswordResetForm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author raymond
 */
@Component
public class PasswordResetFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return PasswordResetForm.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target != null && errors != null) {
            PasswordResetForm passwordResetForm = (PasswordResetForm) target;

            if (passwordResetForm.getNewPassword().length() != 0 || passwordResetForm.getPasswordConfirmation().length() != 0) {
                if (!passwordResetForm.getNewPassword().equals(passwordResetForm.getPasswordConfirmation())) {
                    //errors.rejectValue("passwordConfirmation","Password and Password Confirmation do not match");
                    errors.reject("password.no.match", "Password and Password Confirmation do not match.");
                }
            }

            if (passwordResetForm.getNewPassword().contains("<") || passwordResetForm.getNewPassword().contains(">") || passwordResetForm.getNewPassword().contains(";")) {
                errors.reject("username", "Password may not contain the following special characters: < > ;");
            }
        }
    }
}
