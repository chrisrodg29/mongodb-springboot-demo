package com.example.mongodb_spring_boot_demo.api.customers;

public class RemoveAccountFromCustomerV1Request {

    private int customerId;
    private int accountNumber;

    public RemoveAccountFromCustomerV1Request() {}

    public RemoveAccountFromCustomerV1Request(int customerId, int accountNumber) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }
}
