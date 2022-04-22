package com.example.mongodb_spring_boot_demo.api.customers;

import com.example.mongodb_spring_boot_demo.model.customers.Customer;
import com.example.mongodb_spring_boot_demo.model.customers.CustomerWithAccountDetail;

public class GetCustomerWithAccountDetailV1Response {

    private String operationSuccessStatus;
    private CustomerWithAccountDetail customer;

    public GetCustomerWithAccountDetailV1Response() {}

    public GetCustomerWithAccountDetailV1Response(String operationSuccessStatus, CustomerWithAccountDetail customer) {
        this.operationSuccessStatus = operationSuccessStatus;
        this.customer = customer;
    }

    public String getOperationSuccessStatus() {
        return operationSuccessStatus;
    }

    public void setOperationSuccessStatus(String operationSuccessStatus) {
        this.operationSuccessStatus = operationSuccessStatus;
    }

    public CustomerWithAccountDetail getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerWithAccountDetail customer) {
        this.customer = customer;
    }
}
