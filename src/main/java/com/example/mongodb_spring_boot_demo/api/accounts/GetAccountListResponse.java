package com.example.mongodb_spring_boot_demo.api.accounts;

import com.example.mongodb_spring_boot_demo.model.accounts.Account;

import java.util.List;

public class GetAccountListResponse {

    private String operationSuccessStatus;
    private List<Account> accountList;

    public GetAccountListResponse() {}

    public GetAccountListResponse(String operationSuccessStatus, List<Account> accountList) {
        this.operationSuccessStatus = operationSuccessStatus;
        this.accountList = accountList;
    }

    public String getOperationSuccessStatus() {
        return operationSuccessStatus;
    }

    public void setOperationSuccessStatus(String operationSuccessStatus) {
        this.operationSuccessStatus = operationSuccessStatus;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }
}
