package com.example.mongodb_spring_boot_demo.api.customers;

public class DeleteCustomerByIdV1Request {

    private int customerId;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
