package com.example.mongodb_spring_boot_demo.service;

import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.accounts.*;
import com.example.mongodb_spring_boot_demo.dao.accounts.AccountsDao;
import com.example.mongodb_spring_boot_demo.dao.accounts.AccountsSummaryDao;
import com.example.mongodb_spring_boot_demo.dao.customers.CustomersDao;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountBucket;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountTotalsSummary;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountType;
import com.mongodb.MongoException;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class AccountsService {

    static final String SUCCESS = "success";
    static final String GENERIC_RETRIEVAL_ERROR = "An error occurred while retrieving data";
    static final String ACCOUNT_NOT_FOUND_MSG = "No account with that number was found";
    static final String NEW_ACCOUNTS_INSERTED_MSG = "New Accounts inserted";
    static final String ERROR_INSERTING_ACCOUNTS_MSG = "Error inserting accounts";
    static final String ACCOUNT_BALANCE_UPDATED_MSG = "Account balance updated";
    static final String ERROR_UPDATING_ACCOUNT_MSG = "Error updating account";
    static final String ACCOUNT_DELETED_MSG = "Account deleted";
    static final String NO_MATCHING_CUSTOMER_MSG = " (no customer was found with that account)";
    static final String ERROR_DELETING_AN_ACCOUNT_MSG = "Error occurred while deleting an account";
    static final String ALL_ACCOUNTS_DELETED_MSG = "All accounts deleted";
    static final String ERROR_DELETING_ACCOUNTS_MSG = "Error occurred while deleting accounts";
    static final String ERROR_REMOVING_ACCOUNTS_FROM_CUSTOMERS_MSG =
            "Error occurred while removing accounts from customers collection";

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsService.class);

    private final FakerService fakerService;
    private final AccountsDao accountsDao;
    private final AccountsSummaryDao accountsSummaryDao;
    private final CustomersDao customersDao;

    public AccountsService(FakerService fakerService, AccountsDao accountsDao, AccountsSummaryDao accountsSummaryDao, CustomersDao customersService) {
        this.fakerService = fakerService;
        this.accountsDao = accountsDao;
        this.accountsSummaryDao = accountsSummaryDao;
        this.customersDao = customersService;
    }

    public GenericWriteResponse insertTenAccounts() {
        try {
            List<Account> accountList = fakerService.getNewAccounts(10);
            accountsDao.insertAccounts(accountList);
            return new GenericWriteResponse(NEW_ACCOUNTS_INSERTED_MSG);
        } catch (MongoException e) {
            String errorMessage = ERROR_INSERTING_ACCOUNTS_MSG;
            LOGGER.error(errorMessage, e);
            return new GenericWriteResponse(errorMessage, e);
        }
    }

    private GetAccountListResponse tryGetAccountListOperation(Supplier<List<Account>> operation) {
        try {
            return new GetAccountListResponse(
                    SUCCESS,
                    operation.get()
            );
        } catch (MongoException e) {
            LOGGER.error(GENERIC_RETRIEVAL_ERROR, e);
            return new GetAccountListResponse(GENERIC_RETRIEVAL_ERROR, null);
        }
    }

    public GetAccountListResponse getAllAccounts() {
        try {
            return new GetAccountListResponse(
                    SUCCESS,
                    accountsDao.getAllAccounts()
            );
        } catch (MongoException e) {
            LOGGER.error(GENERIC_RETRIEVAL_ERROR, e);
            return new GetAccountListResponse(GENERIC_RETRIEVAL_ERROR, null);
        }
    }

    public GetAccountResponse getAccountByNumber(int accountNumber) {
        Account account = accountsDao.getAccountByAccountNumber(accountNumber);
        if (account == null) {
            return new GetAccountResponse(
                    ACCOUNT_NOT_FOUND_MSG,
                    null
            );
        } else {
            return new GetAccountResponse(
                    SUCCESS,
                    account
            );
        }
    }

    public GenericWriteResponse updateAccountBalanceV1(UpdateAccountBalanceV1Request request) {
        try {
            String responseText;
            UpdateResult result = accountsDao.updateBalanceByAccountNumber(
                    request.getAccountNumber(),
                    request.getBalance()
            );
            if (result.getMatchedCount() == 0) {
                responseText = ACCOUNT_NOT_FOUND_MSG;
            } else {
                responseText = ACCOUNT_BALANCE_UPDATED_MSG;
            }
            return new GenericWriteResponse(responseText);
        } catch (MongoException e) {
            String errorMessage = ERROR_UPDATING_ACCOUNT_MSG;
            LOGGER.error(errorMessage, e);
            return new GenericWriteResponse(errorMessage, e);
        }
    }

    public GenericWriteResponse deleteAccountByNumber(int accountNumber) {
        String responseMessage;
        try {
            boolean deletedInAccounts = accountsDao.deleteAccountByAccountNumber(accountNumber);
            if (!deletedInAccounts) {
                responseMessage = ACCOUNT_NOT_FOUND_MSG;
            } else {
                responseMessage = ACCOUNT_DELETED_MSG;
                boolean deletedInCustomers =
                        customersDao.removeAccountFromAllCustomers(accountNumber);
                if (!deletedInCustomers) {
                    responseMessage += NO_MATCHING_CUSTOMER_MSG;
                }
            }
            return new GenericWriteResponse(responseMessage);
        } catch (MongoException e) {
            responseMessage = ERROR_DELETING_AN_ACCOUNT_MSG;
            LOGGER.error(responseMessage, e);
            return new GenericWriteResponse(responseMessage, e);
        }

    }

    public GenericWriteResponse deleteAllAccounts() {
        boolean accountsDaoSuccessful = false;
        try {
            accountsDaoSuccessful = accountsDao.deleteAllAccounts();
            customersDao.removeAllAccountsFromCustomers();
            return new GenericWriteResponse(ALL_ACCOUNTS_DELETED_MSG);
        } catch (MongoException e) {
            String errorMessage;
            if (!accountsDaoSuccessful) {
                errorMessage = ERROR_DELETING_ACCOUNTS_MSG;
            } else {
                errorMessage = ERROR_REMOVING_ACCOUNTS_FROM_CUSTOMERS_MSG;
            }
            LOGGER.error(errorMessage, e);
            return new GenericWriteResponse(errorMessage, e);
        }
    }

    public GetAccountListResponse getAccountsByType(AccountType accountType) {
        try {
            return new GetAccountListResponse(
                    SUCCESS,
                    accountsDao.getAccountsByAccountType(accountType)
            );
        } catch (MongoException e) {
            LOGGER.error(GENERIC_RETRIEVAL_ERROR, e);
            return new GetAccountListResponse(GENERIC_RETRIEVAL_ERROR, null);
        }
    }

    public GetAccountListResponse getTopKLargestAccounts(GetTopKLargestAccountsV1Request request) {
        try {
            return new GetAccountListResponse(
                    SUCCESS,
                    accountsDao.getTopKLargestAccounts(request)
            );
        } catch (MongoException e) {
            LOGGER.error(GENERIC_RETRIEVAL_ERROR, e);
            return new GetAccountListResponse(GENERIC_RETRIEVAL_ERROR, null);
        }
    }

    public GetAccountTotalsSummaryListResponse getAccountTotalsSummaryListV1() {
        return new GetAccountTotalsSummaryListResponse(accountsSummaryDao.getAccountTotalsSummaryListV1());
    }

    public GetAccountTotalsSummaryListResponse getAccountTotalsSummaryListV2() {
        List<Document> daoSummaryList = accountsSummaryDao.getAccountTotalsSummaryListV2();
        List<AccountTotalsSummary> pojoSummaryList = convertDocumentsToAccountTypeSummaryList(daoSummaryList);
        return new GetAccountTotalsSummaryListResponse(pojoSummaryList);
    }

    private List<AccountTotalsSummary> convertDocumentsToAccountTypeSummaryList(List<Document> daoSummaryList) {
        List<AccountTotalsSummary> pojoSummaryList = new ArrayList<>();
        daoSummaryList.forEach(daoSummary -> {
            AccountTotalsSummary pojoSummary = new AccountTotalsSummary();
            pojoSummary.setAccountType(
                    AccountType.valueOf(daoSummary.get("_id").toString())
            );
            pojoSummary.setNumberOfAccounts(
                    (Integer) daoSummary.get("numberOfAccounts")
            );
            pojoSummary.setBalancesTotal(
                    new BigDecimal(daoSummary.get("balancesTotal").toString())
            );
            pojoSummaryList.add(pojoSummary);
        });
        return pojoSummaryList;
    }

    public GetAccountBucketSummaryResponseV1 getAccountBucketSummaryV1() {
        Integer[] bucketBoundaries = new Integer[]{ 0, 200000, 400000, 600000, 800000, 1000000 };

        List<Document> daoBucketList = accountsSummaryDao.getAccountBucketsByBoundaries(bucketBoundaries);
        List<AccountBucket> pojoBucketList = new ArrayList<>();

        for (int i = 0; i < daoBucketList.size(); i++) {
            AccountBucket pojoBucket = new AccountBucket();

            String balanceRange = bucketBoundaries[i] + "-" + (bucketBoundaries[i + 1]);
            pojoBucket.setBalanceRange(balanceRange);

            Integer numberOfAccounts = (Integer) daoBucketList.get(i).get("numberOfAccounts");
            pojoBucket.setNumberOfAccounts(numberOfAccounts);

            pojoBucketList.add(pojoBucket);
        }

        return new GetAccountBucketSummaryResponseV1(pojoBucketList);
    }

}
