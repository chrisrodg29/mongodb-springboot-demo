package com.example.mongodb_spring_boot_demo.model.accounts;

public class AccountBucket {

    private String balanceRange;
    private int numberOfAccounts;

    public String getBalanceRange() {
        return balanceRange;
    }

    public void setBalanceRange(String balanceRange) {
        this.balanceRange = balanceRange;
    }

    public int getNumberOfAccounts() {
        return numberOfAccounts;
    }

    public void setNumberOfAccounts(int numberOfAccounts) {
        this.numberOfAccounts = numberOfAccounts;
    }
}
