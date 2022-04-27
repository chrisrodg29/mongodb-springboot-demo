package com.example.mongodb_spring_boot_demo.spring_data_example.repository;

import com.example.mongodb_spring_boot_demo.api.accounts.GetTopKLargestAccountsV1Request;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.math.BigDecimal;
import java.util.List;

public interface CustomAccountsRepository {

    UpdateResult updateBalanceByAccountNumber(int accountNumber, BigDecimal balance);

    List<Account> getTopKLargestAccounts(GetTopKLargestAccountsV1Request request);

    List<Document> getAccountBucketsByBoundaries(Integer[] boundaries);

}
