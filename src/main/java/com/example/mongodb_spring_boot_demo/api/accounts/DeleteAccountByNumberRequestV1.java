package com.example.mongodb_spring_boot_demo.api.accounts;

public class DeleteAccountByNumberRequestV1 {

    private int accountNumber;

    public DeleteAccountByNumberRequestV1() {}

    public DeleteAccountByNumberRequestV1(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }
}
