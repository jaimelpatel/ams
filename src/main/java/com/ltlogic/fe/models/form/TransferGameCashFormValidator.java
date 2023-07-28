/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.models.form;

import com.ltlogic.db.entity.User;
import com.ltlogic.iws.UserIWS;
import com.ltlogic.service.core.UserService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author raymond
 */
@Component
public class TransferGameCashFormValidator implements Validator {

    @Autowired
    UserIWS userIWS;

    @Autowired
    UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return TransferGameCashForm.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }

    public void validateForm(TransferGameCashForm form, User currentUser, Errors errors) {
        if (form != null && errors != null) {
            if (form.getAmount() != null && form.getAmount().compareTo(new BigDecimal(1)) < 0) {
                errors.reject("lessThan", "Transfer amount must be greater than or equal to $1.00.");
            }

            User user = userService.findByUsernameLowercase(form.getRecipientUsername().toLowerCase());

            if (user == null) {
                errors.reject("no.user", "User '" + form.getRecipientUsername() + "' does not exist.");
            }

            if (currentUser != null) {
                BigDecimal totalAmount = currentUser.getBank().getTotalAmount();

                if (form.getAmount() != null) {
                    if (form.getAmount().compareTo(totalAmount) > 0) {
                        errors.reject("over.transfer", "The transfer amount cannot be greater than your current account balance.");
                    } else {
                        BigDecimal newTransferAmount = currentUser.getBank().getTotalTransferAmountForDay().add(form.getAmount());
                        if (newTransferAmount.compareTo(new BigDecimal("50.00")) == 1) {
                            errors.reject("username", "You cannot exceed the transfer amount daily limit of $50.00. You have transferred $" + currentUser.getBank().getTotalTransferAmountForDay() + " today.");

                        }
                    }
                }
            }

//            if (form.getAmount().compareTo(new BigDecimal("50.00")) == 1) {
//                errors.reject("username", "Transfer amount exceeds the maximum daily limit of $50.00.");
//            }
        }
    }
}
