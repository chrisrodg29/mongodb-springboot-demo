package com.example.mongodb_spring_boot_demo.model.accounts;

import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;

//@Document(collection = "accounts") // Only needed for spring data repositories
public class Account {

    // Account Numbers are between 10000001 and 99999999
    private int accountNumber;
    private AccountType accountType;
    private BigDecimal balance;

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance.setScale(2, RoundingMode.HALF_DOWN);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber=" + accountNumber +
                ", accountType=" + accountType +
                ", balance=" + balance +
                '}';
    }
}
