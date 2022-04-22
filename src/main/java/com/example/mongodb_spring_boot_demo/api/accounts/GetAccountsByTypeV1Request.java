package com.example.mongodb_spring_boot_demo.api.accounts;

import com.example.mongodb_spring_boot_demo.model.accounts.AccountType;

public class GetAccountsByTypeV1Request {

    private AccountType accountType;

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
