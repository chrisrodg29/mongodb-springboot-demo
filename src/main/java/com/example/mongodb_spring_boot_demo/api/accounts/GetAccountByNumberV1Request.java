package com.example.mongodb_spring_boot_demo.api.accounts;

public class GetAccountByNumberV1Request {

    private int accountNumber;

    public GetAccountByNumberV1Request() {}

    public GetAccountByNumberV1Request(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

}
