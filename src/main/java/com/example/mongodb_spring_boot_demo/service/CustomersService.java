package com.example.mongodb_spring_boot_demo.service;

import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.customers.*;
import com.example.mongodb_spring_boot_demo.dao.accounts.AccountsDao;
import com.example.mongodb_spring_boot_demo.dao.customers.CustomersDao;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.example.mongodb_spring_boot_demo.model.customers.Customer;
import com.example.mongodb_spring_boot_demo.model.customers.CustomerWithAccountDetail;
import com.mongodb.MongoWriteException;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomersService {

    static final String SUCCESS = "success";
    static final String NO_ACCOUNT_FOUND_MSG = "That account number was not found";
    static final String NO_CUSTOMER_FOUND_MSG = "No customer was found with that id";
    static final String NO_ACCOUNT_NUMBER_MATCH_MSG = "No customers were found with that account number";
    static final String ERROR_INSERTING_ACCOUNTS_MSG = "Error inserting accounts; No customers added";
    static final String NEW_CUSTOMERS_INSERTED_MSG = "New customers inserted";
    static final String ERROR_INSERTING_CUSTOMERS_MSG = "Error inserting customers";
    static final String ACCOUNT_LINKED_MSG = "Account linked to customer";
    static final String ERROR_LINKING_ACCOUNT_MSG = "Error linking account to customer";

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomersService.class);

    private final CustomersDao customersDao;
    private final AccountsDao accountsDao;
    private final FakerService fakerService;

    public CustomersService(CustomersDao customersDao, AccountsDao accountsDao, FakerService fakerService) {
        this.customersDao = customersDao;
        this.accountsDao = accountsDao;
        this.fakerService = fakerService;
    }

    public ArrayList<Customer> getAllCustomers() {
        return customersDao.getAllCustomers();
    }

    public GetCustomerByIdV1Response getCustomerById(int customerId) {
        Customer customer = customersDao.getCustomerById(customerId);
        if (customer == null) {
            return new GetCustomerByIdV1Response(
                    NO_CUSTOMER_FOUND_MSG,
                    null
            );
        } else {
            return new GetCustomerByIdV1Response(
                    SUCCESS,
                    customer
            );
        }
    }

    public GetCustomersByAccountNumberV1Response getCustomersByAccountNumber(int accountNumber) {
        List<Customer> customerList = customersDao.getCustomersByAccountNumber(accountNumber);
        if (customerList.isEmpty()) {
            return new GetCustomersByAccountNumberV1Response(
                    NO_ACCOUNT_NUMBER_MATCH_MSG,
                    null
            );
        } else {
            return new GetCustomersByAccountNumberV1Response(
                    SUCCESS,
                    customerList
            );
        }
    }

    public GetCustomerWithAccountDetailV1Response getCustomerWithAccountDetail(int customerId) {
        CustomerWithAccountDetail customerList =
                customersDao.getCustomerWithAccountDetailById(customerId);
        if (customerList == null) {
            return new GetCustomerWithAccountDetailV1Response(
                    NO_CUSTOMER_FOUND_MSG,
                    null
            );
        } else {
            return new GetCustomerWithAccountDetailV1Response(
                    SUCCESS,
                    customerList
            );
        }
    }

    public GenericWriteResponse insertTenCustomers() {
        List<Account> accountList = fakerService.getNewAccounts(10);
        try {
            accountsDao.insertAccounts(accountList);
        } catch (MongoWriteException e) {
            String errorMessage = ERROR_INSERTING_ACCOUNTS_MSG;
            LOGGER.error(errorMessage, e);
            return new GenericWriteResponse(errorMessage, e);
        }

        List<Customer> customerList = new ArrayList<>();
        int count = 0;
        while (count < accountList.size()) {
            customerList.add(fakerService.getNewCustomer(
                    accountList.get(count)
            ));
            count++;
        }

        try {
            customersDao.insertCustomers(customerList);
            return new GenericWriteResponse(NEW_CUSTOMERS_INSERTED_MSG);
        } catch (MongoWriteException e) {
            String errorMessage = ERROR_INSERTING_CUSTOMERS_MSG;
            LOGGER.error(errorMessage, e);
            return new GenericWriteResponse(errorMessage, e);
        }
    }

    public GenericWriteResponse replaceCustomer(Customer customer) {
        try {
            UpdateResult result = customersDao.replaceCustomer(customer);
            String responseText;
            if (result.getMatchedCount() == 0) {
                responseText = NO_CUSTOMER_FOUND_MSG;
            } else {
                responseText = "Customer entry updated";
            }
            return new GenericWriteResponse(responseText);
        } catch (MongoWriteException e) {
            String errorMessage = "Error updating customer";
            LOGGER.error(errorMessage, e);
            return new GenericWriteResponse(errorMessage, e);
        }
    }

    public GenericWriteResponse linkAccountToCustomer(LinkAccountToCustomerV1Request request) {
        Account account = accountsDao.getAccountByAccountNumber(request.getAccountNumber());
        if (account == null) {
            return new GenericWriteResponse(NO_ACCOUNT_FOUND_MSG);
        }
        try {
            UpdateResult result = customersDao.addAccountNumberToCustomer(
                    request.getCustomerId(),
                    request.getAccountNumber()
            );
            String responseText;
            if (result.getMatchedCount() == 0) {
                responseText = NO_CUSTOMER_FOUND_MSG;
            } else {
                responseText = ACCOUNT_LINKED_MSG;
            }
            return new GenericWriteResponse(responseText);
        } catch (MongoWriteException e) {
            String errorMessage = ERROR_LINKING_ACCOUNT_MSG;
            LOGGER.error(errorMessage, e);
            return new GenericWriteResponse(errorMessage, e);
        }
    }

    public GenericWriteResponse removeAccountFromCustomer(RemoveAccountFromCustomerV1Request request) {
        try {
            UpdateResult result = customersDao.removeAccountFromCustomer(
                    request.getCustomerId(),
                    request.getAccountNumber()
            );
            String responseText;
            if (result.getMatchedCount() == 0) {
                responseText = NO_CUSTOMER_FOUND_MSG;
            } else if (result.getModifiedCount() == 0) {
                responseText = "That account is not linked to that customer";
            } else {
                responseText = "Account removed from customer";
            }
            return new GenericWriteResponse(responseText);
        } catch (MongoWriteException e) {
            String errorMessage = "Error removing account from customer";
            LOGGER.error(errorMessage, e);
            return new GenericWriteResponse(errorMessage, e);
        }
    }

    public GenericWriteResponse removeAccountFromAllCustomers(int accountNumber) {
        try {
            customersDao.removeAccountFromAllCustomers(accountNumber);
            return new GenericWriteResponse("Account removed from all customers");
        } catch (MongoWriteException e) {
            String errorMessage = "Error removing account from customers";
            LOGGER.error(errorMessage, e);
            return new GenericWriteResponse(errorMessage, e);
        }
    }

    public GenericWriteResponse deleteAllCustomers() {
        try {
            customersDao.deleteAllCustomers();
            return new GenericWriteResponse("All customers deleted");
        } catch (MongoWriteException e) {
            String errorMessage = "Error deleting customers";
            LOGGER.error(errorMessage, e);
            return new GenericWriteResponse(errorMessage, e);
        }
    }

}
