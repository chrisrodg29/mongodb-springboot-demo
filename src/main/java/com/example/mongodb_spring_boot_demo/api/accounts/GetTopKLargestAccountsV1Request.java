package com.example.mongodb_spring_boot_demo.api.accounts;

import com.example.mongodb_spring_boot_demo.model.accounts.AccountType;

public class GetTopKLargestAccountsV1Request {

    private int number;
    private AccountType accountType;

    public GetTopKLargestAccountsV1Request(int number, AccountType accountType) {
        this.number = number;
        this.accountType = accountType;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    @Override
    public String toString() {
        return "GetTopKLargestAccountsV1Request{" +
                "number=" + number +
                ", accountType=" + accountType +
                '}';
    }
}
