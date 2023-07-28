/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.service.springsecurity;

import com.ltlogic.db.entity.User;
import com.ltlogic.pojo.LoginDetailsPojo;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 *
 * @author jaimel
 */
@Component
public class BankValidator {
    
    public void withdrawCashValidation(User user, BigDecimal withdrawalAmount, Errors errors) {
        if(withdrawalAmount.compareTo(new BigDecimal("250")) == 1){
            errors.reject("username", "Withdrawal amount exceeds the $250.00 limit.");
        }
        else if(withdrawalAmount.compareTo(user.getBank().getTotalAmount()) == 1){
            errors.reject("username", "You cannot withdraw more cash than you have in your account.");
        }
        else if(user.getBank().isHasWithdrawnToday() == true){
            errors.reject("username", "You have already made a withdrawal for the day. (1 withdraw per 24 hours)");
        }
            
    }
    
    public void transferCashValidation(User userFrom, BigDecimal transferAmount, Errors errors) {
        if(transferAmount.compareTo(new BigDecimal("50")) == 1){
            errors.reject("username", "Transfer amount exceeds the maximum daily limit of $50.00.");
        }
        else if(transferAmount.compareTo(userFrom.getBank().getTotalAmount()) == 1){
            errors.reject("username", "You cannot transfer more cash to another user than you have in your own account. Please visit the shop.");
        }
    }
    
    public void wagerMatchBuyInCashValidation(User user, BigDecimal buyInAmount, Errors errors) {
        if(buyInAmount.compareTo(user.getBank().getTotalAmount()) == 1){
            errors.reject("username", "You do not have enough cash in your account to play in this wager. Please visit the shop.");
        }
    }
    
    public void tournamentBuyInCashValidation(User user, BigDecimal buyInAmount, Errors errors) {
        if(buyInAmount.compareTo(user.getBank().getTotalAmount()) == 1){
            errors.reject("username", "You do not have enough cash in your account to play in this tournament. Please visit the shop.");
        }
    }
    
    public void wagerDonateCashValidation(User user, BigDecimal donateAmount, Errors errors) {
        if(donateAmount.compareTo(user.getBank().getTotalAmount()) == 1){
            errors.reject("username", "You cannot donate more cash to this wager than you have in your account. Please visit the shop.");
        }
    }
    
    public void tournamentDonateCashValidation(User user, BigDecimal donateAmount, Errors errors) {
        if(donateAmount.compareTo(user.getBank().getTotalAmount()) == 1){
            errors.reject("username", "You cannot donate more cash to this tournament than you have in your account. Please visit the shop.");
        }
    }
}
