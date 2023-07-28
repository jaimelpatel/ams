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
public enum TransactionTypeEnum {
     DEPOSIT_CASH(0, "Deposit Cash"),
     WITHDRAW_CASH(1,"Withdraw Cash"),
     TRANSFER_CASH(3, "Transfer"),
     WAGER_BUYIN(4, "Wager Buyin"),
     WAGER_CANCELLED(5, "Wager Cancelled"),
     WAGER_WIN(6, "Wager Win"),
     TOURNAMENT_BUYIN(7,"Tournament Buyin"),
     TOURNAMENT_CANCELLED(8,"Tournament Cancelled"),
     TOURNAMENT_WIN(9,"Tournament Win"),
     WAGER_DONATE(10, "Wager Donate"),
     TOURNAMENT_DONATE(11, "Tournament Donate");

    private final int id;
    private final String desc;

    private TransactionTypeEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getTransactionTypeEnumId() {
        return id;
    }

    public String getTransactionTypeEnumDesc() {
        return desc;
    }

    public static TransactionTypeEnum getTransactionTypeEnumById(int id) {
        for (TransactionTypeEnum rel : TransactionTypeEnum.values()) {
            if (rel.getTransactionTypeEnumId()== id) {
                return rel;
            }
        }
        return null;
    }

}
