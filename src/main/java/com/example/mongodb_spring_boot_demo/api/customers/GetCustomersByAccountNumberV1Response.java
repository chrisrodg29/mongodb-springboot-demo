package com.example.mongodb_spring_boot_demo.api.customers;

import com.example.mongodb_spring_boot_demo.model.customers.Customer;

import java.util.List;

public class GetCustomersByAccountNumberV1Response {

    private String operationSuccessStatus;
    private List<Customer> customerList;

    public GetCustomersByAccountNumberV1Response() {}

    public GetCustomersByAccountNumberV1Response(String operationSuccessStatus, List<Customer> customerList) {
        this.operationSuccessStatus = operationSuccessStatus;
        this.customerList = customerList;
    }

    public String getOperationSuccessStatus() {
        return operationSuccessStatus;
    }

    public void setOperationSuccessStatus(String operationSuccessStatus) {
        this.operationSuccessStatus = operationSuccessStatus;
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }
}
