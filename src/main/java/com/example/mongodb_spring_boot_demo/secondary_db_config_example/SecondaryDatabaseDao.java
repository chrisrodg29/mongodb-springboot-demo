package com.example.mongodb_spring_boot_demo.secondary_db_config_example;

import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Repository
@ConditionalOnProperty(value = "databaseConfiguration.mongodb2.uri")
public class SecondaryDatabaseDao {

    private static final String COLLECTION = "secondary_accounts";

    private final MongoCollection<Account> accountsCollection;

    public SecondaryDatabaseDao(MongoDatabase database2) {
        this.accountsCollection = database2
                .getCollection(COLLECTION, Account.class);
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
        return deleteResult.getDeletedCount() == 1;
    }

    public boolean deleteAllAccounts() {
        DeleteResult deleteResult = accountsCollection.deleteMany(new Document());
        return deleteResult.wasAcknowledged();
    }

}
