package com.example.mongodb_spring_boot_demo.secondary_db_config_example;

import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.accounts.DeleteAccountByNumberRequestV1;
import com.example.mongodb_spring_boot_demo.api.accounts.GetAccountByNumberV1Request;
import com.example.mongodb_spring_boot_demo.api.accounts.GetAccountResponse;
import com.example.mongodb_spring_boot_demo.api.accounts.UpdateAccountBalanceV1Request;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ConditionalOnProperty(value = "spring.mongodb2.uri")
@RequestMapping("api/secondaryDatabaseTest")
public class SecondaryDatabaseController {

    private final SecondaryDatabaseService accountsService;

    public SecondaryDatabaseController(SecondaryDatabaseService accountsService) {
        this.accountsService = accountsService;
    }

    @GetMapping("getAllAccounts/V1")
    public List<Account> getAllAccountsV1() {
        return accountsService.getAllAccounts();
    }

    @GetMapping("getAccountByNumber/V1")
    public GetAccountResponse getAccountByNumberV1(@RequestBody GetAccountByNumberV1Request request) {
        return accountsService.getAccountByNumber(request.getAccountNumber());
    }

    @PostMapping("addTenAccounts/V1")
    public GenericWriteResponse addTenAccountsV1() {
        return accountsService.insertTenAccounts();
    }

    @PutMapping("updateAccountBalance/V1")
    public GenericWriteResponse updateAccountBalanceV1(@RequestBody UpdateAccountBalanceV1Request request) {
        return accountsService.updateAccountBalanceV1(request);
    }

    @DeleteMapping("deleteAccountByNumber/V1")
    public GenericWriteResponse deleteAccountByNumberV1(@RequestBody DeleteAccountByNumberRequestV1 request) {
        return accountsService.deleteAccountByNumber(request.getAccountNumber());
    }

    @DeleteMapping("deleteAllAccounts/V1")
    public GenericWriteResponse deleteAllAccountsV1() {
        return accountsService.deleteAllAccounts();
    }

}
