package com.example.mongodb_spring_boot_demo.service;

import com.example.mongodb_spring_boot_demo.api.GenericReadResponse;
import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.customers.LinkAccountToCustomerV1Request;
import com.example.mongodb_spring_boot_demo.api.customers.RemoveAccountFromCustomerV1Request;
import com.example.mongodb_spring_boot_demo.dao.accounts.AccountsDao;
import com.example.mongodb_spring_boot_demo.dao.customers.CustomersDao;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.example.mongodb_spring_boot_demo.model.customers.Customer;
import com.example.mongodb_spring_boot_demo.model.customers.CustomerWithAccountDetail;
import com.mongodb.client.result.UpdateResult;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.mongodb_spring_boot_demo.util.MongoExceptionHelper.*;

@Service
public class CustomersService {

    static final String NO_ACCOUNT_FOUND_MSG = "That account number was not found";
    static final String NO_CUSTOMER_FOUND_MSG = "No customer was found with that id";
    static final String NO_ACCOUNT_NUMBER_MATCH_MSG = "No customers were found with that account number";
    static final String ERROR_INSERTING_ACCOUNTS_MSG = "Error inserting accounts; No customers added";
    static final String NEW_CUSTOMERS_INSERTED_MSG = "New customers inserted";
    static final String ERROR_INSERTING_CUSTOMERS_MSG = "Error inserting customers";
    static final String CUSTOMER_UPDATED_MSG = "Customer entry updated";
    static final String ERROR_UPDATING_CUSTOMER_MSG = "Error updating customer";
    static final String ACCOUNT_LINKED_MSG = "Account linked to customer";
    static final String ERROR_LINKING_ACCOUNT_MSG = "Error linking account to customer";
    static final String ACCOUNT_NOT_LINKED_MSG = "That account is not linked to that customer";
    static final String ACCOUNT_REMOVED_FROM_ONE_MSG = "Account removed from customer";
    static final String ACCOUNT_REMOVED_FROM_ALL_MSG = "Account removed from all customers";
    static final String ERROR_REMOVING_ACCOUNT_MSG = "Error removing account";
    static final String CUSTOMER_DELETED_MSG = "Customer was deleted";
    static final String ERROR_DELETING_CUSTOMER_MSG = "Error deleting customer";
    static final String ALL_CUSTOMERS_DELETED_MSG = "All customers deleted";
    static final String ERROR_DELETING_CUSTOMERS_MSG = "Error deleting customers";

    private final CustomersDao customersDao;
    private final AccountsDao accountsDao;
    private final FakerService fakerService;

    public CustomersService(CustomersDao customersDao, AccountsDao accountsDao, FakerService fakerService) {
        this.customersDao = customersDao;
        this.accountsDao = accountsDao;
        this.fakerService = fakerService;
    }

    public GenericReadResponse<List<Customer>> getAllCustomers() {
        return safeRead(customersDao::getAllCustomers);
    }

    public GenericReadResponse<Customer> getCustomerById(int customerId) {
        return safeRead(
                () -> customersDao.getCustomerById(customerId),
                NO_CUSTOMER_FOUND_MSG
        );
    }

    public GenericReadResponse<List<Customer>> getCustomersByAccountNumber(int accountNumber) {
        return safeRead(
                () -> customersDao.getCustomersByAccountNumber(accountNumber),
                NO_ACCOUNT_NUMBER_MATCH_MSG
        );
    }

    public GenericReadResponse<CustomerWithAccountDetail> getCustomerWithAccountDetail(int customerId) {
        return safeRead(
                () -> customersDao.getCustomerWithAccountDetailById(customerId),
                NO_CUSTOMER_FOUND_MSG
        );
    }

    public GenericWriteResponse insertTenCustomers() {
        List<Account> accountList = fakerService.getNewAccounts(10);
        GenericWriteResponse firstResponse = safeWrite(() -> {
            accountsDao.insertAccounts(accountList);
            return SUCCESS;
        });
        if (!firstResponse.getResponseText().equals(SUCCESS)) {
            firstResponse.setResponseText(ERROR_INSERTING_ACCOUNTS_MSG);
            return firstResponse;
        }

        List<Customer> customerList = fakerService.getNewCustomers(accountList);
        GenericWriteResponse secondResponse = safeWrite(() -> {
            customersDao.insertCustomers(customerList);
            return NEW_CUSTOMERS_INSERTED_MSG;
        });
        if (!secondResponse.getResponseText().equals(NEW_CUSTOMERS_INSERTED_MSG)) {
            secondResponse.setResponseText(ERROR_INSERTING_CUSTOMERS_MSG);
        }
        return secondResponse;
    }

    public GenericWriteResponse replaceCustomer(Customer customer) {
        return safeWrite(() -> {
            UpdateResult result = customersDao.replaceCustomer(customer);
            if (result.getMatchedCount() == 0) {
                return NO_CUSTOMER_FOUND_MSG;
            } else {
                return CUSTOMER_UPDATED_MSG;
            }
        }, ERROR_UPDATING_CUSTOMER_MSG);
    }

    public GenericWriteResponse linkAccountToCustomer(LinkAccountToCustomerV1Request request) {
        GenericReadResponse<Account> readResponse = safeRead(
                () -> accountsDao.getAccountByAccountNumber(request.getAccountNumber())
        );
        if (readResponse.getExceptionMessage() != null) {
            return new GenericWriteResponse(
                    GENERIC_WRITE_ERROR,
                    readResponse.getExceptionMessage()
            );
        } else if (readResponse.getData() == null) {
            return new GenericWriteResponse(NO_ACCOUNT_FOUND_MSG);
        }
        return safeWrite(() -> {
            UpdateResult result = customersDao.addAccountNumberToCustomer(
                    request.getCustomerId(),
                    request.getAccountNumber()
            );
            if (result.getMatchedCount() == 0) {
                return NO_CUSTOMER_FOUND_MSG;
            } else {
                return ACCOUNT_LINKED_MSG;
            }
        }, ERROR_LINKING_ACCOUNT_MSG);
    }

    public GenericWriteResponse removeAccountFromCustomer(RemoveAccountFromCustomerV1Request request) {
        return safeWrite(() -> {
            UpdateResult result = customersDao.removeAccountFromCustomer(
                    request.getCustomerId(),
                    request.getAccountNumber()
            );
            if (result.getMatchedCount() == 0) {
                return NO_CUSTOMER_FOUND_MSG;
            } else if (result.getModifiedCount() == 0) {
                return ACCOUNT_NOT_LINKED_MSG;
            } else {
                return ACCOUNT_REMOVED_FROM_ONE_MSG;
            }
        }, ERROR_REMOVING_ACCOUNT_MSG);
    }

    public GenericWriteResponse removeAccountFromAllCustomers(int accountNumber) {
        return safeWrite(() -> {
            customersDao.removeAccountFromAllCustomers(accountNumber);
            return ACCOUNT_REMOVED_FROM_ALL_MSG;
        }, ERROR_REMOVING_ACCOUNT_MSG);
    }

    public GenericWriteResponse deleteCustomerById(int customerId) {
        return safeWrite(() -> {
            boolean deleted = customersDao.deleteCustomerById(customerId);
            if (deleted) {
                return CUSTOMER_DELETED_MSG;
            } else {
                return NO_CUSTOMER_FOUND_MSG;
            }
        }, ERROR_DELETING_CUSTOMER_MSG);
    }

    public GenericWriteResponse deleteAllCustomers() {
        return safeWrite(() -> {
            customersDao.deleteAllCustomers();
            return ALL_CUSTOMERS_DELETED_MSG;
        }, ERROR_DELETING_CUSTOMERS_MSG);
    }

}
