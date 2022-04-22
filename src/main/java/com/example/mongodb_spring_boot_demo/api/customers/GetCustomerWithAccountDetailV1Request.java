package com.example.mongodb_spring_boot_demo.api.customers;

public class GetCustomerWithAccountDetailV1Request {

    private int customerId;

    public GetCustomerWithAccountDetailV1Request() {}

    public GetCustomerWithAccountDetailV1Request(int customerId) {
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
