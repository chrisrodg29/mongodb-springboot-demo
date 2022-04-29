package com.example.mongodb_spring_boot_demo.api.accounts;

import com.example.mongodb_spring_boot_demo.model.accounts.AccountType;

public class GetTopKLargestAccountsV1Request {

    private int k;
    private AccountType accountType;

    public GetTopKLargestAccountsV1Request(int k, AccountType accountType) {
        this.k = k;
        this.accountType = accountType;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
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
                "number=" + k +
                ", accountType=" + accountType +
                '}';
    }
}
