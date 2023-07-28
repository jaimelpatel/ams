/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.fe.models.paypal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author raymond
 */
public class PaypalTransaction {
    private String id;
    private BigDecimal amount;
    private List<Item> items = new ArrayList<>();
    private PayerInfo payer_info = new PayerInfo();
    
    
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
     * @return the items
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    
    /**
     * @return the payer_info
     */
    public PayerInfo getPayer_info() {
        return payer_info;
    }

    /**
     * @param payer_info the payer_info to set
     */
    public void setPayer_info(PayerInfo payer_info) {
        this.payer_info = payer_info;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    
}
