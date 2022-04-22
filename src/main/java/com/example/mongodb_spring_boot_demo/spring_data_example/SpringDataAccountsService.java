package com.example.mongodb_spring_boot_demo.spring_data_example;

import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.accounts.*;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountBucket;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountType;
import com.example.mongodb_spring_boot_demo.service.AccountsService;
import com.example.mongodb_spring_boot_demo.service.FakerService;
import com.example.mongodb_spring_boot_demo.spring_data_example.repository.SpringDataAccountsRepository;
import com.mongodb.MongoWriteException;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

    public GetAccountResponse getAccountByNumber(int accountNumber) {
        System.out.println("here");
        Account account = repository.getByAccountNumber(accountNumber);
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

    public GenericWriteResponse insertTenAccounts() {
        try {
            List<Account> accountList = fakerService.getNewAccounts(10);
            repository.insert(accountList);
            return new GenericWriteResponse(NEW_ACCOUNTS_INSERTED_MSG);
        } catch (MongoWriteException e) {
            String errorMessage = ERROR_INSERTING_ACCOUNTS_MSG;
            LOGGER.error(errorMessage, e);
            return new GenericWriteResponse(errorMessage, e);
        }
    }

    public GenericWriteResponse updateAccountBalanceV1(UpdateAccountBalanceV1Request request) {
        try {
            String responseText;
            UpdateResult result = repository.updateBalanceByAccountNumber(
                    request.getAccountNumber(),
                    request.getBalance()
            );
            if (result.getMatchedCount() == 0) {
                responseText = ACCOUNT_NOT_FOUND_MSG;
            } else {
                responseText = ACCOUNT_BALANCE_UPDATED_MSG;
            }
            return new GenericWriteResponse(responseText);
        } catch (MongoWriteException e) {
            String errorMessage = ERROR_UPDATING_ACCOUNT_MSG;
            LOGGER.error(errorMessage, e);
            return new GenericWriteResponse(errorMessage, e);
        }
    }

    public GenericWriteResponse deleteAccountByNumber(int accountNumber) {
        String responseMessage;
        try {
            int deletedAccounts = repository.deleteByAccountNumber(accountNumber);
            if (deletedAccounts == 0) {
                responseMessage = ACCOUNT_NOT_FOUND_MSG;
            } else {
                responseMessage = ACCOUNT_DELETED_MSG;
            }
            return new GenericWriteResponse(responseMessage);
        } catch (MongoWriteException e) {
            responseMessage = ERROR_DELETING_AN_ACCOUNT_MSG;
            LOGGER.error(responseMessage, e);
            return new GenericWriteResponse(responseMessage, e);
        }

    }

    public GenericWriteResponse deleteAllAccounts() {
        try {
            repository.deleteAll();
            return new GenericWriteResponse(ALL_ACCOUNTS_DELETED_MSG);
        } catch (MongoWriteException e) {
            LOGGER.error(ERROR_DELETING_ACCOUNTS_MSG, e);
            return new GenericWriteResponse(ERROR_DELETING_ACCOUNTS_MSG, e);
        }
    }

    public List<Account> getAccountByType(AccountType accountType) {
        return repository.getByAccountType(accountType);
    }

    public List<Account> getTopKLargestAccounts(GetTopKLargestAccountsV1Request request) {
        return repository.getTopKLargestAccounts(request);
    }

    public GetAccountTotalsSummaryListResponse getAccountTotalsSummaryListV1() {
        return new GetAccountTotalsSummaryListResponse(repository.getAccountTotalsSummaryListV1());
    }

    public GetAccountBucketSummaryResponseV1 getAccountBucketSummaryV1() {
        Integer[] bucketBoundaries = new Integer[]{ 0, 200000, 400000, 600000, 800000, 1000000 };

        List<Document> daoBucketList = repository.getAccountBucketsByBoundaries(bucketBoundaries);
        List<AccountBucket> pojoBucketList = new ArrayList<>();

        for (int i = 0; i < daoBucketList.size(); i++) {
            AccountBucket pojoBucket = new AccountBucket();

            String balanceRange = bucketBoundaries[i] + "-" + (bucketBoundaries[i + 1]);
            pojoBucket.setBalanceRange(balanceRange);

            Integer numberOfAccounts = (Integer) daoBucketList.get(i).get("numberOfAccounts");
            pojoBucket.setNumberOfAccounts(numberOfAccounts);

            pojoBucketList.add(pojoBucket);
        }

//        List<AccountBucket> pojoBucketList =
//                repository.getAccountBucketsByBoundaries(bucketBoundaries);

        return new GetAccountBucketSummaryResponseV1(pojoBucketList);
    }

}
