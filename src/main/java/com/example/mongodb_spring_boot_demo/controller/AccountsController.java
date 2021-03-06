package com.example.mongodb_spring_boot_demo.controller;

import com.example.mongodb_spring_boot_demo.api.GenericReadResponse;
import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.accounts.*;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountBucket;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountTotalsSummary;
import com.example.mongodb_spring_boot_demo.service.AccountsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/accounts")
public class AccountsController {

    private final AccountsService accountsService;

    public AccountsController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @GetMapping("getAllAccounts/V1")
    public GenericReadResponse<List<Account>> getAllAccountsV1() {
        return accountsService.getAllAccounts();
    }

    @PostMapping("getAccountByNumber/V1")
    public GenericReadResponse<Account> getAccountByNumberV1(@RequestBody GetAccountByNumberV1Request request) {
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

    @PostMapping("deleteAccountByNumber/V1")
    public GenericWriteResponse deleteAccountByNumberV1(@RequestBody DeleteAccountByNumberRequestV1 request) {
        return accountsService.deleteAccountByNumber(request.getAccountNumber());
    }

    @DeleteMapping("deleteAllAccounts/V1")
    public GenericWriteResponse deleteAllAccountsV1() {
        return accountsService.deleteAllAccounts();
    }

    @PostMapping("getAccountsByType/V1")
    public GenericReadResponse<List<Account>> getAccountByTypeV1(@RequestBody GetAccountsByTypeV1Request request) {
        return accountsService.getAccountsByType(request.getAccountType());
    }

    @PostMapping("getTopKLargestAccounts/V1")
    public GenericReadResponse<List<Account>> getTopKLargestAccountsV1(@RequestBody GetTopKLargestAccountsV1Request request) {
        return accountsService.getTopKLargestAccounts(request);
    }

    @GetMapping("getAccountTotalsSummaryList/V1")
    public GenericReadResponse<List<AccountTotalsSummary>> getAccountTotalsSummaryListV1() {
        return accountsService.getAccountTotalsSummaryListV1();
    }

    @GetMapping("getAccountTotalsSummaryList/V2")
    public GenericReadResponse<List<AccountTotalsSummary>> getAccountTotalsSummaryListV2() {
        return accountsService.getAccountTotalsSummaryListV2();
    }

    @GetMapping("getAccountBucketSummary/V1")
    public GenericReadResponse<List<AccountBucket>> getAccountBucketSummaryV1() {
        return accountsService.getAccountBucketSummaryV1();
    }

}
