package com.example.mongodb_spring_boot_demo.service;

import com.example.mongodb_spring_boot_demo.api.GenericReadResponse;
import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.customers.LinkAccountToCustomerV1Request;
import com.example.mongodb_spring_boot_demo.dao.accounts.AccountsDao;
import com.example.mongodb_spring_boot_demo.dao.customers.CustomersDao;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.example.mongodb_spring_boot_demo.model.customers.Customer;
import com.mongodb.MongoWriteException;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static com.example.mongodb_spring_boot_demo.service.CustomersService.*;
import static com.example.mongodb_spring_boot_demo.service.MongoExceptionHelper.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CustomersServiceTest {

    @InjectMocks
    CustomersService customersService;

    @Mock
    CustomersDao customersDao;
    @Mock
    AccountsDao accountsDao;
    @Mock
    FakerService fakerService;
    @Captor
    ArgumentCaptor<List<Customer>> customerListCaptor;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void testGetAllCustomers() {
        ArrayList<Customer> expected = new ArrayList<>();
        when(customersDao.getAllCustomers()).thenReturn(expected);
        GenericReadResponse<List<Customer>> actual = customersService.getAllCustomers();
        assertEquals(expected, actual.getData());
    }

    @Test
    void testGetCustomerById_NullResponse() {
        GenericReadResponse<Customer> response = customersService.getCustomerById(0);
        assertEquals(NO_CUSTOMER_FOUND_MSG, response.getOperationSuccessStatus());
        assertNull(response.getData());
    }

    @Test
    void testGetCustomerById_Success() {
        int customerId = 1;
        Customer expectedCustomer = new Customer();
        when(customersDao.getCustomerById(customerId)).thenReturn(expectedCustomer);

        GenericReadResponse<Customer> response = customersService.getCustomerById(customerId);

        assertEquals(SUCCESS, response.getOperationSuccessStatus());
        assertEquals(expectedCustomer, response.getData());
    }

    @Test
    void testGetCustomersByAccountNumber_NoCustomers() {
        int accountNumber = 1;
        List<Customer> customerList = new ArrayList<>();
        when(customersDao.getCustomersByAccountNumber(accountNumber)).thenReturn(customerList);

        GenericReadResponse<List<Customer>> response =
                customersService.getCustomersByAccountNumber(accountNumber);

        assertEquals(NO_ACCOUNT_NUMBER_MATCH_MSG, response.getOperationSuccessStatus());
        assertEquals(0, response.getData().size());
    }

    @Test
    void testGetCustomersByAccountNumber_MatchingCustomers() {
        int accountNumber = 1;
        List<Customer> customerList = List.of(new Customer());
        when(customersDao.getCustomersByAccountNumber(accountNumber)).thenReturn(customerList);

        GenericReadResponse<List<Customer>> response =
                customersService.getCustomersByAccountNumber(accountNumber);

        assertEquals(SUCCESS, response.getOperationSuccessStatus());
        assertEquals(customerList, response.getData());
    }

    private List<Account> stubFakerServiceGetNewAccounts() {
        List<Account> accountList = new ArrayList<>();
        int count = 0;
        while (count < 10) {
            accountList.add(new Account());
            count++;
        }
        when(fakerService.getNewAccounts(10)).thenReturn(accountList);
        return accountList;
    }

    private void stubGetNewCustomer() {
        when(fakerService.getNewCustomers(any())).thenReturn(new ArrayList<>());
    }

    @Test
    void testInsertTenCustomers_AccountInsertFailure() {
        List<Account> accountList = stubFakerServiceGetNewAccounts();
        MongoWriteException e = mock(MongoWriteException.class);
        when(accountsDao.insertAccounts(accountList)).thenThrow(e);

        GenericWriteResponse response = customersService.insertTenCustomers();

        assertEquals(
                ERROR_INSERTING_ACCOUNTS_MSG,
                response.getResponseText()
        );
        assertEquals(e, response.getException());
    }

    @Test
    void testInsertTenCustomers_Success() {
        List<Account> accountList = stubFakerServiceGetNewAccounts();
        stubGetNewCustomer();

        GenericWriteResponse response = customersService.insertTenCustomers();

        verify(fakerService).getNewCustomers(accountList);
        verify(customersDao).insertCustomers(customerListCaptor.capture());

        assertEquals(
                NEW_CUSTOMERS_INSERTED_MSG,
                response.getResponseText()
        );
    }

    @Test
    void testInsertTenCustomers_Exception() {
        stubFakerServiceGetNewAccounts();
        stubGetNewCustomer();
        MongoWriteException e = mock(MongoWriteException.class);
        doThrow(e).when(customersDao).insertCustomers(any());

        GenericWriteResponse response = customersService.insertTenCustomers();

        assertEquals(ERROR_INSERTING_CUSTOMERS_MSG, response.getResponseText());
        assertEquals(e, response.getException());
    }

    // skipped replaceCustomer method

    @Test
    void testLinkAccountToCustomer_NoAccountFound() {
        LinkAccountToCustomerV1Request request = new LinkAccountToCustomerV1Request();
        request.setCustomerId(1);
        request.setAccountNumber(2);
        when(accountsDao.getAccountByAccountNumber(request.getAccountNumber()))
                .thenReturn(null);

        GenericWriteResponse response = customersService.linkAccountToCustomer(request);

        assertEquals(NO_ACCOUNT_FOUND_MSG, response.getResponseText());
    }

    @Test
    void testLinkAccountToCustomer_NoCustomerFound() {
        LinkAccountToCustomerV1Request request = new LinkAccountToCustomerV1Request();
        request.setCustomerId(1);
        request.setAccountNumber(2);
        when(accountsDao.getAccountByAccountNumber(request.getAccountNumber()))
                .thenReturn(new Account());
        UpdateResult updateResult = mock(UpdateResult.class);
        when(customersDao.addAccountNumberToCustomer(request.getCustomerId(), request.getAccountNumber()))
                .thenReturn(updateResult);
        when(updateResult.getMatchedCount()).thenReturn(0L);

        GenericWriteResponse response = customersService.linkAccountToCustomer(request);

        assertEquals(NO_CUSTOMER_FOUND_MSG, response.getResponseText());
    }

    @Test
    void testLinkAccountToCustomer_Success() {
        LinkAccountToCustomerV1Request request = new LinkAccountToCustomerV1Request();
        request.setCustomerId(1);
        request.setAccountNumber(2);
        when(accountsDao.getAccountByAccountNumber(request.getAccountNumber()))
                .thenReturn(new Account());
        UpdateResult updateResult = mock(UpdateResult.class);
        when(customersDao.addAccountNumberToCustomer(request.getCustomerId(), request.getAccountNumber()))
                .thenReturn(updateResult);
        when(updateResult.getMatchedCount()).thenReturn(1L);

        GenericWriteResponse response = customersService.linkAccountToCustomer(request);

        assertEquals(ACCOUNT_LINKED_MSG, response.getResponseText());
    }

    @Test
    void testLinkAccountToCustomer_Exception() {
        LinkAccountToCustomerV1Request request = new LinkAccountToCustomerV1Request();
        request.setCustomerId(1);
        request.setAccountNumber(2);
        when(accountsDao.getAccountByAccountNumber(request.getAccountNumber()))
                .thenReturn(new Account());
        MongoWriteException e = mock(MongoWriteException.class);
        doThrow(e).when(customersDao).addAccountNumberToCustomer(anyInt(), anyInt());

        GenericWriteResponse response = customersService.linkAccountToCustomer(request);

        assertEquals(ERROR_LINKING_ACCOUNT_MSG, response.getResponseText());
        assertEquals(e, response.getException());
    }

}