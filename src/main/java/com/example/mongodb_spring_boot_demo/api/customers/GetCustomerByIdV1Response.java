package com.example.mongodb_spring_boot_demo.api.customers;

import com.example.mongodb_spring_boot_demo.model.customers.Customer;

public class GetCustomerByIdV1Response {

    private String operationSuccessStatus;
    private Customer customer;

    public GetCustomerByIdV1Response() {}

    public GetCustomerByIdV1Response(String operationSuccessStatus, Customer customer) {
        this.operationSuccessStatus = operationSuccessStatus;
        this.customer = customer;
    }

    public String getOperationSuccessStatus() {
        return operationSuccessStatus;
    }

    public void setOperationSuccessStatus(String operationSuccessStatus) {
        this.operationSuccessStatus = operationSuccessStatus;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
