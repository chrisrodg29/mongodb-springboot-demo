package com.example.mongodb_spring_boot_demo.api.accounts;

import java.math.BigDecimal;

public class UpdateAccountBalanceV1Request {

    private final int accountNumber;
    private final BigDecimal balance;

    public UpdateAccountBalanceV1Request(int accountNumber, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
