package com.example.mongodb_spring_boot_demo.api.customers;

public class GetCustomerByIdV1Request {

    private int customerId;

    public GetCustomerByIdV1Request() {}

    public GetCustomerByIdV1Request(int customerId) {
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
