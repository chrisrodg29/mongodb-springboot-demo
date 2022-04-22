package com.example.mongodb_spring_boot_demo.api.accounts;

import com.example.mongodb_spring_boot_demo.model.accounts.AccountBucket;

import java.util.List;

public class GetAccountBucketSummaryResponseV1 {

    private List<AccountBucket> accountBucketList;

    public GetAccountBucketSummaryResponseV1() {
    }

    public GetAccountBucketSummaryResponseV1(List<AccountBucket> accountBucketList) {
        this.accountBucketList = accountBucketList;
    }

    public List<AccountBucket> getAccountBucketList() {
        return accountBucketList;
    }

    public void setAccountBucketList(List<AccountBucket> accountBucketList) {
        this.accountBucketList = accountBucketList;
    }
}
