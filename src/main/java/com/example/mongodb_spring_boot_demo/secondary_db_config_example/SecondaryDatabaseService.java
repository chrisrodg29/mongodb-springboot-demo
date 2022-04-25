package com.example.mongodb_spring_boot_demo.secondary_db_config_example;

import com.example.mongodb_spring_boot_demo.api.GenericReadResponse;
import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.accounts.UpdateAccountBalanceV1Request;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.example.mongodb_spring_boot_demo.service.FakerService;
import com.mongodb.client.result.UpdateResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.mongodb_spring_boot_demo.util.MongoExceptionHelper.*;

@Service
@ConditionalOnProperty(value = "spring.mongodb2.uri")
public class SecondaryDatabaseService {

    static final String ACCOUNT_NOT_FOUND_MSG = "No account with that number was found";
    static final String NEW_ACCOUNTS_INSERTED_MSG = "New Accounts inserted";
    static final String ERROR_INSERTING_ACCOUNTS_MSG = "Error inserting accounts";
    static final String ACCOUNT_BALANCE_UPDATED_MSG = "Account balance updated";
    static final String ERROR_UPDATING_ACCOUNT_MSG = "Error updating account";
    static final String ACCOUNT_DELETED_MSG = "Account deleted";
    static final String ERROR_DELETING_AN_ACCOUNT_MSG = "Error occurred while deleting an account";
    static final String ERROR_DELETING_ACCOUNTS_MSG = "Error occurred while deleting accounts";

    private final FakerService fakerService;
    private final SecondaryDatabaseDao secondaryDao;

    public SecondaryDatabaseService(FakerService fakerService, SecondaryDatabaseDao secondaryDao) {
        this.fakerService = fakerService;
        this.secondaryDao = secondaryDao;
    }

    public GenericReadResponse<List<Account>> getAllAccounts() {
        return safeRead(secondaryDao::getAllAccounts);
    }

    public GenericReadResponse<Account> getAccountByNumber(int accountNumber) {
        return safeRead(
                () -> secondaryDao.getAccountByAccountNumber(accountNumber),
                ACCOUNT_NOT_FOUND_MSG
        );
    }

    public GenericWriteResponse insertTenAccounts() {
        return safeWrite(() -> {
            List<Account> accountList = fakerService.getNewAccounts(10);
            secondaryDao.insertAccounts(accountList);
            return NEW_ACCOUNTS_INSERTED_MSG;
        }, ERROR_INSERTING_ACCOUNTS_MSG);
    }

    public GenericWriteResponse updateAccountBalanceV1(UpdateAccountBalanceV1Request request) {
        return safeWrite(() -> {
            UpdateResult result = secondaryDao.updateBalanceByAccountNumber(
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
            boolean deletedInAccounts = secondaryDao.deleteAccountByAccountNumber(accountNumber);
            if (!deletedInAccounts) {
                return ACCOUNT_NOT_FOUND_MSG;
            } else {
                return ACCOUNT_DELETED_MSG;
            }
        }, ERROR_DELETING_AN_ACCOUNT_MSG);
    }

    public GenericWriteResponse deleteAllAccounts() {
        return safeWrite(() -> {
            secondaryDao.deleteAllAccounts();
            return SUCCESS;
        }, ERROR_DELETING_ACCOUNTS_MSG);
    }

}
