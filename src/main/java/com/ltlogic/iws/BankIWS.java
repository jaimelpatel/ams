/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.iws;

import com.ltlogic.db.entity.Transaction;
import com.ltlogic.db.entity.User;
import com.ltlogic.service.core.BankService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jaimel
 */


@Service
public class BankIWS {
    
    @Autowired 
    BankService bankService;
    
    public void depositCash(User user, BigDecimal depositAmount, String paypalTransactionId){
        bankService.depositCash(user, depositAmount, paypalTransactionId);
    }
    
    public BigDecimal withdrawCash(User user, BigDecimal withdrawalAmount) throws Exception{
        return bankService.withdrawCash(user, withdrawalAmount);
    }
    
    public void transferCash(User userFrom, User userTo, BigDecimal withdrawalAmount) throws Exception{
        bankService.transferCash(userFrom, userTo, withdrawalAmount);
    }
    
    public List<Transaction> getAllTransactionsForUser(long userPk, int pageNumber){
        return bankService.getAllTransactionsForUser(userPk, pageNumber);
    }
    
    public Integer getTotalTransactionsForUser(long userPk){
        return bankService.getTotalTransactionsForUser(userPk);
    }
}
