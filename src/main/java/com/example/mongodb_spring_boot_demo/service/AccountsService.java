package com.example.mongodb_spring_boot_demo.service;

import com.example.mongodb_spring_boot_demo.api.GenericReadResponse;
import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.accounts.GetTopKLargestAccountsV1Request;
import com.example.mongodb_spring_boot_demo.api.accounts.UpdateAccountBalanceV1Request;
import com.example.mongodb_spring_boot_demo.dao.accounts.AccountsDao;
import com.example.mongodb_spring_boot_demo.dao.accounts.AccountTransformationsDao;
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

import static com.example.mongodb_spring_boot_demo.util.MongoExceptionHelper.*;

@Service
public class AccountsService {

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
    private final AccountTransformationsDao accountTransformationsDao;
    private final CustomersDao customersDao;

    public AccountsService(FakerService fakerService, AccountsDao accountsDao, AccountTransformationsDao accountTransformationsDao, CustomersDao customersService) {
        this.fakerService = fakerService;
        this.accountsDao = accountsDao;
        this.accountTransformationsDao = accountTransformationsDao;
        this.customersDao = customersService;
    }

    public GenericWriteResponse insertTenAccounts() {
        return safeWrite(() -> {
            List<Account> accountList = fakerService.getNewAccounts(10);
            accountsDao.insertAccounts(accountList);
            return NEW_ACCOUNTS_INSERTED_MSG;
        }, ERROR_INSERTING_ACCOUNTS_MSG);
    }

    public GenericReadResponse<List<Account>> getAllAccounts() {
        return safeRead(accountsDao::getAllAccounts);
    }

    public GenericReadResponse<Account> getAccountByNumber(int accountNumber) {
        return safeRead(
                () -> accountsDao.getAccountByAccountNumber(accountNumber),
                ACCOUNT_NOT_FOUND_MSG
        );
    }

    public GenericWriteResponse updateAccountBalanceV1(UpdateAccountBalanceV1Request request) {
        return safeWrite(() -> {
            UpdateResult result = accountsDao.updateBalanceByAccountNumber(
                    request.getAccountNumber(),
                    request.getBalance()
            );
            if (result.getMatchedCount() == 0) {
                return ACCOUNT_NOT_FOUND_MSG;
            } else {
                return ACCOUNT_BALANCE_UPDATED_MSG;
            }
        }, ERROR_UPDATING_ACCOUNT_MSG);
    }

    public GenericWriteResponse deleteAccountByNumber(int accountNumber) {
        return safeWrite(() -> {
            boolean deletedInAccounts = accountsDao.deleteAccountByAccountNumber(accountNumber);
            if (!deletedInAccounts) {
                return ACCOUNT_NOT_FOUND_MSG;
            } else {
                boolean deletedInCustomers =
                        customersDao.removeAccountFromAllCustomers(accountNumber);
                if (deletedInCustomers) {
                    return ACCOUNT_DELETED_MSG;
                } else {
                    return ACCOUNT_DELETED_MSG + NO_MATCHING_CUSTOMER_MSG;
                }
            }
        }, ERROR_DELETING_AN_ACCOUNT_MSG);
    }

    public GenericWriteResponse deleteAllAccounts() {
        GenericWriteResponse firstResponse = safeWrite(() -> {
            accountsDao.deleteAllAccounts();
            return SUCCESS;
        });
        if (!firstResponse.getResponseText().equals(SUCCESS)) {
            firstResponse.setResponseText(ERROR_DELETING_ACCOUNTS_MSG);
            return firstResponse;
        }
        GenericWriteResponse secondResponse = safeWrite(() -> {
            customersDao.removeAllAccountsFromCustomers();
            return ALL_ACCOUNTS_DELETED_MSG;
        });
        if (!secondResponse.getResponseText().equals(ALL_ACCOUNTS_DELETED_MSG)) {
            secondResponse.setResponseText(ERROR_REMOVING_ACCOUNTS_FROM_CUSTOMERS_MSG);
        }
        return secondResponse;
    }

    public GenericReadResponse<List<Account>> getAccountsByType(AccountType accountType) {
        return safeRead(() -> accountsDao.getAccountsByAccountType(accountType));
    }

    public GenericReadResponse<List<Account>> getTopKLargestAccounts(GetTopKLargestAccountsV1Request request) {
        return safeRead(() -> accountsDao.getTopKLargestAccounts(request));
    }

    public GenericReadResponse<List<AccountTotalsSummary>> getAccountTotalsSummaryListV1() {
        return safeRead(accountTransformationsDao::getAccountTotalsSummaryListV1);
    }

    public GenericReadResponse<List<AccountTotalsSummary>> getAccountTotalsSummaryListV2() {
        List<Document> daoSummaryList;
        try {
            daoSummaryList = accountTransformationsDao.getAccountTotalsSummaryListV2();
        } catch (MongoException e) {
            LOGGER.error(GENERIC_READ_ERROR, e);
            return new GenericReadResponse<>(
                    GENERIC_READ_ERROR,
                    null,
                    e.getMessage()
            );
        }
        List<AccountTotalsSummary> pojoSummaryList = convertDocumentsToAccountTypeSummaryList(daoSummaryList);
        return new GenericReadResponse<>(SUCCESS, pojoSummaryList);
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

    public GenericReadResponse<List<AccountBucket>> getAccountBucketSummaryV1() {
        Integer[] bucketBoundaries = new Integer[]{ 0, 200000, 400000, 600000, 800000, 1000000 };
        List<Document> daoBucketList;
        try {
            daoBucketList = accountTransformationsDao.getAccountBucketsByBoundaries(bucketBoundaries);
        } catch (MongoException e) {
            LOGGER.error(GENERIC_READ_ERROR, e);
            return new GenericReadResponse<>(
                    GENERIC_READ_ERROR,
                    null,
                    e.getMessage()
            );
        }
        List<AccountBucket> pojoBucketList = new ArrayList<>();

        for (int i = 0; i < daoBucketList.size(); i++) {
            AccountBucket pojoBucket = new AccountBucket();

            String balanceRange = "At least " + bucketBoundaries[i] + ", Less than " + (bucketBoundaries[i + 1]);
            pojoBucket.setBalanceRange(balanceRange);

            Integer numberOfAccounts = (Integer) daoBucketList.get(i).get("numberOfAccounts");
            pojoBucket.setNumberOfAccounts(numberOfAccounts);

            pojoBucketList.add(pojoBucket);
        }

        return new GenericReadResponse<>(SUCCESS, pojoBucketList);
    }

}
