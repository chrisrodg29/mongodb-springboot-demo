package com.example.mongodb_spring_boot_demo.service;

import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.customers.GetCustomerByIdV1Response;
import com.example.mongodb_spring_boot_demo.api.customers.GetCustomersByAccountNumberV1Response;
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
import static org.junit.jupiter.api.Assertions.*;
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
        ArrayList<Customer> actual = customersService.getAllCustomers();
        assertEquals(expected, actual);
    }

    @Test
    void testGetCustomerById_NullResponse() {
        GetCustomerByIdV1Response response = customersService.getCustomerById(0);
        assertEquals(NO_CUSTOMER_FOUND_MSG, response.getOperationSuccessStatus());
        assertNull(response.getCustomer());
    }

    @Test
    void testGetCustomerById_Success() {
        int customerId = 1;
        Customer expectedCustomer = new Customer();
        when(customersDao.getCustomerById(customerId)).thenReturn(expectedCustomer);

        GetCustomerByIdV1Response response = customersService.getCustomerById(customerId);

        assertEquals(SUCCESS, response.getOperationSuccessStatus());
        assertEquals(expectedCustomer, response.getCustomer());
    }

    @Test
    void testGetCustomersByAccountNumber_NoCustomers() {
        int accountNumber = 1;
        List<Customer> customerList = new ArrayList<>();
        when(customersDao.getCustomersByAccountNumber(accountNumber)).thenReturn(customerList);

        GetCustomersByAccountNumberV1Response response = customersService.getCustomersByAccountNumber(accountNumber);

        assertEquals(NO_ACCOUNT_NUMBER_MATCH_MSG, response.getOperationSuccessStatus());
        assertNull(response.getCustomerList());
    }

    @Test
    void testGetCustomersByAccountNumber_MatchingCustomers() {
        int accountNumber = 1;
        List<Customer> customerList = List.of(new Customer());
        when(customersDao.getCustomersByAccountNumber(accountNumber)).thenReturn(customerList);

        GetCustomersByAccountNumberV1Response response = customersService.getCustomersByAccountNumber(accountNumber);

        assertEquals(SUCCESS, response.getOperationSuccessStatus());
        assertEquals(customerList, response.getCustomerList());
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
        when(fakerService.getNewCustomer(any(Account.class))).thenReturn(new Customer());
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

        for (Account account: accountList) {
            verify(fakerService).getNewCustomer(account);
        }
        verify(customersDao).insertCustomers(customerListCaptor.capture());
        assertEquals(10, customerListCaptor.getValue().size());

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