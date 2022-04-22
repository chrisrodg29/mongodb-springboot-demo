package com.example.mongodb_spring_boot_demo.api.customers;

public class GetCustomersByAccountNumberV1Request {

    private int accountNumber;

    public GetCustomersByAccountNumberV1Request() {}

    public GetCustomersByAccountNumberV1Request(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }
}
