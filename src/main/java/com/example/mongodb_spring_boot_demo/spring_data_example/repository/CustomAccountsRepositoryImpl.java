package com.example.mongodb_spring_boot_demo.spring_data_example.repository;

import com.example.mongodb_spring_boot_demo.api.accounts.GetTopKLargestAccountsV1Request;
import com.example.mongodb_spring_boot_demo.model.accounts.Account;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CustomAccountsRepositoryImpl implements CustomAccountsRepository {

    private final MongoTemplate mongoTemplate;

    public CustomAccountsRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public UpdateResult updateBalanceByAccountNumber(int accountNumber, BigDecimal balance) {
        Query query = Query.query(Criteria.where("accountNumber").is(accountNumber));
        Update update = Update.update("balance", balance);
        return mongoTemplate.updateFirst(query, update, Account.class);
    }

    @Override
    public List<Account> getTopKLargestAccounts(GetTopKLargestAccountsV1Request request) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        if (request.getAccountType() != null) {
            MatchOperation match = Aggregation.match(
                    new Criteria("accountType").is(request.getAccountType())
            );
            aggregationOperations.add(match);
        }

        SortOperation sort = Aggregation.sort(
                Sort.by(Sort.Direction.DESC, "balance")
        );
        aggregationOperations.add(sort);

        LimitOperation limit = Aggregation.limit(request.getK());
        aggregationOperations.add(limit);

        AggregationResults<Account> results = mongoTemplate.aggregate(
                Aggregation.newAggregation(aggregationOperations),
                "account",
                Account.class
        );
        return results.getMappedResults();
    }

    @Override
    public List<Document> getAccountBucketsByBoundaries(Integer[] boundaries) {
        BucketOperation bucketOperation =
                Aggregation.bucket(ConvertOperators.ToDecimal.toDecimal("$balance"))
                        .withBoundaries((Object[]) boundaries)
                        .andOutputCount().as("numberOfAccounts");
        AggregationResults<Document> results = mongoTemplate.aggregate(
                Aggregation.newAggregation(bucketOperation),
                "account",
                Document.class
        );

        return results.getMappedResults();
    }

}
