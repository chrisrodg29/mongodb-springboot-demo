package com.example.mongodb_spring_boot_demo.dao.accounts;

import com.example.mongodb_spring_boot_demo.api.accounts.GetTopKLargestAccountsV1Request;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.example.mongodb_spring_boot_demo.model.accounts.AccountType;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

@Repository
public class AccountsDao {

    private static final String COLLECTION = "accounts";

    private final MongoCollection<Account> accountsCollection;

    public AccountsDao(MongoDatabase database) {
        // Because every method in this DAO returns an Account, we set Account
        // as the default class for the collection.
        this.accountsCollection = database.getCollection(COLLECTION, Account.class);
    }

    public boolean insertAccounts(List<Account> accountList) {
        InsertManyResult insertManyResult = accountsCollection.insertMany(accountList);
        return insertManyResult.wasAcknowledged();
    }

    public List<Account> getAllAccounts() {
        return accountsCollection.find().into(new ArrayList<>());
    }

    public Account getAccountByAccountNumber(int accountNumber) {
        Bson query = eq("accountNumber", accountNumber);
        return accountsCollection.find(query).first();
    }

    public UpdateResult updateBalanceByAccountNumber(int accountNumber, BigDecimal balance) {
        Bson query = eq("accountNumber", accountNumber);
        Bson update = Updates.set("balance", balance);
        return accountsCollection.updateOne(query, update);
    }

    public boolean deleteAccountByAccountNumber(int accountNumber) {
        Bson query = eq("accountNumber", accountNumber);
        DeleteResult deleteResult = accountsCollection.deleteOne(query);
        System.out.println(deleteResult);
        return deleteResult.getDeletedCount() == 1;
    }

    public boolean deleteAllAccounts() {
        DeleteResult deleteResult = accountsCollection.deleteMany(new Document());
        return deleteResult.wasAcknowledged();
    }

    public ArrayList<Account> getAccountsByAccountType(AccountType accountType) {
        return accountsCollection.find(
                eq("accountType", accountType.name())
        ).into(new ArrayList<>());
    }

    public ArrayList<Account> getTopKLargestAccounts(GetTopKLargestAccountsV1Request request) {
        List<Bson> aggregationPipeline = new ArrayList<>();

        if (request.getAccountType() != null) {
            Bson matchStage = match(eq("accountType", request.getAccountType().name()));
            aggregationPipeline.add(matchStage);
        }

        Bson sortStage = sort(descending("balance"));
        aggregationPipeline.add(sortStage);

        Bson limitStage = limit(request.getNumber());
        aggregationPipeline.add(limitStage);

        return accountsCollection.aggregate(aggregationPipeline).into(new ArrayList<>());
    }

}
