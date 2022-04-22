package com.example.mongodb_spring_boot_demo.api.customers;

public class RemoveAccountFromAllCustomersV1Request {

    private int accountNumber;

    public RemoveAccountFromAllCustomersV1Request() {}

    public RemoveAccountFromAllCustomersV1Request(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }
}
