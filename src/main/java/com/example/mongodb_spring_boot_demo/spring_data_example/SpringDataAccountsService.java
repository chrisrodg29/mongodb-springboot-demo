package com.example.mongodb_spring_boot_demo.spring_data_example;

import com.example.mongodb_spring_boot_demo.api.GenericReadResponse;
import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.accounts.*;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountBucket;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountTotalsSummary;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountType;
import com.example.mongodb_spring_boot_demo.service.AccountsService;
import com.example.mongodb_spring_boot_demo.service.FakerService;
import com.example.mongodb_spring_boot_demo.spring_data_example.repository.SpringDataAccountsRepository;
import com.mongodb.MongoException;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.mongodb_spring_boot_demo.util.MongoExceptionHelper.*;

@Service
public class SpringDataAccountsService {

    static final String SUCCESS = "success";
    static final String ACCOUNT_NOT_FOUND_MSG = "No account with that number was found";
    static final String NEW_ACCOUNTS_INSERTED_MSG = "New Accounts inserted";
    static final String ERROR_INSERTING_ACCOUNTS_MSG = "Error inserting accounts";
    static final String ACCOUNT_BALANCE_UPDATED_MSG = "Account balance updated";
    static final String ERROR_UPDATING_ACCOUNT_MSG = "Error updating account";
    static final String ACCOUNT_DELETED_MSG = "Account deleted";
    static final String ERROR_DELETING_AN_ACCOUNT_MSG = "Error occurred while deleting an account";
    static final String ALL_ACCOUNTS_DELETED_MSG = "All accounts deleted";
    static final String ERROR_DELETING_ACCOUNTS_MSG = "Error occurred while deleting accounts";

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsService.class);

    private final FakerService fakerService;
    private final SpringDataAccountsRepository repository;

    public SpringDataAccountsService(FakerService fakerService, SpringDataAccountsRepository repository) {
        this.fakerService = fakerService;
        this.repository = repository;
    }

    public GenericReadResponse<List<Account>> getAllAccounts() {
        return safeRead(repository::findAll);
    }

    public GenericReadResponse<Account> getAccountByNumber(int accountNumber) {
        return safeRead(
                () -> repository.getByAccountNumber(accountNumber),
                ACCOUNT_NOT_FOUND_MSG
        );
    }

    public GenericWriteResponse insertTenAccounts() {
        return safeWrite(() -> {
            List<Account> accountList = fakerService.getNewAccounts(10);
            repository.insert(accountList);
            return NEW_ACCOUNTS_INSERTED_MSG;
        }, ERROR_INSERTING_ACCOUNTS_MSG);
    }

    public GenericWriteResponse updateAccountBalanceV1(UpdateAccountBalanceV1Request request) {
        return safeWrite(() -> {
            UpdateResult result = repository.updateBalanceByAccountNumber(
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
            int deletedAccounts = repository.deleteByAccountNumber(accountNumber);
            if (deletedAccounts > 0) {
                return ACCOUNT_NOT_FOUND_MSG;
            } else {
                return ACCOUNT_DELETED_MSG;
            }
        }, ERROR_DELETING_AN_ACCOUNT_MSG);
    }

    public GenericWriteResponse deleteAllAccounts() {
        return safeWrite(() -> {
            repository.deleteAll();
            return ALL_ACCOUNTS_DELETED_MSG;
        }, ERROR_DELETING_ACCOUNTS_MSG);
    }

    public GenericReadResponse<List<Account>> getAccountByType(AccountType accountType) {
        return safeRead(() -> repository.getByAccountType(accountType));
    }

    public GenericReadResponse<List<Account>> getTopKLargestAccounts(GetTopKLargestAccountsV1Request request) {
        return safeRead(() -> repository.getTopKLargestAccounts(request));
    }

    public GenericReadResponse<List<AccountTotalsSummary>> getAccountTotalsSummaryListV1() {
        return safeRead(repository::getAccountTotalsSummaryListV1);
    }

    public GenericReadResponse<List<AccountBucket>> getAccountBucketSummaryV1() {
        Integer[] bucketBoundaries = new Integer[]{ 0, 200000, 400000, 600000, 800000, 1000000 };
        List<Document> daoBucketList;
        try {
            daoBucketList = repository.getAccountBucketsByBoundaries(bucketBoundaries);
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

            String balanceRange = bucketBoundaries[i] + "-" + (bucketBoundaries[i + 1]);
            pojoBucket.setBalanceRange(balanceRange);

            Integer numberOfAccounts = (Integer) daoBucketList.get(i).get("numberOfAccounts");
            pojoBucket.setNumberOfAccounts(numberOfAccounts);

            pojoBucketList.add(pojoBucket);
        }

        return new GenericReadResponse<>(SUCCESS, pojoBucketList);
    }

}
