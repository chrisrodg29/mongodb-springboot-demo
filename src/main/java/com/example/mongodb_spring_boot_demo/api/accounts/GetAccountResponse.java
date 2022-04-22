package com.example.mongodb_spring_boot_demo.api.accounts;

import com.example.mongodb_spring_boot_demo.model.accounts.Account;

public class GetAccountResponse {

    private String operationSuccessStatus;
    private Account account;

    public GetAccountResponse() {}

    public GetAccountResponse(String operationSuccessStatus, Account account) {
        this.operationSuccessStatus = operationSuccessStatus;
        this.account = account;
    }

    public String getOperationSuccessStatus() {
        return operationSuccessStatus;
    }

    public void setOperationSuccessStatus(String operationSuccessStatus) {
        this.operationSuccessStatus = operationSuccessStatus;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
