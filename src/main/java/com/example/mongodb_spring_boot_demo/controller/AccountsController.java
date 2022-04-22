package com.example.mongodb_spring_boot_demo.controller;

import com.example.mongodb_spring_boot_demo.api.GenericWriteResponse;
import com.example.mongodb_spring_boot_demo.api.accounts.*;
import com.example.mongodb_spring_boot_demo.service.AccountsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/accounts")
public class AccountsController {

    private final AccountsService accountsService;

    public AccountsController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @GetMapping("getAllAccounts/V1")
    public GetAccountListResponse getAllAccountsV1() {
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

    @GetMapping("getAccountsByType/V1")
    public GetAccountListResponse getAccountByTypeV1(@RequestBody GetAccountsByTypeV1Request request) {
        return accountsService.getAccountsByType(request.getAccountType());
    }

    @GetMapping("getTopKLargestAccounts/V1")
    public GetAccountListResponse getTopKLargestAccountsV1(@RequestBody GetTopKLargestAccountsV1Request request) {
        return accountsService.getTopKLargestAccounts(request);
    }

    @GetMapping("getAccountTotalsSummaryList/V1")
    public GetAccountTotalsSummaryListResponse getAccountTotalsSummaryListV1() {
        return accountsService.getAccountTotalsSummaryListV1();
    }

    @GetMapping("getAccountTotalsSummaryList/V2")
    public GetAccountTotalsSummaryListResponse getAccountTotalsSummaryListV2() {
        return accountsService.getAccountTotalsSummaryListV2();
    }

    @GetMapping("getAccountBucketSummary/V1")
    public GetAccountBucketSummaryResponseV1 getAccountBucketSummaryV1() {
        return accountsService.getAccountBucketSummaryV1();
    }

}
