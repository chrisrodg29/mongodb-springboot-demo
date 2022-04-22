package com.example.mongodb_spring_boot_demo.controller;

import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.customers.*;
import com.example.mongodb_spring_boot_demo.model.customers.Customer;
import com.example.mongodb_spring_boot_demo.service.CustomersService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("api/customers")
public class CustomersController {

    private final CustomersService customersService;

    public CustomersController(CustomersService customersService) {
        this.customersService = customersService;
    }

    @GetMapping("getAllCustomers/V1")
    public ArrayList<Customer> getAllCustomersV1() {
        return customersService.getAllCustomers();
    }

    @GetMapping("getCustomerById/V1")
    public GetCustomerByIdV1Response getCustomerById(@RequestBody GetCustomerByIdV1Request request) {
        return customersService.getCustomerById(request.getCustomerId());
    }

    @GetMapping("getCustomersByAccountNumber/V1")
    public GetCustomersByAccountNumberV1Response getCustomersByAccountNumberV1(@RequestBody GetCustomersByAccountNumberV1Request request) {
        return customersService.getCustomersByAccountNumber(request.getAccountNumber());
    }

    @GetMapping("getCustomerWithAccountDetail/V1")
    public GetCustomerWithAccountDetailV1Response getCustomerWithAccountDetailV1(@RequestBody GetCustomerWithAccountDetailV1Request request) {
        return customersService.getCustomerWithAccountDetail(request.getCustomerId());
    }

    @PostMapping("addTenCustomers/V1")
    public GenericWriteResponse addTenCustomersV1() {
        return customersService.insertTenCustomers();
    }

    @PutMapping("replaceCustomer/V1")
    public GenericWriteResponse replaceCustomerV1(@RequestBody Customer customer) {
        return customersService.replaceCustomer(customer);
    }

    @PutMapping("linkAccountToCustomer/V1")
    public GenericWriteResponse linkAccountToCustomerV1(@RequestBody LinkAccountToCustomerV1Request request) {
        return customersService.linkAccountToCustomer(request);
    }

    @PutMapping("removeAccountFromCustomer/V1")
    public GenericWriteResponse removeAccountFromCustomerV1(@RequestBody RemoveAccountFromCustomerV1Request request) {
        return customersService.removeAccountFromCustomer(request);
    }

    @PutMapping("removeAccountFromAllCustomers/V1")
    public GenericWriteResponse removeAccountFromAllCustomersV1(@RequestBody RemoveAccountFromAllCustomersV1Request request) {
        return customersService.removeAccountFromAllCustomers(request.getAccountNumber());
    }

    @DeleteMapping("deleteAllCustomers/V1")
    public GenericWriteResponse deleteAllCustomersV1() {
        return customersService.deleteAllCustomers();
    }

}
