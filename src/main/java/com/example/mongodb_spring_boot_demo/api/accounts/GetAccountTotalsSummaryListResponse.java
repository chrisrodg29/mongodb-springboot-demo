package com.example.mongodb_spring_boot_demo.api.accounts;

import com.example.mongodb_spring_boot_demo.model.accounts.AccountTotalsSummary;

import java.util.List;

public class GetAccountTotalsSummaryListResponse {

    private List<AccountTotalsSummary> accountTotalsSummaryList;

    public GetAccountTotalsSummaryListResponse(List<AccountTotalsSummary> accountTotalsSummaryList) {
        this.accountTotalsSummaryList = accountTotalsSummaryList;
    }

    public List<AccountTotalsSummary> getAccountTotalsSummaryList() {
        return accountTotalsSummaryList;
    }

    public void setAccountTotalsSummaryList(List<AccountTotalsSummary> accountTotalsSummaryList) {
        this.accountTotalsSummaryList = accountTotalsSummaryList;
    }

    @Override
    public String toString() {
        return "GetAccountsSummaryV1Response{" +
                "accountTotalsSummaryList=" + accountTotalsSummaryList +
                '}';
    }
}
