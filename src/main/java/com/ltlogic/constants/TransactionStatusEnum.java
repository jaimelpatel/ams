/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.constants;

/**
 *
 * @author jaimel
 */
public enum TransactionStatusEnum {
    
    PENDING(0, "PENDING"),
    COMPLETED(1, "COMPLETED"),
    ERROR(2, "ERROR"),
    CANCELLED(3, "CANCELLED");
    
    
    private final int id;
    private final String desc;
    
    private TransactionStatusEnum(int id, String desc){
        this.id = id;
        this.desc = desc;
    }

    public int getTransactionStatusId() {
        return id;
    }

    public String getTransactionStatusDesc() {
        return desc;
    }
    
    public static TransactionStatusEnum getTransactionStatusEnumById(int id){
        for(TransactionStatusEnum status: TransactionStatusEnum.values()){
            if(status.getTransactionStatusId() == id)
                return status;
        }
        return null;
    }
    
}
