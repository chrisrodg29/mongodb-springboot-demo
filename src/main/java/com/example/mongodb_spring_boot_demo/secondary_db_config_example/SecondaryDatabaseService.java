package com.example.mongodb_spring_boot_demo.secondary_db_config_example;

import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.accounts.GetAccountResponse;
import com.example.mongodb_spring_boot_demo.api.accounts.UpdateAccountBalanceV1Request;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.example.mongodb_spring_boot_demo.service.FakerService;
import com.mongodb.MongoWriteException;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(value = "spring.mongodb2.uri")
public class SecondaryDatabaseService {

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

    private static final Logger LOGGER = LoggerFactory.getLogger(SecondaryDatabaseService.class);

    private final FakerService fakerService;
    private final SecondaryDatabaseDao secondaryDao;

    public SecondaryDatabaseService(FakerService fakerService, SecondaryDatabaseDao secondaryDao) {
        this.fakerService = fakerService;
        this.secondaryDao = secondaryDao;
    }

    public List<Account> getAllAccounts() {
        return secondaryDao.getAllAccounts();
    }

    public GetAccountResponse getAccountByNumber(int accountNumber) {
        Account account = secondaryDao.getAccountByAccountNumber(accountNumber);
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
            secondaryDao.insertAccounts(accountList);
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
            UpdateResult result = secondaryDao.updateBalanceByAccountNumber(
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
            boolean deletedInAccounts = secondaryDao.deleteAccountByAccountNumber(accountNumber);
            if (!deletedInAccounts) {
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
            secondaryDao.deleteAllAccounts();
            return new GenericWriteResponse(ALL_ACCOUNTS_DELETED_MSG);
        } catch (MongoWriteException e) {
            LOGGER.error(ERROR_DELETING_ACCOUNTS_MSG, e);
            return new GenericWriteResponse(ERROR_DELETING_ACCOUNTS_MSG, e);
        }
    }

}
