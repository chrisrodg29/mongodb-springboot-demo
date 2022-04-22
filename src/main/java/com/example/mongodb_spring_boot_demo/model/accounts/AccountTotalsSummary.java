package com.example.mongodb_spring_boot_demo.model.accounts;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AccountTotalsSummary {

    @Id // required by spring data
    @BsonProperty("_id") // required for converting field name
    private AccountType accountType;
    private int numberOfAccounts;
    private BigDecimal balancesTotal;

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public int getNumberOfAccounts() {
        return numberOfAccounts;
    }

    public void setNumberOfAccounts(int numberOfAccounts) {
        this.numberOfAccounts = numberOfAccounts;
    }

    public BigDecimal getBalancesTotal() {
        return balancesTotal;
    }

    public void setBalancesTotal(BigDecimal balancesTotal) {
        this.balancesTotal = balancesTotal.setScale(2, RoundingMode.HALF_DOWN);
    }

    @Override
    public String toString() {
        return "AccountTypeSummary{" +
                "accountType=" + accountType +
                ", numberOfAccounts=" + numberOfAccounts +
                ", balancesTotal=" + balancesTotal +
                '}';
    }
}
