/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.models.form;

import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 *
 * @author raymond
 */
public class TransferGameCashForm {
    
    //@NotNull(message="Transfer Amount is required.")
    private BigDecimal amount;
   // @NotNull(message="Recipient Username is required.")
    @Pattern(regexp = "^[A-Za-z0-9_]*$", message="Username may only contain letters, digits and underscores.")
    private String recipientUsername;
    
    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return the recipientUsername
     */
    public String getRecipientUsername() {
        return recipientUsername;
    }

    /**
     * @param recipientUsername the recipientUsername to set
     */
    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }
}
