package com.example.mongodb_spring_boot_demo.service;

import com.example.mongodb_spring_boot_demo.api.GenericReadResponse;
import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.accounts.UpdateAccountBalanceV1Request;
import com.example.mongodb_spring_boot_demo.dao.accounts.AccountsDao;
import com.example.mongodb_spring_boot_demo.dao.accounts.AccountTransformationsDao;
import com.example.mongodb_spring_boot_demo.dao.customers.CustomersDao;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountBucket;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountTotalsSummary;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountType;
import com.mongodb.MongoWriteException;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.example.mongodb_spring_boot_demo.MockConstants.MOCK_EXCEPTION_MESSAGE;
import static com.example.mongodb_spring_boot_demo.service.AccountsService.*;
import static com.example.mongodb_spring_boot_demo.util.MongoExceptionHelper.SUCCESS;
import static com.mongodb.assertions.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class AccountsServiceTest {
    
    @InjectMocks
    AccountsService accountsService;

    @Mock
    FakerService fakerService;
    @Mock
    AccountsDao accountsDao;
    @Mock
    AccountTransformationsDao accountTransformationsDao;
    @Mock
    CustomersDao customersDao;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void testGetAllAccounts() {
        List<Account> expected = new ArrayList<>();
        when(accountsDao.getAllAccounts()).thenReturn(expected);

        GenericReadResponse<List<Account>> response = accountsService.getAllAccounts();

        assertEquals(expected, response.getData());
    }

    @Test
    void testGetAccountByNumber_NoMatchingAccount() {
        when(accountsDao.getAccountByAccountNumber(0))
                .thenReturn(null);

        GenericReadResponse<Account> response = accountsService.getAccountByNumber(0);

        assertEquals(ACCOUNT_NOT_FOUND_MSG, response.getOperationSuccessStatus());
        assertNull(response.getData());
    }

    @Test
    void testGetAccountByNumber_MatchingAccount() {
        int accountNumber = 1;
        Account expectedAccount = new Account();
        when(accountsDao.getAccountByAccountNumber(accountNumber))
                .thenReturn(expectedAccount);

        GenericReadResponse<Account> response = accountsService.getAccountByNumber(accountNumber);

        assertEquals(SUCCESS, response.getOperationSuccessStatus());
        assertEquals(expectedAccount, response.getData());
    }

    @Test
    void testInsertTenAccounts_AccountInsertException() {
        MongoWriteException e = mock(MongoWriteException.class);
        when(e.getMessage()).thenReturn(MOCK_EXCEPTION_MESSAGE);
        when(accountsDao.insertAccounts(any())).thenThrow(e);

        GenericWriteResponse response = accountsService.insertTenAccounts();

        assertEquals(ERROR_INSERTING_ACCOUNTS_MSG, response.getResponseText());
        assertEquals(MOCK_EXCEPTION_MESSAGE, response.getExceptionMessage());
    }

    @Test
    void testInsertTenAccounts_Successful() {
        List<Account> accountList = new ArrayList<>();

        GenericWriteResponse response = accountsService.insertTenAccounts();

        verify(accountsDao).insertAccounts(accountList);
        assertEquals(NEW_ACCOUNTS_INSERTED_MSG, response.getResponseText());
    }

    private UpdateAccountBalanceV1Request createUpdateAccountBalanceRequest() {
        int accountNumber = 1;
        BigDecimal balance = new BigDecimal("2.00");
        return new UpdateAccountBalanceV1Request(
                accountNumber,
                balance
        );
    }

    @Test
    void testUpdateAccountBalanceV1_NoMatches() {
        UpdateAccountBalanceV1Request request = createUpdateAccountBalanceRequest();
        UpdateResult result = mock(UpdateResult.class);
        when(accountsDao.updateBalanceByAccountNumber(request.getAccountNumber(), request.getBalance()))
                .thenReturn(result);
        when(result.getMatchedCount()).thenReturn(0L);

        GenericWriteResponse response = accountsService.updateAccountBalanceV1(request);

        assertEquals(ACCOUNT_NOT_FOUND_MSG, response.getResponseText());
    }

    @Test
    void testUpdateAccountBalanceV1_Match() {
        UpdateAccountBalanceV1Request request = createUpdateAccountBalanceRequest();
        UpdateResult result = mock(UpdateResult.class);
        when(accountsDao.updateBalanceByAccountNumber(request.getAccountNumber(), request.getBalance()))
                .thenReturn(result);
        when(result.getMatchedCount()).thenReturn(1L);

        GenericWriteResponse response = accountsService.updateAccountBalanceV1(request);

        assertEquals(ACCOUNT_BALANCE_UPDATED_MSG, response.getResponseText());
    }

    @Test
    void testUpdateAccountBalanceV1_Exception() {
        UpdateAccountBalanceV1Request request = createUpdateAccountBalanceRequest();
        MongoWriteException e = mock(MongoWriteException.class);
        when(e.getMessage()).thenReturn(MOCK_EXCEPTION_MESSAGE);
        when(accountsDao.updateBalanceByAccountNumber(request.getAccountNumber(), request.getBalance()))
                .thenThrow(e);

        GenericWriteResponse response = accountsService.updateAccountBalanceV1(request);

        assertEquals(ERROR_UPDATING_ACCOUNT_MSG, response.getResponseText());
        assertEquals(MOCK_EXCEPTION_MESSAGE, response.getExceptionMessage());
    }

    @Test
    void testDeleteAccountByNumber_AccountNotFound() {
        int accountNumber = 1;
        when(accountsDao.deleteAccountByAccountNumber(accountNumber)).thenReturn(false);

        GenericWriteResponse response = accountsService.deleteAccountByNumber(accountNumber);

        assertEquals(ACCOUNT_NOT_FOUND_MSG, response.getResponseText());
    }

    @Test
    void testDeleteAccountByNumber_CustomerNotFound() {
        int accountNumber = 1;
        when(accountsDao.deleteAccountByAccountNumber(accountNumber)).thenReturn(true);
        when(customersDao.removeAccountFromAllCustomers(accountNumber)).thenReturn(false);

        GenericWriteResponse response = accountsService.deleteAccountByNumber(accountNumber);

        assertEquals(ACCOUNT_DELETED_MSG + NO_MATCHING_CUSTOMER_MSG, response.getResponseText());
    }

    @Test
    void testDeleteAccountByNumber_Success() {
        int accountNumber = 1;
        when(accountsDao.deleteAccountByAccountNumber(accountNumber)).thenReturn(true);
        when(customersDao.removeAccountFromAllCustomers(accountNumber)).thenReturn(true);

        GenericWriteResponse response = accountsService.deleteAccountByNumber(accountNumber);

        assertEquals(ACCOUNT_DELETED_MSG, response.getResponseText());
    }

    @Test
    void testDeleteAccountByNumber_Exception() {
        int accountNumber = 1;
        MongoWriteException e = mock(MongoWriteException.class);
        when(e.getMessage()).thenReturn(MOCK_EXCEPTION_MESSAGE);
        when(accountsDao.deleteAccountByAccountNumber(accountNumber)).thenThrow(e);

        GenericWriteResponse response = accountsService.deleteAccountByNumber(accountNumber);

        assertEquals(ERROR_DELETING_AN_ACCOUNT_MSG, response.getResponseText());
        assertEquals(MOCK_EXCEPTION_MESSAGE, response.getExceptionMessage());
    }

    @Test
    void testDeleteAllAccounts_Success() {
        GenericWriteResponse response = accountsService.deleteAllAccounts();
        assertEquals(ALL_ACCOUNTS_DELETED_MSG, response.getResponseText());
    }

    @Test
    void testDeleteAllAccounts_Exception() {
        MongoWriteException e = mock(MongoWriteException.class);
        when(e.getMessage()).thenReturn(MOCK_EXCEPTION_MESSAGE);
        when(accountsDao.deleteAllAccounts()).thenThrow(e);

        GenericWriteResponse response = accountsService.deleteAllAccounts();

        assertEquals(ERROR_DELETING_ACCOUNTS_MSG, response.getResponseText());
        assertEquals(MOCK_EXCEPTION_MESSAGE, response.getExceptionMessage());
    }

    @Test
    void testGetAccountTotalsSummaryListV1() {
        List<AccountTotalsSummary> expectedList = new ArrayList<>();
        when(accountTransformationsDao.getAccountTotalsSummaryListV1()).thenReturn(expectedList);

        GenericReadResponse<List<AccountTotalsSummary>> response = accountsService.getAccountTotalsSummaryListV1();

        assertEquals(expectedList, response.getData());
    }

    @Test
    void testGetAccountTotalsSummaryListV2() {
        AccountType accountType = AccountType.SAVINGS;
        int numberOfAccounts = 5;
        BigDecimal balancesTotal = new BigDecimal("2.00");
        Document returnedDocument = new Document("_id", accountType)
                .append("numberOfAccounts", numberOfAccounts)
                .append("balancesTotal", new Decimal128(balancesTotal));
        List<Document> documentList = List.of(returnedDocument);
        when(accountTransformationsDao.getAccountTotalsSummaryListV2()).thenReturn(documentList);

        GenericReadResponse<List<AccountTotalsSummary>> response =
                accountsService.getAccountTotalsSummaryListV2();

        AccountTotalsSummary summary = response.getData().get(0);
        assertEquals(accountType, summary.getAccountType());
        assertEquals(numberOfAccounts, summary.getNumberOfAccounts());
        assertEquals(balancesTotal, summary.getBalancesTotal());
    }

    @Test
    void testGetAccountBucketSummaryV1() {
        int[] accountsPerBucket = {7, 8, 9, 10, 22};
        List<Document> documentList = new ArrayList<>();
        for (int number : accountsPerBucket) {
            documentList.add(new Document("numberOfAccounts", number));
        }
        when(accountTransformationsDao.getAccountBucketsByBoundaries(any())).thenReturn(documentList);

        GenericReadResponse<List<AccountBucket>> response = accountsService.getAccountBucketSummaryV1();

        List<AccountBucket> pojoBucketList = response.getData();
        String[] expectedBucketRanges = {
                "At least 0, Less than 200000",
                "At least 200000, Less than 400000",
                "At least 400000, Less than 600000",
                "At least 600000, Less than 800000",
                "At least 800000, Less than 1000000",
        };
        int count = 0;
        while (count < 5) {
            assertEquals(expectedBucketRanges[count], pojoBucketList.get(count).getBalanceRange());
            assertEquals(accountsPerBucket[count], pojoBucketList.get(count).getNumberOfAccounts());
            count++;
        }
    }

}